package com.kbindiedev.verse.gfx.strategy.attributes;

import java.util.ArrayList;
import java.util.List;

//Future note: changes may come if new graphics implementations are added.
//TODO: usage into "static or nonstatic" ?
/** stride = size in bytes consumed per vertex, usage (see glBuffer*Data) ex. GL_DYNAMIC_DRAW or GL_STATIC_DRAW, localIndex = VertexAttributes provide multiple, used to differentiate */
class AttributeFragment {

    private int stride, localIndex, usage;
    private List<VertexAttribute> attributes = new ArrayList<>();

    public int bytesPerVertex() { return stride; }
    public int getUsage() { return usage; }
    public int getLocalIndex() { return localIndex; }
    public List<VertexAttribute> getAttributes() { return attributes; } //TODO: consider making UnmodifiableArrayList. Only downside is overhead; supposed to be immutable.
    //TODO: or just remove "public"
}

