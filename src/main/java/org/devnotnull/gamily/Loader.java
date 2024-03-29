package org.devnotnull.gamily;

import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

// VAO - > Parent data structure - A list of variable data
// Stores vertexes as well as color data and other value.
// -> VBO -> Keyed item stored as with a numerical value within a VAO
public class Loader {

    private List<Integer> vaoList = new ArrayList();
    private List<Integer> vboList = new ArrayList();

    public RawModel loadToVAO(float[] positions, int[] indices) {
        int vaoId = createVao();
        bindIndicesBuffer(indices);
        storeAtttributesInList(0, positions);
        unbindVao();
        // Divide by 3 because we store x,y and z coordinates within the position float array and it has nth length and we use using triangle to present vertices
        return new RawModel(vaoId, indices.length);
}

    private int createVao() {
        // Create a VAO
        int vaoId = glGenVertexArrays();
        // We need to keep track of the VAOs are they are created
        vaoList.add(vaoId);
        // Bind to opengl
        GL30.glBindVertexArray(vaoId);
        // Assign or new vao vertex array
        return vaoId;
    }

    private void storeAtttributesInList(int index, float[] payload) {
        // Create a VBO
        int vboId = glGenBuffers();
        // We need to keep trac of the VBO as we create it
        vboList.add(vboId);
        // Bind to opengl
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        //
        FloatBuffer buffer = storeInFloatBuffer(payload);
        // Create buffered data and use static draw as we will not be modifying this data structure.
        // GL_ARRAY_BUFFER is used to represent vertices
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        // We use 3 given we are using x,y,z
        glVertexAttribPointer(index, 3, GL_FLOAT, false, 0, 0);
    }

    public void unbindVao() {
        glBindVertexArray(0);
    }

    private void bindIndicesBuffer (int[] indices) {
        int vboId = glGenBuffers();
        //
        vboList.add(vboId);
        // GL_ELEMENT_ARRAY_BUFFER used to represent indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        //
        IntBuffer buffer = storeInIntBuffer(indices);
        //
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private IntBuffer storeInIntBuffer(int[] payload) {
        IntBuffer buffer = BufferUtils.createIntBuffer(payload.length);
        buffer.put(payload);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeInFloatBuffer(float[] payload) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(payload.length);
        buffer.put(payload);
        buffer.flip();
        return buffer;
    }

    public void cleanUp() {
        vaoList.forEach(GL30::glDeleteVertexArrays);
        vboList.forEach(GL30::glDeleteBuffers);
    }
}

