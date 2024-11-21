package com.codingassistant.codingassistantplugin.connection.tabby;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * A record representing segments with various properties.
 * 
 * @param prefix The prefix string.
 * @param suffix The suffix string.
 * @param filePath The file path, aliased as "filepath" in JSON.
 * @param gitUrl The Git URL, aliased as "git_url" in JSON.
 * @param declarations The declarations associated with the segments.
 * @param relavantSnippetsFromChangedFiles The relevant snippets from changed files, aliased as "relevant_snippets_from_changed_files" in JSON.
 * @param clipboard The clipboard content.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Segments(String prefix, String suffix, @JsonAlias("filepath") String filePath,
		@JsonAlias("git_url") String gitUrl, Declarations declarations,
		@JsonAlias("relevant_snippets_from_changed_files") RelevantSnippets relavantSnippetsFromChangedFiles,
		String clipboard) {
}