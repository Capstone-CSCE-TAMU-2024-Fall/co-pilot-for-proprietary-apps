package com.plugin.copilotassistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * Handler to open the preferences dialog and navigate to a specific preferences page.
 * This class extends AbstractHandler and overrides the execute method to perform the action.
 */
public class OpenPreferencesHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Open the preferences dialog and navigate to your specific preferences page
		PreferenceDialog prefDialog = PreferencesUtil.createPreferenceDialogOn(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"com.plugin.copilotassistant.preferences.PluginPreferencePage", // ID of your preferences page
				null, null);
		if (prefDialog != null) {
			prefDialog.open();
		}
		return null;
	}
}
