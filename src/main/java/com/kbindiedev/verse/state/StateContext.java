package com.kbindiedev.verse.state;

import com.kbindiedev.verse.util.Properties;

/**
 * Context for transitions.
 *
 * @see StateTransition
 */
public class StateContext {

    private Properties properties;

    public StateContext(Properties properties) { this.properties = properties; }

    public Properties getProperties() { return properties; }

}
