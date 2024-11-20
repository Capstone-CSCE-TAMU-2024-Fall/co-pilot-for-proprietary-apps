package com.plugin.copilotassistant.fauxpilotconnection;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
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
import com.plugin.copilotassistant.tabbyconnection.TabbyResponse;

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
