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
