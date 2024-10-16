package com.plugin.copilotassistant.backendconnection;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TextCompletionChoice(@JsonAlias("finish_reason") String finishReason, int index,
		@JsonAlias("logprobs") List<Float> logProbs, String text) {
}