package com.plugin.copilotassistant.backendconnection;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface BackendConnection {
	CompletableFuture<HttpResponse<String>> getResponse(String prompt) throws JsonProcessingException;

	CompletableFuture<BackendResponse> parseResponse(CompletableFuture<HttpResponse<String>> response);

}
