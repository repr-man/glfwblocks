@file:Suppress("NOTHING_TO_INLINE")

enum class KeyAction {
    RELEASE,
    PRESS,
    REPEAT;

    companion object {
        inline fun from(value: Int) = when (value) {
            0 -> RELEASE
            1 -> PRESS
            2 -> REPEAT
            else -> throw NotImplementedError("GLFW Error: Key action $value is invalid")
        }
        inline fun from(b: Byte) = from(b.toInt())
    }
}