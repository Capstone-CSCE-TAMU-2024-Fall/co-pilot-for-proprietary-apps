package com.plugin.copilotassistant.connection.tabby;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.plugin.copilotassistant.connection.backend.BackendResponse;
import com.plugin.copilotassistant.connection.backend.TextCompletionChoice;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TabbyResponse(String id, List<TextCompletionChoice> choices) implements BackendResponse {
	TabbyResponse() {
		this("", new ArrayList<>());
	}
}
