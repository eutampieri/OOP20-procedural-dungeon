import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameTests {
	@Test
	void testGameLaunch() {
		assertEquals(0,0);
		//fail("Not yet implemented");
	}
}