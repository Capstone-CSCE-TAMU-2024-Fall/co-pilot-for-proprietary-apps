package com.plugin.copilotassistant;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.fauxpilotconnection.FauxpilotConnection;

// Acts as the controller, calling the TextRenderer when necessary
// with the responses that this class gets.
public class TextCompletionService {

	private BackendConnection conn;
	private Map<ITextViewer, TextRenderer> textRenderers = new HashMap<>();
	private Job job;
	private String lastTextToInsert;
	private int insertOffset;

	private static class LazyHolder {
		private static final TextCompletionService INSTANCE = new TextCompletionService();
	}

	public static TextCompletionService getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void registerRenderer(ITextViewer textViewer, TextRenderer textRenderer) {
		StyledText styledText = textViewer.getTextWidget();
		if (styledText != null) {
			styledText.getDisplay().asyncExec(() -> {
				((ITextViewerExtension2) textViewer).addPainter(textRenderer);
				styledText.addPaintListener(textRenderer);
			});
			textRenderers.put(textViewer, textRenderer);
		}
	}

	public void unregisterRenderer(ITextViewer textViewer) {
		TextRenderer textRenderer = textRenderers.get(textViewer);
		if (textRenderer != null) {
			textRenderers.remove(textViewer);
		}
	}

	public void connect() throws URISyntaxException {
		IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"com.plugin.copilotassistant");
		InetSocketAddress socketAddress = new InetSocketAddress(preferenceStore.getString("SERVER_HOST"),
				Integer.parseInt(preferenceStore.getString("SERVER_PORT")));
		conn = new FauxpilotConnection(socketAddress);
	}

	public void trigger() {
		ITextEditor textEditor = getTextEditor();

		if (textEditor != null) {

			if (job != null) {
				job.cancel();
			}

			IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
			ITextSelection selection = Adapters.adapt(textEditor.getSelectionProvider().getSelection(),
					ITextSelection.class);
			Display display = Display.getDefault();
			ITextViewer textViewer = Adapters.adapt(textEditor, ITextViewer.class);
			StyledText styledText = textViewer.getTextWidget();
			TextRenderer textRenderer = textRenderers.get(textViewer);
			System.out.println("textEditor: " + textEditor);
			System.out.println("textViewer: " + textViewer);
			System.out.println("styledText: " + styledText);
			System.out.println("textRenderer: " + textRenderer);

			textRenderer.cleanupPainting();
			IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
					"com.plugin.copilotassistant");
			if (preferenceStore.getBoolean("DEBUG_MODE")) {

				job = Job.create("Trigger", monitor -> {
					System.out.println("Running job");
					lastTextToInsert = "Test";
					insertOffset = selection.getOffset();
					display.asyncExec(() -> {
						textRenderer.cleanupPainting();
						textRenderer.setupPainting(lastTextToInsert);
						styledText.redraw();
					});
				});
			} else {
				job = Job.create("Trigger", monitor -> {
					System.out.println("Running job");
					try {
						insertOffset = selection.getOffset();
						String context = document.get(0, insertOffset);
						if (conn == null) {
							connect();
						}
						CompletableFuture<HttpResponse<String>> response = conn.getResponse(context);
						conn.parseResponse(response).thenAccept(r -> {
							if (!monitor.isCanceled()) {
								lastTextToInsert = r.choices().getFirst().text();
								System.out.println("set lastTextToInsert: " + lastTextToInsert);
								display.asyncExec(() -> {
									textRenderer.cleanupPainting();
									textRenderer.setupPainting(lastTextToInsert);
									styledText.redraw();

								});
							}
						}).exceptionally(e -> {
							e.printStackTrace();
							return null;
						}).join();
					} catch (JsonProcessingException | BadLocationException | URISyntaxException e) {
						e.printStackTrace();
					}
				});
			}
			job.setSystem(true);
			job.schedule(preferenceStore.getInt("SUGGESTION_DELAY"));
			System.out.println("Scheduled job");
		}
	}

	public boolean accept() {
		System.out.println("current lastTextToInsert: " + lastTextToInsert);

		if (lastTextToInsert.isEmpty()) {
			return false;
		}
		
		System.out.println("getting textEditor");
		
		ITextEditor textEditor = getTextEditor();

		if (textEditor != null) {
			System.out.println("textEditor is not null: " + textEditor);
			if (job != null) {
				job.cancel();
			}

			IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
			try {
				ITextSelection selection = new TextSelection(insertOffset + lastTextToInsert.length() + 1, 0);
				System.out.println("inserted " + lastTextToInsert);
				document.replace(insertOffset + 1, 0, lastTextToInsert);
				textEditor.getSelectionProvider().setSelection(selection);
				return true;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

		}

		System.out.println("textEditor is null: " + textEditor);
		return false;
	}

	public void dismiss() {
		ITextEditor textEditor = getTextEditor();

		if (textEditor != null) {

			if (job != null) {
				job.cancel();
			}

			ITextViewer textViewer = Adapters.adapt(textEditor, ITextViewer.class);
			TextRenderer textRenderer = textRenderers.get(textViewer);
			textRenderer.cleanupPainting();
			System.out.println("Resetting " + lastTextToInsert);
			lastTextToInsert = "";
			insertOffset = -1;
		}
	}

	private ITextEditor getTextEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = null;
		if (window != null) {
			page = window.getActivePage();
		}

		ITextEditor textEditor = null;
		if (page != null) {
			IEditorPart activeEditor = page.getActiveEditor();
			if (activeEditor instanceof ITextEditor textEditor1) {
				textEditor = textEditor1;
			}
		}
		return textEditor;
	}
}
