package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.RigidBody2D;
import com.kbindiedev.verse.ecs.components.SpriteAnimator;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.net.NetworkManager;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.profiling.Assertions;

import java.io.IOException;
import java.util.Iterator;

public class PlayerNetSyncSystem extends ComponentSystem {

    private EntityQuery query;

    public PlayerNetSyncSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(Transform.class, PlayerComponent.class, SpriteRenderer.class, SpriteAnimator.class, RigidBody2D.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
    }

    @Override
    public void update(float dt) {

        NetworkManager network = getSpace().getNetworkManager();
        if (network == null) return;

        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) {
            Entity entity = entities.next();

            if (!network.doIOwnEntity(entity)) continue;

            try { network.synchronizeEntity(entity); } catch (IOException e) {
                Assertions.warn("unable to sync player entity");
                e.printStackTrace();
            }
        }

    }
}
