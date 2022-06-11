package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.gfx.strategy.IBufferSlicable;
import com.kbindiedev.verse.gfx.strategy.IBufferable;
import com.kbindiedev.verse.gfx.strategy.IMemoryOccupant;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.NotEnoughMemoryException;
import com.kbindiedev.verse.util.Unit;
import com.sun.istack.internal.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

//TODO: rename class to VertexBufferObject
/** VertexBufferObject. Note: does not consider memory-management. @see ResourceManager for that. */
public class VBO implements IBufferable, IBufferSlicable<VBO.VBOSlice> {

    private int vboID;
    private long sizeInBytes;
    private int usage;                      //usage in terms of glBufferData
    private ArrayList<VBOSlice> allocations;
    private long currentAllocationByteIndex;    //next allocation will happen from this byte

    protected VBO(int id) { vboID = id; }   //TODO: remove
    public VBO(long sizeInBytes, int usage) {
        vboID = GEOpenGL33.gl33.glGenBuffers();
        this.sizeInBytes = sizeInBytes;
        this.usage = usage;
        allocations = new ArrayList<>();

        //TODO: verify usage (?)
        //TODO: GL_ARRAY_BUFFER? different choices are 'ok'
        StateTracker.pushVertexBufferObject(this);
        GEOpenGL33.gl33.glBufferData(GEOpenGL33.gl33.GL_ARRAY_BUFFER, sizeInBytes, usage);      //TODO: potentially, abstract popVertexBufferObject, etc...
        StateTracker.popVertexBufferObject();
        currentAllocationByteIndex = 0;
    }

    public int getUsage() { return usage; }

    //TODO: better limiting
    @Override
    public void buffer(long offset, ByteBuffer data, long numBytes) {
        if (!data.isDirect()) Assertions.warn("data is not a direct buffer. glBufferSubData does nothing.");
        long inputDataSizeBytes = data.position() + data.limit();
        if (inputDataSizeBytes > numBytes) {
            Assertions.warn("VBO buffer limiting input buffer. old size: %d, new size: %d. position: %d, old limit: %d, new limit: %d", inputDataSizeBytes, numBytes, data.position(), data.position() + (int)numBytes, data.limit());
            data.limit(data.position() + (int)numBytes);
        }

        StateTracker.pushVertexBufferObject(this);
        GEOpenGL33.gl33.glBufferSubData(GEOpenGL33.gl33.GL_ARRAY_BUFFER, offset, data);
        StateTracker.popVertexBufferObject();
    }

    @Override
    public long bufferByteSize() { return sizeInBytes; }

    @Override
    public VBOSlice slice(long numBytes, @Nullable IMemoryOccupant occupant) throws NotEnoughMemoryException {
        if (maxSliceSizeBytes() < numBytes) throw new NotEnoughMemoryException(String.format("VBO slice: slice too large, cannot fit in memory. wanted size: %d (%.2f KiB), max: %d (%.2f KiB)", numBytes, Unit.toKiB(numBytes), maxSliceSizeBytes(), Unit.toKiB(maxSliceSizeBytes())));
        System.out.printf("VBOSlice slice: numBytes: %d, relative offset: %d\n", numBytes, currentAllocationByteIndex);
        VBOSlice slice = new VBOSlice(this, numBytes, currentAllocationByteIndex, occupant);
        allocations.add(slice);
        currentAllocationByteIndex += numBytes;
        return slice;
    }

    @Override
    public long maxSliceSizeBytes() {
        return sizeInBytes - currentAllocationByteIndex;
    }

    public int getID() { return vboID; }

