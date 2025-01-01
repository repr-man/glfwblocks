# glfwblocks
A Kotlin-ified wrapper over GLFW.
It's _the easiest_ way to get a window and OpenGL context up and running.

## How to Use
1. Clone the project.
2. Modify any settings in [build.gradle.kts]() (e.g. Java toolchain version).
3. Run `./gradlew publishToMavenLocal`
4. Import into your own project.

## Show Me Code
Turn this:
```kotlin
fun main() {
    GLFWErrorCallback.createPrint(System.err).set()
    if (!glfwInit())
        throw new IllegalStateException ("Unable to initialize GLFW")
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
    val window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL)
    if (window == NULL)
        throw new RuntimeException ("Failed to create the GLFW window")
    glfwSetKeyCallback(window) {window, key, scancode, action, mods ->
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
            glfwSetWindowShouldClose(window, true)
    }
    stackPush().use { stack ->
        val pWidth = stack.mallocInt (1)
        val pHeight = stack.mallocInt (1)
        glfwGetWindowSize(window, pWidth, pHeight)
        val vidmode = glfwGetVideoMode (glfwGetPrimaryMonitor())
        glfwSetWindowPos(
            window,
            (vidmode.width() - pWidth.get(0)) / 2,
            (vidmode.height() - pHeight.get(0)) / 2
        )
    }
    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwShowWindow(window)
    GL.createCapabilities()
    glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    while (!glfwWindowShouldClose(window)) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glfwSwapBuffers(window)
        glfwPollEvents()
    }
    glfwFreeCallbacks(window)
    glfwDestroyWindow(window)
    glfwTerminate()
    glfwSetErrorCallback(null).free()
}
```

Into this:
```kotlin
fun main() {
    glfw().init {
        val w = window("Hello World!", 300, 300) {
            visible = false
            resizable = true
            onKey { window, key, _, action, _ ->
                if (key == Key.ESCAPE && action == KeyAction.RELEASE)
                    window.shouldClose = true
            }
        }
        val vidmode = primaryMonitor.videoMode
        val (width, height) = w.size
        w.pos = ((vidmode!!.width() - width) / 2) to ((vidmode.height() - height) / 2)
        w
    }.setup {
        swapInterval = 1
        currentContext.show()
        GL.createCapabilities()
        glClearColor(1f, 0f, 0f, 0f)
    }.loop {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }
}
```