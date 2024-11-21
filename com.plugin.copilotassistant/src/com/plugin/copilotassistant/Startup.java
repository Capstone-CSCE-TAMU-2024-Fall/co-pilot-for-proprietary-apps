package com.plugin.copilotassistant;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.State;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * The Startup class implements the IStartup interface to perform actions during the early startup phase of the Eclipse plugin.
 * It registers an EditorActivationListener and triggers a toggle command to set the initial UI state.
 */
public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		System.out.println("Starting up.");
		EditorActivationListener listener = new EditorActivationListener();
		listener.registerEditorActivationListener();
		System.out.println("Registered EditorActivationListener.");

		// Trigger the toggle command on startup to set the correct initial UI state
		PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
			try {
				toggleEnableCodeInsertionCommand();
			} catch (NotDefinedException | NotEnabledException | NotHandledException e) {
				e.printStackTrace();
			}
		});
	}

	private void toggleEnableCodeInsertionCommand()
			throws NotDefinedException, NotEnabledException, NotHandledException {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		if (commandService == null) {
			System.out.println("Startup: ICommandService not available, toggle command failed.");
			return;
		}
		Command command = commandService.getCommand("com.plugin.copilotassistant.commands.enableCodeInsertion");
		if (command == null) {
			System.out.println("Command enableCodeInsertion not found.");
			return;
		}
		try {
			State state = command.getState("org.eclipse.ui.commands.toggleState");
			boolean isEnabled = (Boolean) state.getValue();
			state.setValue(!isEnabled); // Toggle the state

			// Execute the command to apply the toggle and update the UI
			command.executeWithChecks(new ExecutionEvent());
			System.out.println("Startup: Toggled enableCodeInsertion command to set UI state.");
		} catch (ExecutionException e) {
			System.err.println("Failed to execute enableCodeInsertion command: " + e.getMessage());
		}
	}
}
