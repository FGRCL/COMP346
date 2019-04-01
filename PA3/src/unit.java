import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

class unit {
	
	Monitor monitor = new Monitor(4);
	
	@RepeatedTest(100)
	@ValueSource(ints = { 1,2,3,4 })
	void pickinpUp(int argument) {
	}

}
