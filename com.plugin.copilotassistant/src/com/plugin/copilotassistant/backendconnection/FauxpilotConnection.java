package com.plugin.copilotassistant.backendconnection;

import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record TextCompletionUsage(@JsonAlias("completion_tokens") double completionTokens,
		@JsonAlias("prompt_tokens") double promptTokens, @JsonAlias("total_tokens") double totalTokens) {

	public TextCompletionUsage() {
		this(0, 0, 0);
	}
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record FauxpilotRequest(String prompt, int maxTokens, float temperature, List<String> stop) {
}

interface FauxpilotConnection {
	CompletableFuture<HttpResponse<String>> getResponse(String prompt) throws JsonProcessingException;

}
