package com.plugin.copilotassistant.preferences;


import org.eclipse.jface.util.IPropertyChangeListener;
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

public class PluginPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PluginPreferencePage() {
		super(GRID);
		// Use a ScopedPreferenceStore instead of an Activator to manage preferences
		ScopedPreferenceStore copilotPreferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant");
		IPropertyChangeListener  propertyListener = new PropertyChangeListener();
		copilotPreferences.addPropertyChangeListener(propertyListener);
		setPreferenceStore(copilotPreferences);
	}

	@Override
	protected void createFieldEditors() {
		// Add fields for your preferences
		addField(new StringFieldEditor("SERVER_HOST", "Server Host:", getFieldEditorParent()));
		addField(new StringFieldEditor("SERVER_PORT", "Server Port:", getFieldEditorParent()));
		// Dropdown for selecting engine
	    String[][] engineOptions = {
	    	{"FauxPilot", "FauxPilot"},
	        {"Tabby", "Tabby"}
	    };
	    addField(new ComboFieldEditor(
	        "ENGINE", // Key to save preference
	        "Select Engine:", // Label text
	        engineOptions, 
	        getFieldEditorParent()
	    ));
		addField(new RadioGroupFieldEditor("SCHEME", "Protocol:", 1,
				new String[][] { { "HTTP", "http" }, { "HTTPS", "https" } }, getFieldEditorParent()));
		addField(new IntegerFieldEditor("MAX_TOKENS", "Max Tokens:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("MAX_LINES", "Max Lines:", getFieldEditorParent()));
		//addField(new StringFieldEditor("ENGINE", "Engine:", getFieldEditorParent()));
	
		//addField(new StringFieldEditor("MODEL", "Model:", getFieldEditorParent()));
		addField(new StringFieldEditor("TEMPERATURE", "Temperature:", getFieldEditorParent()));
		addField(new IntegerFieldEditor("SUGGESTION_DELAY", "Suggestion Delay (ms):", getFieldEditorParent()));
		// Add a Boolean field for your preferences
		addField(new BooleanFieldEditor("DEBUG_MODE", "Debug Mode", getFieldEditorParent()));
		
		// Add a Boolean (Toggle Button) field for your preferences
		addField(new BooleanFieldEditor("ENABLE_INSERTION", "Enable Code Insertion", getFieldEditorParent()));
		
	    // Synchronize the preference with the handler state
	    getPreferenceStore().addPropertyChangeListener(event -> {
	        if ("ENABLE_INSERTION".equals(event.getProperty())) {
	        	boolean newState = Boolean.parseBoolean(event.getNewValue().toString());
	            
	            ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
	            Command command = commandService.getCommand("com.plugin.copilotassistant.commands.enableCodeInsertion");
	            State state = command.getState("org.eclipse.ui.commands.toggleState");
	            state.setValue(newState);

	            commandService.refreshElements("com.plugin.copilotassistant.commands.enableCodeInsertion", null);
	        }
	    });
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
