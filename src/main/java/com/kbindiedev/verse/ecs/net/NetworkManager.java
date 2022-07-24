package com.kbindiedev.verse.ecs.net;

import com.kbindiedev.verse.Main;
import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.Space;
import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.io.comms.ICommunicationChannel;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.system.BiHashMap;
import com.kbindiedev.verse.system.ISerializable;
import com.kbindiedev.verse.util.CompressedLong;
import com.kbindiedev.verse.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Connects an ECS environment to some networking functionality */
public class NetworkManager {

    private Space space;

    private ICommunicationChannel channel;

    private BiHashMap<Long, Entity> entityMap;
    private HashSet<Long> externalEntities;

    private ConcurrentLinkedQueue<Runnable> syncTasks;

    // TODO: removed socket temporarily
    public NetworkManager(Space space, ICommunicationChannel channel) {
        this.space = space;
        this.channel = channel;

        entityMap = new BiHashMap<>();
        externalEntities = new HashSet<>();

        syncTasks = new ConcurrentLinkedQueue<>();
    }

    // update by main thread, before render
    public void update() {
        Runnable task = syncTasks.poll();
        while (task != null) {
            task.run();
            task = syncTasks.poll();
        }
    }

    private long getNewId() {
        return (long)(Math.random() * 0xFFFFFFFFL);
    }

    public boolean doIOwnEntity(Entity entity) {
        if (!entityMap.containsValue(entity)) return true;
        long id = entityMap.getForValue(entity);
        return !externalEntities.contains(id);
    }

    // make an entity appear to others (spawn or update)
    public long synchronizeEntity(Entity entity) throws IOException {
        OutputStream stream = channel.getWritingStream();

        stream.write(1); // identifier = change data of network entity (or spawn if not exist)

        long id;
        if (entityMap.containsValue(entity)) id = entityMap.getForValue(entity);
        else id = getNewId();

        CompressedLong.serialize(id, stream); // id of entity

        CompressedLong.serialize(entity.getAllComponents().size(), stream); // number of components // TODO: consider if component cannot be serialized

        for (IComponent component : entity.getAllComponents()) {
            if (!(component instanceof ISerializable)) {
                Assertions.warn("component not serializable: " + component.getClass().getCanonicalName());
                return -1; // TODO: intermediary stream ?
            }

            StreamUtil.writeString(component.getClass().getCanonicalName(), stream);
            ((ISerializable)component).serialize(stream);
        }

        entityMap.put(id, entity);
        return id;
    }

    public void destroyNetworkEntity(Entity entity) throws IOException {
        OutputStream stream = channel.getWritingStream();

        stream.write(2); // identifier = destroy network entity

        long id = entityMap.getForValue(entity);
        CompressedLong.serialize(id, stream); // id of entity
    }

    /** Process the ICommunicationChannel. */
    public void processStream() throws IOException { // TODO TEMP: public
        InputStream stream = channel.getReadingStream();
        int identifier = stream.read();
        switch (identifier) {
            case 1: onSyncNetworkEntity(stream); break;
            case 2: onDestroyNetworkEntity(stream); break;
            default: Assertions.warn("unknown identifier: %d", identifier);
        }
    }

    public void onSyncNetworkEntity(InputStream stream) throws IOException {

        long entityId = CompressedLong.deserialize(stream);
        long componentCount = CompressedLong.deserialize(stream);

        IComponent[] components = new IComponent[(int)componentCount];

        for (int i = 0; i < componentCount; ++i) {
            String className = StreamUtil.readString(stream);
            try {
                Object o = Class.forName(className).newInstance();
                ((ISerializable)o).deserialize(stream);
                components[i] = (IComponent)o;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                Assertions.warn("could not instantiate class: %s", className);
                e.printStackTrace();
            } catch (ClassCastException e) {
                Assertions.warn("could not cast class: '%s' into ISerializable or IComponent", className);
                e.printStackTrace();
            }
        }

        // TODO TEMP
        for (IComponent component : components) {
            if (component instanceof SpriteRenderer) {
                ((SpriteRenderer)component).sprite = Main.playerSprite;
            }
        }

        Runnable sync = () -> {
            if (!entityMap.containsKey(entityId)) {
                Entity entity = space.getEntityManager().instantiate(components);
                entityMap.put(entityId, entity);
                externalEntities.add(entityId);
            } else {
                Entity entity = entityMap.get(entityId);
                //TODO TEMP: really heavy
                entity.removeComponents(entity.getAllComponents());
                entity.putComponents(Arrays.asList(components));
            }

            //System.out.println("Synchronized entity by network request. id: " + entityId);
        };

        syncTasks.add(sync);

    }

    public void onDestroyNetworkEntity(InputStream stream) throws IOException {
        long id = CompressedLong.deserialize(stream);

        Runnable sync = () -> {
            Entity entity = entityMap.remove(id);
            space.getEntityManager().destroy(entity);
            //System.out.println("Destroyed entity by network request: " + id);
        };

        syncTasks.add(sync);

    }

}
