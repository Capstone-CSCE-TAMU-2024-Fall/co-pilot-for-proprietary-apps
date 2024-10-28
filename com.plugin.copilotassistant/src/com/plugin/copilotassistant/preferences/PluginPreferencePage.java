package com.plugin.copilotassistant.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;



public class PluginPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public PluginPreferencePage() {
        super(GRID);
        // Use a ScopedPreferenceStore instead of an Activator to manage preferences
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant"));
    }

	@Override
	protected void createFieldEditors() {
		// Add fields for your preferences
		addField(new StringFieldEditor("SERVER_HOST", "Server Host:", getFieldEditorParent()));
		addField(new StringFieldEditor("SERVER_PORT", "Server Port:", getFieldEditorParent()));
		addField(new StringFieldEditor("MAX_TOKENS", "Max Tokens:", getFieldEditorParent()));
		addField(new StringFieldEditor("MAX_LINES", "Max Lines:", getFieldEditorParent()));
		addField(new StringFieldEditor("ENGINE", "Engine:", getFieldEditorParent()));
		addField(new StringFieldEditor("MODEL", "Model:", getFieldEditorParent()));
		addField(new StringFieldEditor("TEMPERATURE", "Temperature:", getFieldEditorParent()));
		addField(new StringFieldEditor("SUGGESTION_DELAY", "Suggestion Delay (ms):", getFieldEditorParent()));

		// Add a Boolean field for your preferences
		addField(new BooleanFieldEditor("DEBUG_MODE", "Debug Mode", getFieldEditorParent()));
	}

    @Override
    public void init(IWorkbench workbench) {
        
    }
}
