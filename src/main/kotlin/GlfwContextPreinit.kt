@file:Suppress("NOTHING_TO_INLINE", "unused")

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWAllocator
import org.lwjgl.system.MemoryStack

object GlfwContextPreinit {
    inline val version: Triple<Int, Int, Int>
        inline get() = MemoryStack.stackPush().use { stack ->
            val major = stack.mallocInt(1)
            val minor = stack.mallocInt(1)
            val rev = stack.mallocInt(1)
            GLFW.glfwGetVersion(major, minor, rev)
            Triple(major.get(0), minor.get(0), rev.get(0))
        }

    inline val versionString: String
        inline get() = GLFW.glfwGetVersionString()

    /**
     * Specifies the platform to use for windowing and input.  The default
     * value is `GLFW_ANY_PLATFORM`, which will choose any platform the library
     * includes support for except for the Null backend.
     */
    inline var platform: Platform
        inline get() = throw NotImplementedError("GLFW Error: Hints may not be retrieved via getter")
        inline set(value) {
            GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, value.value)
        }

    /**
     * Specifies whether to also expose joystick hats as buttons, for
     * compatibility with earlier versions of GLFW that did not have
     * glfwGetJoystickHats.
     */
    inline var joystickHatsAsButtons: Boolean
        inline get() = throw NotImplementedError("GLFW Error: Hints may not be retrieved via getter")
        inline set(value) {
            GLFW.glfwWindowHint(GLFW.GLFW_JOYSTICK_HAT_BUTTONS, value.compareTo(false))
        }

    /**
     * Specifies the platform type (rendering backend) to request when using
     * OpenGL ES and EGL via ANGLE. If the requested platform type is
     * unavailable, ANGLE will use its default.
     *
     * The ANGLE platform type is specified via the `EGL_ANGLE_platform_angle`
     * extension. This extension is not used if this hint is
     * `GLFW_ANGLE_PLATFORM_TYPE_NONE`, which is the default value.
     */
    inline var anglePlatform: AnglePlatform
        inline get() = throw NotImplementedError("GLFW Error: Hints may not be retrieved via getter")
        inline set(value) {
            GLFW.glfwWindowHint(GLFW.GLFW_ANGLE_PLATFORM_TYPE, value.value)
        }

    /**
     * Specifies whether to set the current directory to the application to the
     * Contents/Resources subdirectory of the application's bundle, if present.
     */
    inline var cocoaChdirToBundleSubdir: Boolean
        inline get() = throw NotImplementedError("GLFW Error: Hints may not be retrieved via getter")
        inline set(value) {
            GLFW.glfwWindowHint(GLFW.GLFW_COCOA_CHDIR_RESOURCES, value.compareTo(false))
        }

    /**
     * Specifies whether to create the menu bar and dock icon when GLFW is
     * initialized. This applies whether the menu bar is created from a nib or
     * manually by GLFW.
     */
    inline var cocoaCreateMenubar: Boolean
        inline get() = throw NotImplementedError("GLFW Error: Hints may not be retrieved via getter")
        inline set(value) {
            GLFW.glfwWindowHint(GLFW.GLFW_COCOA_MENUBAR, value.compareTo(false))
        }

    /**
     * Specifies whether to use libdecor for window decorations where available.
     */
    inline var waylandUseLibdecor: Boolean
        inline get() = throw NotImplementedError("GLFW Error: Hints may not be retrieved via getter")
        inline set(value) {
            GLFW.glfwWindowHint(GLFW.GLFW_WAYLAND_LIBDECOR, GLFW.GLFW_WAYLAND_DISABLE_LIBDECOR - value.compareTo(false))
        }

    /**
     * Specifies whether to prefer the `VK_KHR_xcb_surface` extension for
     * creating Vulkan surfaces, or whether to use the `VK_KHR_xlib_surface`
     * extension.
     */
    inline var x11UseXcbForVulkanSurfaces: Boolean
        inline get() = throw NotImplementedError("GLFW Error: Hints may not be retrieved via getter")
        inline set(value) {
            GLFW.glfwWindowHint(GLFW.GLFW_X11_XCB_VULKAN_SURFACE, value.compareTo(false))
        }

    inline fun initAllocator(allocator: GLFWAllocator?) {
        GLFW.glfwInitAllocator(allocator)
        MemoryStack.stackPush().use { stack ->
            val msg = stack.mallocPointer(1)
            val code = GLFW.glfwGetError(msg)
            if (msg.get(0) > 0) {
                error("GLFW Error($code): ${msg.stringUTF8}")
            }
        }
    }

    inline fun terminate() = GLFW.glfwTerminate()

    enum class Platform {
        ANY,
        WIN32,
        COCOA,
        WAYLAND,
        X11,
        NULL;
        inline val value
            inline get() = 0x60000 + ordinal
        inline val isSupported
            inline get() = GLFW.glfwPlatformSupported(this.value)
    }

    enum class AnglePlatform {
        NONE,
        OPENGL,
        OPENGLES,
        D3D9,
        D3D11,
        VULKAN,
        METAL;
        inline val value
            inline get() = 225281 + ordinal
    }
}