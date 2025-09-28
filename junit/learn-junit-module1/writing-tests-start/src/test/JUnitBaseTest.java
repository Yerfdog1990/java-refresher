import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitBaseTest {

    @Test
    void genericTest() {
        int a = 2;
        int b = 3;
        int sum = a + b;
        assertEquals(5,sum);
        // assertEquals(-1,b-a); -> Test set to intentionally fail: Expected: -1, Actual: 1
    }
}
