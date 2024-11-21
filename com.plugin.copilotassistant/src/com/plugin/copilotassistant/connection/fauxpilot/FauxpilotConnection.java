package com.plugin.copilotassistant.connection.fauxpilot;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jface.preference.IPreferenceStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plugin.copilotassistant.connection.backend.BackendConnection;
import com.plugin.copilotassistant.connection.backend.BackendResponse;

/**
 * The FauxpilotConnection class extends BackendConnection to provide a connection
 * to a Fauxpilot server. It is responsible for constructing the URI for the server
 * and handling the request and response for code completion.
 * 
 * <p>There are two constructors available:
 * <ul>
 *   <li>One that takes an InetSocketAddress and a scheme (e.g., "http" or "https")</li>
 *   <li>One that takes only an InetSocketAddress and defaults to "http" scheme</li>
 * </ul>
 * 
 * <p>The class overrides two methods from BackendConnection:
 * <ul>
 *   <li>{@code getResponse(String prefix, String suffix, IPreferenceStore preferenceStore)}:
 *       Sends an asynchronous HTTP request to the Fauxpilot server with the given prefix and
 *       suffix, and returns a CompletableFuture of the HTTP response.</li>
 *   <li>{@code parseResponse(CompletableFuture<HttpResponse<String>> response)}:
 *       Parses the HTTP response into a BackendResponse of type FauxpilotResponse.</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>
 * {@code
 * InetSocketAddress address = new InetSocketAddress("localhost", 8080);
 * FauxpilotConnection connection = new FauxpilotConnection(address);
 * CompletableFuture<HttpResponse<String>> response = connection.getResponse("prefix", "suffix", preferenceStore);
 * }
 * </pre>
 * 
 */
public class FauxpilotConnection extends BackendConnection {
	public FauxpilotConnection(InetSocketAddress serverAddress, String scheme) throws URISyntaxException {
		super(new URI(scheme, null, serverAddress.getHostName(), serverAddress.getPort(),
				"/v1/engines/codegen/completions", null, null));
	}

	public FauxpilotConnection(InetSocketAddress serverAddress) throws URISyntaxException {
		this(serverAddress, "http");
	}

	@Override
	public CompletableFuture<HttpResponse<String>> getResponse(String prefix, String suffix,
			IPreferenceStore preferenceStore) throws JsonProcessingException {
		int maxTokens = preferenceStore.getInt("MAX_TOKENS");
		float temperature = Float.parseFloat(preferenceStore.getString("TEMPERATURE"));
		System.out.println("max tokens: " + maxTokens + ", temperature: " + temperature);

		BodyPublisher body = BodyPublishers.ofString(new ObjectMapper()
				.writeValueAsString(new FauxpilotRequest(prefix, maxTokens, temperature, new ArrayList<>())));
		return client.sendAsync(request.POST(body).build(), BodyHandlers.ofString());
	}

	@Override
	public <T extends BackendResponse> CompletableFuture<T> parseResponse(
			CompletableFuture<HttpResponse<String>> response) {
		return (CompletableFuture<T>) super.parseResponse(response, FauxpilotResponse.class);
	}

}
