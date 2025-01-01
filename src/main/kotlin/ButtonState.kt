@file:Suppress("NOTHING_TO_INLINE")

enum class ButtonState {
    RELEASE,
    PRESS;

    companion object {
        inline fun from(value: Int) = when (value) {
            0 -> RELEASE
            1 -> PRESS
            else -> throw NotImplementedError("GLFW Error: Button state $value is invalid")
        }
        inline fun from(b: Byte) = from(b.toInt())
    }
}