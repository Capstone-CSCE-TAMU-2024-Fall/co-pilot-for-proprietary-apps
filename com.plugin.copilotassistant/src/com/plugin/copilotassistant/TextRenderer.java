package com.plugin.copilotassistant;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;

// Displays any text passed to it in the current TextViewer
public class TextRenderer implements PaintListener {
	ITextViewer viewer;

	public void setViewer(ITextViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void paintControl(PaintEvent e) {
		StyledText styledText = viewer.getTextWidget();
		int caretOffset = styledText.getCaretOffset();
		try {
			Point location = styledText.getLocationAtOffset(caretOffset);
			e.gc.drawString("↯ Runick Tip", location.x, location.y - 10, true);
		} catch (IllegalArgumentException ex) {
			// Handle out of bounds offsets
			System.out.println("Out of bounds offset: " + caretOffset);
		}
	}
}
