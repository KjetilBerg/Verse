package com.kbindiedev.verse.system.memory;

/** Something that can be "recycled" to be used again as brand new. */
public interface IRecyclable {

    /**
     * Reset this object to its "new" state (as if its constructor was just called).
     * There must be no difference between utilizing this object after it has been "recycled", and utilizing a brand new instance of this object.
     */
    void recycle();

}