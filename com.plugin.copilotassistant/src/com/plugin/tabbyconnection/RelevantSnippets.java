package com.plugin.tabbyconnection;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record RelevantSnippets(@JsonAlias("filepath") String filePath, String body, float score) {
}