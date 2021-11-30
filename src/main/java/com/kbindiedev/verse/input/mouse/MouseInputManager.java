package com.kbindiedev.verse.input.mouse;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * All mouse inputs from varying implementations are ultimately sent here, for this class to then dispatch.
 * All events are dispatched right before the game .update function is run.
 * Note: comments and method names describe everything on a per-frame-basis, though the actual definition is
 *      per-handleEvents-method-is-run-basis. This SHOULD happen once per frame, right before the global game .update
 *      method is executed.
 * In regards to the 'click' event, timestamps of events are recorded when the events are handled (once per frame),
 *      making them essentially work like framerules (sub-frame accuracy is not considered).
 * mouseClick events are ALWAYS dispatched AFTER mouseUp events.
 * The mouse events are handled AFTER the keyboard events are handled.
 */
//TODO: maybe make non-static
public class MouseInputManager {

    private static HashMap<Integer, Boolean> buttonStates = new HashMap<>();
    private static HashMap<Integer, Long> buttonStatesTimestamps = new HashMap<>();  //timestamps (unix) for when state last changed
    private static HashSet<Integer> stateChangesThisFrame = new HashSet<>();    //map of buttons that changed their state this frame
    private static ArrayList<MouseEvent> unhandledEvents = new ArrayList<>();   //events are piled and handled once per frame
    private static int currentMouseX = 0, currentMouseY = 0;

    private static long unixLastMoveOrDrag = System.currentTimeMillis();

    private static boolean isAnyButtonPressed = false;

    /** Initialize processor to blank */
    private static IMouseInputProcessor processor = new IMouseInputProcessor() {
        @Override public boolean mouseDown(int screenX, int screenY, int button) { return false; }
        @Override public boolean mouseUp(int screenX, int screenY, int button) { return false; }
        @Override public boolean mouseClicked(int screenX, int screenY, int button, float holdDuration) { return false; }
        @Override public boolean mouseDragged(int screenX, int screenY) { return false; }
        @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
        @Override public boolean mouseScrolled(float amountX, float amountY) { return false; }
    };

    /** Set the IMouseInputProcessor system wide. */
    public static void setProcessor(IMouseInputProcessor p) { processor = p; }

    /**
     * Check whether a button is pressed.
     * Note that if checked during event dispatch, registry may not be "up-to-date" with current state of final frame.
     *      Things are guaranteed to be "up-to-date" once the global game .update method is executed, though.
     * @param button - The button code {@see MouseButtons}
     * @return true if button by button code is pressed, false otherwise
     */
    public static boolean isButtonDown(int button) { return buttonStates.getOrDefault(button, false); }

    /**
     * Get the x coordinate of the last recorded mouse position.
     * Note that if checked during event dispatch, registry may not be "up-to-date" with current state of final frame.
     *      Things are guaranteed to be "up-to-date" once the global game .update method is executed, though.
     * @return the x coordinate of the mouse position
     */
    public static int getMouseX() { return currentMouseX; }

    /**
     * Get the y coordinate of the last recorded mouse position.
     * Note that if checked during event dispatch, registry may not be "up-to-date" with current state of final frame.
     *      Things are guaranteed to be "up-to-date" once the global game .update method is executed, though.
     * @return the y coordinate of the mouse position
     */
    public static int getMouseY() { return currentMouseY; }

    /**
     * Check whether a button was pressed this frame.
     * Note that if checked during event dispatch, registry may not be "up-to-date" with current state of final frame.
     *      Things are guaranteed to be "up-to-date" once the global game .update method is executed, though.
     * @param button - The button code {@see MouseButtons}
     * @return true if button by button code was pressed this frame
     */
    public static boolean wasButtonPressedThisFrame(int button) {
        return stateChangesThisFrame.contains(button) && buttonStates.get(button);
    }

    /**
     * Check whether a button was released this frame.
     * Note that if checked during event dispatch, registry may not be "up-to-date" with current state of final frame.
     *      Things are guaranteed to be "up-to-date" once the global game .update method is executed, though.
     * @param button - The button code {@see MouseButtons}
     * @return true if button by button code was released this frame
     */
    public static boolean wasButtonReleasedThisFrame(int button) {
        return stateChangesThisFrame.contains(button) && !buttonStates.get(button);
    }

    /**
     * Handle all unhandled events. Should happen once per frame.
     * This will also notify the processor of all events that have happened since last call to this function.
     * Note that all events are handled "in-order".
     *      This means if there are several events during a single frame, the .isButtonDown and such functions
     *      that depend on the event registry, may not be "up-to-date" with events that are yet to be dispatched this frame.
     */
    public static void handleEvents() {
        stateChangesThisFrame.clear();

        for (MouseEvent event : unhandledEvents) {

            if (event instanceof MouseButtonEvent) handleButtonEvent((MouseButtonEvent)event);
            else if (event instanceof MouseMovedEvent) handleMovedEvent((MouseMovedEvent)event);
            else if (event instanceof MouseScrolledEvent) handleScrolledEvent((MouseScrolledEvent)event);
            else Assertions.warn("unknown MouseEvent class type: %s", event.getClass().getCanonicalName());

        }

        unhandledEvents.clear();
    }

