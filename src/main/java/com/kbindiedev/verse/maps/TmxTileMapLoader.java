package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.animation.SpriteAnimation;
import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.gfx.Texture;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.InvalidDataException;
import com.kbindiedev.verse.system.parse.DOMElementUtil;
import com.kbindiedev.verse.util.Properties;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: mention unsupported parts

/** Loads .tmx files (Tiled maps). */
public class TmxTileMapLoader implements ITileMapLoaderImplementation {

    public static final String TILE_METADATA_PROPERTY_NAME = "tiled_metadata";

    private static DocumentBuilderFactory dbf;

    // TODO temp.
    private static final String TEMPPREFIXFILEPATH = "./../spritepack_demo/";

    public TmxTileMapLoader() {
        dbf = DocumentBuilderFactory.newInstance();
    }

    @Override
    public Tilemap loadTileMap(InputStream stream) throws IOException, InvalidDataException {

        Document document = parseXML(stream);

        System.out.println("Root element name: " + document.getDocumentElement().getNodeName());
        //document.getDocumentElement().getName

        Element mapElement = document.getDocumentElement();
        int widthInTiles = DOMElementUtil.getIntAttribute(mapElement, "width", 0);
        int heightInTiles = DOMElementUtil.getIntAttribute(mapElement, "height", 0);
        int tileWidth = DOMElementUtil.getIntAttribute(mapElement, "tilewidth", 0);
        int tileHeight = DOMElementUtil.getIntAttribute(mapElement, "tileheight", 0);

        Tileset mainSet = new Tileset();
        for (Element tilesetElement : DOMElementUtil.getDirectChildrenByName(document.getDocumentElement(), "tileset")) {
            Tileset tileset = loadTileset(tilesetElement);
            boolean didClash = !mainSet.merge(tileset, 0, true);
            if (didClash) Assertions.warn("unexpected clash in main merge for .tmx file");
        }

        Vector3f relativeOrigin = new Vector3f(0f, heightInTiles * tileHeight, 0f);
        return loadAllLayers(document.getDocumentElement(), mainSet, tileWidth, tileHeight, relativeOrigin);
    }

    private Document parseXML(InputStream stream) throws IOException, InvalidDataException {
        DocumentBuilder builder;
        try {
            synchronized(dbf) { builder = dbf.newDocumentBuilder(); }
        } catch (ParserConfigurationException e) {
            Assertions.error("could not create DocumentBuilder: %s", e);
            throw new IllegalStateException("could not create DocumentBuilder: " + e);
        }

        Document document;
        try {
            document = builder.parse(stream);
        } catch (SAXException e) {
            throw new InvalidDataException("TmxTileMapLoader parse error from XML decoder: " + e.toString());
        }

        document.getDocumentElement().normalize();

        return document;
    }

    // TODO tileoffset etc
    private Tilemap loadAllLayers(Element root, Tileset tileset, int tileWidth, int tileHeight, Vector3f relativeOrigin) {
        Tilemap map = new Tilemap(tileset);
        for (Element layerElement : DOMElementUtil.getDirectChildrenByName(root, "layer")) {
            loadLayer(layerElement, map, tileWidth, tileHeight);
        }
        for (Element layerElement : DOMElementUtil.getDirectChildrenByName(root, "objectgroup")) { // TODO: inline with other for-loop, TODO 2: layers have ids in .tmx
            loadObjectLayer(layerElement, map, relativeOrigin);
        }
        return map;
    }

    private void loadLayer(Element layerElement, Tilemap tilemap, int tileWidth, int tileHeight) {
        if (layerElement == null) throw new IllegalArgumentException("element must be a 'layer', but got null");
        if (!layerElement.getNodeName().equals("layer"))
            throw new IllegalArgumentException("element must be a 'layer', but got '" + layerElement.getNodeName() + "'");

        if (DOMElementUtil.getDirectChildByName(layerElement, "chunk") != null) throw new IllegalArgumentException("infinite Tiled maps are not supported");

        int mapWidth = DOMElementUtil.getIntAttribute(layerElement, "width", 1);
        int mapHeight = DOMElementUtil.getIntAttribute(layerElement, "height", 1);

        Element dataElement = DOMElementUtil.getDirectChildByName(layerElement, "data");

        if (!dataElement.getAttribute("encoding").equals("csv")) throw new IllegalArgumentException("Unsupported encoding: " + dataElement.getAttribute("encoding"));

        String[] indices = dataElement.getTextContent().replaceAll("\\r?\\n", "").split(",");
        int index = 0;

        TileLayer layer = tilemap.createTileLayer(new Properties(), layerElement.getAttribute("name"));
        while (index < indices.length) {
            int tileId = Integer.parseInt(indices[index]);
            if (tileId != 0) {
                Tile tile = tilemap.getTileset().getTile(tileId);

                int column = (index % mapWidth);
                int row = mapHeight - (index / mapWidth);

                int xPos = column * tileWidth;
                //int yPos = row * tileHeight + (tileHeight - tile.getHeight()); // .tmx assumes 0,0 is bottom left
                int yPos = row * tileHeight; // TODO boolean: flipY

                layer.addEntry(tile, xPos, yPos); // TODO: is width and height right here?
            }
            index++;
        }
    }

