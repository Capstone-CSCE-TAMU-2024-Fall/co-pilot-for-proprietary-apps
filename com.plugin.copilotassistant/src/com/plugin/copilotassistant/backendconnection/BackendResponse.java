package com.plugin.copilotassistant.backendconnection;

import java.util.List;

public interface BackendResponse {
	List<TextCompletionChoice> choices();
}
