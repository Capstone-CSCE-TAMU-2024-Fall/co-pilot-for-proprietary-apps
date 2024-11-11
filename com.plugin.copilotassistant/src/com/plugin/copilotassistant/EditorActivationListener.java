package com.plugin.copilotassistant;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorActivationListener implements IPartListener2 {
	private Map<IWorkbenchPartReference, TextCompletionListener> suggestionTriggers = new HashMap<>();

	public void registerEditorActivationListener() {
		// Get the active workbench page
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			IWorkbenchPage[] pages = window.getPages();
			for (IWorkbenchPage page : pages) {
				page.addPartListener(this);
				IEditorReference[] editorReferences = page.getEditorReferences();

				for (IEditorReference editorRef : editorReferences) {
					IEditorPart editorPart = editorRef.getEditor(false);
					if (editorPart instanceof ITextEditor textEditor) {

						ITextViewer textViewer = Adapters.adapt(textEditor, ITextViewer.class);
						if (textViewer != null) {
							StyledText styledText = textViewer.getTextWidget();
							TextRenderer textRenderer = new TextRenderer(textViewer);
							styledText.getDisplay().asyncExec(() -> {
								styledText.addPaintListener(textRenderer);
							});
							TextCompletionService.getInstance().registerRenderer(textViewer, textRenderer);
						}

						TextCompletionListener suggestionTrigger = new TextCompletionListener();
						suggestionTrigger.register(textEditor);
					}
				}
			}
		}
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		System.out.println("Part activated: " + partRef);
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		System.out.println("Part Deactivated: " + partRef);
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		System.out.println("Part opened: " + partRef);

		IEditorPart activeEditor = null;
		if (partRef instanceof IEditorReference) {
			activeEditor = Adapters.adapt(partRef, IEditorReference.class).getEditor(false);
			System.out.println("activeEditor: " + activeEditor);
		}

		ITextViewer textViewer = null;
		if (activeEditor != null) {
			textViewer = Adapters.adapt(activeEditor, ITextViewer.class);
			System.out.println("textViewer: " + textViewer);

			TextRenderer textRenderer = new TextRenderer(textViewer);
			TextCompletionService.getInstance().registerRenderer(textViewer, textRenderer);

			ITextEditor textEditor = Adapters.adapt(activeEditor, ITextEditor.class);
			TextCompletionListener suggestionTrigger = new TextCompletionListener();
			suggestionTrigger.register(textEditor);
			suggestionTriggers.put(partRef, suggestionTrigger);
		}

	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		System.out.println("Part closed: " + partRef);

		IEditorPart activeEditor = null;
		if (partRef instanceof IEditorReference) {
			activeEditor = Adapters.adapt(partRef, IEditorReference.class).getEditor(false);
			System.out.println("activeEditor: " + activeEditor);
		}

		ITextViewer textViewer = null;
		if (activeEditor != null) {
			textViewer = Adapters.adapt(activeEditor, ITextViewer.class);
			System.out.println("textViewer: " + textViewer);

			TextCompletionService textCompletionService = TextCompletionService.getInstance();
			textCompletionService.unregisterRenderer(textViewer);

			ITextEditor textEditor = Adapters.adapt(activeEditor, ITextEditor.class);
			TextCompletionListener textCompletionTrigger = suggestionTriggers.get(partRef);
			if (textCompletionTrigger != null) {
				textCompletionTrigger.unregister(textEditor);
			}
			suggestionTriggers.remove(partRef);
		}
	}
}