    private void loadObjectLayer(Element objectgroupElement, Tilemap map, Vector3f relativeOrigin) {

        Element propertiesElement = DOMElementUtil.getDirectChildByName(objectgroupElement, "properties");
        Properties objectLayerProperties = loadProperties(propertiesElement);

        String layerName = DOMElementUtil.getStringAttribute(objectgroupElement, "name", "");
        String typeName = DOMElementUtil.getStringAttribute(objectgroupElement, "class", "");
        ObjectLayer layer = map.createObjectLayer(objectLayerProperties, layerName, typeName);

        for (Element objectElement : DOMElementUtil.getDirectChildrenByName(objectgroupElement, "object")) {

            MapObject object = loadMapObject(objectElement, map, 0, relativeOrigin);
            layer.addMapObject(object);
        }
    }

    private MapObject loadMapObject(Element objectElement, Tilemap tilemap, int relativeGid, Vector3f relativeOrigin) {
        if (objectElement == null) return null;

        String name = DOMElementUtil.getStringAttribute(objectElement, "name", "");
        String type = DOMElementUtil.getStringAttribute(objectElement, "class", "");
        if (type.equals("")) type = DOMElementUtil.getStringAttribute(objectElement, "type", ""); // pre Tiled 1.9
        float x = DOMElementUtil.getFloatAttribute(objectElement, "x", 0f);
        float y = DOMElementUtil.getFloatAttribute(objectElement, "y", 0f);
        float width = DOMElementUtil.getFloatAttribute(objectElement, "width", 0f);
        float height = DOMElementUtil.getFloatAttribute(objectElement, "height", 0f);
        float rotation = DOMElementUtil.getFloatAttribute(objectElement, "rotation", 0f);
        // TODO: visible ?

        int localgid = DOMElementUtil.getIntAttribute(objectElement, "gid", -1);
        int referencedTileId = relativeGid + localgid;
        if (localgid < 0) referencedTileId = localgid;

        Element objPropertiesElement = DOMElementUtil.getDirectChildByName(objectElement, "properties");
        Properties properties = loadProperties(objPropertiesElement);

        // TODO: ellipse, point, polyline and text
        MapObjectContent content = null;

        Element polygonElement = DOMElementUtil.getDirectChildByName(objectElement, "polygon");
        if (polygonElement != null) {
            String p = DOMElementUtil.getStringAttribute(polygonElement, "points", "");
            String[] coords = p.split(" ");
            List<Vector2f> points = new ArrayList<>();
            for (String coord : coords) {
                String[] xy = coord.split(",");
                float px = Float.parseFloat(xy[0]), py = Float.parseFloat(xy[1]);
                points.add(new Vector2f(px + x - 8, relativeOrigin.y - (py + y - 8))); // TODO: better relative origin (tileDirection or something), TODO 2: -tileWidth/2, -tileHeight/2 because instead of -8 of centering
            }
            content = MapObjectPolygon.fromPoints(points);
        }


        // TODO: layer.createMapObject() ?
        return new MapObject(tilemap.getTileset(), properties, content, name, type, x, y, width, height, rotation, referencedTileId);
    }

    private Properties loadProperties(Element propertiesElement) {
        Properties properties = new Properties();

        if (propertiesElement == null) return properties;

        for (Element propertyElement : DOMElementUtil.getDirectChildrenByName(propertiesElement, "property")) {

            String name = DOMElementUtil.getStringAttribute(propertyElement, "name", "");
            String type = DOMElementUtil.getStringAttribute(propertyElement, "type", "string");
            // TODO: propertytype ?
            switch (type) {
                case "string":
                case "color":
                case "file":
                case "object":
                case "class":
                    properties.put(name, DOMElementUtil.getStringAttribute(propertyElement, "value", ""));
                    break;
                case "int":
                    properties.put(name, DOMElementUtil.getIntAttribute(propertyElement, "value", 0));
                    break;
                case "float":
                    properties.put(name, DOMElementUtil.getFloatAttribute(propertyElement, "value", 0f));
                    break;
                case "bool":
                    properties.put(name, DOMElementUtil.getStringAttribute(propertyElement, "value", "false")); // TODO: boolean
                    break;
                default:
                    Assertions.warn("unknown property type: %s", type);
            }

        }

        return properties;
    }

