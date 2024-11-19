package com.plugin.copilotassistant;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.State;
import org.eclipse.core.commands.common.NotDefinedException;

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
			} catch (NotDefinedException e) {
				e.printStackTrace();
			} catch (NotEnabledException e) {
				e.printStackTrace();
			} catch (NotHandledException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void toggleEnableCodeInsertionCommand() throws NotDefinedException, NotEnabledException, NotHandledException {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		if (commandService != null) {
			Command command = commandService.getCommand("com.plugin.copilotassistant.commands.enableCodeInsertion");
			if (command != null) {
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
			} else {
				System.out.println("Command enableCodeInsertion not found.");
			}
		} else {
			System.out.println("Startup: ICommandService not available, toggle command failed.");
		}
	}
}
