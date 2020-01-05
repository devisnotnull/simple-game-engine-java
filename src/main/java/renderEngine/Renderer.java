package renderEngine;

import org.lwjgl.opengl.GL30;

public class Renderer {

    public void prepare() {
        GL30.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    public void render(RawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL30.glEnableVertexAttribArray(0);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, model.getVertexCount());
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

}
