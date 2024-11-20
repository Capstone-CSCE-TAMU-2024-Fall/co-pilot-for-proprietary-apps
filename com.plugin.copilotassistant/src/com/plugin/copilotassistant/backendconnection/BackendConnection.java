package com.plugin.copilotassistant.backendconnection;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jface.preference.IPreferenceStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BackendConnection implements IBackendConnection {

	protected Builder request;
	protected HttpClient client;

	protected BackendConnection(URI uri) throws URISyntaxException {
		client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
		request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1).uri(uri).timeout(Duration.ofSeconds(3))
				.headers("Content-Type", "application/json", "Accept", "application/json");
	}

	protected BackendConnection(InetSocketAddress serverAddress, String scheme) {
	}
	
	protected BackendConnection(InetSocketAddress serverAddress) throws URISyntaxException {
		this(serverAddress, "http");
	}


	public CompletableFuture<HttpResponse<String>> getResponse(String prefix, IPreferenceStore preferenceStore)
			throws JsonProcessingException {
		return this.getResponse(prefix, "", preferenceStore);
	}

	public <T extends BackendResponse> CompletableFuture<T> parseResponse(
			CompletableFuture<HttpResponse<String>> response, Class<T> responseType) {
		if (response == null) {
			return CompletableFuture.failedFuture(new NullPointerException());
		}
		
		return response.thenApply(HttpResponse::body).thenApply(r -> {
			try {
				System.out.println("response: " + r);
				return new ObjectMapper().readValue(r, responseType);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to parse response", e);
			}

		});
	}

}