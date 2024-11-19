package com.plugin.copilotassistant.fauxpilotconnection;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.backendconnection.BackendResponse;

public class FauxpilotConnection implements BackendConnection {
	private Builder request;
	private HttpClient client;
	private IPreferenceStore preferenceStore;

	public FauxpilotConnection(InetSocketAddress serverAddress, String scheme) throws URISyntaxException {
		URI uri = new URI(scheme, null, serverAddress.getHostName(), serverAddress.getPort(),
				"/v1/engines/codegen/completions", null, null);
		client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
		request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1).uri(uri).timeout(Duration.ofSeconds(3))
				.headers("Content-Type", "application/json", "Accept", "application/json");

		preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant");
	}
	
	public FauxpilotConnection(InetSocketAddress serverAddress) throws URISyntaxException {
		this(serverAddress, "http");
	}

	@Override
	public CompletableFuture<HttpResponse<String>> getResponse(String prompt) throws JsonProcessingException {
		int maxTokens = preferenceStore.getInt("MAX_TOKENS");
		float temperature = Float.parseFloat(preferenceStore.getString("TEMPERATURE"));
		System.out.println("max tokens: " + maxTokens + ", temperature: " + temperature);

		BodyPublisher body = BodyPublishers.ofString(new ObjectMapper()
				.writeValueAsString(new FauxpilotRequest(prompt, maxTokens, temperature, new ArrayList<>())));
		return client.sendAsync(request.POST(body).build(), BodyHandlers.ofString());
	}

	@Override
	public CompletableFuture<BackendResponse> parseResponse(CompletableFuture<HttpResponse<String>> response) {
		if (response == null) {
			return CompletableFuture.supplyAsync(FauxpilotResponse::new);
		}

		return response.thenApply(HttpResponse::body).thenApply(r -> {
			try {
				return new ObjectMapper().readValue(r, FauxpilotResponse.class);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return new FauxpilotResponse();
			}
		});
	}

}
