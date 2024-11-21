package com.plugin.copilotassistant.connection.fauxpilot;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.plugin.copilotassistant.connection.backend.BackendResponse;
import com.plugin.copilotassistant.connection.backend.TextCompletionChoice;

/**
 * Represents a response from the Fauxpilot backend service.
 * 
 * <p>This record encapsulates the details of a response, including an identifier,
 * model information, object type, creation timestamp, a list of text completion choices,
 * and usage statistics.</p>
 * 
 * <p>The {@code FauxpilotResponse} record implements the {@code BackendResponse} interface.</p>
 * 
 * @param id The unique identifier for the response.
 * @param model The model used to generate the response.
 * @param object The type of object returned.
 * @param created The timestamp when the response was created.
 * @param choices A list of text completion choices provided in the response.
 * @param usage The usage statistics related to the response.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record FauxpilotResponse(String id, String model, String object, int created, List<TextCompletionChoice> choices,
		TextCompletionUsage usage) implements BackendResponse {

	public FauxpilotResponse() {
		this("", "", "", 0, new ArrayList<>(), new TextCompletionUsage());
	}
}