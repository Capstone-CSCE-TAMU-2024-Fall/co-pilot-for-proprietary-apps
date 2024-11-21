package com.codingassistant.codingassistantplugin.connection.backend;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jface.preference.IPreferenceStore;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Interface representing a backend connection.
 */
public interface IBackendConnection {

	/**
	 * Sends a request to the backend and returns a CompletableFuture containing the HttpResponse.
	 *
	 * @param prefix the prefix to be used in the request URL
	 * @param suffix the suffix to be used in the request URL
	 * @param preferenceStore the preference store containing configuration settings
	 * @return a CompletableFuture containing the HttpResponse
	 * @throws JsonProcessingException if there is an error processing JSON
	 */
	CompletableFuture<HttpResponse<String>> getResponse(String prefix, String suffix, IPreferenceStore preferenceStore)
			throws JsonProcessingException;

	/**
	 * Parses the HttpResponse into a BackendResponse object of the specified type.
	 *
	 * @param <T> the type of BackendResponse
	 * @param response the CompletableFuture containing the HttpResponse
	 * @param responseType the class of the response type
	 * @return a CompletableFuture containing the parsed BackendResponse
	 */
	<T extends BackendResponse> CompletableFuture<T> parseResponse(CompletableFuture<HttpResponse<String>> response,
			Class<T> responseType);

	/**
	 * Parses the HttpResponse into a BackendResponse object.
	 *
	 * @param <T> the type of BackendResponse
	 * @param response the CompletableFuture containing the HttpResponse
	 * @return a CompletableFuture containing the parsed BackendResponse
	 */
	<T extends BackendResponse> CompletableFuture<T> parseResponse(CompletableFuture<HttpResponse<String>> response);

}
