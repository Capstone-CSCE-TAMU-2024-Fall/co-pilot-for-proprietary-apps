package com.plugin.copilotassistant.connection.backend;

import java.util.List;

public interface BackendResponse {
	List<TextCompletionChoice> choices();
}
