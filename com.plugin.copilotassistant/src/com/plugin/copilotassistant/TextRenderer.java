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
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

// Methods for rendering are largely from the Tabby Eclipse plugin, with some modifications

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
		if (textToInsert == null) {
			return;
		}
		StyledText styledText = viewer.getTextWidget();
		int offset = styledText.getCaretOffset();

		if (textToInsert.isEmpty()) {
			return;
		}
		System.out.println("Begin setupPainting...");

		int currentLineEndOffset;
		int nextLineNumber = styledText.getLineAtOffset(offset) + 1;
		if (nextLineNumber < styledText.getLineCount()) {
			currentLineEndOffset = styledText.getOffsetAtLine(nextLineNumber) - 1;
		} else {
			currentLineEndOffset = styledText.getCharCount() - 1;
		}
		String currentLineSuffix = "";
		if (offset < styledText.getCharCount() && offset < currentLineEndOffset) {
			currentLineSuffix = styledText.getText(offset, currentLineEndOffset);
		}
		System.out.println("currentLineSuffix: " + currentLineSuffix);

		String textCurrentLine;
		String textSuffixLines;
		int firstLineBreakIndex = textToInsert.indexOf("\n");
		if (firstLineBreakIndex == -1) {
			textCurrentLine = textToInsert;
			textSuffixLines = "";
		} else {
			textCurrentLine = textToInsert.substring(0, firstLineBreakIndex);
			textSuffixLines = textToInsert.substring(firstLineBreakIndex + 1);
		}

		int suffixReplaceLength = 0;

		if (suffixReplaceLength == 0 || currentLineSuffix.isEmpty()) {
			// No replace range to handle
			if (textSuffixLines.isEmpty()) {
				drawInsertPartText(styledText, textCurrentLine, offset);
			} else {
				if (!textCurrentLine.isEmpty()) {
					drawOverwriteText(styledText, textCurrentLine, offset);
				}
				drawSuffixLines(styledText, textSuffixLines + currentLineSuffix, offset);
			}
		} else if (suffixReplaceLength == 1) {
			// Replace range contains one char
			char replaceChar = currentLineSuffix.charAt(0);
			int replaceCharIndex = textCurrentLine.indexOf(replaceChar);
			if (replaceCharIndex > 0) {
				// If textCurrentLine contain the replaceChar
				// InsertPart is substring of textCurrentLine that before the replaceChar
				// AppendPart is substring of textCurrentLine that after the replaceChar
				String insertPart = textCurrentLine.substring(0, replaceCharIndex);
				String appendPart = textCurrentLine.substring(replaceCharIndex + 1);
				if (!insertPart.isEmpty()) {
					drawInsertPartText(styledText, insertPart, offset);
				}
				if (!appendPart.isEmpty()) {
					if (textSuffixLines.isEmpty()) {
						drawInsertPartText(styledText, appendPart, offset + 1);
					} else {
						drawOverwriteText(styledText, appendPart, offset + 1);
					}
				}
			} else {
				drawReplacePartText(styledText, textCurrentLine, currentLineSuffix.substring(0, 1), offset);
			}
			if (!textSuffixLines.isEmpty()) {
				drawSuffixLines(styledText, textSuffixLines + currentLineSuffix.substring(1), offset);
			}
		} else {
			// Replace range contains multiple chars
			if (textSuffixLines.isEmpty()) {
				drawReplacePartText(styledText, textCurrentLine, currentLineSuffix.substring(0, suffixReplaceLength),
						offset);
			} else {
				if (!textCurrentLine.isEmpty()) {
					drawOverwriteText(styledText, textCurrentLine, offset);
				}
				drawSuffixLines(styledText, textSuffixLines + currentLineSuffix.substring(suffixReplaceLength), offset);
			}
		}

	}

	private void drawOverwriteText(StyledText styledText, String textToInsert, int offset) {
		System.out.println("drawCurrentLineText:" + offset + ":" + textToInsert);
		TextWithTabs textWithTabs = splitLeadingTabs(textToInsert);

		paintFunctions.add((gc) -> {
			// Draw ghost text
			setStyleToGhostText(styledText, gc);
			int spaceWidth = gc.textExtent(" ").x;
			int tabWidth = textWithTabs.tabs * styledText.getTabs() * spaceWidth;
			Point location = styledText.getLocationAtOffset(offset);
			gc.drawString(textWithTabs.text, location.x + tabWidth, location.y);
		});
	}

	private void drawInsertPartText(StyledText styledText, String textToInsert, int offset) {
		drawReplacePartText(styledText, textToInsert, "", offset);
	}

	private void drawReplacePartText(StyledText styledText, String textToInsert, String replacedText, int offset) {
		System.out.println("drawReplacePartText:" + offset + ":" + textToInsert + ":" + replacedText);
		TextWithTabs textWithTabs = splitLeadingTabs(textToInsert);

		int targetOffset = offset + replacedText.length();
		if (targetOffset >= styledText.getCharCount()) {
			// End of document, draw the ghost text only
			paintFunctions.add((gc) -> {
				// Draw ghost text
				setStyleToGhostText(styledText, gc);
				int spaceWidth = gc.textExtent(" ").x;
				int tabWidth = textWithTabs.tabs * styledText.getTabs() * spaceWidth;
				Point location = styledText.getLocationAtOffset(offset);
				gc.drawString(textWithTabs.text, location.x + tabWidth, location.y);
			});

		} else {
			// otherwise, draw the ghost text, and move target char after the ghost text
			String targetChar = styledText.getText(targetOffset, targetOffset);
			StyleRange originStyleRange;
			if (styledText.getStyleRangeAtOffset(targetOffset) != null) {
				originStyleRange = styledText.getStyleRangeAtOffset(targetOffset);
				System.out.println(
						"Find origin StyleRange:" + originStyleRange.start + " -> " + originStyleRange.metrics);
			} else {
				originStyleRange = new StyleRange();
				originStyleRange.start = targetOffset;
				originStyleRange.length = 1;
				System.out.println("Create StyleRange:" + originStyleRange.start + " -> " + originStyleRange.metrics);
			}

			paintFunctions.add((gc) -> {
				// Draw ghost text
				setStyleToGhostText(styledText, gc);
				int spaceWidth = gc.textExtent(" ").x;
				int tabWidth = textWithTabs.tabs * styledText.getTabs() * spaceWidth;
				int ghostTextWidth = tabWidth + gc.stringExtent(textWithTabs.text).x;
				Point location = styledText.getLocationAtOffset(offset);
				gc.drawString(textWithTabs.text, location.x + tabWidth, location.y);

				// Leave the space for the ghost text
				setStyle(styledText, gc, originStyleRange);
				int shiftWidth = ghostTextWidth - gc.stringExtent(replacedText).x;
				int targetCharWidth = gc.stringExtent(targetChar).x;

				StyleRange currentStyleRange = styledText.getStyleRangeAtOffset(targetOffset);
				if (currentStyleRange != null && currentStyleRange.metrics != null
						&& currentStyleRange.metrics.width == shiftWidth + targetCharWidth) {
					// nothing to do
				} else {
					StyleRange styleRange = (StyleRange) originStyleRange.clone();
					styleRange.start = targetOffset;
					styleRange.length = 1;
					FontMetrics fontMetrics = gc.getFontMetrics();
					GlyphMetrics glyphMetrics = new GlyphMetrics(fontMetrics.getAscent(), fontMetrics.getDescent(),
							shiftWidth + targetCharWidth);
					modifiedGlyphMetrics.add(glyphMetrics);
					styleRange.metrics = glyphMetrics;
					styledText.setStyleRange(styleRange);
					System.out.println("Set StyleRange:" + styleRange.start + " -> " + styleRange.metrics);
				}

				// Draw the moved char
				Point targetCharLocation = styledText.getLocationAtOffset(targetOffset);
				gc.drawString(targetChar, targetCharLocation.x + shiftWidth, targetCharLocation.y, true);
			});
		}
	}

	private void drawSuffixLines(StyledText styledText, String textToInsert, int offset) {
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

	private void setStyle(StyledText styledText, GC gc, StyleRange styleRange) {
		if (styleRange.foreground != null) {
			gc.setForeground(styleRange.foreground);
		} else {
			gc.setForeground(styledText.getForeground());
		}
		if (styleRange.font != null) {
			gc.setFont(styleRange.font);
		} else {
			gc.setFont(styledText.getFont());
		}
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

	}

	@Override
	public void deactivate(boolean redraw) {

	}

	@Override
	public void setPositionManager(IPaintPositionManager manager) {
		this.positionManager = manager;

	}
}
