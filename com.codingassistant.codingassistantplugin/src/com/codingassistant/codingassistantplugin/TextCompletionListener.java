package com.codingassistant.codingassistantplugin;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.texteditor.ITextEditor;

// Listens to the TextEditor and Document to determine when it is necessary to call the TextCompletionService
/**
 * The TextCompletionListener class implements CaretListener and VerifyKeyListener
 * to provide text completion functionality in an ITextEditor.
 * 
 * <p>This listener registers itself to an ITextEditor and listens for caret movements
 * and key events to trigger or dismiss text completions.</p>
 * 
 * <p>It uses the TextCompletionService to manage text completions.</p>
 * 
 * <p>Methods:</p>
 * <ul>
 *   <li>{@link #register(ITextEditor)} - Registers the listener to the given ITextEditor.</li>
 *   <li>{@link #unregister(ITextEditor)} - Unregisters the listener from the given ITextEditor.</li>
 *   <li>{@link #caretMoved(CaretEvent)} - Handles caret movement events.</li>
 *   <li>{@link #verifyKey(VerifyEvent)} - Handles key verification events.</li>
 * </ul>
 * 
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code ITextEditor textEditor} - The text editor to which this listener is registered.</li>
 *   <li>{@code boolean justTriggered} - A flag to indicate if a text completion was just triggered.</li>
 * </ul>
 */
public class TextCompletionListener implements CaretListener, VerifyKeyListener {

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
			System.out.println("Caret moved, justTriggered is false");
			TextCompletionService textCompletionService = TextCompletionService.getInstance();
			textCompletionService.dismiss();
		} else {
			System.out.println("Caret moved, justTriggered set to false");
			justTriggered = false;
		}
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

		TextCompletionService textCompletionService = TextCompletionService.getInstance();

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

		System.out.println("accept: " + accept);

		if (accept) {
			event.doit = false;
			justTriggered = true;
		} else if (!isArrowKey) {
			textCompletionService.trigger();
			justTriggered = true;
		}
	}

}
