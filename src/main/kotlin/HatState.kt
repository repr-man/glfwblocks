import kotlin.experimental.or

enum class HatState(val state: Byte) {
    CENTERED(0),
    UP(1),
    RIGHT(2),
    DOWN(4),
    LEFT(8),
    RIGHT_UP(RIGHT.state or UP.state),
    RIGHT_DOWN(RIGHT.state or DOWN.state),
    LEFT_UP(LEFT.state or UP.state),
    LEFT_DOWN(LEFT.state or DOWN.state);

    companion object {
        fun from(b: Byte) = when (b) {
            CENTERED.state -> CENTERED
            UP.state -> UP
            RIGHT.state -> RIGHT
            DOWN.state -> DOWN
            LEFT.state -> LEFT
            RIGHT_UP.state -> RIGHT_UP
            RIGHT_DOWN.state -> RIGHT_DOWN
            LEFT_UP.state -> LEFT_UP
            LEFT_DOWN.state -> LEFT_DOWN
            else -> throw NotImplementedError("GLFW Error: Hat state $b is invalid")
        }
    }
}