package com.plugin.tabbyconnection;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.backendconnection.BackendResponse;
import com.plugin.copilotassistant.fauxpilotconnection.FauxpilotResponse;

public class TabbyConnection extends BackendConnection {
	Random rand;

	public TabbyConnection(InetSocketAddress serverAddress, String scheme) throws URISyntaxException {
		super(serverAddress, scheme);
		rand = new Random();
	}

	public TabbyConnection(InetSocketAddress serverAddress) throws URISyntaxException {
		this(serverAddress, "https");
	}

	@Override
	public CompletableFuture<HttpResponse<String>> getResponse(String prefix, String suffix)
			throws JsonProcessingException {
		int maxTokens = preferenceStore.getInt("MAX_TOKENS");
		float temperature = Float.parseFloat(preferenceStore.getString("TEMPERATURE"));
		String authorizationToken = preferenceStore.getString("AUTHORIZATION_TOKEN");
		System.out.println("max tokens: " + maxTokens + ", temperature: " + temperature);
		Segments segments = new Segments(prefix, suffix, null, null, null, null, null);
		BodyPublisher body = BodyPublishers.ofString(new ObjectMapper()
				.writeValueAsString(new TabbyRequest("java", segments, null, temperature, rand.nextInt())));
		Builder requestBuilder = request.POST(body);
		if (!authorizationToken.isEmpty()) {
			requestBuilder.setHeader("Authorization", authorizationToken);
		}
		return client.sendAsync(requestBuilder.build(), BodyHandlers.ofString());
	}

	@Override
	public <T extends BackendResponse> CompletableFuture<T> parseResponse(
			CompletableFuture<HttpResponse<String>> response) {
		return (CompletableFuture<T>) super.parseResponse(response, TabbyResponse.class);
	}

}
