package com.codingassistant.codingassistantplugin.connection.tabby;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jface.preference.IPreferenceStore;

import com.codingassistant.codingassistantplugin.connection.backend.BackendConnection;
import com.codingassistant.codingassistantplugin.connection.backend.BackendResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The TabbyConnection class extends BackendConnection and is responsible for
 * establishing a connection to a server and handling requests and responses.
 * It supports both HTTP and HTTPS schemes.
 * 
 * <p>This class provides methods to:
 * <ul>
 *   <li>Initialize a connection with a specified server address and scheme.</li>
 *   <li>Send a request to the server and receive a response asynchronously.</li>
 *   <li>Parse the server response into a specific response type.</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>
 * {@code
 * InetSocketAddress serverAddress = new InetSocketAddress("hostname", port);
 * TabbyConnection connection = new TabbyConnection(serverAddress, "http");
 * CompletableFuture<HttpResponse<String>> response = connection.getResponse(prefix, suffix, preferenceStore);
 * }
 * </pre>
 * 
 * <p>Note: This class requires a valid server address and scheme to function correctly.
 * It also utilizes a preference store to retrieve configuration settings such as
 * temperature and authorization token.
 * 
 **/
public class TabbyConnection extends BackendConnection {
	Random rand;

	public TabbyConnection(InetSocketAddress serverAddress, String scheme) throws URISyntaxException {
		super(serverAddress, scheme, "/v1/completions");
		rand = new Random();
	}

	public TabbyConnection(InetSocketAddress serverAddress) throws URISyntaxException {
		this(serverAddress, "http");
	}

	@Override
	public CompletableFuture<HttpResponse<String>> getResponse(String prefix, String suffix,
			IPreferenceStore preferenceStore) throws JsonProcessingException {
		float temperature = Float.parseFloat(preferenceStore.getString("TEMPERATURE"));
		String authorizationToken = preferenceStore.getString("AUTHORIZATION_TOKEN");
		System.out.println("temperature: " + temperature);
		Segments segments = new Segments(prefix, suffix, null, null, null, null, null);
		TabbyRequest tabbyRequest = new TabbyRequest("java", segments, null, temperature,
				rand.nextLong(Long.MAX_VALUE));
		BodyPublisher body = BodyPublishers.ofString(new ObjectMapper().writeValueAsString(tabbyRequest));
		Builder requestBuilder = request.POST(body);
		if (!authorizationToken.isEmpty()) {
			requestBuilder.setHeader("Authorization", "Bearer " + authorizationToken);
		}
		System.out.println("body: " + tabbyRequest);
		return client.sendAsync(requestBuilder.build(), BodyHandlers.ofString());
	}

	@Override
	public <T extends BackendResponse> CompletableFuture<T> parseResponse(
			CompletableFuture<HttpResponse<String>> response) {
		return (CompletableFuture<T>) super.parseResponse(response, TabbyResponse.class);
	}

}
