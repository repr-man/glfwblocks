@file:Suppress("unused")

import GlfwContext.Window
import org.lwjgl.glfw.GLFW.glfwInit
import java.io.PrintStream

inline fun glfw(
    errorStream: PrintStream = System.err,
    preinit: GlfwContextPreinit.() -> Unit = {},
): GlfwContext {
    // To provide type safety, we make an initialized context, return it as an
    // uninitialized context (to prevent users from calling methods that can
    // only be used after initialization), and downcast it back to an initialized
    // context after the `init` function has been called.
    return GlfwContextInitialized(errorStream).also {
        GlfwContextPreinit.preinit()
    }
}

/**
 * Sets up any windows, resources, etc. that are needed for the main loop.
 * The `setup` block returns the window that should be made current.
 *
 * @param init initialization tasks
 */
inline fun GlfwContext.init(init: GlfwContext.() -> Window): GlfwContextInitialized {
    if (!glfwInit()) error("GLFW Error: Unable to initialize GLFW")
    this.init().makeCurrent()
    return this as GlfwContextInitialized
}

inline fun GlfwContextInitialized.setup(setup: GlfwContextInitialized.() -> Unit): GlfwContextInitialized {
    setup()
    return this
}

/**
 * Runs the main loop until the current window should close or until the
 * additional condition is false.  It handles swapping buffers and polling
 * events automatically.  When the loop finishes, the context is closed.
 *
 * @param additionalCondition additional logic governing the loop
 * @param fn the loop body
 */
inline fun GlfwContextInitialized.loop(additionalCondition: () -> Boolean = {true}, fn: GlfwContextInitialized.() -> Unit = {}) {
    while (!currentWindow.shouldClose && additionalCondition()) {
        this.fn()

        currentWindow.swapBuffers()
        pollEvents()
    }
    close()
}