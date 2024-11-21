package com.codingassistant.codingassistanttests.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.codingassistant.codingassistantplugin.connection.backend.BackendConnection;
import com.codingassistant.codingassistantplugin.connection.backend.BackendResponse;
import com.codingassistant.codingassistantplugin.connection.backend.TextCompletionChoice;
import com.codingassistant.codingassistantplugin.connection.fauxpilot.FauxpilotConnection;
import com.codingassistant.codingassistantplugin.connection.fauxpilot.FauxpilotResponse;
import com.codingassistant.codingassistantplugin.connection.fauxpilot.TextCompletionUsage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@TestInstance(Lifecycle.PER_CLASS)
class FauxpilotConnectionTest {

	IPreferenceStore preferenceStore = new PreferenceStore();
	InetSocketAddress address;
	BackendConnection conn;

	@BeforeAll
	void setupPreferences() {
		preferenceStore.setValue("TEMPERATURE", "0.1");
		preferenceStore.setValue("AUTHORIZATION_TOKEN", null);
		InetAddress ip = InetAddress.getLoopbackAddress();
		int port = 5000;
		address = new InetSocketAddress(ip, port);
		try {
			conn = new FauxpilotConnection(address);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("Invalid URI syntax while initializing Tabby connection.");
		}
	}

	@Test
	void testGetResponse() {
		try {
			System.out.println(conn.getResponse("what does the ", preferenceStore).thenApply(HttpResponse::body).get());
		} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("Request failed");
		}
	}

	@Test
	void testParseResponse() {
		String response = """
				{"id": "cmpl-yafLPqMEkmW0PMQCr19xJbWycgXM7", "model": "codegen", "object": \
				"text_completion", "created": 1728486685, "choices": [{"text": "\\n\\t * \\tuser want to \
				do?\\n\\t */\\n\\tpublic", "index": 0, "finish_reason": "length", "logprobs": null}], \
				"usage": {"completion_tokens": 16, "prompt_tokens": 4, "total_tokens": 20}}""";
		try {
			BackendResponse parsed = new ObjectMapper().readValue(response, FauxpilotResponse.class);
			System.out.println(parsed);
			assertEquals(parsed, new FauxpilotResponse("cmpl-yafLPqMEkmW0PMQCr19xJbWycgXM7", "codegen",
					"text_completion", 1728486685,
					List.of(new TextCompletionChoice("length", 0, null, "\n\t * \tuser want to do?\n\t */\n\tpublic")),
					new TextCompletionUsage(16, 4, 20)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail(MessageFormat.format("Failed to parse {0}", response));
		}
	}

	@Test
	void testRequestResponse() {
		try {
			BackendResponse response = conn.parseResponse(conn.getResponse("what does the ", preferenceStore)).join();
			System.out.println(MessageFormat.format("Parsed response: {0}", response));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Failed to parse");
		}
	}
}
