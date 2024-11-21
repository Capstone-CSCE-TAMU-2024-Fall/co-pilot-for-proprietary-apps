package com.codingassistant.codingassistantplugin.connection.tabby;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * A record that represents a declaration with a file path and body content.
 * 
 * <p>This record is annotated with {@link JsonAutoDetect} to specify that all fields
 * should be automatically detected for JSON serialization and deserialization.
 * The {@link JsonAlias} annotation is used to specify an alternative name for the
 * {@code filePath} field during JSON processing.
 * 
 * @param filePath the path of the file associated with the declaration
 * @param body the body content of the declaration
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Declarations(@JsonAlias("filepath") String filePath, String body) {
}