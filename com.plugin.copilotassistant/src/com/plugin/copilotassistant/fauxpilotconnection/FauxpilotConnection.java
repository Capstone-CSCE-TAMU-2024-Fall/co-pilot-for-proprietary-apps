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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.backendconnection.BackendResponse;

public class FauxpilotConnection implements BackendConnection {
	Builder request;
	HttpClient client;

	public FauxpilotConnection(InetSocketAddress serverAddress) throws URISyntaxException {
		URI uri = new URI("http", null, serverAddress.getHostName(), serverAddress.getPort(),
				"/v1/engines/codegen/completions", null, null);
		this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
		this.request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1).uri(uri)
				.timeout(Duration.ofSeconds(3))
				.headers("Content-Type", "application/json", "Accept", "application/json");
	}

	@Override
	public CompletableFuture<HttpResponse<String>> getResponse(String prompt) throws JsonProcessingException {
		BodyPublisher body = BodyPublishers.ofString(
				new ObjectMapper().writeValueAsString(new FauxpilotRequest(prompt, 200, 0.1f, new ArrayList<>())));
		return this.client.sendAsync(this.request.POST(body).build(), BodyHandlers.ofString());
	}

	@Override
	public CompletableFuture<BackendResponse> parseResponse(CompletableFuture<HttpResponse<String>> response) {
		if (response == null) {
			return CompletableFuture.supplyAsync(() -> new FauxpilotResponse());
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
