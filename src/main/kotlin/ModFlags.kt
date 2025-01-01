@file:Suppress("unused", "PropertyName")

@JvmInline
value class ModFlags(val flags: Int) {
    inline val SHIFT
        inline get() = flags and 0x1
    inline val CONTROL
        inline get() = flags and 0x2
    inline val ALT
        inline get() = flags and 0x4
    inline val SUPER
        inline get() = flags and 0x8
    inline val CAPS_LOCK
        inline get() = flags and 0x10
    inline val NUM_LOCK
        inline get() = flags and 0x20


    companion object {
        const val SHIFT = 0x1
        const val CONTROL = 0x2
        const val ALT = 0x4
        const val SUPER = 0x8
        const val CAPS_LOCK = 0x10
        const val NUM_LOCK = 0x20
    }
}