    private Tileset loadTileset(Element tilesetElement) throws IOException {
        if (tilesetElement == null) throw new IllegalArgumentException("element must be a 'tileset', but got null");
        if (!tilesetElement.getNodeName().equals("tileset"))
            throw new IllegalArgumentException("element must be a 'tileset', but got '" + tilesetElement.getNodeName() + "'");

        int firstgid = DOMElementUtil.getIntAttribute(tilesetElement, "firstgid", 1);
        int tileWidth = DOMElementUtil.getIntAttribute(tilesetElement, "tilewidth", 0);
        int tileHeight = DOMElementUtil.getIntAttribute(tilesetElement, "tileheight", 0);
        int spacing = DOMElementUtil.getIntAttribute(tilesetElement, "spacing", 0);
        int margin = DOMElementUtil.getIntAttribute(tilesetElement, "margin", 0);

        // TODO: <tileoffset>

        Tileset tileset = new Tileset(tilesetElement.getAttribute("name"));
        if (tilesetElement.hasAttribute("source")) {
            // TODO: test this
            // TODO: this needs improvement (+ offset)
            InputStream sourceStream = new FileInputStream(new File(TEMPPREFIXFILEPATH + tilesetElement.getAttribute("source"))); // TODO: filepath
            Tileset other = loadTileset(parseXML(sourceStream).getDocumentElement());
            boolean didClash = !tileset.merge(other, firstgid - 1, true);
            if (didClash) Assertions.warn("unexpected clash in GID's in .tmx file, from gid: %d", firstgid);
        } else {

            Tileset animatedTileset = new Tileset();

            Node node = tilesetElement.getFirstChild();
            while (node != null) {

                if (!(node instanceof Element)) { node = node.getNextSibling(); continue; }
                Element element = (Element)node;

                if (element.getNodeName().equals("image")) {

                    Texture texture = new GLTexture(TEMPPREFIXFILEPATH + element.getAttribute("source")); // TODO: filepath
                    Tileset textureTileset = splitToTileset(texture, tileWidth, tileHeight, spacing, margin);
                    boolean didClash = !tileset.merge(textureTileset, firstgid, true); // note: assuming only one image can exist per tileset
                    if (didClash) Assertions.warn("unexpected clash in GID's in .tmx file, from gid: %d", firstgid);

                } else if (element.getNodeName().equals("tile")) {

                    // TODO: tiles without images
                    Element imageElement = DOMElementUtil.getDirectChildByName(element, "image");
                    if (imageElement != null) {
                        Texture texture = new GLTexture(imageElement.getAttribute("source"));
                        Sprite sprite = new Sprite(texture);

                        int localgid = DOMElementUtil.getIntAttribute(imageElement, "id", 0);
                        StaticTile tile = new StaticTile(sprite);

                        boolean didClash = !tileset.registerTile(firstgid + localgid, tile, true);
                        if (didClash) Assertions.warn("unexpected clash in GID's in .tmx file, from gid: %d", firstgid + localgid);
                    }

                    int tileid = DOMElementUtil.getIntAttribute(element, "id", 0);
                    Element animationElement = DOMElementUtil.getDirectChildByName(element, "animation");
                    if (animationElement != null) {
                        SpriteAnimation animation = new SpriteAnimation();
                        for (Element frameElement : DOMElementUtil.getDirectChildrenByName(animationElement, "frame")) {
                            int localgid = DOMElementUtil.getIntAttribute(frameElement, "tileid", 0);
                            int durationMS = DOMElementUtil.getIntAttribute(frameElement, "duration", 100);

                            Tile referredTile = tileset.getTile(firstgid + localgid);
                            if (!(referredTile instanceof StaticTile)) throw new InvalidDataException("animated tile must refer to a StaticTile");
                            Sprite sprite = ((StaticTile)referredTile).getSprite();

                            animation.createFrame(sprite, durationMS / 1000f);
                        }

                        animatedTileset.registerTile(tileid, new AnimatedTile(animation), true);

                    }

                    Properties properties = new Properties();

                    TiledTileMetadata metadata = new TiledTileMetadata(tileset); // TODO: does tileset work?
                    properties.put(TILE_METADATA_PROPERTY_NAME, metadata);

                    Element objectgroupElement = DOMElementUtil.getDirectChildByName(element, "objectgroup");
                    if (objectgroupElement != null) {
                        for (Element objectElement : DOMElementUtil.getDirectChildrenByName(objectgroupElement, "object")) {

                            //MapObject object = loadMapObject(objectElement, map, firstgid); // TODO: use loadMapObject, but find "map" somehow
                            //metadata.getObjects().addMapObject(object);

                            String name = DOMElementUtil.getStringAttribute(objectElement, "name", "");
                            String type = DOMElementUtil.getStringAttribute(objectElement, "class", "");
                            if (type.equals("")) type = DOMElementUtil.getStringAttribute(objectElement, "type", ""); // pre Tiled 1.9
                            float x = DOMElementUtil.getFloatAttribute(objectElement, "x", 0f);
                            float y = DOMElementUtil.getFloatAttribute(objectElement, "y", 0f);
                            float width = DOMElementUtil.getFloatAttribute(objectElement, "width", 0f);
                            float height = DOMElementUtil.getFloatAttribute(objectElement, "height", 0f);
                            float rotation = DOMElementUtil.getFloatAttribute(objectElement, "rotation", 0f);
                            int localgid = DOMElementUtil.getIntAttribute(objectElement, "gid", -1);

                            int referencedTileId = firstgid + localgid;
                            if (localgid < 0) referencedTileId = localgid;

                            // TODO: visible ?

                            // TODO: tileset works, but ideally should be the finalized global tileset. instantiate metadata after tileset created?
                            MapObject object = new MapObject(tileset, new Properties(), null, name, type, x, y, width, height, rotation, referencedTileId);
                            metadata.getObjects().addMapObject(object);

                        }
                    }

                    Element propertiesElement = DOMElementUtil.getDirectChildByName(element, "properties");
                    Properties p = loadProperties(propertiesElement);
                    properties.putAll(p, true);

                    // TODO: dirty. clean up

                    Properties tileProperties = tileset.getTile(firstgid + tileid).getProperties();
                    for (Map.Entry<String, Object> property : properties.entrySet()) {
                        tileProperties.put(property.getKey(), property.getValue());
                    }

                }

                node = node.getNextSibling();

            }

            tileset.merge(animatedTileset, firstgid, true);

        }

        // ensure all have non-null metadata
        for (Tile tile : tileset.getAllTiles()) {
            Properties properties = tile.getProperties();
            if (!properties.containsKey(TILE_METADATA_PROPERTY_NAME)) properties.put(TILE_METADATA_PROPERTY_NAME, new TiledTileMetadata(tileset)); // TODO: does tileset work?
        }

        return tileset;
    }

