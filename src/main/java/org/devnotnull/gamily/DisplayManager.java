package org.devnotnull.gamily;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class DisplayManager {

    private static final String TITLE = "title";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int V_SYNC = 0;

    // The window handle
    private long window;
    private static long    windowID;
    private static boolean running;

    // The callbacks
    GLFWErrorCallback       errorCallback;
    GLFWKeyCallback         keyCallback;
    GLFWCursorPosCallback   cursorPosCallback;
    GLFWMouseButtonCallback mouseButtonCallback;
    GLFWScrollCallback      scrollCallback;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW

        // Window Hints for OpenGL context
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        windowID = glfwCreateWindow(640, 480, "My GLFW Window", NULL, NULL);

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);

        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        System.out.println("OS name " + System.getProperty("os.name"));
        System.out.println("OS version " + System.getProperty("os.version"));
        System.out.println("OpenGL version " + glfwGetVersionString());
        System.out.println("OpenGL version " + glGetString(GL_VERSION));

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);
    }

    private int vaoID;
    private int vboID;

    private void loop() {
        // This line is critical for LWJGL"s interoperation with GLFW"s
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        /**
        //

        float now, last, delta;

        last = 0;

        // Set the callbacks
        glfwSetErrorCallback(errorCallback);
        glfwSetKeyCallback(windowID, keyCallback);
        glfwSetCursorPosCallback(windowID, cursorPosCallback);
        glfwSetMouseButtonCallback(windowID, mouseButtonCallback);
        glfwSetScrollCallback(windowID, scrollCallback);

        //
        Loader loader = new Loader();
        Renderer renderer = new Renderer();
        // StaticShader shader = new StaticShader();

        float[] vertices = {
            -0.5f,0.5f,0,   // V0
            -0.5f,-0.5f,0,  // V1
            0.5f,-0.5f,0,   // V2
            0.5f,0.5f,0     // V3
        };

        int[] indices = {
            0,1,3,  //Top left triangle (V0,V1,V3)
            3,1,2   //Bottom right triangle (V3,V1,V2)
        };

        RawModel model = loader.loadToVAO(vertices, indices);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            //
            renderer.prepare();
            // shader.start();

            renderer.render(model);

            // System.out.println("VAOID, " + model.getVaoID());
            // System.out.println("getVertexCount, " + model.getVertexCount());

            // shader.stop();

            glfwSwapBuffers(window);
            // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

        // shader.cleanup();
        loader.cleanUp();
         **/

        // Generate and bind a Vertex Array
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // The vertices of our Triangle
        float[] vertices = new float[]
                {
                        +0.0f, +0.8f,    // Top coordinate
                        -0.8f, -0.8f,    // Bottom-left coordinate
                        +0.8f, -0.8f     // Bottom-right coordinate
                };

        // Create a FloatBuffer of vertices
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();

        // Create a Buffer Object and upload the vertices buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        // Point the buffer at location 0, the location we set
        // inside the vertex shader. You can use any location
        // but the locations should match
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glBindVertexArray(0);

        while ( !glfwWindowShouldClose(window) ) {
            // Clear the screen
            //glClear(GL_COLOR_BUFFER_BIT);


            // Bind the vertex array and enable our location
            glBindVertexArray(vaoID);
            glEnableVertexAttribArray(0);

            // Draw a triangle of 3 vertices
            glDrawArrays(GL_TRIANGLES, 0, 3);

            // Disable our location
            glDisableVertexAttribArray(0);
            glBindVertexArray(0);


        }
    }

    public static void main(String[] args) {
        new DisplayManager().run();
    }

}