package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.animation.AnimationController;
import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.SpriteAnimator;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.animation.SpriteAnimation;

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

            AnimationController<SpriteAnimation> controller = animator.controller;

            controller.getContext().setDeltaTime(dt);
            SpriteAnimation animation = controller.pickAnimation();

            float leftoverDeltatime = controller.getContext().getDeltaTime();
            animation.progress(leftoverDeltatime);

            if (animation.getFrames().size() == 0) continue; // TODO: more checks

            renderer.sprite = animation.getCurrentSprite();

        }

    }

}