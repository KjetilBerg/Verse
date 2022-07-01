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
import java.util.function.Function;

/**
 * Creates an Entity structure (hierarchy) by a given LayeredTileMap.
 *
 * @see com.kbindiedev.verse.maps.LayeredTileMap
 */
public class TilemapToEntitiesGenerator {


    public static final Function<Integer, Float> depthFunction = (y -> -y * 0.0005f); //TODO TEMP

    /**
     * Generate a hierarchical set of entities that are applied to the destination, from the LayeredTileMap.
     * The top-level entity (returned value) will be empty, and only contain the generated children.
     * @param destination - The destination of the generated entities.
     * @param tilemap - The Tilemap to generate entities from.
     * @return the parent entity of the hierarchy.
     */
    public static Entity generateEntities(EntityManager destination, Tilemap tilemap) {

        //TODO SOON: parent system does not exist yet.
        Entity parent = null;

        List<TileLayer> tileLayers = tilemap.getLayersOfType(TileLayer.class);
        float zLayer = -0.1f; // TODO: different numbers (remember camera near/far planes)

        for (TileLayer layer : tileLayers) {

            zLayer += 0.1f;
            System.out.println(layer.getName() + " = z: " + zLayer);
            for (TileLayer.Entry entry : layer.getAllEntries()) {

                Tile t = entry.getTile();

                List<IComponent> list = new ArrayList<>();

                Transform transform = new Transform();
                // TODO: transforms. better? widht/height
                transform.position.x = entry.getX();
                transform.position.y = entry.getY();
                int relativeFooting = entry.getTile().getProperties().getAsOrDefault("relativeFooting", Integer.class, 0);
                transform.position.z = zLayer + depthFunction.apply(entry.getY() - 8 + relativeFooting);
                transform.scale.x = entry.getWidth();
                transform.scale.y = entry.getHeight();
                list.add(transform);

                TiledTileMetadata metadata = t.getProperties().getAs(TmxTileMapLoader.TILE_METADATA_PROPERTY_NAME, TiledTileMetadata.class);
                if (metadata != null) {
                    for (MapObject object : metadata.getObjects().getByType("collider")) {
                        PolygonCollider2D collider2D = new PolygonCollider2D();

                        float x1 = object.getX() - 8;
                        float x2 = x1 + object.getWidth();

                        float y1 = 16 - (object.getY() + object.getHeight()) - 8;
                        float y2 = y1 + object.getHeight();

                        Polygon polygon = new Polygon();
                        polygon.addPoint(new Vector3f(x1, y1, 0f).div(16));
                        polygon.addPoint(new Vector3f(x2, y1, 0f).div(16));
                        polygon.addPoint(new Vector3f(x2, y2, 0f).div(16));
                        polygon.addPoint(new Vector3f(x1, y2, 0f).div(16));
                        collider2D.polygon = polygon;
                        list.add(collider2D);
                    }
                }

                if (t instanceof StaticTile) {

                    StaticTile tile = (StaticTile)t;
                    SpriteRenderer spriteRenderer = new SpriteRenderer();
                    spriteRenderer.sprite = tile.getSprite();
                    list.add(spriteRenderer);

                    // TODO: TEMP
                    /*
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
                    }*/

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

        ObjectLayer soundmap = tilemap.getLayerByName("soundmap", ObjectLayer.class);
        if (soundmap != null) {
            for (MapObject o : soundmap.getMapObjects().getAllObjects()) {
                System.out.println("Generating soundmap: " + o.getName());

                PolygonCollider2D collider = new PolygonCollider2D();
                Polygon polygon = new Polygon();

                // TODO: objects don't actually have shapes yet.

                collider.polygon = polygon;
                destination.instantiate(collider);
            }
        }


        // parent = null up top to avoid NotImplementedException
        parent = destination.instantiate();
        return parent;
    }

}