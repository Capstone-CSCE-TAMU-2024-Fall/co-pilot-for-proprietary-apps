package com.codingassistant.codingassistantplugin.connection.tabby;

import java.util.ArrayList;
import java.util.List;

import com.codingassistant.codingassistantplugin.connection.backend.BackendResponse;
import com.codingassistant.codingassistantplugin.connection.backend.TextCompletionChoice;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TabbyResponse(String id, List<TextCompletionChoice> choices) implements BackendResponse {
	TabbyResponse() {
		this("", new ArrayList<>());
	}
}
