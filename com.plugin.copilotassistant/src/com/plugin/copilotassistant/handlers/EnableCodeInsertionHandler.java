package com.plugin.copilotassistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class EnableCodeInsertionHandler extends AbstractHandler {


    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();        

        ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
        Command command = commandService.getCommand("com.plugin.copilotassistant.commands.enableCodeInsertion");
        State state = command.getState("org.eclipse.ui.commands.toggleState");        

        boolean isEnabled = !(Boolean) state.getValue();
        state.setValue(isEnabled);
        
        String message = isEnabled ? "Code Insertion Enabled" : "Code Insertion Disabled";
        MessageDialog.openInformation(shell, "Code Insertion", message);

        return null;
    }
}
