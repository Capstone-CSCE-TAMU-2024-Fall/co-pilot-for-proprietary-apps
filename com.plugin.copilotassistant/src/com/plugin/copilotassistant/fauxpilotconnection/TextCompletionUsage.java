package com.plugin.copilotassistant.fauxpilotconnection;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TextCompletionUsage(@JsonAlias("completion_tokens") double completionTokens,
		@JsonAlias("prompt_tokens") double promptTokens, @JsonAlias("total_tokens") double totalTokens) {

	public TextCompletionUsage() {
		this(0, 0, 0);
	}
}