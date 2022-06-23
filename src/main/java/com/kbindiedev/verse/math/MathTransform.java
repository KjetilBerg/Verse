package com.kbindiedev.verse.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A structure containing position, scale and rotation.
 * Different from {@link com.kbindiedev.verse.ecs.components.Transform} in that there is no parenting,
 *      and this class implements some simple functionality.
 *
 * @see com.kbindiedev.verse.ecs.components.Transform
 */
public class MathTransform {

    private Vector3f position;
    private Vector3f scale;
    private Quaternionf rotation;

    public MathTransform() { this(new Vector3f()); }
    public MathTransform(Vector3f position) { this(position, new Vector3f(1f, 1f, 1f), new Quaternionf()); }
    public MathTransform(Vector3f position, Vector3f scale, Quaternionf rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Vector3f getPosition() { return position; }
    public void setPosition(Vector3f position) { this.position = new Vector3f(position); }

    public Vector3f getScale() { return scale; }
    public void setScale(Vector3f scale) { this.scale = new Vector3f(scale); }

    public Quaternionf getRotation() { return rotation; }
    public void setRotation(Quaternionf rotation) { this.rotation = new Quaternionf(rotation); }

    public void translate(Vector3f by) { position.add(by); }

    /** @return the distance from this vector to the given position. */
    public Vector3f distanceTo(Vector3f position) { return new Vector3f(position).sub(this.position); }

    /** @return the vector of total distance moved to get to the given destination. */
    public Vector3f translateTo(Vector3f position) {
        Vector3f distance = distanceTo(position);
        this.position.set(position);
        return distance;
    }

    /** @return the vector representing how much this MathTransform scaled to reach the given scale. */
    public Vector3f scaleTo(Vector3f scale) {
        Vector3f magnitude = new Vector3f(scale).div(this.scale);
        this.scale.set(scale);
        return magnitude;
    }

    /** @return the quaternion of what rotation had to be applied to this MathTransform's current rotation to achieve the given rotation. */
    public Quaternionf rotateTo(Quaternionf rotation) {
        Quaternionf difference = new Quaternionf(this.rotation).difference(rotation);
        this.rotation.set(rotation);
        return difference;
    }

}