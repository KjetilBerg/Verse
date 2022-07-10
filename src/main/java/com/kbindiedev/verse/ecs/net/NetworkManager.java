package com.kbindiedev.verse.ecs.net;

import com.kbindiedev.verse.AssetPool;
import com.kbindiedev.verse.Main;
import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.Space;
import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.io.net.socket.ResponseSocket;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.UnsupportedFormatException;
import com.kbindiedev.verse.system.BiHashMap;
import com.kbindiedev.verse.system.ISerializable;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Connects an ECS environment to some networking functionality */
public class NetworkManager {

    private Space space;
    private ResponseSocket socket;

    BiHashMap<Long, Entity> entityMap;

    public NetworkManager(Space space, ResponseSocket socket) {
        this.space = space;
        this.socket = socket;

        entityMap = new BiHashMap<>();
    }

    // make an entity appear to others
    public long makeNetworkEntity(Entity entity) throws IOException {
        ByteOutputStream stream = new ByteOutputStream();

        stream.write(1); // identifier = spawn network entity

        long entityId = (long)(Math.random() * Long.MAX_VALUE);
        byte[] idBytes = ByteBuffer.allocate(8).putLong(entityId).array();
        stream.write(idBytes);

        for (IComponent component : entity.getAllComponents()) {
            if (!(component instanceof ISerializable)) {
                Assertions.warn("component not serializable: " + component.getClass().getCanonicalName());
                return -1;
            }

            stream.write(component.getClass().getCanonicalName().getBytes(StandardCharsets.UTF_8));
            stream.write(0);
            ((ISerializable)component).serialize(stream);
        }

        byte[] data = stream.getBytes();
        long netId = socket.send(data);
        stream.close();
        byte[] response = socket.retrieveBlocking(netId); // "ok"

//        long entityId = ByteBuffer.wrap(response).getLong();
        entityMap.put(entityId, entity);
        return entityId;
    }

    public void destroyNetworkEntity(Entity entity) throws IOException {
        long entityId = entityMap.getForValue(entity);

        ByteOutputStream stream = new ByteOutputStream();

        stream.write(2); // identifier = destroy network entity
        byte[] entityIdData = ByteBuffer.allocate(8).putLong(entityId).array();
        stream.write(entityIdData);

        byte[] toSend = stream.getBytes();
        long netId = socket.send(toSend);
        stream.close();
        socket.retrieveBlocking(netId); // "ok"
    }

    public void updateNetworkEntity(Entity entity) throws IOException {
        long entityId = entityMap.getForValue(entity);
        if (!entityMap.containsValue(entity)) {
            Assertions.warn("entity is not network managed");
            return;
        }

        ByteOutputStream stream = new ByteOutputStream();

        stream.write(3); // identifier = update network entity
        byte[] entityIdData = ByteBuffer.allocate(8).putLong(entityId).array();
        stream.write(entityIdData);

        // assume all are in order
        for (IComponent component : entity.getAllComponents()) {
            ((ISerializable)component).serialize(stream);
        }

        byte[] toSend = stream.getBytes();
        long netId = socket.send(toSend);
        stream.close();
        socket.retrieveBlocking(netId); // "ok"
    }

    public void onMakeNetworkEntity(byte[] data) {
        List<IComponent> components = new ArrayList<>();

        ByteInputStream stream = new ByteInputStream(data, data.length);

        byte[] l = new byte[8];
        try { stream.read(l); } catch (IOException e) { e.printStackTrace(); }
        long entityId = ByteBuffer.wrap(l).getLong();

        while (true) {
            String clazz = readNullTerminatedString(stream);
            if (clazz.isEmpty()) break; // null-terminated or EOF string.
            try {
                Object o = Class.forName(clazz).newInstance();
                if (!(o instanceof IComponent) || !(o instanceof ISerializable)) {
                    Assertions.warn("class not IComponent and ISerializable: %s", o.getClass().getCanonicalName());
                    return;
                }
                IComponent component = (IComponent)o;
                ((ISerializable)o).deserialize(stream);

                // TODO TEMP
                if (component instanceof SpriteRenderer) {
                    ((SpriteRenderer)component).sprite = Main.playerSprite;
                }
                components.add(component);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        }

        Entity entity = space.getEntityManager().instantiate(components.toArray(new IComponent[0]));
        entityMap.put(entityId, entity);
        try { stream.close(); } catch (IOException e) { e.printStackTrace(); }
        System.out.println("Spawned entity by network request. id: " + entityId);
    }

    public void onDestroyNetworkEntity(byte[] data) {
        ByteInputStream stream = new ByteInputStream(data, data.length);
        byte[] l = new byte[8];
        try { stream.read(l); } catch (IOException e) { e.printStackTrace(); }
        long entityId = ByteBuffer.wrap(l).getLong();
        try { stream.close(); } catch (IOException e) { e.printStackTrace(); }
        Entity entity = entityMap.remove(entityId);
        space.getEntityManager().destroy(entity);
        System.out.println("Destroyed entity by network request");
    }

    public void onUpdateNetworkEntity(byte[] data) {
        ByteInputStream stream = new ByteInputStream(data, data.length);
        byte[] l = new byte[8];
        try { stream.read(l); } catch (IOException e) { e.printStackTrace(); }
        long entityId = ByteBuffer.wrap(l).getLong();

        Entity entity = entityMap.get(entityId);
        if (entity == null) {
            Assertions.warn("entity does not exist for id: %d", entityId);
            return;
        }

        // temp, assume order is maintained
        for (IComponent component : entity.getAllComponents()) {
            ((ISerializable)component).deserialize(stream);
        }
    }

    public void poll() {
        while (socket.hasUnmarkedPackage()) {
            byte[] data = socket.getUnmarkedPackage();
            byte identifier = data[0];
            byte[] newData = new byte[data.length - 1];
            System.arraycopy(data, 1, newData, 0, newData.length);

            switch (identifier) {
                case 1: // spawn entity
                    onMakeNetworkEntity(newData);
                    break;
                case 2: // remove entity
                    onDestroyNetworkEntity(newData);
                    break;
                case 3: // update entity
                    onUpdateNetworkEntity(newData);
                    break;
                default:
                    Assertions.warn("unknown identifier: %d", identifier);
                    break;
            }
        }

        if (counter++ % 120 == 0) {
            try {
                for (Entity entity : entityMap.values()) updateNetworkEntity(entity);
            } catch (IOException e) { e.printStackTrace(); }
        }

    }

    private int counter = 0;

    private String readNullTerminatedString(ByteInputStream stream) {
        List<Byte> bytes = new ArrayList<>();
        int d;
        while ((d = stream.read()) != -1 && d != 0) bytes.add((byte)d);
        byte[] b = new byte[bytes.size()];
        for (int i = 0; i < b.length; ++i) b[i] = bytes.get(i);
        return new String(b, StandardCharsets.UTF_8);
    }

}
