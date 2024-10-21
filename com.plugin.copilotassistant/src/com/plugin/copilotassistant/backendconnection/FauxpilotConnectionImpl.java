package com.plugin.copilotassistant.backendconnection;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FauxpilotConnectionImpl implements FauxpilotConnection {
	Builder request;
	HttpClient client;

	public FauxpilotConnectionImpl(InetAddress serverIP, int serverPort) {
		this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
		this.request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1)
				.uri(URI.create(MessageFormat.format("http://{0}:{1}/v1/engines/codegen/completions",
						serverIP.getHostAddress(), Integer.toString(serverPort))))
				.timeout(Duration.ofSeconds(3))
				.headers("Content-Type", "application/json", "Accept", "application/json");
	}

	@Override
	public CompletableFuture<HttpResponse<String>> getResponse(String prompt) throws JsonProcessingException {
		BodyPublisher body = BodyPublishers.ofString(new ObjectMapper()
				.writeValueAsString(new FauxpilotRequest(prompt, 200, 0.1f, new ArrayList<String>())));
		return this.client.sendAsync(this.request.POST(body).build(), BodyHandlers.ofString());
	}
	
	public static CompletableFuture<FauxpilotResponse> parseResponse(CompletableFuture<HttpResponse<String>> response) {
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
