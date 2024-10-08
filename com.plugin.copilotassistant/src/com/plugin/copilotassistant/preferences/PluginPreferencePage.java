package com.plugin.copilotassistant.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class PluginPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public PluginPreferencePage() {
        super(GRID);
        // Use the default preference store from the PlatformUI
        setPreferenceStore(PlatformUI.getPreferenceStore());
        setDescription("Custom settings for Your Plugin.");
    }

    @Override
    protected void createFieldEditors() {
        // Add fields for your preferences
        addField(new StringFieldEditor("YOUR_SETTING_KEY", "Your Setting:", getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench) {
        // No specific initialization required
    }
}
