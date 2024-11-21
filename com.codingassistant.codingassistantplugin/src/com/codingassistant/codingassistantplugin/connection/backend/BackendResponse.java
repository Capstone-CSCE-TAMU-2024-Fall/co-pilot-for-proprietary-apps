package com.codingassistant.codingassistantplugin.connection.backend;

import java.util.List;

/**
 * Interface representing a response from the backend.
 * 
 * <p>This interface defines a method to retrieve a list of text completion choices
 * from the backend response.</p>
 */
public interface BackendResponse {
	List<TextCompletionChoice> choices();
}
