package com.plugin.copilotassistant.handlers;

import java.net.InetAddress;
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

import com.plugin.copilotassistant.backendconnection.FauxpilotConnection;
import com.plugin.copilotassistant.backendconnection.FauxpilotConnectionImpl;

public class CodeInsertHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		// Retrieve the preference value from the preference store
		IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();
		boolean enabled = preferenceStore.getBoolean("ENABLE_INSERTION"); // Retrieve the value of the toggle button
		boolean debug = preferenceStore.getBoolean("DEBUG_MODE");

		var textEditor = Adapters.adapt(window.getActivePage().getActiveEditor(), ITextEditor.class);
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		var selection = Adapters.adapt(textEditor.getSelectionProvider().getSelection(), ITextSelection.class);
		int offset = selection.getOffset();

		Display display = Display.getDefault();

		if (debug) {
			String textToInsert = "Test";
			display.asyncExec(new CodeInsertRunnable(enabled, textToInsert, document, offset, textEditor));
		} else {
			try {
				InetAddress ip = InetAddress.getByName(preferenceStore.getString("SERVER_HOST"));
				int port = 5000;
				FauxpilotConnection conn = new FauxpilotConnectionImpl(ip, port);

				String context = document.get(0, offset);
				CompletableFuture<HttpResponse<String>> response = conn.getResponse(context);

				FauxpilotConnectionImpl.parseResponse(response).thenAccept(r -> {
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
