package com.kbindiedev.verse.ecs.generators;

import com.kbindiedev.verse.animation.AnimationController;
import com.kbindiedev.verse.animation.AnimatorContext;
import com.kbindiedev.verse.animation.SpriteAnimation;
import com.kbindiedev.verse.animation.SpriteAnimationMap;
import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.EntityManager;
import com.kbindiedev.verse.ecs.components.*;
import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.maps.*;
import com.kbindiedev.verse.math.shape.Polygon;
import com.kbindiedev.verse.profiling.Assertions;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        float zLayer = -0.1f; // TODO: different numbers (remember camera near/far planes)

        while (iterator.hasNext()) {
            TileMap tilemap = iterator.next();

            zLayer += 0.1f;

            for (TileMap.Entry entry : tilemap.getAllEntries()) {

                Tile t = entry.getTile();

                List<IComponent> list = new ArrayList<>();

                Transform transform = new Transform();
                // TODO: transforms. better? widht/height
                transform.position.x = entry.getX();
                transform.position.y = entry.getY();
                transform.position.z = zLayer;
                transform.scale.x = entry.getWidth();
                transform.scale.y = entry.getHeight();
                list.add(transform);

                if (t instanceof StaticTile) {

                    StaticTile tile = (StaticTile)t;
                    SpriteRenderer spriteRenderer = new SpriteRenderer();
                    spriteRenderer.sprite = tile.getSprite();
                    list.add(spriteRenderer);

                    // TODO: TEMP
                    if (tile == layeredTileMap.getTileset().getTile(16)) {
                        System.out.println("GENERATING POLYGON COLLIDER");
                        PolygonCollider2D collider2D = new PolygonCollider2D();
                        Polygon polygon = new Polygon();
                        polygon.addPoint(new Vector3f(-8f, -8f, 0f));
                        polygon.addPoint(new Vector3f(8f, -8f, 0f));
                        polygon.addPoint(new Vector3f(8f, 8f, 0f));
                        polygon.addPoint(new Vector3f(-8f, 8f, 0f));
                        collider2D.polygon = polygon;
                        list.add(collider2D);
                    }

                } else if (t instanceof AnimatedTile) {

                    AnimatedTile tile = (AnimatedTile)t;
                    SpriteAnimator spriteAnimator = new SpriteAnimator();
                    SpriteRenderer spriteRenderer = new SpriteRenderer();

                    // TODO TEMP: make dummy AnimationMap
                    SpriteAnimationMap map = new SpriteAnimationMap();
                    map.setEntryState(tile.getAnimation());

                    AnimationController<SpriteAnimation> controller = new AnimationController<>(map, new AnimatorContext());
                    spriteAnimator.controller = controller;

                    // TODO: this is temp
                    Sprite ex = controller.pickAnimation().getFrames().get(0).getSprite();
                    spriteRenderer.offset = new Vector2f(ex.getWidth() / 4f, ex.getHeight() / 4f);

                    list.add(spriteAnimator);
                    list.add(spriteRenderer);

                } else {
                    Assertions.warn("unknown tile type: %s. tile: %s", t.getClass().getCanonicalName(), t);
                }

                destination.instantiate(parent, list.toArray(new IComponent[0]));
            }

        }

        // parent = null up top to avoid NotImplementedException
        parent = destination.instantiate();
        return parent;
    }

}
