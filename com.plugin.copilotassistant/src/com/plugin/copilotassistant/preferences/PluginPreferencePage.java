package com.plugin.copilotassistant.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class PluginPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PluginPreferencePage() {
		super(GRID);
		// Use the default preference store from the PlatformUI
		setPreferenceStore(PlatformUI.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		// Add fields for your preferences
		addField(new StringFieldEditor("SERVER_HOST", "Server Host:", getFieldEditorParent()));
		addField(new StringFieldEditor("SERVER_PORT", "Server Port:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MAX_TOKENS", "Max Tokens:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MAX_LINES", "Max Lines:", getFieldEditorParent()));
		addField(new StringFieldEditor("ENGINE", "Engine:", getFieldEditorParent()));
		addField(new StringFieldEditor("MODEL", "Model:", getFieldEditorParent()));
		addField(new StringFieldEditor("TEMPERATURE", "Temperature:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("SUGGESTION_DELAY", "Suggestion Delay (ms):", getFieldEditorParent()));

		// Add a Boolean (Toggle Button) field for your preferences
		addField(new BooleanFieldEditor("ENABLE_INSERTION", "Enable Code Insertion", getFieldEditorParent()));
		addField(new BooleanFieldEditor("DEBUG_MODE", "Debug Mode", getFieldEditorParent()));

	}

	@Override
	public void init(IWorkbench workbench) {
		// No specific initialization required
	}
}
