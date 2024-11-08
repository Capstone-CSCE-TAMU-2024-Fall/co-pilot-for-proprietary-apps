package com.plugin.copilotassistant;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.texteditor.ITextEditor;

import com.plugin.copilotassistant.backendconnection.TextCompletionService;
import com.plugin.copilotassistant.fauxpilotconnection.FauxpilotCompletionService;

// Listens to the TextEditor and Document to determine when it is necessary to call the TextCompletionService
public class TextCompletionTrigger implements CaretListener, VerifyKeyListener {

	ITextEditor textEditor;
	boolean justTriggered = false;

	public void register(ITextEditor textEditor) {
		this.textEditor = textEditor;

		ITextViewer textViewer = Adapters.adapt(textEditor, ITextViewer.class);
		if (textViewer != null) {
			StyledText styledText = textViewer.getTextWidget();
			if (styledText != null) {
				styledText.getDisplay().asyncExec(() -> {
					styledText.addCaretListener(this);
					styledText.addVerifyKeyListener(this);
				});
			}
		}

	}

	public void unregister(ITextEditor textEditor) {
	}

	@Override
	public void caretMoved(CaretEvent event) {
		if (!justTriggered) {
			TextCompletionService textCompletionService = FauxpilotCompletionService.getInstance();
			textCompletionService.dismiss();
		} else {
			justTriggered = false;
		}
		System.out.println("Caret moved");
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		if (event.character == 0 && (event.keyCode & SWT.KEYCODE_BIT) == 0)
			return;

		if (event.character != 0
				&& (event.stateMask == SWT.ALT || event.stateMask == SWT.CONTROL || event.stateMask == SWT.COMMAND))
			return;
		if (event.keyCode == SWT.TAB) {
			try {
				textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput())
				.replace(FauxpilotCompletionService.getInsertOffset(), 0, FauxpilotCompletionService.getLastTextToInsert());
				FauxpilotCompletionService.setLastTextToInsert("");
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		boolean isValidKey = Character.isLetterOrDigit(event.character) || Character.isWhitespace(event.character);
//		System.out.println("isValid: " + isValid);
//		System.out.println("Character: " + event.character);
		System.out.println("KeyCode: " + event.keyCode);
		boolean isValidKey = !(event.keyCode >= SWT.ARROW_UP && event.keyCode <= SWT.ARROW_RIGHT);
		if (isValidKey) {
			TextCompletionService textCompletionService = FauxpilotCompletionService.getInstance();
			textCompletionService.trigger();
			justTriggered = true;
		}
	}

}
