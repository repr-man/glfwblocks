@file:Suppress("NOTHING_TO_INLINE", "unused")

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWGamepadState
import org.lwjgl.glfw.GLFWJoystickCallback
import java.nio.ByteBuffer

enum class Joystick {
    `1`,
    `2`,
    `3`,
    `4`,
    `5`,
    `6`,
    `7`,
    `8`,
    `9`,
    `10`,
    `11`,
    `12`,
    `13`,
    `14`,
    `15`,
    `16`;

    companion object {
        inline fun from(value: Int) = when (value) {
            0 -> `1`
            1 -> `2`
            2 -> `3`
            3 -> `4`
            4 -> `5`
            5 -> `6`
            6 -> `7`
            7 -> `8`
            8 -> `9`
            9 -> `10`
            10 -> `11`
            11 -> `12`
            12 -> `13`
            13 -> `14`
            14 -> `15`
            15 -> `16`
            else -> throw NotImplementedError("GLFW Error: Joystick id $value is invalid")
        }
    }

    inline val isPresent
        inline get() = GLFW.glfwJoystickPresent(ordinal)

    inline val axes
        inline get() = GLFW.glfwGetJoystickAxes(ordinal)

    inline val buttons
        inline get() = GLFW.glfwGetJoystickButtons(ordinal)?.array()?.map(ButtonState::from)

    inline val hats
        inline get() = GLFW.glfwGetJoystickHats(ordinal)?.array()?.map(HatState::from)

    inline val joystickName
        inline get() = GLFW.glfwGetJoystickName(ordinal)

    inline val guid
        inline get() = GLFW.glfwGetJoystickGUID(ordinal)

    inline var userPointer
        inline get() = GLFW.glfwGetJoystickUserPointer(ordinal)
        inline set(value) {
            GLFW.glfwSetJoystickUserPointer(ordinal, value)
        }

    inline val isGamepad
        inline get() = GLFW.glfwJoystickIsGamepad(ordinal)

    inline fun updateGamepadMappings(string: String) =
        GLFW.glfwUpdateGamepadMappings(ByteBuffer.wrap(string.toByteArray()))

    inline val gamepadName
        inline get() = GLFW.glfwGetGamepadName(ordinal)

    inline val gamepadState: GLFWGamepadState?
        inline get() {
            val buf = ByteBuffer.allocate(21)
            val gps = GLFWGamepadState(buf)
            return if (GLFW.glfwGetGamepadState(ordinal, gps)) gps else null
        }

    inline var onEvent: GLFWJoystickCallback?
        inline get() = throw NotImplementedError("GLFW Error: Joystick callback may not be retrieved via getter")
        inline set(value) {
            GLFW.glfwSetJoystickCallback(value)
        }

    inline fun onEvent(crossinline callback: (joystick: Joystick, event: GlfwContext.JoystickEvent) -> Unit) {
        GLFW.glfwSetJoystickCallback { jid, event ->
            callback(from(jid), GlfwContext.JoystickEvent.Companion.from(event))
        }
    }
}