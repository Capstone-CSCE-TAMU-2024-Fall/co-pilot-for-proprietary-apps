package com.plugin.copilotassistant.backendconnection;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class FauxpilotConnectionImplTest {

	@Test
	void testGetResponse() {
		try {
			var ip = InetAddress.getLoopbackAddress();
			var port = 5000;
			FauxpilotConnection conn = new FauxpilotConnectionImpl(ip, port);
			System.out.println(conn.getResponse("what does the ").thenApply(HttpResponse::body).get());
		} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("Request failed");
		}
	}

	@Test
	void testParseResponse() {
		var response = "{\"id\": \"cmpl-yafLPqMEkmW0PMQCr19xJbWycgXM7\", \"model\": \"codegen\", \"object\": \"text_completion\", \"created\": 1728486685, \"choices\": [{\"text\": \"\\n\\t * \\tuser want to do?\\n\\t */\\n\\tpublic\", \"index\": 0, \"finish_reason\": \"length\", \"logprobs\": null}], \"usage\": {\"completion_tokens\": 16, \"prompt_tokens\": 4, \"total_tokens\": 20}}";
		try {
			var parsed = new ObjectMapper().readValue(response, FauxpilotResponse.class);
			System.out.println(parsed);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail(MessageFormat.format("Failed to parse {0}", response));
		}
	}

	@Test
	void testRequestResponse() {
		try {
			var ip = InetAddress.getLoopbackAddress();
			var port = 5000;
			FauxpilotConnection conn = new FauxpilotConnectionImpl(ip, port);
			var response = FauxpilotConnectionImpl.parseResponse(conn.getResponse("what does the ")).join();
			System.out.println(MessageFormat.format("Parsed response: {0}", response));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Failed to parse");
		}
	}
}
