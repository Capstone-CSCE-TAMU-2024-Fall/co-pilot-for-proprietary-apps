package com.codingassistant.codingassistantplugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
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
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

import com.codingassistant.codingassistantplugin.connection.backend.BackendConnection;
import com.codingassistant.codingassistantplugin.connection.fauxpilot.FauxpilotConnection;
import com.codingassistant.codingassistantplugin.connection.tabby.TabbyConnection;
import com.fasterxml.jackson.core.JsonProcessingException;

// Acts as the controller, calling the TextRenderer when necessary
// with the responses that this class gets.
/**
 * The TextCompletionService class provides functionality for text completion
 * within an Eclipse-based text editor. It manages connections to backend services
 * for generating text completions, registers and unregisters text renderers, and
 * handles the insertion, acceptance, and dismissal of text completions.
 * 
 * <p>This service is implemented as a singleton, accessible via the 
 * {@link #getInstance()} method.</p>
 * 
 * <p>Key functionalities include:</p>
 * <ul>
 *   <li>Connecting to backend services such as Fauxpilot and Tabby for text completion.</li>
 *   <li>Registering and unregistering text renderers to handle the display of text completions.</li>
 *   <li>Triggering text completion jobs based on the current state and user preferences.</li>
 *   <li>Accepting and inserting the generated text completions into the document.</li>
 *   <li>Dismissing text completions and cleaning up the renderer state.</li>
 * </ul>
 * 
 * <p>Configuration and preferences are managed through an Eclipse preference store.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * TextCompletionService service = TextCompletionService.getInstance();
 * service.connect();
 * service.trigger();
 * }
 * </pre>
 */
public class TextCompletionService {

	private BackendConnection conn;
	private Map<ITextViewer, TextRenderer> textRenderers = new HashMap<>();
	private Job job;
	private String lastTextToInsert;
	private int insertOffset;
	private IPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
			"com.codingassistant.codingassistantplugin");

	private static class LazyHolder {
		private static final TextCompletionService INSTANCE = new TextCompletionService();
	}

	public static TextCompletionService getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void registerRenderer(ITextViewer textViewer, TextRenderer textRenderer) {
		// textViewer passed in should not be null
		StyledText styledText = textViewer.getTextWidget();
		if (styledText != null) {
			styledText.getDisplay().asyncExec(() -> {
				((ITextViewerExtension2) textViewer).addPainter(textRenderer);
				styledText.addPaintListener(textRenderer);
				styledText.setLineSpacingProvider(textRenderer);
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
		InetSocketAddress socketAddress = new InetSocketAddress(preferenceStore.getString("SERVER_HOST"),
				Integer.parseInt(preferenceStore.getString("SERVER_PORT")));
		String scheme = preferenceStore.getString("SCHEME");
		String backend = preferenceStore.getString("BACKEND");

		// Get the Class object associated with the class name
		Class<?> backendClaass;
		try {
			backendClaass = Class.forName(backend);
			Class<?>[] type = { InetSocketAddress.class, String.class };
			Constructor<?> backendConstructor = backendClaass.getConstructor(type);

			conn = (BackendConnection) backendConstructor.newInstance(socketAddress, scheme);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(String.format("Invalid backend selected: %s", backend));
		}

	}

	public void trigger() {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand("com.codingassistant.codingassistantplugin.commands.enableCodeInsertion");
		State state = command.getState("org.eclipse.ui.commands.toggleState");
		boolean enabled = state != null && (Boolean) state.getValue();
		System.out.println("state in trigger is " + enabled);
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
					"com.codingassistant.codingassistantplugin");

			if (enabled) {
				if (preferenceStore.getBoolean("DEBUG_MODE")) {

					job = Job.create("Trigger", monitor -> {
						System.out.println("Running job");
						lastTextToInsert = "Test";
						insertOffset = selection.getOffset();
						display.asyncExec(() -> {
							textRenderer.update(lastTextToInsert);
						});
					});
				} else {
					job = Job.create("Trigger", monitor -> {
						System.out.println("Running job");
						try {
							insertOffset = selection.getOffset();
							String prefix = document.get(0, insertOffset);
							String suffix = document.get(insertOffset, document.getLength() - insertOffset);
							if (conn == null) {
								connect();
							}

							CompletableFuture<HttpResponse<String>> response = conn.getResponse(prefix, suffix,
									preferenceStore);
							conn.parseResponse(response).thenAccept(r -> {
								if (!monitor.isCanceled()) {
									lastTextToInsert = r.choices().getFirst().text();
									System.out.println("set lastTextToInsert: " + lastTextToInsert);
									display.asyncExec(() -> {
										textRenderer.update(lastTextToInsert);
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
