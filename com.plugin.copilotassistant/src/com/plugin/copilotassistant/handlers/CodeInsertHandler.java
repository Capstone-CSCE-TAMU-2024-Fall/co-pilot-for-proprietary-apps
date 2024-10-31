package com.plugin.copilotassistant.handlers;

import java.net.InetSocketAddress;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.fauxpilotconnection.FauxpilotConnection;

public class CodeInsertHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		// Retrieve the preference value from the preference store
		IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();
		boolean enabled = preferenceStore.getBoolean("ENABLE_INSERTION"); // Retrieve the value of the toggle button

		ITextEditor textEditor = Adapters.adapt(window.getActivePage().getActiveEditor(), ITextEditor.class);
//		System.out.println("Adding listener");

//		ITextViewer viewer = Adapters.adapt(textEditor, ITextViewer.class);
//		StyledText widget = viewer.getTextWidget();
//		widget.addCaretListener(null);

		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		ITextSelection selection = Adapters.adapt(textEditor.getSelectionProvider().getSelection(),
				ITextSelection.class);
		int offset = selection.getOffset();

//		document.addDocumentListener(null);
//		documentListeners.put(textEditor, listener);

		// TODO: Fix bug when imports are collapsed: offset becomes changed to the wrong
		// place (cursor gets moved way down)
		// If I want to convert from document offset to StyledText widget offset, look
		// at ITextViewerExtension5

		Display display = Display.getDefault();

		if (preferenceStore.getBoolean("DEBUG_MODE")) {
			String textToInsert = "Test";
			display.asyncExec(new CodeInsertRunnable(enabled, textToInsert,  offset, textEditor));
		} else {
			try {
				InetSocketAddress socketAddress = new InetSocketAddress(preferenceStore.getString("SERVER_HOST"),
						Integer.parseInt(preferenceStore.getString("SERVER_PORT")));
				BackendConnection conn = new FauxpilotConnection(socketAddress);

				String context = document.get(0, offset);
				CompletableFuture<HttpResponse<String>> response = conn.getResponse(context);

				conn.parseResponse(response).thenAccept(r -> {
					String textToInsert = r.choices().getFirst().text();
					display.asyncExec(new CodeInsertRunnable(enabled, textToInsert, offset, textEditor));
				}).exceptionally(e -> {
					e.printStackTrace();
					return null;
				}).join();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}
}