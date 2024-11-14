package com.plugin.copilotassistant.preferences;


import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PluginPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PluginPreferencePage() {
		super(GRID);
		// Use a ScopedPreferenceStore instead of an Activator to manage preferences
		ScopedPreferenceStore copilotPreferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant");
		IPropertyChangeListener  protocolListener = new ProtocolChangeListener();
		copilotPreferences.addPropertyChangeListener(protocolListener);
		setPreferenceStore(copilotPreferences);
	}

	@Override
	protected void createFieldEditors() {
		// Add fields for your preferences
		addField(new StringFieldEditor("SERVER_HOST", "Server Host:", getFieldEditorParent()));
		addField(new StringFieldEditor("SERVER_PORT", "Server Port:", getFieldEditorParent()));
		addField(new RadioGroupFieldEditor("SCHEME", "Protocol:", 1,
				new String[][] { { "HTTP", "http" }, { "HTTPS", "https" } }, getFieldEditorParent()));
		addField(new IntegerFieldEditor("MAX_TOKENS", "Max Tokens:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MAX_LINES", "Max Lines:", getFieldEditorParent()));
		addField(new StringFieldEditor("ENGINE", "Engine:", getFieldEditorParent()));
		addField(new StringFieldEditor("MODEL", "Model:", getFieldEditorParent()));
		addField(new StringFieldEditor("TEMPERATURE", "Temperature:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("SUGGESTION_DELAY", "Suggestion Delay (ms):", getFieldEditorParent()));
		// Add a Boolean field for your preferences
		addField(new BooleanFieldEditor("DEBUG_MODE", "Debug Mode", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
