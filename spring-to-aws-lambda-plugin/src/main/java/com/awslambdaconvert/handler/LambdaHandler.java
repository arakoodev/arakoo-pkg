package com.awslambdaconvert.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class LambdaHandler implements RequestStreamHandler {

	private static final Logger LOG = LoggerFactory.getLogger(LambdaHandler.class);

	private static final RequestProcessor requestProcessor = new RequestProcessor();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
		try {
			LOG.info("EVENT");

			Object input = objectMapper.readValue(inputStream, Object.class);
			LOG.info(input.toString());

			// TODO requestProcessor development progress
			ApiResponse response = requestProcessor.handleRequest(new ApiRequest("/orders", "GET"));
			LOG.info("CONTROLLER RESPONSE {}", response.getMessage());
			objectMapper.writeValue(outputStream, response.getMessage());

		} catch (Exception e) {
			LOG.error("Request handle failed", e);
		}
	}
}
