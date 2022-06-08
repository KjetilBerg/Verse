package com.kbindiedev.verse.ecs.util;

// TODO FAR FUTURE: represent any number of dimensions.
// TODO: move to Math package.
// TODO: euleran angles: Gimbal lock is a problem.
// TODO FUTURE: move away from Quaternions and use Rotors (postponed)

/**
 * A structure representing 3D rotation.
 * Most Game Engines utilize "Quaternions".
 * Rotors are very similar to quaternions, but are much simpler to understand.
 */
public class Rotor {

    private float x, y, z;

    public Rotor() { this(0, 0, 0); }
    /** x=roll, y=pitch, z=yaw */
    public Rotor(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setZ(float z) { this.z = z; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }

}