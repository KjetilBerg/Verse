package com.kbindiedev.verse.ecs.generators;

import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.EntityManager;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.maps.LayeredTileMap;
import com.kbindiedev.verse.maps.TileMap;

import java.util.Iterator;

/**
 * Creates an Entity structure (hierarchy) by a given LayeredTileMap.
 *
 * @see com.kbindiedev.verse.maps.LayeredTileMap
 */
public class LayeredTileMapToEntitiesGenerator {

    /**
     * Generate a hierarchical set of entities that are applied to the destination, from the LayeredTileMap.
     * The top-level entity (returned value) will be empty, and only contain the generated children.
     * @param destination - The destination of the generated entities.
     * @param layeredTileMap - The LayeredTileMap to generate entities from.
     * @return the parent entity of the hierarchy.
     */
    public static Entity generateEntities(EntityManager destination, LayeredTileMap layeredTileMap) {

        //TODO SOON: parent system does not exist yet.
        Entity parent = null;

        Iterator<TileMap> iterator = layeredTileMap.iterator();

        while (iterator.hasNext()) {
            TileMap tilemap = iterator.next();

            for (TileMap.Entry entry : tilemap.getAllEntries()) {
                SpriteRenderer sprite = new SpriteRenderer();
                Transform transform = new Transform();
                sprite.sprite = entry.getSprite();
                // TODO: transforms. better? widht/height
                transform.position.x = entry.getX();
                transform.position.y = entry.getY();
                transform.scale.x = entry.getWidth();
                transform.scale.y = entry.getHeight();

                destination.instantiate(parent, sprite, transform);
            }

        }

        // parent = null up top to avoid NotImplementedException
        parent = destination.instantiate();
        return parent;
    }

}
