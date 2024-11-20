package com.plugin.copilotassistanttests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plugin.copilotassistant.connection.backend.BackendConnection;
import com.plugin.copilotassistant.connection.backend.BackendResponse;
import com.plugin.copilotassistant.connection.backend.TextCompletionChoice;
import com.plugin.copilotassistant.connection.tabby.TabbyConnection;
import com.plugin.copilotassistant.connection.tabby.TabbyResponse;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;

@TestInstance(Lifecycle.PER_CLASS)
class TabbyConnectionTest {
	IPreferenceStore preferenceStore = new PreferenceStore();
	InetSocketAddress address;
	BackendConnection conn;

	@BeforeAll
	void setupPreferences() {
		preferenceStore.setValue("TEMPERATURE", "0.1");
		preferenceStore.setValue("AUTHORIZATION_TOKEN", "auth_d6dbb33755064e31b819df431a1949a4");
		InetAddress ip = InetAddress.getLoopbackAddress();
		int port = 8080;
		address = new InetSocketAddress(ip, port);
		try {
			conn = new TabbyConnection(address);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("Invalid URI syntax while initializing Tabby connection.");
		}
	}

	@Test
	void testGetResponse() {
		try {
			System.out.println(
					conn.getResponse("what does the ", " say?", preferenceStore).thenApply(HttpResponse::body).get());
		} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail("Request failed");
		}
	}

	@Test
	void testParseResponse() {
		String response = """
				{"id":"cmpl-148891d8-b290-4ee8-bd42-1080416ae5f8","choices":[{"index":0,"text":"if n == 0:\\n        return 0\\n    elif n == 1:\\n        return 1\\n    else:"}]}""";
		try {
			BackendResponse parsed = new ObjectMapper().readValue(response, TabbyResponse.class);
			System.out.println(parsed);
			assertEquals(parsed,
					new TabbyResponse("cmpl-148891d8-b290-4ee8-bd42-1080416ae5f8",
							List.of(new TextCompletionChoice(null, 0, null,
									"if n == 0:\n        return 0\n    elif n == 1:\n        return 1\n    else:"))));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail(MessageFormat.format("Failed to parse {0}", response));
		}
	}

	@Test
	void testRequestResponse() {
		try {
			BackendResponse response = conn.parseResponse(conn.getResponse("what does the ", " say?", preferenceStore))
					.join();
			System.out.println(MessageFormat.format("Parsed response: {0}", response));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Failed to parse");
		}
	}

}
