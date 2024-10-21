package com.plugin.copilotassistant.handlers;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CodeInsertHandlerTest {
    
    private CodeInsertHandler handler;
    private ITextEditor editor;
    private IDocument document;
    private ITextSelection selection;
    private ExecutionEvent event;
    private IPreferenceStore preferenceStore;
    
    @Before
    public void setUp() {
        handler = new CodeInsertHandler();
        editor = Mockito.mock(ITextEditor.class);
        document = Mockito.mock(IDocument.class);
        selection = Mockito.mock(ITextSelection.class);
        event = Mockito.mock(ExecutionEvent.class);
        
        // Mock the preference store and the platform UI behavior
        preferenceStore = Mockito.mock(IPreferenceStore.class);
        Mockito.when(PlatformUI.getPreferenceStore()).thenReturn(preferenceStore);

        // Setup editor and selection behavior
        Mockito.when(editor.getDocumentProvider().getDocument(Mockito.any())).thenReturn(document);
        Mockito.when(editor.getSelectionProvider().getSelection()).thenReturn(selection);
    }
    
    @Test
    public void testCodeInsertionWhenEnabled() throws Exception {
        // Arrange
        Mockito.when(selection.getOffset()).thenReturn(0);
        Mockito.when(preferenceStore.getBoolean("ENABLE_INSERTION")).thenReturn(true); // Simulate toggle being enabled

        // Act
        handler.execute(event);  // Pass the mock ExecutionEvent

        // Assert
        Mockito.verify(document).replace(0, 0, "$Done");  // Verify that "$Done" was inserted
    }
    
    @Test
    public void testCodeInsertionWhenDisabled() throws Exception {
        // Arrange
        Mockito.when(selection.getOffset()).thenReturn(0);
        Mockito.when(preferenceStore.getBoolean("ENABLE_INSERTION")).thenReturn(false); // Simulate toggle being disabled

        // Act
        handler.execute(event);  // Pass the mock ExecutionEvent

        // Assert
        Mockito.verify(document).replace(0, 0, "$");  // Verify that only "$" was inserted
    }
}

