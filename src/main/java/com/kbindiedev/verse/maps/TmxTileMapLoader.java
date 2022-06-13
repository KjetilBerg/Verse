package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.ecs.datastore.SpriteAnimation;
import com.kbindiedev.verse.ecs.datastore.SpriteFrame;
import com.kbindiedev.verse.ecs.datastore.builders.SpriteAnimationBuilder;
import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.gfx.Texture;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.InvalidDataException;
import com.kbindiedev.verse.system.parse.DOMElementUtil;
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

// TODO: mention unsupported parts

/** Loads .tmx files (Tiled maps). */
public class TmxTileMapLoader implements ITileMapLoaderImplementation {

    private static DocumentBuilderFactory dbf;

    // TODO temp.
    private static final String TEMPPREFIXFILEPATH = "./../spritepack_demo/";

    public TmxTileMapLoader() {
        dbf = DocumentBuilderFactory.newInstance();
    }

    @Override
    public LayeredTileMap loadTileMap(InputStream stream) throws IOException, InvalidDataException {

        Document document = parseXML(stream);

        System.out.println("Root element name: " + document.getDocumentElement().getNodeName());
        //document.getDocumentElement().getName

        Tileset mainSet = new Tileset();
        for (Element tilesetElement : DOMElementUtil.getChildrenByName(document.getDocumentElement(), "tileset")) {
            Tileset tileset = loadTileset(tilesetElement);
            boolean didClash = !mainSet.merge(tileset, 0, true);
            if (didClash) Assertions.warn("unexpected clash in main merge for .tmx file");
        }

        return loadAllLayers(document.getDocumentElement(), mainSet, 16, 16);
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
    private LayeredTileMap loadAllLayers(Element root, Tileset tileset, int tileWidth, int tileHeight) {
        LayeredTileMap map = new LayeredTileMap();
        int index = 0;
        for (Element layerElement : DOMElementUtil.getChildrenByName(root, "layer")) {
            TileMap tilemap = loadLayer(layerElement, tileset, tileWidth, tileHeight);
            map.putForLayer(index++, tilemap);
        }
        return map;
    }

    private TileMap loadLayer(Element layerElement, Tileset tileset, int tileWidth, int tileHeight) {
        if (layerElement == null) throw new IllegalArgumentException("element must be a 'layer', but got null");
        if (!layerElement.getNodeName().equals("layer"))
            throw new IllegalArgumentException("element must be a 'layer', but got '" + layerElement.getNodeName() + "'");

        if (DOMElementUtil.getChildByName(layerElement, "chunk") != null) throw new IllegalArgumentException("infinite Tiled maps are not supported");

        int mapWidth = DOMElementUtil.getIntAttribute(layerElement, "width", 1);

        Element dataElement = DOMElementUtil.getChildByName(layerElement, "data");

        if (!dataElement.getAttribute("encoding").equals("csv")) throw new IllegalArgumentException("Unsupported encoding: " + dataElement.getAttribute("encoding"));

        String[] indices = dataElement.getTextContent().replaceAll("\\r?\\n", "").split(",");
        int index = 0;

        TileMap map = new TileMap(layerElement.getAttribute("name"));
        while (index < indices.length) {
            int tileId = Integer.parseInt(indices[index]);
            if (tileId != 0) {
                Tile tile = tileset.getTile(tileId);

                int column = (index % mapWidth);
                int row = (index / mapWidth);

                int xPos = column * tileWidth;
                int yPos = row * tileHeight + (tileHeight - tile.getHeight()); // .tmx assumes 0,0 is bottom left

                map.addEntry(tile, xPos, yPos); // TODO: is width and height right here?
            }
            index++;
        }
        return map;
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
                    Element imageElement = DOMElementUtil.getChildByName(element, "image");
                    if (imageElement != null) {
                        Texture texture = new GLTexture(imageElement.getAttribute("source"));
                        Sprite sprite = new Sprite(texture);

                        int localgid = DOMElementUtil.getIntAttribute(imageElement, "id", 0);
                        StaticTile tile = new StaticTile(sprite);

                        boolean didClash = !tileset.registerTile(firstgid + localgid, tile, true);
                        if (didClash) Assertions.warn("unexpected clash in GID's in .tmx file, from gid: %d", firstgid + localgid);
                    }

                    int tileid = DOMElementUtil.getIntAttribute(element, "id", 0);
                    Element animationElement = DOMElementUtil.getChildByName(element, "animation");
                    if (animationElement != null) {
                        SpriteAnimationBuilder builder = new SpriteAnimationBuilder();
                        for (Element frameElement : DOMElementUtil.getChildrenByName(animationElement, "frame")) {
                            int localgid = DOMElementUtil.getIntAttribute(frameElement, "tileid", 0);
                            int durationMS = DOMElementUtil.getIntAttribute(frameElement, "duration", 100);

                            Tile referredTile = tileset.getTile(firstgid + localgid);
                            if (!(referredTile instanceof StaticTile)) throw new InvalidDataException("animated tile must refer to a StaticTile");
                            Sprite sprite = ((StaticTile)referredTile).getSprite();

                            SpriteFrame frame = new SpriteFrame(sprite, durationMS / 1000f);
                            builder.addFrame(frame);
                        }

                        SpriteAnimation animation = builder.build(true);
                        animatedTileset.registerTile(tileid, new AnimatedTile(animation), true);

                    }


                }

                node = node.getNextSibling();

            }

            tileset.merge(animatedTileset, firstgid, true);

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