package org.devnotnull.gamily;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class FragmentTriangle {

    // The window handle
    private long window;

    private int progHandle; //the handle to the shader program

    private float verts[] = {
            -1f, -1f, 0f,
            1f, -1f, 0f,
            0f, 1f, 0f
    };

    private void teardown(){
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void run() {
        init();
        loop();
        teardown();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            if (key == GLFW_KEY_A){
                System.out.println("A");
            }
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

        // initialize open GL, and prepare for triangle

        GL.createCapabilities();
        GL11.glClearColor(0f, 0f, 1f, 0f);

        String[] shaderBufferArr = readinShaders("test.frag", "test.vert");

        int fragShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        int vertShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(fragShaderID, shaderBufferArr[0]);
        GL20.glShaderSource(vertShaderID, shaderBufferArr[1]);


        System.out.println("Compiling Frag Shader...");
        GL20.glCompileShader(fragShaderID);
        // String fragLog = GL20.glGetShaderInfoLog(fragShader);
        // System.out.println(fragLog);

        System.out.println("Compiling Vert Shader...");
        GL20.glCompileShader(vertShaderID);
        // String vertLog = GL20.glGetShaderInfoLog(vertShader);
        // System.out.println(vertLog);

        System.out.println("Creating Program...");
        progHandle = GL20.glCreateProgram();
        System.out.println("Attatching Shaders to Program... " + fragShaderID);
        GL20.glAttachShader(progHandle, fragShaderID);
        System.out.println("Attatching Shaders to Program... " + vertShaderID);
        GL20.glAttachShader(progHandle, vertShaderID);
        System.out.println("Linking Program...");
        GL20.glLinkProgram(progHandle);
        String log = GL20.glGetProgramInfoLog(progHandle);
        System.out.println(log);

        // the buffer for the actual array of vertices
        System.out.println("Generating Buffer...");
        int buffer = GL15.glGenBuffers();

        System.out.println("Binding Buffer...");
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        System.out.println("Buffering Data...");
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW);

        GL20.glEnableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0l);

    }

    static String readFile(String path, Charset encoding){
        Path currentDir = Paths.get("src/main/shaders");
        currentDir = currentDir.toAbsolutePath();
        Path p = Paths.get(currentDir.toString(), path);
        try {
            byte[] encoded = Files.readAllBytes(p);
            return new String(encoded, encoding);
        } catch (IOException e){
            System.out.println("Something went wrong with the reading of the shader: " + e.getMessage());
        }
        return null;
    }


    static String[] readinShaders(String fragShader, String vertShader) {
        System.out.println("Reading in Frag Shader...");
        String fragmentShaderBuffer = readFile(fragShader, Charset.defaultCharset());
        System.out.println("Reading in Vert Shader...");
        String vertexShaderBuffer = readFile(vertShader, Charset.defaultCharset());

        if (fragmentShaderBuffer != null && vertexShaderBuffer != null) {
            String[] shaderArr = {fragmentShaderBuffer, vertexShaderBuffer};
            return shaderArr;
        }
        throw new UnsupportedOperationException("There was a problem reading in the shaders");
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        //float temp[] = new float[9];
        //GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0, temp);
        //System.out.println(java.util.Arrays.toString(temp));

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glfwPollEvents();

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            //System.out.println("Using Program...");
            GL20.glUseProgram(progHandle);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
            GL20.glDisableVertexAttribArray(0);

            glfwSwapBuffers(window);

        }
    }

    public static void main(String[] args) {
        new FragmentTriangle().run();
    }

}