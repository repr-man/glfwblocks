@file:Suppress("NOTHING_TO_INLINE", "unused", "SpellCheckingInspection", "OVERRIDE_BY_INLINE")

import org.lwjgl.*
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.io.*

/**
 * Contains the state of the structured GLFW system.
 *
 * @param errorStream the error reporting stream
 */
open class GlfwContext(
    errorStream: PrintStream = System.err
) : AutoCloseable {
    /**
     * A list of all available windows.
     *
     * NOTE: This is only public because Kotlin cannot inline private variables.
     *       It should NOT be modified directly!
     */
    val windows = arrayListOf<Window>()

    ////  Lifecycle Methods  ////

    init {
        GLFWErrorCallback.create { code, message ->
            errorStream.println("GLFW Error($code): ${MemoryUtil.memASCII(message)}")
        }.set()
    }

    /**
     * Terminates the context and cleans up resources.
     */
    override fun close() {
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    ////  Event Stuff  ////

    inline fun pollEvents() = glfwPollEvents()
    inline fun waitEvents() = glfwWaitEvents()
    inline fun waitEvents(timeout: Double) = glfwWaitEventsTimeout(timeout)
    inline fun postEmptyEvent() = glfwPostEmptyEvent()

    enum class JoystickEvent {
        CONNECTED,
        DISCONNECTED;
        inline val value
            inline get() = GLFW_CONNECTED + ordinal
        companion object {
            inline fun from(value: Int) = when (value) {
                GLFW_CONNECTED -> CONNECTED
                GLFW_DISCONNECTED -> DISCONNECTED
                else -> throw NotImplementedError("GLFW Error: Event $value is not a joystick event")
            }
        }
    }


    ////  Monitor Stuff  ////

    inline val primaryMonitor: Monitor
        inline get() = Monitor(glfwGetPrimaryMonitor())

    inline val monitors: Sequence<Monitor>
        inline get() = sequence {
            val tmp = glfwGetMonitors() ?: return@sequence
            while (tmp.hasRemaining())
                yield(Monitor(tmp.get()))
        }

    @JvmInline
    value class Monitor(val handle: Long) {
        inline val pos: Pair<Int, Int>
            inline get() = stackPush().use { stack ->
                val x = stack.mallocInt(1)
                val y = stack.mallocInt(1)
                glfwGetMonitorPos(handle, x, y)
                x.get(0) to y.get(0)
            }
        inline val workarea: Pair<Pair<Int, Int>, Pair<Int, Int>>
            inline get() = stackPush().use { stack ->
                val x = stack.mallocInt(1)
                val y = stack.mallocInt(1)
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                glfwGetMonitorWorkarea(handle, x, y, w, h)
                (x.get(0) to y.get(0)) to (w.get(0) to h.get(0))
            }
        inline val physicalSize: Pair<Int, Int>
            inline get() = stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                glfwGetMonitorPhysicalSize(handle, w, h)
                w.get(0) to h.get(0)
            }
        inline val contentScale: Pair<Int, Int>
            inline get() = stackPush().use { stack ->
                val x = stack.mallocInt(1)
                val y = stack.mallocInt(1)
                glfwGetMonitorPos(handle, x, y)
                x.get(0) to y.get(0)
            }
        inline val name: String?
            inline get() = glfwGetMonitorName(handle)
        inline var userPointer: Long
            inline get() = glfwGetMonitorUserPointer(handle)
            inline set(value) = glfwSetMonitorUserPointer(handle, value)
        inline val videoModes: GLFWVidMode.Buffer
            inline get() = glfwGetVideoModes(handle)!!
        inline val videoMode: GLFWVidMode?
            inline get() = glfwGetVideoMode(handle)
        inline var gammaRamp: GLFWGammaRamp?
            inline get() = glfwGetGammaRamp(handle)
            inline set(value) = glfwSetGammaRamp(handle, value!!)

        inline fun setCallback(fn: GLFWMonitorCallbackI) = glfwSetMonitorCallback(fn)
        inline fun setCallback(noinline fn: (Long, Int) -> Unit) = glfwSetMonitorCallback(fn)
        inline fun setGamma(gamma: Float) = glfwSetGamma(handle, gamma)
    }


    ////  Window Stuff  ////

    fun window(
        title: String,
        width: Int = 640,
        height: Int = 480,
        monitor: Monitor? = null,
        share: Window? = null,
        config: Window.Config.() -> Unit = {}
    ): Window {
        glfwDefaultWindowHints()
        val cfg = Window.Config()
        cfg.config()
        val w = glfwCreateWindow(width, height, title, monitor?.handle ?: NULL, share?.handle ?: NULL)
        if (w == NULL) error("GLFW Error: Unable to create window (title: $title)!")
        val win = Window(w)
        cfg.applyCallbacks(win)
        windows.add(win)

        return win
    }

    @JvmInline
    value class Window(val handle: Long) {
        inline var shouldClose: Boolean
            inline get() = glfwWindowShouldClose(handle)
            inline set(value) { glfwSetWindowShouldClose(handle, value)}
        inline var title: String?
            inline get() = glfwGetWindowTitle(handle)
            inline set(value) { glfwSetWindowTitle(handle, value!!) }
        inline var pos: Pair<Int, Int>
            inline get() = stackPush().use { stack ->
                val x = stack.mallocInt(1)
                val y = stack.mallocInt(1)
                glfwGetWindowPos(handle, x, y)
                x.get(0) to y.get(0)
            }
            inline set(value) { glfwSetWindowPos(handle, value.first, value.second) }
        inline var size: Pair<Int, Int>
            inline get() = stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                glfwGetWindowSize(handle, w, h)
                w.get(0) to h.get(0)
            }
            inline set(value) { glfwSetWindowSize(handle, value.first, value.second) }
        inline var framebufferSize: Pair<Int, Int>
            inline get() = stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                glfwGetFramebufferSize(handle, w, h)
                w.get(0) to h.get(0)
            }
            inline set(value) { glfwGetFramebufferSize(handle, intArrayOf(value.first), intArrayOf(value.second)) }
        inline val frameSize: Pair<Pair<Int, Int>, Pair<Int, Int>>
            inline get() = stackPush().use { stack ->
                val x = stack.mallocInt(1)
                val y = stack.mallocInt(1)
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                glfwGetWindowFrameSize(handle, x, y, w, h)
                (x.get(0) to y.get(0)) to (w.get(0) to h.get(0))
            }
        inline val contentScale: Pair<Float, Float>
            inline get() = stackPush().use { stack ->
                val x = stack.mallocFloat(1)
                val y = stack.mallocFloat(1)
                glfwGetWindowContentScale(handle, x, y)
                x.get(0) to y.get(0)
            }
        inline var opacity: Float
            inline get() = glfwGetWindowOpacity(handle)
            inline set(value) { glfwSetWindowOpacity(handle, value) }
        inline val monitor: Monitor
            inline get() = Monitor(glfwGetWindowMonitor(handle))
        inline var userPointer: Long
            inline get() = glfwGetWindowUserPointer(handle)
            inline set(value) = glfwSetWindowUserPointer(handle, value)

        inline fun iconify() = glfwIconifyWindow(handle)
        inline fun restore() = glfwRestoreWindow(handle)
        inline fun maximize() = glfwMaximizeWindow(handle)
        inline fun show() = glfwShowWindow(handle)
        inline fun hide() = glfwHideWindow(handle)
        inline fun focus() = glfwFocusWindow(handle)
        inline fun requestAttention() = glfwRequestWindowAttention(handle)

        inline var focused: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_FOCUSED) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_FOCUSED, value.compareTo(false))
        inline var iconified: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_ICONIFIED) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_ICONIFIED, value.compareTo(false))
        inline var maximized: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_MAXIMIZED) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_MAXIMIZED, value.compareTo(false))
        inline var hovered: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_HOVERED) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_HOVERED, value.compareTo(false))
        inline var visible: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_VISIBLE) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_VISIBLE, value.compareTo(false))
        inline var resizable: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_RESIZABLE) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_RESIZABLE, value.compareTo(false))
        inline var decorated: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_DECORATED) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_DECORATED, value.compareTo(false))
        inline var autoIconify: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_AUTO_ICONIFY) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_AUTO_ICONIFY, value.compareTo(false))
        inline var floating: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_FLOATING) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_FLOATING, value.compareTo(false))
        inline var transparentFramebuffer: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_TRANSPARENT_FRAMEBUFFER) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_TRANSPARENT_FRAMEBUFFER, value.compareTo(false))
        inline var focusOnShow: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_FOCUS_ON_SHOW) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_FOCUS_ON_SHOW, value.compareTo(false))
        inline var mousePassthrough: Boolean
            inline get() = glfwGetWindowAttrib(handle, GLFW_MOUSE_PASSTHROUGH) == 1
            inline set(value) = glfwSetWindowAttrib(handle, GLFW_MOUSE_PASSTHROUGH, value.compareTo(false))

        inline fun setIcon(images: GLFWImage.Buffer) { glfwSetWindowIcon(handle, images) }
        inline fun setSizeLimits(minWidth: Int, minHeight: Int, maxWidth: Int, maxHeight: Int) {
            glfwSetWindowSizeLimits(handle, minWidth, minHeight, maxWidth, maxHeight)
        }
        inline fun setAspectRatio(numerator: Int, denominator: Int) = glfwSetWindowAspectRatio(handle, numerator, denominator)
        inline fun setMonitor(monitor: Monitor, x: Int, y: Int, width: Int, height: Int, refreshRate: Int) {
            glfwSetWindowMonitor(handle, monitor.handle, x, y, width, height, refreshRate)
        }


        inline fun swapBuffers() = glfwSwapBuffers(handle)

        inline val key
            inline get() = object : WindowKey {
                override inline operator fun get(key: Key) = ButtonState.from(glfwGetKey(handle, key.token))
            }

        inline val mouseButton
            inline get() = object : WindowMouseButton {
                override inline operator fun get(button: MouseButton) = ButtonState.from(glfwGetMouseButton(handle, button.ordinal))
            }

        inline var cursorPos
            inline get() = stackPush().use { stack ->
                val x = stack.mallocDouble(1)
                val y = stack.mallocDouble(1)
                glfwGetCursorPos(handle, x, y)
                x.get(0) to y.get(0)
            }
            inline set(value) { glfwSetCursorPos(handle, value.first, value.second) }

        inline var cursorInputMode: CursorInputMode
            inline get() = CursorInputMode.from(glfwGetInputMode(handle, GLFW_CURSOR))
            inline set(value) { glfwSetInputMode(handle, GLFW_CURSOR, value.value())}
        inline var stickyKeysInputMode: Boolean
            inline get() = glfwGetInputMode(handle, GLFW_STICKY_KEYS) == 1
            inline set(value) { glfwSetInputMode(handle, GLFW_STICKY_KEYS, value.compareTo(false))}
        inline var stickyMouseButtonsInputMode: Boolean
            inline get() = glfwGetInputMode(handle, GLFW_STICKY_MOUSE_BUTTONS) == 1
            inline set(value) { glfwSetInputMode(handle, GLFW_STICKY_MOUSE_BUTTONS, value.compareTo(false))}
        inline var lockKeyModsInputMode: Boolean
            inline get() = glfwGetInputMode(handle, GLFW_LOCK_KEY_MODS) == 1
            inline set(value) { glfwSetInputMode(handle, GLFW_LOCK_KEY_MODS, value.compareTo(false))}
        inline var rawMouseMotionInputMode: Boolean
            inline get() = glfwGetInputMode(handle, GLFW_RAW_MOUSE_MOTION) == 1
            inline set(value) { glfwSetInputMode(handle, GLFW_RAW_MOUSE_MOTION, value.compareTo(false))}

        inline var clipboardString
            inline get() = glfwGetClipboardString(handle)
            inline set(value) { glfwSetClipboardString(handle, value!!) }

        inline var cursor: Cursor?
            inline get() = throw NotImplementedError("GLFW Error: Cursor may not be retrieved via getter")
            inline set(value) { glfwSetCursor(handle, value?.cursor ?: NULL) }


        interface WindowKey {
            operator fun get(key: Key): ButtonState
        }

        interface WindowMouseButton {
            operator fun get(button: MouseButton): ButtonState
        }

        enum class CursorInputMode {
            NORMAL,
            HIDDEN,
            DISABLED,
            CAPTURED;
            inline fun value() = GLFW_CURSOR_NORMAL + ordinal
            companion object {
                inline fun from(int: Int) = when (int - GLFW_CURSOR_NORMAL) {
                    NORMAL.ordinal -> NORMAL
                    HIDDEN.ordinal -> HIDDEN
                    DISABLED.ordinal -> DISABLED
                    CAPTURED.ordinal -> CAPTURED
                    else -> throw NotImplementedError("GLFW Error: Cursor input mode $int is invalid")
                }
            }
        }

        class Config {
            inline var resizable: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_RESIZABLE, value.compareTo(false))}
            inline var visible: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_VISIBLE, value.compareTo(false))}
            inline var decorated: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_DECORATED, value.compareTo(false))}
            inline var focused: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_FOCUSED, value.compareTo(false))}
            inline var autoIconify: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_AUTO_ICONIFY, value.compareTo(false))}
            inline var floating: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_FLOATING, value.compareTo(false))}
            inline var maximized: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_MAXIMIZED, value.compareTo(false))}
            inline var centerCursor: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CENTER_CURSOR, value.compareTo(false))}
            inline var transparentFramebuffer: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, value.compareTo(false))}
            inline var focusOnShow: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_FOCUS_ON_SHOW, value.compareTo(false))}
            inline var scaleToMonitor: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_SCALE_TO_MONITOR, value.compareTo(false))}
            inline var scaleFramebuffer: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_SCALE_FRAMEBUFFER, value.compareTo(false))}
            inline var mousePassthrough: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_MOUSE_PASSTHROUGH, value.compareTo(false))}
            inline var x: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_POSITION_X, value)}
            inline var y: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_POSITION_Y, value)}
            inline var redBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_RED_BITS, value)}
            inline var greenBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_GREEN_BITS, value)}
            inline var blueBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_BLUE_BITS, value)}
            inline var alphaBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_ALPHA_BITS, value)}
            inline var depthBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_DEPTH_BITS, value)}
            inline var stencilBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_STENCIL_BITS, value)}
            inline var accumRedBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_ACCUM_RED_BITS, value)}
            inline var accumGreenBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_ACCUM_GREEN_BITS, value)}
            inline var accumBlueBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_ACCUM_BLUE_BITS, value)}
            inline var accumAlphaBits: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_ACCUM_ALPHA_BITS, value)}
            inline var auxBuffers: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_AUX_BUFFERS, value)}
            inline var samples: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_SAMPLES, value)}
            inline var refreshRate: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_REFRESH_RATE, value)}
            inline var stereo: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_STEREO, value.compareTo(false))}
            inline var srgbCapable: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_SRGB_CAPABLE, value.compareTo(false))}
            inline var doubleBuffer: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_DOUBLEBUFFER, value.compareTo(false))}
            inline var clientApi: ClientApi
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CLIENT_API, value.value)}
            inline var contextCreationApi: ContextCreationApi
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CLIENT_API, value.value)}
            inline var contextVersionMajor: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, value)}
            inline var contextVersionMinor: Int
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, value)}
            inline var contextRobustness: ContextRobustness
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CLIENT_API, value.value)}
            inline var contextReleaseBehavior: ContextReleaseBehavior
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CLIENT_API, value.value)}
            inline var openglForwardCompat: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, value.compareTo(false))}
            inline var contextDebug: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CONTEXT_DEBUG, value.compareTo(false))}
            inline var openglProfile: OpenGLProfile
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_CLIENT_API, value.value)}
            inline var win32KeyboardMenu: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_WIN32_KEYBOARD_MENU, value.compareTo(false))}
            inline var win32ShowDefault: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_WIN32_SHOWDEFAULT, value.compareTo(false))}
            inline var cocoaFrameName: String
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHintString(GLFW_COCOA_FRAME_NAME, value)}
            inline var cocoaGraphicsSwitching: Boolean
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHint(GLFW_COCOA_GRAPHICS_SWITCHING, value.compareTo(false))}
            inline var waylandAppId: String
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHintString(GLFW_WAYLAND_APP_ID, value)}
            inline var x11ClassName: String
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHintString(GLFW_X11_CLASS_NAME, value)}
            inline var x11InstanceName: String
                inline get() = throw NotImplementedError("GLFW Error: Window hints may not be retrieved via getter")
                inline set(value) { glfwWindowHintString(GLFW_X11_INSTANCE_NAME, value)}

            enum class ClientApi {
                OPENGL,
                OPENGL_ES,
                NO_API;
                inline val value
                    inline get() = if (this == NO_API) 0 else GLFW_OPENGL_API + ordinal
                companion object {
                    inline fun from(value: Int) = when (value) {
                        GLFW_OPENGL_API -> OPENGL
                        GLFW_OPENGL_ES_API -> OPENGL_ES
                        GLFW_NO_API -> NO_API
                        else -> throw NotImplementedError("GLFW Error: Client API value $value is invalid")
                    }
                }
            }
            enum class ContextCreationApi {
                NATIVE,
                EGL,
                OSMESA;
                inline val value
                    inline get() = GLFW_NATIVE_CONTEXT_API + ordinal
                companion object {
                    inline fun from(value: Int) = when (value) {
                        GLFW_NATIVE_CONTEXT_API -> NATIVE
                        GLFW_EGL_CONTEXT_API -> EGL
                        GLFW_OSMESA_CONTEXT_API -> OSMESA
                        else -> throw NotImplementedError("GLFW Error: Context creation API value $value is invalid")
                    }
                }
            }
            enum class ContextRobustness {
                NO_ROBUSTNESS,
                NO_RESET_NOTIFICATION,
                LOSE_CONTEXT_ON_RESET;
                inline val value
                    inline get() = if (this == NO_ROBUSTNESS) 0 else 200704 + ordinal
                companion object {
                    inline fun from(value: Int) = when (value) {
                        GLFW_NO_ROBUSTNESS -> NO_ROBUSTNESS
                        GLFW_NO_RESET_NOTIFICATION -> NO_RESET_NOTIFICATION
                        GLFW_LOSE_CONTEXT_ON_RESET -> LOSE_CONTEXT_ON_RESET
                        else -> throw NotImplementedError("GLFW Error: Context robustness value $value is invalid")
                    }
                }
            }
            enum class ContextReleaseBehavior {
                ANY,
                FLUSH,
                NONE;
                inline val value
                    inline get() = if (this == ANY) 0 else 217088 + ordinal
                companion object {
                    inline fun from(value: Int) = when (value) {
                        GLFW_ANY_RELEASE_BEHAVIOR -> ANY
                        GLFW_RELEASE_BEHAVIOR_FLUSH -> FLUSH
                        GLFW_RELEASE_BEHAVIOR_NONE -> NONE
                        else -> throw NotImplementedError("GLFW Error: Context release behavior value $value is invalid")
                    }
                }
            }
            enum class OpenGLProfile {
                ANY,
                CORE,
                COMPAT;
                inline val value
                    inline get() = if (this == ANY) 0 else 204800 + ordinal
                companion object {
                    inline fun from(value: Int) = when (value) {
                        GLFW_OPENGL_ANY_PROFILE -> ANY
                        GLFW_OPENGL_CORE_PROFILE -> CORE
                        GLFW_OPENGL_COMPAT_PROFILE -> COMPAT
                        else -> throw NotImplementedError("GLFW Error: OpenGL profile value $value is invalid")
                    }
                }
            }

            var onMove:                     GLFWWindowPosCallbackI? = null
            var onResize:                   GLFWWindowSizeCallbackI? = null
            var onClose:                    GLFWWindowCloseCallbackI? = null
            var onRefresh:                  GLFWWindowRefreshCallbackI? = null
            var onFocus:                    GLFWWindowFocusCallbackI? = null
            var onIconify:                  GLFWWindowIconifyCallbackI? = null
            var onMaximize:                 GLFWWindowMaximizeCallbackI? = null
            var onFramebufferSizeChange:    GLFWFramebufferSizeCallbackI? = null
            var onContentScaleChange:       GLFWWindowContentScaleCallbackI? = null

            var onKey:          GLFWKeyCallbackI? = null
            var onChar:         GLFWCharCallbackI? = null
            var onCharMods:     GLFWCharModsCallbackI? = null
            var onMouseButton:  GLFWMouseButtonCallbackI? = null
            var onCursorMove:   GLFWCursorPosCallbackI? = null
            var onCursorEnter:  GLFWCursorEnterCallbackI? = null
            var onScroll:       GLFWScrollCallbackI? = null
            var onDrop:         GLFWDropCallbackI? = null

            inline fun applyCallbacks(window: Window) {
                onMove?.let { glfwSetWindowPosCallback(window.handle, it) }
                onResize?.let { glfwSetWindowSizeCallback(window.handle, it) }
                onClose?.let { glfwSetWindowCloseCallback(window.handle, it) }
                onRefresh?.let { glfwSetWindowRefreshCallback(window.handle, it) }
                onFocus?.let { glfwSetWindowFocusCallback(window.handle, it) }
                onIconify?.let { glfwSetWindowIconifyCallback(window.handle, it) }
                onMaximize?.let { glfwSetWindowMaximizeCallback(window.handle, it) }
                onFramebufferSizeChange?.let { glfwSetFramebufferSizeCallback(window.handle, it) }
                onContentScaleChange?.let { glfwSetWindowContentScaleCallback(window.handle, it) }

                onKey?.let { glfwSetKeyCallback(window.handle, it) }
                onChar?.let { glfwSetCharCallback(window.handle, it) }
                onCharMods?.let { glfwSetCharModsCallback(window.handle, it) }
                onMouseButton?.let { glfwSetMouseButtonCallback(window.handle, it) }
                onCursorMove?.let { glfwSetCursorPosCallback(window.handle, it) }
                onCursorEnter?.let { glfwSetCursorEnterCallback(window.handle, it) }
                onScroll?.let { glfwSetScrollCallback(window.handle, it) }
                onDrop?.let { glfwSetDropCallback(window.handle, it) }
            }
        }

    }

    // Window callback setter functions
    // Yes, we have to use objects instead of lambdas because the GLFW*CallbackI
    // interfaces are not SAMs.

    inline fun Window.Config.onMove(crossinline callback: (window: Window, xPos: Int, yPos: Int) -> Unit) {
        onMove = object : GLFWWindowPosCallbackI {
            override fun invoke(window: Long, xPos: Int, yPos: Int) {
                callback(Window(window), xPos, yPos)
            }
        }
    }

    inline fun Window.Config.onResize(crossinline callback: (window: Window, width: Int, height: Int) -> Unit) {
        onResize = object : GLFWWindowSizeCallbackI {
            override fun invoke(window: Long, width: Int, height: Int) {
                callback(Window(window), width, height)
            }
        }
    }

    inline fun Window.Config.onClose(crossinline callback: (window: Window) -> Unit) {
        onClose = object : GLFWWindowCloseCallbackI {
            override fun invoke(window: Long) {
                callback(Window(window))
            }
        }
    }

    inline fun Window.Config.onRefresh(crossinline callback: (window: Window) -> Unit) {
        onRefresh = object : GLFWWindowRefreshCallbackI {
            override fun invoke(window: Long) {
                callback(Window(window))
            }
        }
    }

    inline fun Window.Config.onFocus(crossinline callback: (window: Window, focused: Boolean) -> Unit) {
        onFocus = object : GLFWWindowFocusCallbackI {
            override fun invoke(window: Long, focused: Boolean) {
                callback(Window(window), focused)
            }
        }
    }

    inline fun Window.Config.onIconify(crossinline callback: (window: Window, iconified: Boolean) -> Unit) {
        onIconify = object : GLFWWindowIconifyCallbackI {
            override fun invoke(window: Long, iconified: Boolean) {
                callback(Window(window), iconified)
            }
        }
    }

    inline fun Window.Config.onMaximize(crossinline callback: (window: Window, maximized: Boolean) -> Unit) {
        onMaximize = object : GLFWWindowMaximizeCallbackI {
            override fun invoke(window: Long, maximized: Boolean) {
                callback(Window(window), maximized)
            }
        }
    }

    inline fun Window.Config.onFramebufferSizeChange(crossinline callback: (window: Window, width: Int, height: Int) -> Unit) {
        onFramebufferSizeChange = object : GLFWFramebufferSizeCallbackI {
            override fun invoke(window: Long, width: Int, height: Int) {
                callback(Window(window), width, height)
            }
        }
    }

    inline fun Window.Config.onContentScaleChange(crossinline callback: (window: Window, xScale: Float, yScale: Float) -> Unit) {
        onContentScaleChange = object : GLFWWindowContentScaleCallbackI {
            override fun invoke(window: Long, xScale: Float, yScale: Float) {
                callback(Window(window), xScale, yScale)
            }
        }
    }

    inline fun Window.Config.onKey(crossinline callback: (window: Window, key: Key, scancode: Int, action: KeyAction, mods: ModFlags) -> Unit) {
        onKey = object : GLFWKeyCallbackI {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                callback(Window(window), Key.from(key), scancode, KeyAction.from(action), ModFlags(mods))
            }
        }
    }

    inline fun Window.Config.onChar(crossinline callback: (window: Window, codepoint: Int) -> Unit) {
        onChar = object : GLFWCharCallbackI {
            override fun invoke(window: Long, codepoint: Int) {
                callback(Window(window), codepoint)
            }
        }
    }

    inline fun Window.Config.onCharMods(crossinline callback: (window: Window, codepoint: Int, mods: ModFlags) -> Unit) {
        onCharMods = object : GLFWCharModsCallbackI {
            override fun invoke(window: Long, codepoint: Int, mods: Int) {
                callback(Window(window), codepoint, ModFlags(mods))
            }
        }
    }

    inline fun Window.Config.onMouseButton(crossinline callback: (window: Window, button: MouseButton, action: ButtonState, mods: ModFlags) -> Unit) {
        onMouseButton = object : GLFWMouseButtonCallbackI {
            override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
                callback(Window(window), MouseButton.from(button), ButtonState.from(action), ModFlags(mods))
            }
        }
    }

    inline fun Window.Config.onCursorMove(crossinline callback: (window: Window, xPos: Double, yPos: Double) -> Unit) {
        onCursorMove = object : GLFWCursorPosCallbackI {
            override fun invoke(window: Long, xPos: Double, yPos: Double) {
                callback(Window(window), xPos, yPos)
            }
        }
    }

    inline fun Window.Config.onCursorEnter(crossinline callback: (window: Window, entered: Boolean) -> Unit) {
        onCursorEnter = object : GLFWCursorEnterCallbackI {
            override fun invoke(window: Long, entered: Boolean) {
                callback(Window(window), entered)
            }
        }
    }

    inline fun Window.Config.onScroll(crossinline callback: (window: Window, xOffset: Double, yOffset: Double) -> Unit) {
        onScroll = object : GLFWScrollCallbackI {
            override fun invoke(window: Long, xOffset: Double, yOffset: Double) {
                callback(Window(window), xOffset, yOffset)
            }
        }
    }

    inline fun Window.Config.onDrop(crossinline callback: (receiver: Window, droppedPaths: Sequence<String>) -> Unit) {
        onDrop = object : GLFWDropCallbackI {
            override fun invoke(window: Long, count: Int, names: Long) {
                val pathPtrBuf = PointerBuffer.create(names, count)
                callback(Window(window), sequence {
                    while (pathPtrBuf.hasRemaining())
                        yield(pathPtrBuf.stringUTF8)
                })
            }
        }
    }


    // Window methods that affect GlfwContext
    inline fun Window.makeCurrent() = glfwMakeContextCurrent(handle)

    inline fun Window.destroy() {
        if (!windows.remove(this)) error("GLFW Error: Unregistered window destroyed")
        glfwDestroyWindow(handle)
    }



    inline val rawMouseMotionSupported: Boolean
        inline get() = glfwRawMouseMotionSupported()

    inline var time
        inline get() = glfwGetTime()
        inline set(value) { glfwSetTime(value) }
    inline val timerValue
        inline get() = glfwGetTimerValue()
    inline val timerFrequency
        inline get() = glfwGetTimerFrequency()
}