package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.state.StateContext;
import com.kbindiedev.verse.util.Properties;

/** Context for {@link AnimationTransition}. */
public class AnimatorContext extends StateContext {

    private float dt;

    public AnimatorContext() { this(new Properties()); }
    public AnimatorContext(Properties properties) {
        super(properties);
        dt = 0f;
    }

    public float getDeltaTime() { return dt; }
    public void setDeltaTime(float dt) { this.dt = dt; }

}