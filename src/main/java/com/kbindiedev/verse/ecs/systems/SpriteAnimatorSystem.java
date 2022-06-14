package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.SpriteAnimator;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.datastore.SpriteAnimation;
import com.kbindiedev.verse.ecs.datastore.SpriteFrame;

import java.util.Iterator;

public class SpriteAnimatorSystem extends ComponentSystem {

    private EntityQuery query;

    public SpriteAnimatorSystem(Space space) { super(space); }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(SpriteAnimator.class, SpriteRenderer.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
    }

    @Override
    public void update(float dt) {

        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) {
            Entity entity = entities.next();

            SpriteAnimator animator = entity.getComponent(SpriteAnimator.class);
            SpriteRenderer renderer = entity.getComponent(SpriteRenderer.class);

            SpriteAnimation animation = animator.animation;
            if (animation.getFrames().size() == 0) continue; // TODO: more checks

            // TODO: instead, create some wrapper for SpriteAnimation, so that direct access is easier for systems
            animation.setCurrentFrameSeconds(animation.getCurrentFrameSeconds() + dt);
            SpriteFrame frame = animation.getFrames().get(animation.getCurrentFrameIndex());
            while (animation.getCurrentFrameSeconds() >= frame.getDuration()) {
                animation.setCurrentFrameSeconds(animation.getCurrentFrameSeconds() - frame.getDuration());
                animation.setCurrentFrameIndex((animation.getCurrentFrameIndex() + 1) % animation.getFrames().size());
                frame = animation.getFrames().get(animation.getCurrentFrameIndex());
            }

            renderer.sprite = frame.getSprite();

        }

    }

}