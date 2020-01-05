package org.devnotnull.gamily.shaders;

import org.lwjgl.opengl.GL40;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class AbstractShader {

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public AbstractShader(String vertexFile, String fragmentFile) {
        // Vertex -> Fragment
        vertexShaderId = loadShader(vertexFile, GL40.GL_VERTEX_SHADER);
        fragmentShaderId = loadShader(fragmentFile, GL40.GL_FRAGMENT_SHADER);
        // Bind our Vertex and Fragment shader via an assigned opengl program
        programId = GL40.glCreateProgram();
        GL40.glAttachShader(programId, vertexShaderId);
        GL40.glAttachShader(programId, fragmentShaderId);
    }

    private static int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine())!=null){
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }

        int shaderID = GL40.glCreateShader(type);
        GL40.glShaderSource(shaderID, shaderSource);
        GL40.glCompileShader(shaderID);

        if(GL40.glGetShaderi(shaderID, GL40.GL_COMPILE_STATUS )== GL40.GL_FALSE){
            System.out.println(GL40.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }

        return shaderID;
    }

    protected abstract void bindAttributes(int attributes, String name);

    protected void bindAttribute(int attributes, String name) {
        GL40.glBindAttribLocation(programId, attributes, name);
    }

    public void start() {
        GL40.glUseProgram(programId);
    }

    public void stop() {
        GL40.glUseProgram(0);
    }

    public void cleanup() {
        stop();
        GL40.glDetachShader(programId, vertexShaderId);
        GL40.glDetachShader(programId, fragmentShaderId);
        //
        GL40.glDeleteShader(vertexShaderId);
        GL40.glDeleteShader(fragmentShaderId);
        //
        GL40.glDeleteProgram(programId);
    }

}
