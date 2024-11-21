package com.codingassistant.codingassistanttests;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Before;
import org.junit.Test;

import com.codingassistant.codingassistantplugin.handlers.EnableCodeInsertionHandler;

public class EnableCodeInsertionHandlerTest {

    private EnableCodeInsertionHandler handler;
    private ICommandService commandService;
    private Command command;
    private ExecutionEvent event;

    @Before
    public void setUp() {
        handler = new EnableCodeInsertionHandler();
        commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
        command = commandService.getCommand("com.codingassistant.codingassistantplugin.commands.enableCodeInsertion");
        event = new ExecutionEvent();
    }

    @Test
    public void testExecute_whenStateIsTrue_shouldSetToFalse() throws ExecutionException {
        // Set initial state to true
        State state = command.getState("org.eclipse.ui.commands.toggleState");
        state.setValue(true);
        
        // Execute handler
        handler.execute(event);
        
        // Assert that the state has changed to false
        assertEquals(false, state.getValue());
    }

    @Test
    public void testExecute_whenStateIsFalse_shouldSetToTrue() throws ExecutionException {
        // Set initial state to false
        State state = command.getState("org.eclipse.ui.commands.toggleState");
        state.setValue(false);
        
        // Execute handler
        handler.execute(event);
        
        // Assert that the state has changed to true
        assertEquals(true, state.getValue());
    }
}
