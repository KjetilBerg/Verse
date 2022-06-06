package com.kbindiedev.verse;

import com.kbindiedev.verse.ecs.EntityManager;
import com.kbindiedev.verse.ecs.Space;

public class Main {

    //TODO: note tag ordering (esp. @throws) https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
    //TODO: verse exceptions ?


    // TODO: add @kb.time O(x) to many methods


    public static void main(String[] args) {

        Space space = new Space();

        ExampleComponent c1 = new ExampleComponent();
        ExampleComponent c2 = new ExampleComponent();
        c1.data = "c1 here";
        c2.data = "I am c2";

        space.getEntityManager().instantiate(c1);
        space.getEntityManager().instantiate(c2);

        space.addSystem(new ExampleSystem(space));

        space.start();

        try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }

        space.stop();
        //GeneralTesting.run();

    }

}
