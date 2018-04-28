package com.kikirikii;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KikirikiiAppTests {

	Logger logger = Logger.getLogger("KikirikiiAppTests");

	@Test
	public void contextLoads() {
		logger.info("Hello World");
	}

}
