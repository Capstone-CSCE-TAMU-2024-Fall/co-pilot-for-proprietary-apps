package com.plugin.copilotassistant.connection.backend;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jface.preference.IPreferenceStore;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IBackendConnection {
	CompletableFuture<HttpResponse<String>> getResponse(String prefix, String suffix, IPreferenceStore preferenceStore)
			throws JsonProcessingException;

	<T extends BackendResponse> CompletableFuture<T> parseResponse(CompletableFuture<HttpResponse<String>> response,
			Class<T> responseType);

	<T extends BackendResponse> CompletableFuture<T> parseResponse(CompletableFuture<HttpResponse<String>> response);

}
