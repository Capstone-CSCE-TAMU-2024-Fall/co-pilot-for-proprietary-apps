package com.plugin.copilotassistant.handlers;

import java.awt.event.KeyEvent;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;

public record SuggestionKeyListener(IDocument document, int offset, int insertedLength, StyledText styledText)
		implements VerifyKeyListener {

	@Override
	public void verifyKey(VerifyEvent e) {
		// If Tab is pressed, keep the text as is
		if (e.keyCode == KeyEvent.VK_TAB) {
			// Do nothing on Tab key
			e.doit = false;
		} else {
			// Remove the inserted text
			Display.getDefault().asyncExec(() -> {
				try {
					document.replace(offset, insertedLength, "");
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			});
		}
		styledText.removeVerifyKeyListener(this);
	}

}
