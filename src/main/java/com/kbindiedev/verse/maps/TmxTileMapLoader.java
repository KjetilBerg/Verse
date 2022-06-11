package com.kbindiedev.verse.maps;

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
            mainSet.merge(tileset, 0);
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
            int spriteId = Integer.parseInt(indices[index]);
            if (spriteId != 0) {
                Sprite sprite = tileset.getSprite(spriteId);

                int column = (index % mapWidth);
                int row = (index / mapWidth);

                int xPos = column * tileWidth;
                int yPos = row * tileHeight + (tileHeight - sprite.getHeight()); // .tmx assumes 0,0 is bottom left

                map.addEntry(sprite, xPos, yPos); // TODO: is width and height right here?
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
        // TODO: <animation>

        Tileset tileset = new Tileset(tilesetElement.getAttribute("name"));
        if (tilesetElement.hasAttribute("source")) {
            // TODO: test this
            // TODO: this needs improvement (+ offset)
            InputStream sourceStream = new FileInputStream(new File(TEMPPREFIXFILEPATH + tilesetElement.getAttribute("source"))); // TODO: filepath
            Tileset other = loadTileset(parseXML(sourceStream).getDocumentElement());
            tileset.merge(other, firstgid - 1);
        } else {
            Node node = tilesetElement.getFirstChild();
            while (node != null) {

                if (!(node instanceof Element)) { node = node.getNextSibling(); continue; }
                Element element = (Element)node;

                if (element.getNodeName().equals("image")) {

                    Texture texture = new GLTexture(TEMPPREFIXFILEPATH + element.getAttribute("source")); // TODO: filepath
                    Tileset textureTileset = splitToTileset(texture, tileWidth, tileHeight, spacing, margin);
                    tileset.merge(textureTileset, firstgid); // note: assuming only one image can exist per tileset

                } else if (element.getNodeName().equals("tile")) {

                    // TODO: tiles without images
                    Element imageElement = DOMElementUtil.getChildByName(element, "image");
                    if (imageElement != null) {
                        Texture texture = new GLTexture(imageElement.getAttribute("source"));
                        Sprite sprite = new Sprite(texture);

                        int localgid = DOMElementUtil.getIntAttribute(imageElement, "id", 0);
                        tileset.registerSprite(firstgid + localgid, sprite);
                    }


                }

                node = node.getNextSibling();

            }

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
                tileset.registerSprite(id++, sprite);
            }
        }

        return tileset;
    }

}