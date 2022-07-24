package com.kbindiedev.verse.ecs.components;

// TODO FAR FUTURE: represent any dimension
// TODO FUTURE: replace Quaternion with Rotor

import com.kbindiedev.verse.system.ISerializable;
import com.kbindiedev.verse.util.StreamUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Represents a 3D position, scale and rotation. */
public class Transform implements IComponent, ISerializable {

    public Vector3f position    = new Vector3f();
    public Vector3f scale       = new Vector3f(1f, 1f, 1f);
    public Quaternionf rotation = new Quaternionf();

    // TODO: for now, always serialize full
    @Override
    public void serialize(OutputStream stream) throws IOException {
        StreamUtil.writeVector3f(position, stream);
        StreamUtil.writeVector3f(scale, stream);
        StreamUtil.writeQuaternionf(rotation, stream);
    }

    @Override
    public void deserialize(InputStream stream) throws IOException {
        position = StreamUtil.readVector3f(stream);
        scale = StreamUtil.readVector3f(stream);
        rotation = StreamUtil.readQuaternionf(stream);
    }

}