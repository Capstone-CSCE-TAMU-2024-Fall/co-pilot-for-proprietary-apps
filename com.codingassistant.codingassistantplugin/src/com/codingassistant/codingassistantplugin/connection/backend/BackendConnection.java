package com.codingassistant.codingassistantplugin.connection.backend;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jface.preference.IPreferenceStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract class representing a backend connection.
 * This class provides the basic structure for making HTTP requests and handling responses.
 * It implements the {@link IBackendConnection} interface.
 * 
 * <p>It initializes an {@link HttpClient} with a connection timeout of 5 seconds and 
 * a {@link HttpRequest.Builder} with a timeout of 3 seconds. The request builder is 
 * configured to use HTTP/1.1 and sets the "Content-Type" and "Accept" headers to "application/json".</p>
 * 
 * <p>Subclasses should provide specific implementations for making requests and handling responses.</p>
 * 
 * @see IBackendConnection
 */
public abstract class BackendConnection implements IBackendConnection {

	protected Builder request;
	protected HttpClient client;

	protected BackendConnection(InetSocketAddress serverAddress, String scheme, String path) throws URISyntaxException {
		URI uri = new URI(scheme, null, serverAddress.getHostName(), serverAddress.getPort(),
				path, null, null);
		client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
		request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1).uri(uri).timeout(Duration.ofSeconds(3))
				.headers("Content-Type", "application/json", "Accept", "application/json");
	}

	public CompletableFuture<HttpResponse<String>> getResponse(String prefix, IPreferenceStore preferenceStore)
			throws JsonProcessingException {
		return this.getResponse(prefix, "", preferenceStore);
	}

	@Override
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