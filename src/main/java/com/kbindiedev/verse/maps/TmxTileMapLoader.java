package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.InvalidDataException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/** Loads .tmx files (Tiled maps). */
public class TmxTileMapLoader implements ITileMapLoaderImplementation {

    private static DocumentBuilderFactory dbf;

    public TmxTileMapLoader() {
        dbf = DocumentBuilderFactory.newInstance();
    }

    @Override
    public LayeredTileMap loadTileMap(InputStream stream) throws IOException, InvalidDataException {

        DocumentBuilder builder;
        try {
            synchronized(dbf) { builder = dbf.newDocumentBuilder(); }
        } catch (ParserConfigurationException e) {
            Assertions.error("could not create DocumentBuilder: %s", e);
            return null;
        }

        Document document;
        try {
            document = builder.parse(stream);
        } catch (SAXException e) {
            throw new InvalidDataException("TmxTileMapLoader parse error from XML decoder: " + e.toString());
        }

        document.getDocumentElement().normalize();

        System.out.println("Root element name: " + document.getDocumentElement().getNodeName());

        return null;
    }

}