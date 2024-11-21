package com.codingassistant.codingassistantplugin.connection.tabby;

import java.util.ArrayList;
import java.util.List;

import com.codingassistant.codingassistantplugin.connection.backend.BackendResponse;
import com.codingassistant.codingassistantplugin.connection.backend.TextCompletionChoice;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents a response from the Tabby backend service.
 * 
 * <p>This record encapsulates the response data which includes an identifier
 * and a list of text completion choices.</p>
 * 
 * <p>The {@code @JsonAutoDetect} annotation is used to specify that all fields
 * should be automatically detected and included in the JSON serialization and
 * deserialization process.</p>
 * 
 * @param id The unique identifier for the response.
 * @param choices A list of text completion choices returned by the backend.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TabbyResponse(String id, List<TextCompletionChoice> choices) implements BackendResponse {
	TabbyResponse() {
		this("", new ArrayList<>());
	}
}
