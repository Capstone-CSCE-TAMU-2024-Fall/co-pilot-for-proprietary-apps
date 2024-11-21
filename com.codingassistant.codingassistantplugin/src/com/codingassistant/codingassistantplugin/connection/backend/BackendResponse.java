package com.codingassistant.codingassistantplugin.connection.backend;

import java.util.List;

public interface BackendResponse {
	List<TextCompletionChoice> choices();
}
