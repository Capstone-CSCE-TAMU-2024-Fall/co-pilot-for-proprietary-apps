package com.codingassistant.codingassistantplugin.connection.fauxpilot;

import java.util.ArrayList;
import java.util.List;

import com.codingassistant.codingassistantplugin.connection.backend.BackendResponse;
import com.codingassistant.codingassistantplugin.connection.backend.TextCompletionChoice;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record FauxpilotResponse(String id, String model, String object, int created, List<TextCompletionChoice> choices,
		TextCompletionUsage usage) implements BackendResponse {

	public FauxpilotResponse() {
		this("", "", "", 0, new ArrayList<>(), new TextCompletionUsage());
	}
}