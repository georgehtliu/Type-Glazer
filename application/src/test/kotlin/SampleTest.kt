import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SampleTest {
    private val testSample: Sample = Sample()

    @Test
    fun testSum() {
        val expected = 42
        Assertions.assertEquals(expected, testSample.sum(40, 2))
    }
}