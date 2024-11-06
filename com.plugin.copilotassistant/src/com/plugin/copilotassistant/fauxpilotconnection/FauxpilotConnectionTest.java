package com.plugin.copilotassistant.fauxpilotconnection;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.backendconnection.BackendResponse;
import com.plugin.copilotassistant.backendconnection.TextCompletionChoice;

class FauxpilotConnectionTest {

	@Test
	void testGetResponse() {
		try {
			InetAddress ip = InetAddress.getLoopbackAddress();
			int port = 5000;
			InetSocketAddress address = new InetSocketAddress(ip, port);
			BackendConnection conn = new FauxpilotConnection(address);
			System.out.println(conn.getResponse("what does the ").thenApply(HttpResponse::body).get());
		} catch (JsonProcessingException | InterruptedException | ExecutionException | URISyntaxException e) {
			e.printStackTrace();
			fail("Request failed");
		}
	}

	@Test
	void testParseResponse() {
		String response = "{\"id\": \"cmpl-yafLPqMEkmW0PMQCr19xJbWycgXM7\", \"model\": \"codegen\", \"object\": "
				+ "\"text_completion\", \"created\": 1728486685, \"choices\": [{\"text\": \"\\n\\t * \\tuser want to "
				+ "do?\\n\\t */\\n\\tpublic\", \"index\": 0, \"finish_reason\": \"length\", \"logprobs\": null}], "
				+ "\"usage\": {\"completion_tokens\": 16, \"prompt_tokens\": 4, \"total_tokens\": 20}}";
		try {
			FauxpilotResponse parsed = new ObjectMapper().readValue(response, FauxpilotResponse.class);
			System.out.println(parsed);
			assertEquals(parsed,
					new FauxpilotResponse("cmpl-yafLPqMEkmW0PMQCr19xJbWycgXM7", "codegen", "text_completion",
							1728486685,
							List.of(new TextCompletionChoice("length", 0,
									null, "\n\t * \tuser want to do?\n\t */\n\tpublic")),
							new TextCompletionUsage(16, 4, 20)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail(MessageFormat.format("Failed to parse {0}", response));
		}
	}

	@Test
	void testRequestResponse() {
		try {
			InetAddress ip = InetAddress.getLoopbackAddress();
			int port = 5000;
			InetSocketAddress address = new InetSocketAddress(ip, port);
			BackendConnection conn = new FauxpilotConnection(address);
			BackendResponse response = conn.parseResponse(conn.getResponse("what does the ")).join();
			System.out.println(MessageFormat.format("Parsed response: {0}", response));
		} catch (JsonProcessingException | URISyntaxException e) {
			e.printStackTrace();
			fail("Failed to parse");
		}
	}
}
