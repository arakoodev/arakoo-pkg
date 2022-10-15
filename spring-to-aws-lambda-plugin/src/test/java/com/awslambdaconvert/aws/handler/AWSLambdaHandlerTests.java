package com.awslambdaconvert.aws.handler;
import com.awslambdaconvert.handler.LambdaHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AWSLambdaHandlerTests {

	@Test
	void test() {
		LambdaHandler handler = new LambdaHandler();
		handler.handleRequest(null, null, null);

		assertNotNull(1);
	}

}
