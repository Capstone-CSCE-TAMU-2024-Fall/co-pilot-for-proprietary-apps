package com.plugin.copilotassistant.connection.tabby;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * A record that represents relevant code snippets.
 * 
 * <p>This record is used to store information about code snippets that are
 * considered relevant based on a certain criteria. It includes the file path
 * of the snippet, the body of the snippet, and a score indicating the relevance
 * of the snippet.</p>
 * 
 * @param filePath the file path of the code snippet
 * @param body the body of the code snippet
 * @param score the relevance score of the code snippet
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record RelevantSnippets(@JsonAlias("filepath") String filePath, String body, float score) {
}