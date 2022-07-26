package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.TextRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.ShapeDrawer;
import com.kbindiedev.verse.gfx.SpriteBatch;
import com.kbindiedev.verse.ui.font.FlowMode;
import com.kbindiedev.verse.ui.font.Text;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextRendererSystem extends ComponentSystem {

    private EntityQuery query;
    private SpriteBatch spritebatch;

    public TextRendererSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(TextRenderer.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        spritebatch = new SpriteBatch(getSpace().getGfxImplementation(), 512, 8); // TODO: move to space
    }

    @Override
    public void render(RenderContext context) {

        Iterator<Entity> entities = query.execute().iterator();
        List<TextEntry> screenText = new ArrayList<>();
        List<TextEntry> worldText = new ArrayList<>();
        while (entities.hasNext()) {
            Entity entity = entities.next();
            TextRenderer textComp = entity.getComponent(TextRenderer.class);
            Text text = new Text(textComp.sequence, textComp.font, textComp.fontSize, textComp.flowmode, 50); // TODO: move text object into component ??
            TextEntry entry = new TextEntry(text, entity.getTransform());
            if (textComp.screenMode) screenText.add(entry); else worldText.add(entry);
        }

        spritebatch.setZPos(0.3f);

        if (worldText.size() > 0) {
            spritebatch.setProjectionMatrix(context.getCameraComponent().projectionMatrix);
            spritebatch.setViewMatrix(context.getCameraComponent().viewMatrix);
            spritebatch.begin();

            for (TextEntry entry : worldText) entry.text.draw(spritebatch, entry.transform.position.x, entry.transform.position.y);

            spritebatch.end();
        }

        if (screenText.size() > 0) {
            Matrix4f proj = new Matrix4f().ortho(0f, 200f, 0f, 200f, -1f, 1f);
            Matrix4f view = new Matrix4f().identity();
            spritebatch.setProjectionMatrix(proj);
            spritebatch.setViewMatrix(view);
            spritebatch.begin();

            for (TextEntry entry : screenText) entry.text.draw(spritebatch, entry.transform.position.x, entry.transform.position.y);

            spritebatch.end();
        }

    }

    private static class TextEntry {
        private Text text;
        private Transform transform;
        public TextEntry(Text text, Transform transform) { this.text = text; this.transform = transform; }
    }

    @Override
    public void onDrawGizmos(RenderContext context) {
        ShapeDrawer drawer = getSpace().getShapeDrawer();

        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) {
            Entity entity = entities.next();

            TextRenderer textComp = entity.getComponent(TextRenderer.class);
            Text text = new Text(textComp.sequence, textComp.font, textComp.fontSize, textComp.flowmode, 50);

            drawer.drawPoint(entity.getTransform().position, 1f, Pixel.RED);

            Vector2f directionToCenter = FlowMode.directionToCenter(text.getFlowMode());
            Vector3f pos = new Vector3f(directionToCenter.x, directionToCenter.y, 0f).mul(text.getWidth(), text.getHeight(), 0).add(entity.getTransform().position);
            drawer.drawOutlineSquare(pos, text.getWidth(), text.getHeight(), Pixel.RED); // TODO: does not consider "screenMode"
        }
    }

}