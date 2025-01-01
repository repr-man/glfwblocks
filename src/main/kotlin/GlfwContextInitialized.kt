@file:Suppress("NOTHING_TO_INLINE", "unused")

import org.lwjgl.glfw.GLFW
import java.io.PrintStream

/**
 * A GLFW context that has a current window and is ready for OpenGL calls.
 */
class GlfwContextInitialized(errorStream: PrintStream = System.err) : GlfwContext(errorStream) {
    /**
     * The same as [currentContext].  Exists to make code more readable.
     */
    inline val currentWindow: Window
        inline get() = Window(GLFW.glfwGetCurrentContext())
    inline val currentContext: Window
        inline get() = Window(GLFW.glfwGetCurrentContext())
    inline var swapInterval: Int
        inline get() = throw NotImplementedError("GLFW Error: Swap interval may not be retrieved via getter")
        inline set(value) = GLFW.glfwSwapInterval(value)

    inline fun isExtensionSupported(extension: String) = GLFW.glfwExtensionSupported(extension)
    inline fun getProcAddress(procName: String) = GLFW.glfwGetProcAddress(procName).takeUnless { it == 0L }


    inline var Window.clientApi: Window.Config.ClientApi
        inline get() = Window.Config.ClientApi.from(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_CLIENT_API))
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CLIENT_API, value.value)
    inline var Window.contextCreationApi: Window.Config.ContextCreationApi
        inline get() = Window.Config.ContextCreationApi.from(
            GLFW.glfwGetWindowAttrib(
                handle,
                GLFW.GLFW_CONTEXT_CREATION_API
            )
        )
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CONTEXT_CREATION_API, value.value)
    inline var Window.contextVersionMajor: Int
        inline get() = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_CONTEXT_VERSION_MAJOR)
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CONTEXT_VERSION_MAJOR, value)
    inline var Window.contextVersionMinor: Int
        inline get() = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_CONTEXT_VERSION_MINOR)
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CONTEXT_VERSION_MINOR, value)
    inline var Window.contextRevision: Int
        inline get() = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_CONTEXT_REVISION)
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CONTEXT_REVISION, value)
    inline var Window.openGLForwardCompat: Boolean
        inline get() = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_OPENGL_FORWARD_COMPAT) == 1
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_OPENGL_FORWARD_COMPAT, value.compareTo(false))
    inline var Window.contextDebug: Boolean
        inline get() = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_CONTEXT_DEBUG) == 1
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CONTEXT_DEBUG, value.compareTo(false))
    inline var Window.openGLProfile: Window.Config.OpenGLProfile
        inline get() = Window.Config.OpenGLProfile.from(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_OPENGL_PROFILE))
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_OPENGL_PROFILE, value.value)
    inline var Window.contextReleaseBehavior: Window.Config.ContextReleaseBehavior
        inline get() = Window.Config.ContextReleaseBehavior.from(
            GLFW.glfwGetWindowAttrib(
                handle,
                GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR
            )
        )
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR, value.value)
    inline var Window.contextNoError: Boolean
        inline get() = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_CONTEXT_NO_ERROR) == 1
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CONTEXT_NO_ERROR, value.compareTo(false))
    inline var Window.contextRobustness: Window.Config.ContextRobustness
        inline get() = Window.Config.ContextRobustness.from(
            GLFW.glfwGetWindowAttrib(
                handle,
                GLFW.GLFW_CONTEXT_ROBUSTNESS
            )
        )
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_CONTEXT_ROBUSTNESS, value.value)
    inline var Window.doubleBuffer: Boolean
        inline get() = GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_DOUBLEBUFFER) == 1
        inline set(value) = GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DOUBLEBUFFER, value.compareTo(false))
}