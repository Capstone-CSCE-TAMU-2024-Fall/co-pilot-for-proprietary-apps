package com.plugin.copilotassistant.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * PreferenceInitializer is responsible for initializing the default preferences
 * for the Copilot Assistant plugin. It extends AbstractPreferenceInitializer and
 * overrides the initializeDefaultPreferences method to set default values for
 * various preferences using ScopedPreferenceStore.
 * 
 * Default preferences initialized:
 * - SERVER_HOST: Default server host (default: "localhost")
 * - SERVER_PORT: Default server port (default: "5000")
 * - BACKEND: Backend service to use (default: "Fauxpilot")
 * - AUTHORIZATION_TOKEN: Authorization token for the backend (default: "")
 * - SCHEME: URL scheme (default: "http")
 * - MAX_TOKENS: Maximum number of tokens (default: 200)
 * - MAX_LINES: Maximum number of lines (default: 20)
 * - TEMPERATURE: Temperature setting for the backend (default: "0.2")
 * - SUGGESTION_DELAY: Delay for suggestions in milliseconds (default: 500)
 * - DEBUG_MODE: Debug mode flag (default: false)
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		// Use ScopedPreferenceStore to initialize default values
		IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant");

		// Set default values
		store.setDefault("SERVER_HOST", "localhost");
		store.setDefault("SERVER_PORT", "5000");
		store.setDefault("BACKEND", "Fauxpilot");
		store.setDefault("AUTHORIZATION_TOKEN", "");
		store.setDefault("SCHEME", "http");
		store.setDefault("MAX_TOKENS", 200);
		store.setDefault("MAX_LINES", 20);
		store.setDefault("TEMPERATURE", "0.2");
		store.setDefault("SUGGESTION_DELAY", 500);
		store.setDefault("DEBUG_MODE", false);
	}
}
