package com.plugin.copilotassistant.fauxpilotconnection;

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
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plugin.copilotassistant.TextRenderer;
import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.backendconnection.TextCompletionService;
import com.plugin.copilotassistant.handlers.CodeInsertRunnable;

// Acts as the controller, calling the TextRenderer when necessary 
// with the responses that this class gets.
public class FauxpilotCompletionService implements TextCompletionService {

	private BackendConnection conn;
	private Map<ITextViewer, TextRenderer> textRenderers = new HashMap<>();
	private Job job;

	private static class LazyHolder {
		private static final TextCompletionService INSTANCE = new FauxpilotCompletionService();
	}

	public static TextCompletionService getInstance() {
		return LazyHolder.INSTANCE;
	}

	@Override
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

	@Override
	public void unregisterRenderer(ITextViewer textViewer) {
		TextRenderer textRenderer = textRenderers.get(textViewer);
		if (textRenderer != null) {
			textRenderers.remove(textViewer);
		}
	}

	@Override
	public void connect() throws URISyntaxException {
		IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant");
		InetSocketAddress socketAddress = new InetSocketAddress(preferenceStore.getString("SERVER_HOST"),
				Integer.parseInt(preferenceStore.getString("SERVER_PORT")));
		conn = new FauxpilotConnection(socketAddress);
	}

	@Override
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
			IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.plugin.copilotassistant");
			if (preferenceStore.getBoolean("DEBUG_MODE")) {

				job = Job.create("Trigger", monitor -> {
					System.out.println("Running job");
					String textToInsert = "Test";
					display.asyncExec(new Runnable() {
						public void run() {
							textRenderer.setupPainting(textToInsert);
							styledText.redraw();
						}
					});
				});
			} else {
				job = Job.create("Trigger", monitor -> {
					System.out.println("Running job");
					try {
						int offset = selection.getOffset();
						String context = document.get(0, offset);
						if (conn == null) {
							connect();
						}
						CompletableFuture<HttpResponse<String>> response = conn.getResponse(context);
						conn.parseResponse(response).thenAccept(r -> {
							String textToInsert = r.choices().getFirst().text();
							display.asyncExec(new Runnable() {
								public void run() {
									textRenderer.setupPainting(textToInsert);
									styledText.redraw();
								}
							});
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

	// Need to trigger only when the caret movement was not caused by typing a new
	// character
	@Override
	public void dismiss() {
		ITextEditor textEditor = getTextEditor();

		if (textEditor != null) {
			if (job != null) {
				job.cancel();
			}

			ITextViewer textViewer = Adapters.adapt(textEditor, ITextViewer.class);
			TextRenderer textRenderer = textRenderers.get(textViewer);
			textRenderer.cleanupPainting();
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
