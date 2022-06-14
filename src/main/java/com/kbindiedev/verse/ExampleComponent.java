package com.kbindiedev.verse;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.ecs.datastore.SpriteAnimation;

import java.util.ArrayList;
import java.util.List;

public class ExampleComponent implements IComponent {

    public List<SpriteAnimation> animations     = new ArrayList<>();
    public int currentIndex                     = 0;

}
