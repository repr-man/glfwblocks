@file:Suppress("NOTHING_TO_INLINE", "unused")

enum class MouseButton {
    BUTTON_1,
    BUTTON_2,
    BUTTON_3,
    BUTTON_4,
    BUTTON_5,
    BUTTON_6,
    BUTTON_7,
    BUTTON_8;

    inline val LEFT
        inline get() = BUTTON_1
    inline val RIGHT
        inline get() = BUTTON_2
    inline val MIDDLE
        inline get() = BUTTON_3

    companion object {
        fun from(value: Int) = when (value) {
            0 -> BUTTON_1
            1 -> BUTTON_2
            2 -> BUTTON_3
            3 -> BUTTON_4
            4 -> BUTTON_5
            5 -> BUTTON_6
            6 -> BUTTON_7
            7 -> BUTTON_8
            else -> throw NotImplementedError("GLFW Error: Mouse button $value is invalid")
        }
    }
}