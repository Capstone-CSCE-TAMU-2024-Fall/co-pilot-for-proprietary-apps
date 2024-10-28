package com.plugin.copilotassistant;

import java.net.InetSocketAddress;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.fauxpilotconnection.FauxpilotConnection;
import com.plugin.copilotassistant.handlers.CodeInsertRunnable;

// Listens to the TextEditor and Document to determine when it is necessary to call the TextCompletionService
public class TextCompletionTrigger implements CaretListener, IDocumentListener {

	ITextEditor textEditor;

	public void register(ITextEditor textEditor) {
		this.textEditor = textEditor;

		ITextViewer textViewer = Adapters.adapt(textEditor, ITextViewer.class);
		if (textViewer != null) {
			StyledText styledText = textViewer.getTextWidget();
			styledText.addCaretListener(this);
		}

		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		if (document != null) {
			document.addDocumentListener(this);
		}
	}

	public void unregister(ITextEditor textEditor) {
		ITextViewer textViewer = Adapters.adapt(textEditor, ITextViewer.class);
		if (textViewer != null) {
			StyledText styledText = textViewer.getTextWidget();
			styledText.removeCaretListener(this);
		}

		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		if (document != null) {
			document.removeDocumentListener(this);
		}
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {

	}

	@Override
	public void documentChanged(DocumentEvent event) {
		// TODO: Move logic that gets response to FauxpilotCompletionService

		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		// Retrieve the preference value from the preference store
		IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();
		boolean enabled = preferenceStore.getBoolean("ENABLE_INSERTION"); // Retrieve the value of the toggle button

		ITextSelection selection = Adapters.adapt(textEditor.getSelectionProvider().getSelection(),
				ITextSelection.class);
		int offset = selection.getOffset();

		// TODO: Make it so this does not infinitely loop because it triggers itself by
		// updating the document.
		Display display = Display.getDefault();
		if (preferenceStore.getBoolean("DEBUG_MODE")) {
			String textToInsert = "Test";
			display.asyncExec(new CodeInsertRunnable(enabled, textToInsert, document, offset, textEditor));
		} else {
			Job job = Job.create("", monitor -> {
				try {
					InetSocketAddress socketAddress = new InetSocketAddress(preferenceStore.getString("SERVER_HOST"),
							Integer.parseInt(preferenceStore.getString("SERVER_PORT")));
					BackendConnection conn = new FauxpilotConnection(socketAddress);

					String context = document.get(0, offset);
					CompletableFuture<HttpResponse<String>> response = conn.getResponse(context);

					conn.parseResponse(response).thenAccept(r -> {
						String textToInsert = r.choices().getFirst().text();
						display.asyncExec(new CodeInsertRunnable(enabled, textToInsert, document, offset, textEditor));
					}).exceptionally(e -> {
						e.printStackTrace();
						return null;
					}).join();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			job.setSystem(true);
			job.schedule(preferenceStore.getInt("SUGGESTION_DELAY"));
		}

//		styledText.addPaintListener(paintListener);
//		styledText.redraw();
//		styledText.removePaintListener(paintListener);
		System.out.println("Document changed");
	}

	@Override
	public void caretMoved(CaretEvent event) {
		System.out.println("Caret moved");
//		styledText.redraw();

	}

}
