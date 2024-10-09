package com.plugin.copilotassistant.backendConnection;

import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record TextCompletionChoice(@JsonAlias("finish_reason") String finishReason, int index, @JsonAlias("logprobs") List<Float> logProbs, String text) {
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record TextCompletionUsage(@JsonAlias("completion_tokens") double completionTokens, @JsonAlias("prompt_tokens") double promptTokens, @JsonAlias("total_tokens") double totalTokens) {
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record FauxpilotRequest(String prompt, int maxTokens, float temperature, List<String> stop) {
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record FauxpilotResponse(String id, String model, String object, int created, List<TextCompletionChoice> choices,
		TextCompletionUsage usage) {
}

interface FauxpilotConnection {
	CompletableFuture<HttpResponse<String>> getResponse(String prompt) throws JsonProcessingException;
}
