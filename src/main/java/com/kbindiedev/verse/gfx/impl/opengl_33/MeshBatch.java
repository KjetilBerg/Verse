package com.kbindiedev.verse.gfx.impl.opengl_33;

import java.util.ArrayList;

//TODO note: sorting strategy is really not straight forward. consider https://stackoverflow.com/questions/4305911/opengl-depth-sorting
/** Batches meshes together for mass-rendering */
public class MeshBatch {

    private ArrayList<GLMesh> GLMeshes;

    public MeshBatch() {
        GLMeshes = new ArrayList<>();
    }

    /**
     * Add a Mesh to be rendered this frame. Meshes must be re-added every frame. Otherwise {@see StaticMeshBatch}. //TODO: StaticMeshBatch does not exist
     * @param GLMesh - The mesh to render this frame.
     */
    public void addMesh(GLMesh GLMesh) {
        //TODO: currently does no sorting / ordering
        GLMeshes.add(GLMesh);
    }

    /**
     * Most optimal ordering (given no overlapping?):
     *      Sort render target
     *      Sort program
     *      Sort ROP (?)
     *      Sort Texture bindings
     *      Sort Texture Format (none?)
     *      Sort UBO Bindings (?)
     *      Sort Vertex Bindings
     *      Sort Uniform Updates

     * Depends on scenario, but methods could include:
     *
     * Drawing each vertex-binding individually, rebinding textures as need-be
     *
     * Drawing each texture-binding individually, rebinding vertex-bindings as need-be
     *

     * On render-step, the renderer MUST:
     * For each mesh:
     *      Bind corresponding program
     *      Set ROP
     *      Bind textures
     *      Bind VAO
     *      Uniform updates
     * */

    public void render() {
        for (GLMesh mesh : GLMeshes) {

            /*
            //TODO: statetracker consider shaders
            mesh.getMaterial().getShader().use();

            //TODO: materials dont support ROP yet

            //TODO: materials dont support texture bindings yet

            StateTracker.pushVertexArrayObject(mesh.getAssignedVAO());
            //mesh.getAssignedVAO().bind(); //do this instead

            //TODO: materials dont support uniforms yet

            //GL40.glDrawRangeElementsBaseVertex(GL33.GL_TRIANGLES, 0, 3, GL33.GL_UNSIGNED_SHORT, indices, 0); //TODO: indices offset to parent (baseVertex)

            mesh.getMaterial().getShader().detach();

            StateTracker.popVertexArrayObject();
*/

            mesh.render();
            System.out.println("OUTDATED: GLMesh.render() call MeshBatch.java");
        }

        GLMeshes.clear();
    }

}
