package com.codingassistant.codingassistantplugin.connection.tabby;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents a request to the Tabby service.
 * 
 * @param language    The programming language for the request.
 * @param segments    The segments of code or text to be processed.
 * @param user        The user making the request.
 * @param temperature The temperature parameter for the request, influencing randomness.
 * @param seed        The seed value for random number generation.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TabbyRequest(String language, Segments segments, String user, float temperature, long seed) {
}
