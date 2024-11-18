package com.plugin.copilotassistant.backendconnection;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface IBackendConnection {
	CompletableFuture<HttpResponse<String>> getResponse(String prefix, String suffix) throws JsonProcessingException;

	<T extends BackendResponse> CompletableFuture<T> parseResponse(CompletableFuture<HttpResponse<String>> response,
			Class<T> responseType);

	<T extends BackendResponse> CompletableFuture<T> parseResponse(CompletableFuture<HttpResponse<String>> response);

}
