package com.plugin.copilotassistant;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

// Displays any text passed to it in the current TextViewer
public class TextRenderer implements IPainter, PaintListener {
	private ITextViewer viewer;
	private List<Consumer<GC>> paintFunctions = new ArrayList<>();
	private IPaintPositionManager positionManager;
	private Font font;
	private List<ModifiedLineVerticalIndent> modifiedLinesVerticalIndent = new ArrayList<>();
	private List<GlyphMetrics> modifiedGlyphMetrics = new ArrayList<>();

	public TextRenderer(ITextViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void paintControl(PaintEvent event) {
		paintFunctions.forEach((fn) -> {
			fn.accept(event.gc);
		});
	}

	@Override
	public void dispose() {
		if (font != null) {
			font.dispose();
			font = null;
		}
	}

	public void setupPainting(String textToInsert) {
		StyledText styledText = viewer.getTextWidget();
		int offset = styledText.getCaretOffset();
		int lineHeight = styledText.getLineHeight();
		List<String> lines = textToInsert.lines().toList();

		// Leave the space for the ghost text
		int nextLine = styledText.getLineAtOffset(offset) + 1;
		if (nextLine < styledText.getLineCount()) {
			int lineCount = lines.size();
			int originVerticalIndent = styledText.getLineVerticalIndent(nextLine);
			Position position = new Position(styledText.getOffsetAtLine(nextLine), 0);
			positionManager.managePosition(position);
			int modifiedVerticalIndent = originVerticalIndent + lineCount * lineHeight;
			modifiedLinesVerticalIndent
					.add(new ModifiedLineVerticalIndent(position, originVerticalIndent, modifiedVerticalIndent));
			styledText.setLineVerticalIndent(nextLine, modifiedVerticalIndent);
			System.out.println("Set LineVerticalIndent:" + nextLine + " -> " + modifiedVerticalIndent);
		}

		List<TextWithTabs> linesTextWithTab = new ArrayList<>();
		for (String line : lines) {
			linesTextWithTab.add(splitLeadingTabs(line));
		}

		paintFunctions.add(gc -> {
			// Draw ghost text
			setStyleToGhostText(styledText, gc);
			
			int spaceWidth = gc.textExtent(" ").x;
			Point location = styledText.getLocationAtOffset(offset);
			int y = location.y;
			for (TextWithTabs textWithTabs : linesTextWithTab) {
				int x = styledText.getLeftMargin() + textWithTabs.tabs * styledText.getTabs() * spaceWidth;
				y += lineHeight;
				gc.drawString(textWithTabs.text, x, y, true);
			}
		});
	}

	private void setStyleToGhostText(StyledText styledText, GC gc) {
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		FontDescriptor fontDescriptor = FontDescriptor.createFrom(styledText.getFont());
		fontDescriptor = fontDescriptor.setStyle(SWT.ITALIC);
		this.dispose();
		font = fontDescriptor.createFont(Display.getCurrent());
		gc.setFont(font);
	}

	public void cleanupPainting() {
		try {
			paintFunctions.clear();

			StyledText styledText = viewer.getTextWidget();
			modifiedLinesVerticalIndent.forEach((modifiedLineVerticalIndent) -> {
				Position position = modifiedLineVerticalIndent.position;
				int line = styledText.getLineAtOffset(position.getOffset());
				positionManager.unmanagePosition(position);
				int indent = modifiedLineVerticalIndent.indent;
				int modifiedIndent = modifiedLineVerticalIndent.modifiedIndent;
				// Find the line to restore the indent
				int lineToRestore = -1;
				int delta = 0;
				while (delta < styledText.getLineCount()) {
					lineToRestore = line + delta;
					if (lineToRestore >= 0 && lineToRestore < styledText.getLineCount()
							&& styledText.getLineVerticalIndent(lineToRestore) == modifiedIndent) {
						break;
					}
					lineToRestore = line - delta;
					if (lineToRestore >= 0 && lineToRestore < styledText.getLineCount()
							&& styledText.getLineVerticalIndent(lineToRestore) == modifiedIndent) {
						break;
					}
					delta++;
				}
				if (lineToRestore >= 0 && lineToRestore < styledText.getLineCount()) {
					styledText.setLineVerticalIndent(lineToRestore, indent);
					System.out.println("Restore LineVerticalIndent: " + lineToRestore + " -> " + indent);
				}
				
			});
			modifiedLinesVerticalIndent.clear();

			StyleRange[] styleRanges = styledText.getStyleRanges();
			for (StyleRange styleRange : styleRanges) {
				if (modifiedGlyphMetrics.contains(styleRange.metrics)) {
					styleRange.metrics = null;
					styledText.setStyleRange(styleRange);
					System.out.println("Restore StyleRange:" + styleRange.start + " -> " + styleRange.metrics);
				}
			}
			modifiedGlyphMetrics.clear();

		} catch (Exception e) {
			System.out.println("Failed to cleanup renderer." + e);
		}
	}

	private void paintText(GC gc) {
		StyledText styledText = viewer.getTextWidget();
		int caretOffset = styledText.getCaretOffset();
		try {
			Point location = styledText.getLocationAtOffset(caretOffset);
			gc.drawString("Test", location.x, location.y, true);
		} catch (IllegalArgumentException ex) {
			// Handle out of bounds offsets
			System.out.println("Out of bounds offset: " + caretOffset);
		}
	}

	static final Pattern PATTERN_LEADING_TABS = Pattern.compile("^(\\t*)(.*)$");

	private static TextWithTabs splitLeadingTabs(String text) {
		Matcher matcher = PATTERN_LEADING_TABS.matcher(text);
		if (matcher.matches()) {
			return new TextWithTabs(matcher.group(1).length(), matcher.group(2));
		} else {
			return new TextWithTabs(0, text);
		}
	}

	private static class ModifiedLineVerticalIndent {
		private Position position;
		private int indent;
		private int modifiedIndent;

		public ModifiedLineVerticalIndent(Position position, int indent, int modifiedIndent) {
			this.position = position;
			this.indent = indent;
			this.modifiedIndent = modifiedIndent;
		}
	}

	private static class TextWithTabs {
		private int tabs;
		private String text;

		public TextWithTabs(int tabs, String text) {
			this.tabs = tabs;
			this.text = text;
		}
	}

	@Override
	public void paint(int reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate(boolean redraw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositionManager(IPaintPositionManager manager) {
		this.positionManager = manager;

	}
}
