package com.kbindiedev.verse.ecs.systems;

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

            animator.currentAnimation = animator.map.pickAnimation(animator.currentAnimation, animator.properties);

            SpriteAnimation animation = animator.currentAnimation;

            if (animation.getFrames().size() == 0) continue; // TODO: more checks

            // TODO: instead, create some wrapper for SpriteAnimation, so that direct access is easier for systems

            // TODO better
            animation.progress(dt);
            renderer.sprite = animation.getCurrentSprite();

        }

    }

}