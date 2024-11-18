package com.plugin.copilotassistant.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

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
