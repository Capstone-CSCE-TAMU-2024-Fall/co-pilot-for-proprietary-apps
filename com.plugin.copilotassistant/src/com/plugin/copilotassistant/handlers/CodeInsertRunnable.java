package com.plugin.copilotassistant.handlers;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;

public record CodeInsertRunnable(boolean enabled, String textToInsert, int offset,
		ITextEditor textEditor) implements Runnable {

	@Override
	public void run() {
		String actualTextToInsert = "$";
		if (enabled) {
			actualTextToInsert = textToInsert;
		}

		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		
		try {
			document.replace(offset, 0, actualTextToInsert);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return;
		}
		// Get the StyledText widget
		var styledText = Adapters.adapt(Adapters.adapt(textEditor, Control.class), StyledText.class);
		if (styledText != null) {
			// Create a StyleRange to apply the gray color
			int insertedLength = actualTextToInsert.length();
			var styleRange = new StyleRange();
			styleRange.start = offset;
			styleRange.length = insertedLength;
//			System.out.println(offset);
			styleRange.foreground = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);

			// Apply the StyleRange (make the text gray)
			styledText.setStyleRange(styleRange);

			// Move the cursor to the end of the inserted text
			styledText.setCaretOffset(offset + insertedLength);
			if (enabled) {
				// Add a listener for key events after insertion
				styledText
						.addVerifyKeyListener(new SuggestionKeyListener(document, offset, insertedLength, styledText));
			}
		}
	}

}
