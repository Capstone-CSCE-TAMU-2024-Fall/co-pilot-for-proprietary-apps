package com.plugin.copilotassistant.connection.fauxpilot;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * A record representing the usage statistics for text completion.
 * 
 * <p>This record includes the following fields:</p>
 * <ul>
 *   <li>{@code completionTokens} - The number of tokens used for the completion.</li>
 *   <li>{@code promptTokens} - The number of tokens used for the prompt.</li>
 *   <li>{@code totalTokens} - The total number of tokens used.</li>
 * </ul>
 * 
 * <p>All fields are annotated with {@link JsonAlias} to map JSON properties to the record fields.</p>
 * 
 * <p>Example JSON:</p>
 * <pre>
 * {
 *   "completion_tokens": 123.0,
 *   "prompt_tokens": 45.0,
 *   "total_tokens": 168.0
 * }
 * </pre>
 * 
 * <p>The default constructor initializes all fields to zero.</p>
 * 
 * @param completionTokens The number of tokens used for the completion.
 * @param promptTokens The number of tokens used for the prompt.
 * @param totalTokens The total number of tokens used.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TextCompletionUsage(@JsonAlias("completion_tokens") double completionTokens,
		@JsonAlias("prompt_tokens") double promptTokens, @JsonAlias("total_tokens") double totalTokens) {

	public TextCompletionUsage() {
		this(0, 0, 0);
	}
}