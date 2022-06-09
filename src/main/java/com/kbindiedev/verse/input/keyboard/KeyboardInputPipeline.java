package com.kbindiedev.verse.input.keyboard;

/** The overarching collection of all keyboard handling */
public class KeyboardInputPipeline {

    private KeyboardInputEventQueue queue;
    private KeyboardInputEventProcessor processor;
    private KeyEventDispatcher dispatcher;
    private KeyEventMultiplexerListener multiplexer;
    private KeyEventTracker tracker;

    private IKeyEventListener externalListener;

    public KeyboardInputPipeline() { this(new KeyboardInputEventQueue()); }
    public KeyboardInputPipeline(KeyboardInputEventQueue queue) {
        this.queue = queue;
        processor = new KeyboardInputEventProcessor();
        dispatcher = new KeyEventDispatcher();
        multiplexer = new KeyEventMultiplexerListener();
        dispatcher.setListener(multiplexer);

        tracker = new KeyEventTracker();
        multiplexer.addListener(tracker);

        externalListener = null;
    }

    /** Pass all events from the queue to the processor, and have it handle them. */
    public void iterate() {
        processor.iterate(queue.getQueuedEvents());
        queue.clear();

        dispatcher.dispatch(processor.getOutputEvents());
    }

    public void setQueue(KeyboardInputEventQueue queue) { this.queue = queue; }
    public KeyboardInputEventQueue getQueue() { return queue; }

    /** Set a listener for KeyEvents. If one already exists, then it will be overwritten. Input "null" will remove existing listener. */
    public void setExternalListener(IKeyEventListener listener) {
        if (externalListener != null) multiplexer.removeListener(externalListener);
        externalListener = listener;
        if (externalListener != null) multiplexer.addListener(listener);
    }

    public KeyEventTracker getTracker() { return tracker; }
    public KeyboardInputEventProcessor getProcessor() { return processor; }

}