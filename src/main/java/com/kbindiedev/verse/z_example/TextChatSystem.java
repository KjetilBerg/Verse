package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.Main;
import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.TextRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.net.NetworkManager;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.input.keyboard.KeyEventTracker;
import com.kbindiedev.verse.input.keyboard.Keys;
import com.kbindiedev.verse.input.keyboard.event.KeyEvent;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.ui.font.BitmapFont;
import com.kbindiedev.verse.ui.font.FlowMode;
import com.kbindiedev.verse.ui.font.GlyphSequence;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class TextChatSystem extends ComponentSystem {

    private EntityQuery query;
    private FlowMode tempFlowMode = FlowMode.TOP_LEFT;

    public TextChatSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(TextChatComponent.class, TextRenderer.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
    }

    @Override
    public void update(float dt) {

        KeyEventTracker keys = getSpace().getKeyboardTracker();
        List<KeyEvent> keyEvents = keys.getAllEventsThisIteration();

        // in-game adjustment of flowmode
        if (keys.wasKeyReleasedThisIteration(Keys.KEY_P)) {
            int i = 0;
            for (FlowMode mode : FlowMode.values()) { if (mode == tempFlowMode) break; i++; }
            i += 1;
            i %= FlowMode.values().length;
            tempFlowMode = FlowMode.values()[i];
        }

        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) {
            Entity entity = entities.next();
            TextChatComponent chat = entity.getComponent(TextChatComponent.class);
            TextRenderer text = entity.getComponent(TextRenderer.class);

            for (KeyEvent event : keyEvents) {
                if (event.getType() == KeyEvent.KeyEventType.KEYUP) continue;

                int keycode = event.getKeycode();
                if (keycode == Keys.KEY_ENTER) {
                    if (chat.active) publishMessage(chat, text.font);
                    chat.active = !chat.active;
                }

                if (!chat.active) continue;

                // TODO: better conversion, part of event stuff ?
                if (!keys.isKeyDown(Keys.KEY_LEFT_SHIFT)) {
                    if (keycode >= Keys.KEY_A && keycode <= Keys.KEY_Z) keycode += 32; // to lowercase
                }
                // TODO: keys.isToggled (caps)
                if (keys.isKeyDown(Keys.KEY_LEFT_SHIFT)) {
                    if (keycode == Keys.KEY_PERIOD) keycode = 58; // TODO: no colon key
                    if (keycode == Keys.KEY_COMMA) keycode = Keys.KEY_SEMICOLON;
                    if (keycode == Keys.KEY_7) keycode = Keys.KEY_SLASH;
                }

                chat.currentText.addGlyphIfValid(keycode, text.font);
                if (keycode == Keys.KEY_BACKSPACE && chat.currentText.count() > 0) {
                    chat.currentText.remove(1);
                }
            }

            text.sequence = chat.currentText;
        }

    }

    private void publishMessage(TextChatComponent component, BitmapFont font) {

        String text = component.currentText.getStringValue();
        if (text.startsWith("c ")) {
            text = text.substring(2);
            component.currentText.clear();
            Main.tempActivateServerMode(text);
            return;
        }

        TextRenderer textComp = new TextRenderer();
        //textComp.flowmode = FlowMode.CENTER;
        textComp.flowmode = tempFlowMode;
        textComp.fontSize = 6;
        textComp.sequence = GlyphSequence.copy(component.currentText);
        textComp.font = font;
        textComp.screenMode = false;

        Transform transform = new Transform();
        transform.position = new Vector3f(component.target.position).add(new Vector3f(0, 5f, 0));
        transform.scale = new Vector3f(component.target.scale);
        transform.rotation = new Quaternionf(component.target.rotation);

        Entity entity = getSpace().getEntityManager().instantiate(textComp, transform); // TODO: target = parent, transform = (0,0)

        NetworkManager network = getSpace().getNetworkManager();
        if (network != null) {
            try { network.synchronizeEntity(entity); } catch (IOException e) { Assertions.warn("unable to publish message (synchronize entity) to network: "); e.printStackTrace(); }
        }

        Thread destroyer = new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
            try { if (network != null) network.destroyNetworkEntity(entity); } catch (IOException e) { Assertions.warn("unable to destroy message on network: "); e.printStackTrace(); }
            getSpace().getEntityManager().destroy(entity);
        });

        destroyer.start();

        component.currentText.clear();
    }
}