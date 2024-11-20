package com.plugin.copilotassistant.connection.tabby;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Declarations(@JsonAlias("filepath") String filePath, String body) {
}