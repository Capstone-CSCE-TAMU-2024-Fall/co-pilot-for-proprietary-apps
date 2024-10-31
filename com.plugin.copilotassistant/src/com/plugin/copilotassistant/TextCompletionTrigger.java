package com.plugin.copilotassistant;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
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

//		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
//		if (document != null) {
//			document.addDocumentListener(this);
//		}
	}

	public void unregister(ITextEditor textEditor) {
//		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
//		if (document != null) {
//			document.removeDocumentListener(this);
//		}
	}
//
//	@Override
//	public void documentAboutToBeChanged(DocumentEvent event) {
//
//	}
//
//	@Override
//	public void documentChanged(DocumentEvent event) {
//		// TODO: Move logic that gets response to FauxpilotCompletionService
//
//		TextCompletionService textCompletionService = FauxpilotCompletionService.getInstance();
//
//		// TODO: Make it so this does not infinitely loop because it triggers itself by
//		// updating the document.
//		textCompletionService.trigger();
//
////		styledText.addPaintListener(paintListener);
////		styledText.redraw();
////		styledText.removePaintListener(paintListener);
//		System.out.println("Document changed");
//	}

	@Override
	public void caretMoved(CaretEvent event) {
		if (!justTriggered) {
			TextCompletionService textCompletionService = FauxpilotCompletionService.getInstance();
			textCompletionService.dismiss();
		} else {
			justTriggered = false;
		}
		System.out.println("Caret moved");
//		styledText.redraw();

	}

	@Override
	public void verifyKey(VerifyEvent event) {
		if (event.character == 0 && (event.keyCode & SWT.KEYCODE_BIT) == 0)
			return;

		if (event.character != 0
				&& (event.stateMask == SWT.ALT || event.stateMask == SWT.CONTROL || event.stateMask == SWT.COMMAND))
			return;
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
