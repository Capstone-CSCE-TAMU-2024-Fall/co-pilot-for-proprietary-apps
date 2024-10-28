package com.plugin.copilotassistant.backendconnection;

import java.net.URISyntaxException;

public interface TextCompletionService {
	void connect() throws URISyntaxException;

	void trigger();
}
