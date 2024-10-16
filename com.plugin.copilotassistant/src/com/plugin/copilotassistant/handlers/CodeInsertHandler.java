package com.plugin.copilotassistant.handlers;

import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.plugin.copilotassistant.backendconnection.FauxpilotConnectionImpl;

public class CodeInsertHandler extends AbstractHandler {
	public Runnable run(boolean enabled, String textToInsert, IDocument document, int offset, ITextEditor textEditor) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					if (!enabled) {
						document.replace(offset, 0, "$");
						return;
					} else {
						document.replace(offset, 0, textToInsert);
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

				// Get the StyledText widget
				StyledText styledText = (StyledText) textEditor.getAdapter(org.eclipse.swt.widgets.Control.class);

				if (styledText != null) {
					// Create a StyleRange to apply the gray color
					StyleRange styleRange = new StyleRange();
					styleRange.start = offset;
					styleRange.length = textToInsert.length();
					styleRange.foreground = Display.getDefault().getSystemColor(org.eclipse.swt.SWT.COLOR_GRAY);

					// Apply the StyleRange (make the text gray)
					styledText.setStyleRange(styleRange);

					// Move the cursor to the end of the inserted text
					styledText.setCaretOffset(offset + textToInsert.length());

					// Add a listener for key events after insertion
					styledText.addVerifyKeyListener(new VerifyKeyListener() {
						@Override
						public void verifyKey(VerifyEvent e) {
							// If Tab is pressed, keep the text as is
							if (e.keyCode == KeyEvent.VK_TAB) {
								// Do nothing on Enter key
								styledText.removeVerifyKeyListener(this); // Remove listener once Tab is
																			// pressed
							} else {
								// Remove the inserted text
								Display.getDefault().asyncExec(() -> {
									try {
										document.replace(offset, textToInsert.length(), "");
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								});
								styledText.removeVerifyKeyListener(this); // Remove listener once text is
																			// replaced
							}
						}
					});
				}
			}
		};
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IEditorPart editor = window.getActivePage().getActiveEditor();

		// Retrieve the preference value from the preference store
		IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();
		boolean enabled = preferenceStore.getBoolean("ENABLE_INSERTION"); // Retrieve the value of the toggle button
		boolean debug = preferenceStore.getBoolean("DEBUG_MODE");
		var ip = InetAddress.getLoopbackAddress();
		var port = 5000;

		if (editor instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) editor;
			IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

			ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
			int offset = selection.getOffset();

			if (debug) {
				String textToInsert = "Test";
				Display.getDefault().asyncExec(run(enabled, textToInsert, document, offset, textEditor));
			} else {
				var conn = new FauxpilotConnectionImpl(ip, port);
				CompletableFuture<HttpResponse<String>> response = null;
				try {
					response = conn.getResponse(document.get(0, offset));
				} catch (Exception e) {
					e.printStackTrace();
				}

				FauxpilotConnectionImpl.parseResponse(response).thenAccept(r -> {
					String textToInsert = MessageFormat.format("{0}", r.choices().getFirst().text());
					Display.getDefault().asyncExec(run(enabled, textToInsert, document, offset, textEditor));
				}).exceptionally((e) -> {
					return null;
				}).join();
			}
		}

		return null;
	}
}
