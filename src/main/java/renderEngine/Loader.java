package renderEngine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

// VAO - > Parent data structure - A list of variable data
// Stores vertexes as well as color data and other value.
// -> VBO -> Keyed item stored as with a numerical value within a VAO
public class Loader {

    private List<Integer> vaoList = new ArrayList();
    private List<Integer> vboList = new ArrayList();

    public RawModel loadToVao(float[] positions) {
        int vaoId = createVao();
        storeAtttributesInList(0, positions);
        unbindVao();
        // Divide my 3 because we store x,y and z coordinates within the position float array and it has nth length.
        return new RawModel(vaoId, positions.length/3);
    }

    private int createVao() {
        // Create a VAO
        int vaoId = GL30.glGenVertexArrays();
        // We need to keep track of the VAOs are they are created
        vaoList.add(vaoId);
        // Bind to opengl
        GL30.glBindVertexArray(vaoId);
        // Assign or new vao vertex array
        return vaoId;
    }

    private void storeAtttributesInList(int index, float[] payload) {
        // Create a VBO
        int vboId = GL30.glGenBuffers();
        // We need to keep trac of the VBO as we create it
        vboList.add(vboId);
        // Bind to opengl
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        //
        FloatBuffer buffer = storeInFloatBuffer(payload);
        // Create buffered data and use static draw as we will not be modifying this data structure.
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
        // We use 3 given we are using x,y,z
        GL30.glVertexAttribPointer(index, 3, GL30.GL_FLOAT, false, 0, 0);
    }

    public void unbindVao() {
        GL30.glBindVertexArray(0);
    }

    private FloatBuffer storeInFloatBuffer(float[] payload) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(payload.length);
        buffer.put(payload.length);
        buffer.flip();
        return buffer;
    }

    public void cleanup() {
        vaoList.forEach(GL30::glDeleteVertexArrays);
        vboList.forEach(GL30::glDeleteBuffers);
    }
}
