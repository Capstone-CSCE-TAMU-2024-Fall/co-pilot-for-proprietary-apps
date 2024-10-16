package com.plugin.copilotassistant.backendconnection;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record FauxpilotResponse(String id, String model, String object, int created, List<TextCompletionChoice> choices,
		TextCompletionUsage usage) {

	public FauxpilotResponse() {
		this("", "", "", 0, new ArrayList<TextCompletionChoice>(), new TextCompletionUsage());
	}
}