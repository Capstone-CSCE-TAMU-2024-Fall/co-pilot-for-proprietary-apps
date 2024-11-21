package com.codingassistant.codingassistantplugin.connection.tabby;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TabbyRequest(String language, Segments segments, String user, float temperature, long seed) {
}
