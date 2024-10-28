package com.plugin.copilotassistant.handlers;

import java.net.InetSocketAddress;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
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
		boolean debug = preferenceStore.getBoolean("DEBUG_MODE");

		ITextEditor textEditor = Adapters.adapt(window.getActivePage().getActiveEditor(), ITextEditor.class);
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		ITextSelection selection = Adapters.adapt(textEditor.getSelectionProvider().getSelection(), ITextSelection.class);
		// TODO: Fix bug when imports are expanded: offset becomes changed to the wrong
		// place (cursor gets moved way down)
		int offset = selection.getOffset();

		Display display = Display.getDefault();

		if (debug) {
			String textToInsert = "Test";
			display.asyncExec(new CodeInsertRunnable(enabled, textToInsert, document, offset, textEditor));
		} else {
			try {
				InetSocketAddress socketAddress = new InetSocketAddress(preferenceStore.getString("SERVER_HOST"),
						Integer.parseInt(preferenceStore.getString("SERVER_PORT")));
				BackendConnection conn = new FauxpilotConnection(socketAddress);

				String context = document.get(0, offset);
				CompletableFuture<HttpResponse<String>> response = conn.getResponse(context);

				conn.parseResponse(response).thenAccept(r -> {
					String textToInsert = MessageFormat.format("{0}", r.choices().getFirst().text());
					display.asyncExec(new CodeInsertRunnable(enabled, textToInsert, document, offset, textEditor));
				}).exceptionally(e -> {
					e.printStackTrace();
					return null;
				}).join();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
//				System.out.println(context);
		}

		return null;
	}
}
