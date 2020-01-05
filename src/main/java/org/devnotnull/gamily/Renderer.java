package org.devnotnull.gamily;

import org.lwjgl.opengl.GL40;


public class Renderer {

    public void prepare() {
        GL40.glClearColor(0, 0, 1, 1);
        GL40.glClear(GL40.GL_COLOR_BUFFER_BIT);
    }

    public void render(RawModel model) {
        GL40.glBindVertexArray(model.getVaoID());
        GL40.glEnableVertexAttribArray(0);
        GL40.glDrawElements(GL40.GL_TRIANGLES, model.getVertexCount(), GL40.GL_UNSIGNED_INT, 0);
        GL40.glDisableVertexAttribArray(0);
        GL40.glBindVertexArray(0);
    }
}
