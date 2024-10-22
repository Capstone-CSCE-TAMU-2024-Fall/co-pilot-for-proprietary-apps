package com.plugin.copilotassistant.fauxpilotconnection;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.plugin.copilotassistant.backendconnection.BackendResponse;
import com.plugin.copilotassistant.backendconnection.TextCompletionChoice;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record FauxpilotResponse(String id, String model, String object, int created, List<TextCompletionChoice> choices,
		TextCompletionUsage usage) implements BackendResponse{

	public FauxpilotResponse() {
		this("", "", "", 0, new ArrayList<>(), new TextCompletionUsage());
	}
}