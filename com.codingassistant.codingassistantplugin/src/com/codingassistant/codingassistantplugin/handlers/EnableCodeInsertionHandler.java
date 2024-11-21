package com.codingassistant.codingassistantplugin.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Handler for enabling or disabling code insertion functionality.
 * This class extends AbstractHandler and implements IElementUpdater to handle
 * the state change and UI update for the code insertion feature.
 * 
 * <p>
 * The handler toggles the state of the code insertion command and updates the
 * corresponding icon and tooltip in the UI.
 * </p>
 * 
 * <p>
 * The state is persisted in the ScopedPreferenceStore to maintain the state
 * across sessions.
 * </p>
 * 
 * <p>
 * The following icons are used to represent the state:
 * <ul>
 * <li>{@code /icons/toggle_on.png} - When code insertion is enabled</li>
 * <li>{@code /icons/toggle_off.png} - When code insertion is disabled</li>
 * </ul>
 * </p>
 * 
 */
public class EnableCodeInsertionHandler extends AbstractHandler implements IElementUpdater {

	private static final String ENABLED_ICON_PATH = "/icons/toggle_on.png";
	private static final String DISABLED_ICON_PATH = "/icons/toggle_off.png";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand("com.codingassistant.codingassistantplugin.commands.enableCodeInsertion");
		State state = command.getState("org.eclipse.ui.commands.toggleState");

		boolean isEnabled = !(Boolean) state.getValue();
		state.setValue(isEnabled);

		// Save the new state to the ScopedPreferenceStore
		ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"com.codingassistant.codingassistantplugin");
		preferenceStore.setValue("ENABLE_INSERTION", isEnabled);

		// Refresh the UI to reflect the state change
		commandService.refreshElements("com.codingassistant.codingassistantplugin.commands.enableCodeInsertion", null);

		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
//        ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
//        Command command = commandService.getCommand("com.codingassistant.codingassistantplugin.commands.enableCodeInsertion");
//        State state = command.getState("org.eclipse.ui.commands.toggleState");
//
//        boolean isEnabled = (Boolean) state.getValue();

		ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"com.codingassistant.codingassistantplugin");
		boolean isEnabled = preferenceStore.getBoolean("ENABLE_INSERTION");

		// Set the icon based on the state
		String iconPath = isEnabled ? ENABLED_ICON_PATH : DISABLED_ICON_PATH;

		ImageDescriptor icon = ImageDescriptor.createFromFile(EnableCodeInsertionHandler.class, iconPath);

		if (icon == null) {
			System.out.println("Failed to load icon: " + iconPath);
		}

		element.setIcon(icon);
		System.out.println("Icon set: " + iconPath);

		// Optionally, update tooltip text to indicate the current state
		String tooltip = isEnabled ? "Disable Code Insertion" : "Enable Code Insertion";
		element.setTooltip(tooltip);
	}
}
