package com.codingassistant.codingassistantplugin.connection.fauxpilot;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * A record representing a request to the Fauxpilot service.
 * 
 * @param prompt The input prompt for the Fauxpilot service.
 * @param maxTokens The maximum number of tokens to generate.
 * @param temperature The sampling temperature to use for generation.
 * @param stop A list of stop sequences to end the generation.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
record FauxpilotRequest(String prompt, int maxTokens, float temperature, List<String> stop) {
}