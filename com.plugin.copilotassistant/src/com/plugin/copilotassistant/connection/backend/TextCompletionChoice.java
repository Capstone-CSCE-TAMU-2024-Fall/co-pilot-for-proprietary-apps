package com.plugin.copilotassistant.connection.backend;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents a choice for text completion.
 * 
 * <p>This class is used to encapsulate the details of a text completion choice,
 * including the reason for finishing, the index of the choice, the log probabilities,
 * and the text of the completion.</p>
 * 
 * <p>It uses Jackson annotations to control JSON serialization and deserialization.</p>
 * 
 * @param finishReason The reason why the text completion finished.
 * @param index The index of the choice.
 * @param logProbs The log probabilities associated with the choice.
 * @param text The text of the completion.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TextCompletionChoice(@JsonAlias("finish_reason") String finishReason, int index,
		@JsonAlias("logprobs") List<Float> logProbs, String text) {

	public TextCompletionChoice() {
		this("", -1, new ArrayList<>(), "");
	}
}