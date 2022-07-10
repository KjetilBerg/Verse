package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.ecs.datastore.IConstantSize;

/**
 * Represents a component that may exist on an entity.
 * Components should only contain data, with the exception of {@link HECSScript} and {@link ISerializable} details.
 *
 * Components should not implement .equals():
 * Components are only equal when they share the same identity (.equals() is the same as ==).
 *      The same applies for HECSScript.
 *      This is because any component is only equal to another component if they are the same literal
 *      component in a scene (in other words, by the default implementation of double equals: ==).
 * @see HECSScript
 */
public interface IComponent extends IConstantSize {}
