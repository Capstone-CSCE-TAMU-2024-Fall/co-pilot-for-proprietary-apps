package com.plugin.copilotassistant.preferences;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import java.net.URISyntaxException;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.plugin.copilotassistant.TextCompletionService;

public class PluginPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	StringFieldEditor authorizationTokenEditor;

	public PluginPreferencePage() {
		super(GRID);
		// Use a ScopedPreferenceStore instead of an Activator to manage preferences
		ScopedPreferenceStore copilotPreferences = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"com.plugin.copilotassistant");
		IPropertyChangeListener propertyListener = new PropertyChangeListener();
		copilotPreferences.addPropertyChangeListener(propertyListener);
		setPreferenceStore(copilotPreferences);
	}

	@Override
	protected void createFieldEditors() {
		// Add fields for your preferences
		addField(new StringFieldEditor("SERVER_HOST", "Server Host:", getFieldEditorParent()));
		addField(new StringFieldEditor("SERVER_PORT", "Server Port:", getFieldEditorParent()));
		// Dropdown for selecting engine
		String[][] backendOptions = { { "Fauxpilot", "Fauxpilot" }, { "Tabby", "Tabby" } };
		addField(new ComboFieldEditor("BACKEND", // Key to save preference
				"Select Backend:", // Label text
				backendOptions, getFieldEditorParent()));

		authorizationTokenEditor = new StringFieldEditor("AUTHORIZATION_TOKEN", "Tabby Authorization Token:",
				getFieldEditorParent());
		addField(authorizationTokenEditor);

		String[][] protocols = new String[][] { { "HTTP", "http" }, { "HTTPS", "https" } };
		addField(new RadioGroupFieldEditor("SCHEME", "Protocol:", 1, protocols, getFieldEditorParent()));
		addField(new IntegerFieldEditor("MAX_TOKENS", "Max Tokens:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MAX_LINES", "Max Lines:", getFieldEditorParent()));

		// addField(new StringFieldEditor("MODEL", "Model:", getFieldEditorParent()));
		addField(new StringFieldEditor("TEMPERATURE", "Temperature:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("SUGGESTION_DELAY", "Suggestion Delay (ms):", getFieldEditorParent()));
		// Add a Boolean field for your preferences
		addField(new BooleanFieldEditor("DEBUG_MODE", "Debug Mode", getFieldEditorParent()));

		// Add a Boolean (Toggle Button) field for your preferences
		addField(new BooleanFieldEditor("ENABLE_INSERTION", "Enable Code Insertion", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	class PropertyChangeListener implements IPropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			String propertyChanged = event.getProperty();

			switch (propertyChanged) {
			case "ENABLE_INSERTION": {
				boolean newState = Boolean.parseBoolean(event.getNewValue().toString());

				ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
				Command command = commandService.getCommand("com.plugin.copilotassistant.commands.enableCodeInsertion");
				State state = command.getState("org.eclipse.ui.commands.toggleState");
				state.setValue(newState);

				commandService.refreshElements("com.plugin.copilotassistant.commands.enableCodeInsertion", null);
				break;
			}
			case "BACKEND": {
				String backend = event.getNewValue().toString();
				authorizationTokenEditor.setEnabled(backend.equals("Tabby"), getFieldEditorParent());
			}
			case "SCHEME", "SERVER_HOST", "SERVER_PORT", "AUTHORIZATION_TOKEN": {
				try {
					TextCompletionService.getInstance().connect();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				break;
			}
			default:
				break;

			}
		}
	}
}
