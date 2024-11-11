package com.plugin.copilotassistant;

import org.eclipse.core.runtime.Adapters;
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
		if (event.character == 0 && (event.keyCode & SWT.KEYCODE_BIT) == 0) {
			return;
		}

		if (event.character != 0
				&& (event.stateMask == SWT.ALT || event.stateMask == SWT.CONTROL || event.stateMask == SWT.COMMAND)) {
			return;
		}

		TextCompletionService textCompletionService = FauxpilotCompletionService.getInstance();

		if (event.keyCode == SWT.ESC) {
			textCompletionService.dismiss();
			return;
		}

		System.out.println("KeyCode: " + event.keyCode);
		boolean isArrowKey = event.keyCode >= SWT.ARROW_UP && event.keyCode <= SWT.ARROW_RIGHT;
		boolean accept = false;

		if (event.keyCode == SWT.TAB) {
			accept = textCompletionService.accept();
		}

		if (accept) {
			event.doit = false;
		} else if (!isArrowKey) {
			textCompletionService.trigger();
			justTriggered = true;
		}
	}

}
