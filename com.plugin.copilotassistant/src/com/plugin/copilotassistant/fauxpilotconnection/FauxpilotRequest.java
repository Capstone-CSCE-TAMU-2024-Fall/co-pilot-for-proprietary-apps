package com.plugin.copilotassistant.fauxpilotconnection;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record FauxpilotRequest(String prompt, int maxTokens, float temperature, List<String> stop) {
}