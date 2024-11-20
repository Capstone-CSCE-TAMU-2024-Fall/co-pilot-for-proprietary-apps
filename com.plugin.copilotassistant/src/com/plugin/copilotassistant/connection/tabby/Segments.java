package com.plugin.copilotassistant.connection.tabby;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Segments(String prefix, String suffix, @JsonAlias("filepath") String filePath,
		@JsonAlias("git_url") String gitUrl, Declarations declarations,
		@JsonAlias("relevant_snippets_from_changed_files") RelevantSnippets relavantSnippetsFromChangedFiles,
		String clipboard) {
}