    // TODO test
    // TODO move to another location (more general)
    /**
     * Split a texture into multiple sprites.
     * They will be registered starting at id=0, in ascending order, from left->right, top->down.
     * @param texture - The texture to split.
     * @param tileWidth - The width in pixels, per tile.
     * @param tileHeight - The height in pixels, per tile.
     * @param spacing - The number of pixels between tiles.
     * @param margin - The margin around the tiles.
     * @return a tileset containing all the sprites.
     */
    private Tileset splitToTileset(Texture texture, int tileWidth, int tileHeight, int spacing, int margin) {
        if (tileWidth == 0 || tileHeight == 0) throw new IllegalArgumentException("tileWidth = " + tileWidth + ", tileHeight = " + tileHeight + ". Both must not be 0!");

        float uWidth = (float)tileWidth / texture.getWidth();
        float vHeight = (float)tileHeight / texture.getHeight();

        Tileset tileset = new Tileset();
        int id = 0;

        int lastYFitTexture = texture.getHeight() - tileHeight - margin;
        int lastXFitTexture = texture.getWidth() - tileWidth - margin;

        for (float y = margin; y <= lastYFitTexture; y += tileHeight + spacing) {
            for (float x = margin; x <= lastXFitTexture; x += tileWidth + spacing) {
                float u1 = x / texture.getWidth();
                float v1 = y / texture.getHeight();
                Sprite sprite = new Sprite(texture, u1, v1, u1 + uWidth, v1 + vHeight);
                Tile tile = new StaticTile(sprite);
                tileset.registerTile(id++, tile, true);
            }
        }

        return tileset;
    }

}