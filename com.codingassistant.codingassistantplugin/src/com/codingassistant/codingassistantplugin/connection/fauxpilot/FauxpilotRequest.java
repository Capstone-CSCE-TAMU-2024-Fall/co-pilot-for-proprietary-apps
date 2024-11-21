package com.codingassistant.codingassistantplugin.connection.fauxpilot;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record FauxpilotRequest(String prompt, int maxTokens, float temperature, List<String> stop) {
}