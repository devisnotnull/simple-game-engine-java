package org.devnotnull.gamily.shaders;

public class StaticShader extends AbstractShader {

    private static final String VERTEX_FILE = "src/main/java/org/devnotnull/gamily/shaders/shader.vert";
    private static final String FRAGMENT_FILE = "src/main/java/org/devnotnull/gamily/shaders/shader.frag";

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(int attributes, String name) {
        super.bindAttribute(0, "position");
    }
}