    public List<VBOSlice> getAllocations() { return allocations; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VBO)) return false;
        VBO v = (VBO)o;
        return v.getID() == getID();
    }



    protected class VBOSlice implements IBufferable, IBufferSlicable<VBOSlice> {

        private VBO root;                                       //My root VBO
        private @Nullable VBOSlice parent;                      //My parent VBOSlice
        private ArrayList<VBOSlice> children;                   //My children
        private long sizeInBytes;
        private long absoluteOffset, parentOffset;              //absolute = from byte 0 VBO, parentOffset = from parent (VBO or VBOSlice)
        private long currentAllocationByteIndex;
        private @Nullable IMemoryOccupant occupant;

        public VBOSlice(VBO root, long sizeInBytes, long offset, IMemoryOccupant occupant) { this(root, null, sizeInBytes, offset, offset, occupant); }
        private VBOSlice(VBO root, @Nullable VBOSlice parent, long sizeInBytes, long absoluteOffset, long parentOffset, IMemoryOccupant occupant) {
            if (parent == null && absoluteOffset != parentOffset) {
                Assertions.warn("parent null, yet parentOffset does not equal absoluteOffset. setting both to absoluteOffset... current parentOffset: %d, absoluteOffset: %d", parentOffset, absoluteOffset);
                parentOffset = absoluteOffset;
            }

            this.root = root; this.parent = parent; this.sizeInBytes = sizeInBytes; this.parentOffset = parentOffset;
            this.absoluteOffset = absoluteOffset; this.occupant = occupant; currentAllocationByteIndex = 0;
            children = new ArrayList<>();
        }

        @Override
        public void buffer(long offset, ByteBuffer data, long numBytes) {
            if (!data.isDirect()) Assertions.warn("data is not a direct buffer. glBufferSubData does nothing.");
            if (offset < 0) { Assertions.warn("offset cannot be negative. got '%d'. setting to 0.", offset); offset = 0; }
            if (offset + numBytes > sizeInBytes) {
                long newNumBytes = sizeInBytes - offset;
                Assertions.warn("offset+numBytes cannot exceed maximum memory limit. offset: '%d', numBytes: '%d', limit: '%d'. setting numBytes to: '%d'", offset, numBytes, sizeInBytes, newNumBytes);
                numBytes = newNumBytes;
            }
            if (numBytes < 0) Assertions.warn("numBytes cannot be negative. got '%d'. ignoring...", numBytes);
            if (numBytes <= 0) return;

            int oldLimit = data.limit();
            data.limit(data.position() + (int)numBytes);
            StateTracker.pushVertexBufferObject(root);
            GEOpenGL33.gl33.glBufferSubData(GEOpenGL33.gl33.GL_ARRAY_BUFFER, absoluteOffset + offset, data);
            StateTracker.popVertexBufferObject();
            data.limit(oldLimit);
        }

        @Override
        public VBOSlice slice(long numBytes, @Nullable IMemoryOccupant occupant) throws NotEnoughMemoryException {
            if (numBytes > maxSliceSizeBytes())
                throw new NotEnoughMemoryException(String.format("VBOSlice slice: slice too large, cannot fit in memory. wanted size: %d (%.2f KiB), max: %d (%.2f KiB)", numBytes, Unit.toKiB(numBytes), maxSliceSizeBytes(), Unit.toKiB(maxSliceSizeBytes())));

            System.out.println("VBOSlice SLICE");
            VBOSlice slice = new VBOSlice(root, this, numBytes, absoluteOffset + currentAllocationByteIndex, currentAllocationByteIndex, occupant);
            children.add(slice);
            currentAllocationByteIndex += numBytes;
            return slice;
        }

        @Override
        public long maxSliceSizeBytes() {
            return sizeInBytes - currentAllocationByteIndex;
        }

        @Override
        public long bufferByteSize() { return sizeInBytes; }

        public int getUsage() { return parent.getUsage(); }

        public long getParentOffset() { return parentOffset; }

        public long getAbsoluteOffset() { return absoluteOffset; }

        protected VBOSlice getParent() { return parent; }    //TODO: remove? replace with pushBind and popBind?

        public IMemoryOccupant getOccupant() { return occupant; }

        public List<VBOSlice> getChildren() { return children; }

        protected VBO getRoot() { return root; }

    }

}