    /** Handle MouseButtonEvent, used in handleEvents {@see #handleEvents} */
    private static void handleButtonEvent(MouseButtonEvent event) {
        //check registry
        if (buttonStates.containsKey(event.button)) {
            boolean bad = false;
            if (event.type == MouseButtonEvent.MouseButtonEventType.BUTTONDOWN && buttonStates.get(event.button)) bad = true; //already pressed
            if (event.type == MouseButtonEvent.MouseButtonEventType.BUTTONUP && !buttonStates.get(event.button)) bad = true; //already released

            if (bad) {
                Assertions.warn("mouse button: '%d' got event: '%s', but is already in that state (this should not happen). ignoring event...", event.button, event.type.name());
                return;
            }
        }

        //update timestamp
        long unixNow = System.currentTimeMillis();
        long unixOldTimestamp = buttonStatesTimestamps.getOrDefault(event.button, 0L);
        buttonStatesTimestamps.put(event.button, System.currentTimeMillis());

        //update buttonState registry
        if (event.type == MouseButtonEvent.MouseButtonEventType.BUTTONDOWN) buttonStates.put(event.button, true);
        else if (event.type == MouseButtonEvent.MouseButtonEventType.BUTTONUP) buttonStates.put(event.button, false);

        //update stateChangesThisFrame registry
        stateChangesThisFrame.add(event.button);

        //check if any button is pressed
        isAnyButtonPressed = false;
        for (boolean state : buttonStates.values()) {
            if (state) { isAnyButtonPressed = true; break; }
        }

        //dispatch event
        switch (event.type) {
            case BUTTONDOWN:
                processor.mouseDown(currentMouseX, currentMouseY, event.button);
                break;
            case BUTTONUP:
                processor.mouseUp(currentMouseX, currentMouseY, event.button);
                break;
            default: Assertions.error("unknown event type: %s", event.type.name());
        }

        //check for 'clicked' event: is now released (and thereby WAS pressed)
        //                              and the 'pressed timestamp' came after the last move or drag event
        if (event.type == MouseButtonEvent.MouseButtonEventType.BUTTONUP && unixOldTimestamp > unixLastMoveOrDrag) {
            float holdDurationSeconds = (unixNow - unixOldTimestamp) / 1000f;
            processor.mouseClicked(currentMouseX, currentMouseY, event.button, holdDurationSeconds);
        }
    }

    /** Handle MouseMovedEvent, used in handleEvents {@see #handleEvents} */
    private static void handleMovedEvent(MouseMovedEvent event) {
        currentMouseX = event.screenX;
        currentMouseY = event.screenY;

        unixLastMoveOrDrag = System.currentTimeMillis();

        if (isAnyButtonPressed) {
            processor.mouseDragged(event.screenX, event.screenY);
        } else {
            processor.mouseMoved(event.screenX, event.screenY);
        }
    }

    /** Handle MouseScrolledEvent, used in handleEvents {@see #handleEvents} */
    private static void handleScrolledEvent(MouseScrolledEvent event) {
        processor.mouseScrolled(event.amountX, event.amountY);
    }


    /**
     * Notify that a certain button was pressed. MUST not be called again before .notifyButtonUp has been called.
     * The button code is in accordance to the MouseButtons class {@see MouseButtons}.
     * @param button - The button code {@see MouseButtons}.
     */
    public static void notifyButtonDown(int button) {
        unhandledEvents.add(new MouseButtonEvent(MouseButtonEvent.MouseButtonEventType.BUTTONDOWN, button));
    }

    /**
     * Notify that a certain button was released. MUST not be called again before .notifyButtonDown has been called.
     * The button code is in accordance to the MouseButtons class {@see MouseButtons}.
     * @param button - The button code {@see MouseButtons}.
     */
    public static void notifyButtonUp(int button) {
        unhandledEvents.add(new MouseButtonEvent(MouseButtonEvent.MouseButtonEventType.BUTTONUP, button));
    }

    /**
     * Notify that the mouse moved.
     * @param screenX - The x coordinate, in pixels, of where the mouse moved to. The origin is in the upper left corner.
     * @param screenY - The y coordinate, in pixels, of where the mouse moved to. The origin is in the upper left corner.
     */
    public static void notifyMouseMove(int screenX, int screenY) {
        unhandledEvents.add(new MouseMovedEvent(screenX, screenY));
    }

    /**
     * Notify that the mouse wheel was scrolled.
     * @param amountX - The amount of horizontal scroll (yes such mice do exist),
     *                  negative if the wheel was scrolled towards the left from the user's perspective, and
     *                  positive if the wheel was scrolled towards the right from the user's perspective.
     * @param amountY - The amount of vertical scroll,
     *                  negative if the wheel was scrolled towards the user, and
     *                  positive if the wheel was scrolled away from the user.
     */
    public static void notifyMouseScrolled(float amountX, float amountY) {
        unhandledEvents.add(new MouseScrolledEvent(amountX, amountY));
    }

    private static class MouseEvent {}

    private static class MouseButtonEvent extends MouseEvent {
        enum MouseButtonEventType { BUTTONDOWN, BUTTONUP }

        private MouseButtonEventType type;
        private int button;

        MouseButtonEvent(MouseButtonEventType type, int button) { this.type = type; this.button = button; }
    }

    private static class MouseMovedEvent extends MouseEvent {

        private int screenX, screenY;

        MouseMovedEvent(int screenX, int screenY) { this.screenX = screenX; this.screenY = screenY; }

    }

    private static class MouseScrolledEvent extends MouseEvent {

        private float amountX, amountY;

        MouseScrolledEvent(float amountX, float amountY) { this.amountX = amountX; this.amountY = amountY; }

    }

}
