package com.plugin.copilotassistant.backendConnection;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FauxpilotConnectionImpl implements FauxpilotConnection {
	InetAddress serverIP;
	int serverPort;

	public FauxpilotConnectionImpl(InetAddress serverIP, int serverPort) {
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}

	@Override
	public CompletableFuture<HttpResponse<String>> getResponse(String prompt) throws JsonProcessingException {
		HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
		var body = BodyPublishers.ofString(new ObjectMapper()
				.writeValueAsString(new FauxpilotRequest(prompt, 100, 0.1f, new ArrayList<String>())));
		HttpRequest request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1)
				.uri(URI.create(MessageFormat.format("http://{0}:{1}/v1/engines/codegen/completions",
						this.serverIP.getHostAddress(), Integer.toString(this.serverPort))))
				.timeout(Duration.ofSeconds(3))
				.headers("Content-Type", "application/json", "Accept", "application/json").POST(body).build();
		return client.sendAsync(request, BodyHandlers.ofString());
	}

}
