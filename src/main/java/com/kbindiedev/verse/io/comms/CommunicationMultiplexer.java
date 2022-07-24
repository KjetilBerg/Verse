package com.kbindiedev.verse.io.comms;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.system.buffer.ThroughputStream;
import com.kbindiedev.verse.util.CompressedLong;
import com.kbindiedev.verse.util.CompressedLongAccumulator;
import com.kbindiedev.verse.util.StreamUtil;

import java.io.*;
import java.util.HashMap;

/**
 * A class that communicates multiple other communication channels over a single channel.
 *
 * This utilizes a particular standard of communication:
 *      All writings are compressed into frames.
 *      The first 2 items in the message are {@link CompressedLong}.
 *          The first item represents the channel number. Max number is unlimited.
 *          The second item represents the frame size. Max size is unlimited.
 */
public class CommunicationMultiplexer extends OutputStream {

    private final ICommunicationChannel communicator;
    private final HashMap<Integer, MultiplexedChannel> channels;
    private boolean closed;

    private ProcessingStage stage;
    private int currentChannel;
    private long remainingBytes;
    private CompressedLongAccumulator clAcc;
    private OutputStream currentWriting; // because .getChannel() for every byte is expensive.

    public CommunicationMultiplexer(ICommunicationChannel communicator) {
        this.communicator = communicator;
        channels = new HashMap<>();
        closed = false;

        stage = ProcessingStage.POLLING_CHANNEL;
        currentChannel = -1;
        remainingBytes = 0;
        clAcc = new CompressedLongAccumulator();

        // TODO: replace ICommunicationChannel with some "outputstream x outputstream" or some better system.
        //      hierarchical querying? (channel calls .available() or .read() -> triggers multiplexer read() if available())
        //      however contract of .available() is not consistent. would need to guarantee that available() is accurate (not InputStream, but some intermediary layer).
        // TODO TEMP:
        try { StreamUtil.passForever(communicator.getReadingStream(), this, false); } catch (IOException e) {
            Assertions.warn("communicator failed to get reading-stream. multiplexer breaks: bytes will not be passed on. ex: ");
            e.printStackTrace();
        }
    }

    @Override public void write(int b) throws IOException { process((byte)b); }

    public ICommunicationChannel createChannel(int number) throws IOException {
        if (closed) throw new IOException("multiplexer is closed");
        if (channels.containsKey(number)) throw new IOException("channel already exists");
        MultiplexedChannel channel = new MultiplexedChannel(this, number);
        channels.put(number, channel);
        return channel;
    }

    public boolean hasChannel(int number) { return channels.containsKey(number); }

    public ICommunicationChannel getChannel(int number) { return channels.get(number); }

    public void close() throws IOException {
        for (ICommunicationChannel channel : channels.values()) channel.close();
        closed = true;
    }

    private void process(byte b) throws IOException {
        if (closed) throw new IOException("multiplexer is closed");

        switch (stage) {
            case POLLING_CHANNEL:
                clAcc.addByte(b);
                if (clAcc.isFinished()) {
                    currentChannel = (int)clAcc.getValue();
                    stage = ProcessingStage.POLLING_SIZE;
                    clAcc.recycle();
                }
                break;
            case POLLING_SIZE:
                clAcc.addByte(b);
                if (clAcc.isFinished()) {
                    remainingBytes = clAcc.getValue();
                    clAcc.recycle();

                    MultiplexedChannel channel = (MultiplexedChannel)getChannel(currentChannel);
                    if (channel != null) {
                        stage = ProcessingStage.POLLING_DATA;
                        currentWriting = channel.getOutputToUserInput();
                    } else {
                        Assertions.warn("start-data: multiplexer requested channel: '%d', but that channel does not exist. further data (%d bytes) will be voided...", currentChannel, remainingBytes);
                        stage = ProcessingStage.VOIDING_DATA;
                    }
                }
                break;
            case POLLING_DATA:
                currentWriting.write(b); // no break
            case VOIDING_DATA:
                remainingBytes--;
                if (remainingBytes <= 0) {
                    stage = ProcessingStage.POLLING_CHANNEL;
                    currentWriting = null;
                }
                break;
            default:
                Assertions.error("unknown ProcessingStage: %s", stage.name());
                break;
        }
    }

    private enum ProcessingStage { POLLING_CHANNEL, POLLING_SIZE, POLLING_DATA, VOIDING_DATA }

    //TODO: here too: replace ThroughputStream with some "outputstream x outputstream" or some better system, with Throughput as optional.
    private static class MultiplexedChannel implements ICommunicationChannel {

        private static final int FLUSH_THRESHOLD = 16384;

        private CommunicationMultiplexer core;
        private int number;
        private boolean closed;

        private ThroughputStream userReading;
        private ThroughputStream userWriting;

        private OutputStream wrappedWrite;

        public MultiplexedChannel(CommunicationMultiplexer core, int number) {
            this.core = core;
            this.number = number;

            userReading = new ThroughputStream();
            userWriting = new ThroughputStream();

            wrappedWrite = new OutputStream() {
                @Override public void write(int b) throws IOException {
                    userWriting.getOutputStream().write(b);
                    onUserHasWritten();
                }
                @Override public void flush() throws IOException { MultiplexedChannel.this.flush(); }
            };

        }

        @Override
        public OutputStream getWritingStream() throws IOException {
            if (closed) throw new IOException("channel is closed");
            return wrappedWrite;
        }

        @Override
        public InputStream getReadingStream() throws IOException {
            if (closed) throw new IOException("channel is closed");
            return userReading.getInputStream();
        }

        @Override
        public void close() {
            userWriting.close();
            userReading.close();
            closed = true;
        }

        protected OutputStream getOutputToUserInput() { return userReading.getOutputStream(); }

        /** Gets called automatically when the user writes a byte to {@link #userWriting}. */
        private void onUserHasWritten() throws IOException {
            if (userWriting.getInputStream().available() >= FLUSH_THRESHOLD) flush();
        }

        /** Propogate written data onto the core communicator. */
        private void flush() throws IOException {
            InputStream read = userWriting.getInputStream();
            int numBytes = read.available(); // ThroughputStream defines "available" as "minimum number of bytes that can be read before blocking."

            if (numBytes == 0) return;

            OutputStream write = core.communicator.getWritingStream();

            synchronized (core.communicator) {
                CompressedLong.serialize(number, write);
                CompressedLong.serialize(numBytes, write);
                StreamUtil.pass(read, write, numBytes, 16384);
            }
        }
    }

}