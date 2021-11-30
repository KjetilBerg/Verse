package com.kbindiedev.verse.input.mouse;

/** Used to describe classes that can handle mouse input (or set as mouse input processor) */
//TODO: (if ever android/ios support): make another similar class for touch.
public interface IMouseInputProcessor {

    /**
     * Called when a mouse button was pressed.
     * @param screenX - The x coordinate, in pixels. The origin is in the upper left corner.
     * @param screenY - The y coordinate, in pixels. The origin is in the upper left corner.
     * @param button - Which button was pressed {@see MouseButtons}.
     * @return true if the event was handled, false otherwise.
     */
    boolean mouseDown(int screenX, int screenY, int button);

    /**
     * Called when a mouse button was released.
     * @param screenX - The x coordinate, in pixels. The origin is in the upper left corner.
     * @param screenY - The y coordinate, in pixels. The origin is in the upper left corner.
     * @param button - Which button was pressed {@see MouseButtons}.
     * @return true if the event was handled, false otherwise.
     */
    boolean mouseUp(int screenX, int screenY, int button);

    /**
     * Called when a mouse button was 'clicked'.
     * 'clicked' is defined as a button calling the mouseDown event, followed by a mouseUp event,
     *      without having any mouseMoved or mouseDragged events in-between.
     * 'clicked' events are always dispatched AFTER mouseUp events.
     *      Due to the nature of the 'click' event, a mouseUp event will ALWAYS come before it.
     * @param screenX - The x coordinate, in pixels. The origin is in the upper left corner.
     * @param screenY - The y coordinate, in pixels. The origin is in the upper left corner.
     * @param button - Which button was pressed {@see MouseButtons}.
     * @param holdDuration - How long the button was held before being released, in seconds.
     * @return true if the event was handled, false otherwise.
     */
    boolean mouseClicked(int screenX, int screenY, int button, float holdDuration);

    /**
     * Called when the mouse moved while having a button pressed.
     * The button pressed is not specified since there may be multiple.
     *      You can query which buttons are pressed {@see MouseInputHandler#isButtonDown(int)}.
     * @param screenX - The x coordinate, in pixels, of the new position. The origin is in the upper left corner.
     * @param screenY - The y coordinate, in pixels, of the new position. The origin is in the upper left corner.
     * @return true if the event was handled, false otherwise.
     */
    boolean mouseDragged(int screenX, int screenY);

    /**
     * Called when the mouse moved while not having any buttons pressed.
     * @param screenX - The x coordinate, in pixels, of the new position. The origin is in the upper left corner.
     * @param screenY - The y coordinate, in pixels, of the new position. The origin is in the upper left corner.
     * @return true if the event was handled, false otherwise.
     */
    boolean mouseMoved(int screenX, int screenY);

    /**
     * Called when the mouse wheel was scrolled.
     * @param amountX - The amount of horizontal scroll (yes such mice do exist),
     *                  negative if the wheel was scrolled towards the left from the user's perspective, and
     *                  positive if the wheel was scrolled towards the right from the user's perspective.
     * @param amountY - The amount of vertical scroll,
     *                  negative if the wheel was scrolled towards the user, and
     *                  positive if the wheel was scrolled away from the user.
     * @return true if the event was handled, false otherwise.
     */
    boolean mouseScrolled(float amountX, float amountY);

}
