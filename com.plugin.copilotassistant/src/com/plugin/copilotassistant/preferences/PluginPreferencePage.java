package com.plugin.copilotassistant.preferences;

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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.plugin.copilotassistant.TextCompletionService;

/**
 * This class represents the preference page for the Copilot Assistant plugin.
 * It extends FieldEditorPreferencePage and implements IWorkbenchPreferencePage.
 * The preference page allows users to configure various settings for the plugin.
 * 
 * <p>Preferences include:</p>
 * <ul>
 *   <li>Server Host</li>
 *   <li>Server Port</li>
 *   <li>Backend selection (Fauxpilot or Tabby)</li>
 *   <li>Tabby Authorization Token</li>
 *   <li>Protocol (HTTP or HTTPS)</li>
 *   <li>Max Tokens</li>
 *   <li>Max Lines</li>
 *   <li>Temperature</li>
 *   <li>Suggestion Delay</li>
 *   <li>Debug Mode</li>
 *   <li>Enable Code Insertion</li>
 * </ul>
 * 
 * <p>The class also listens for changes to preferences and updates the plugin's
 * behavior accordingly.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * PluginPreferencePage preferencePage = new PluginPreferencePage();
 * preferencePage.createFieldEditors();
 * }
 * </pre>
 * 
 */
public class PluginPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	ScopedPreferenceStore copilotPreferences;
	IPropertyChangeListener propertyListener;

	public PluginPreferencePage() {
		super(GRID);
		// Use a ScopedPreferenceStore instead of an Activator to manage preferences
		copilotPreferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant");
		setPreferenceStore(copilotPreferences);
	}

	@Override
	protected void createFieldEditors() {
		Composite fieldEditorParent = getFieldEditorParent();
		// Add fields for your preferences
		addField(new StringFieldEditor("SERVER_HOST", "Server Host:", fieldEditorParent));
		addField(new StringFieldEditor("SERVER_PORT", "Server Port:", fieldEditorParent));
		// Dropdown for selecting engine
		String[][] backendOptions = { { "Fauxpilot", "Fauxpilot" }, { "Tabby", "Tabby" } };
		addField(new ComboFieldEditor("BACKEND", // Key to save preference
				"Select Backend:", // Label text
				backendOptions, fieldEditorParent));

		StringFieldEditor authorizationTokenEditor = new StringFieldEditor("AUTHORIZATION_TOKEN",
				"Tabby Authorization Token:", fieldEditorParent);
		addField(authorizationTokenEditor);
		authorizationTokenEditor.setEnabled(copilotPreferences.getString("BACKEND").equals("Tabby"), fieldEditorParent);

		String[][] protocols = new String[][] { { "HTTP", "http" }, { "HTTPS", "https" } };
		addField(new RadioGroupFieldEditor("SCHEME", "Protocol:", 1, protocols, fieldEditorParent));
		addField(new IntegerFieldEditor("MAX_TOKENS", "Max Tokens:", fieldEditorParent));
		addField(new IntegerFieldEditor("MAX_LINES", "Max Lines:", fieldEditorParent));

		// addField(new StringFieldEditor("MODEL", "Model:", getFieldEditorParent()));
		addField(new StringFieldEditor("TEMPERATURE", "Temperature:", fieldEditorParent));
		addField(new IntegerFieldEditor("SUGGESTION_DELAY", "Suggestion Delay (ms):", fieldEditorParent));
		// Add a Boolean field for your preferences
		addField(new BooleanFieldEditor("DEBUG_MODE", "Debug Mode", fieldEditorParent));

		// Add a Boolean (Toggle Button) field for your preferences
		addField(new BooleanFieldEditor("ENABLE_INSERTION", "Enable Code Insertion", fieldEditorParent));
		
		propertyListener = event -> {
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
				System.out.println(backend.equals("Tabby"));
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
		};
		copilotPreferences.addPropertyChangeListener(propertyListener);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public void dispose() {
		super.dispose();
		copilotPreferences.removePropertyChangeListener(propertyListener);
	}

}
