package com.plugin.copilotassistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

public class SampleHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IEditorPart editor = window.getActivePage().getActiveEditor();

        if (editor instanceof ITextEditor) {
            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

            try {
                ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
                int offset = selection.getOffset();
                String textToInsert = "Done";
                document.replace(offset, 0, textToInsert);
                
             // Get the StyledText widget
                StyledText styledText = (StyledText) textEditor.getAdapter(org.eclipse.swt.widgets.Control.class);

                if (styledText != null) {
                    // Create a StyleRange to apply the green color
                    StyleRange styleRange = new StyleRange();
                    styleRange.start = offset;
                    styleRange.length = textToInsert.length();
                    styleRange.foreground = Display.getDefault().getSystemColor(org.eclipse.swt.SWT.COLOR_GREEN);

                    // Apply the StyleRange
                    styledText.setStyleRange(styleRange);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}