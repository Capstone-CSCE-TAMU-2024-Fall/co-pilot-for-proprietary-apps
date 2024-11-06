package com.plugin.copilotassistant.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        // Use ScopedPreferenceStore to initialize default values
        IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant");
        
        // Set default values
        store.setDefault("SERVER_HOST", "localhost");
        store.setDefault("SERVER_PORT", "5000");
        store.setDefault("MAX_TOKENS", "200");
        store.setDefault("MAX_LINES", "20");
        store.setDefault("ENGINE", "davinci");
        store.setDefault("MODEL", "text-davinci-003");
        store.setDefault("TEMPERATURE", "0.1");
        store.setDefault("SUGGESTION_DELAY", "500");
        store.setDefault("ENABLE_INSERTION", true);
        store.setDefault("DEBUG_MODE", false);
    }
}
