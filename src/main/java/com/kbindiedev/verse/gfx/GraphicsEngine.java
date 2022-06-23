package com.kbindiedev.verse.gfx;

public abstract class GraphicsEngine {

    /*
    *   Every GraphicsEngine impl must support:
    *       Mesh - A structure that can accept vertices, as per defined by verse.gfx.strategy, and indices as per defined by verse.gfx.strategy.indexdata
    *               Note: Meshes must accept non-triangular primitives, namely: triangles, lines and points
    *               Meshes must support instanced rendering.
    *       Texture - see Texture
    *
    *       MeshBatch - A structure that provide the methods defined by verse.gfx.MeshBatch
    *
    *
    *
    *
    *       Rendering Text is handled by verse.gfx, and does not need direct support from implementations.
    *               (it is being rendered to SpriteBatch)
    *
    *       SpriteBatching and ShapeBatching is not handled by the implementation, but rather by verse.gfx (by rendering to a Mesh)
    *              (also handles lines and pixels) (if pixel too lage, draw quad instead)
    *
    *       ParticleRenderer / batch is handled by verse.gfx (via Mesh instanced render)
    *           Particle is also handled outside of implementations.
    *
    * 
    *       MeshBatch handling needs to be considered. Implementations should probably handle sorting / batching most efficiently.
    *
    *
    *
    */

    //Note: implementations could more easily be implemented if, for example, ShapeDrawer instead was replaced with some ImmediateRenderer that accepted vertices and some parameters
    //          would make all easy except SpriteBatch and MeshBatch (and potentially ParticleBatch)


     /*
     * Ties to mouseevents / keyboard events that (idk) may be handled by the window context, does not apply here, but rather elsewhere, and is not documented here.
     *      Same goes for utility methods such as screen width and screen height.
     *          as well as viewports.
     *
     */

    //TODO: spritebatch virtually support layers by being able to set render target.


    //pixel sizing??


    ///drawing

    //draw pixel
    //draw line
    //draw circle
    //fill circle
    //draw rect
    //fill rect
    //draw triangle
    //fill triangle

    //draw sprite
    //draw partial sprite

    //draw string (with / without monospacing)
    //get text size (string) (rectangle)


    //warping? (draw polygon, or quad with texture)
    //rotation?
    //scaling?
    //gradient fill rectangle ?

    //clear (color)

    //set font (?)


    //abstract def:

    /** MUST be run before using the engine */
    public abstract void initialize(GraphicsEngineSettings settings);

    public abstract Mesh createMesh(Material material, long numVertices);

}
