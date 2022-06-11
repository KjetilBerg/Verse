package com.kbindiedev.verse.system.parse;

import com.kbindiedev.verse.profiling.Assertions;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.Collection;

/** Provides some utility methods to a {@link Element}. */
public class DOMElementUtil {

    public static int getIntAttribute(Element element, String attributeName, int def) {
        if (!element.hasAttribute(attributeName)) return def;

        String attrib = element.getAttribute(attributeName);
        try { return Integer.parseInt(attrib); } catch (NumberFormatException e) {
            Assertions.warn("element attributeName: '%s' is not a number. Got: %s", attributeName, attrib);
            return def;
        }
    }

    public static String getStringAttribute(Element element, String attributeName, String def) {
        if (!element.hasAttribute(attributeName)) return def;
        return element.getAttribute(attributeName);
    }

    /** @return the element's first child that matches the given tagName, or null if none found. */
    public static Element getChildByName(Element element, String tagName) {
        Node child = element.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName)) return (Element)child;
            child = child.getNextSibling();
        }
        return null;
    }

    public static Collection<Element> getChildrenByName(Element element, String tagName) {
        Collection<Element> list = new ArrayList<>();
        NodeList nList = element.getElementsByTagName(tagName);
        for (int i = 0; i < nList.getLength(); ++i) list.add((Element)nList.item(i));
        return list;
    }

}