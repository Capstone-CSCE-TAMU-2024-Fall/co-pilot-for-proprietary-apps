package com.plugin.tabbyconnection;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.plugin.copilotassistant.backendconnection.BackendResponse;
import com.plugin.copilotassistant.backendconnection.TextCompletionChoice;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TabbyResponse(String id, List<TextCompletionChoice> choices) implements BackendResponse {
	TabbyResponse() {
		this("", new ArrayList<>());
	}
}
