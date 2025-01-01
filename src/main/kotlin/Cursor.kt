@file:Suppress("NOTHING_TO_INLINE", "unused")

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWImage

@JvmInline
value class Cursor(val cursor: Long) : AutoCloseable {
    constructor(image: GLFWImage, xHotspot: Int, yHotspot: Int) : this(GLFW.glfwCreateCursor(image, xHotspot, yHotspot))

    inline fun destroy() = GLFW.glfwDestroyCursor(cursor)
    override fun close() = destroy()

    companion object {
        inline val ARROW
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR))
        inline val IBEAM
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR))
        inline val CROSSHAIR
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR))
        inline val POINTING_HAND
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR))
        inline val RESIZE_EW
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_EW_CURSOR))
        inline val RESIZE_NS
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NS_CURSOR))
        inline val RESIZE_NWSE
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR))
        inline val RESIZE_NESW
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR))
        inline val RESIZE_ALL
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR))
        inline val NOT_ALLOWED
            inline get() = Cursor(GLFW.glfwCreateStandardCursor(GLFW.GLFW_NOT_ALLOWED_CURSOR))
    }
}