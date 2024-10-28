package com.plugin.copilotassistant;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorActivationListener implements IPartListener2 {
	Map<ITextEditor, TextCompletionTrigger> suggestionTriggers = new HashMap<>();

	public void registerEditorActivationListener() {
		// Get the active workbench page
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
//			IWorkbenchPage[] pages = window.getPages();
			if (window != null) {
				window.getActivePage().addPartListener(this);
			}
		}

	}

	@Override
	public void partOpened(IWorkbenchPartReference part) {
		System.out.println("Part opened: " + part);

		IEditorPart activeEditor = null;
		if (part instanceof IEditorReference) {
			activeEditor = Adapters.adapt(part, IEditorReference.class).getEditor(false);
			System.out.println("activeEditor: " + activeEditor);
		}

		ITextViewer textViewer = null;
		if (activeEditor != null) {
			textViewer = Adapters.adapt(activeEditor, ITextViewer.class);
			System.out.println("textViewer: " + textViewer);

//			StyledText styledText = textViewer.getTextWidget();
//			TextRenderer textRenderer = new TextRenderer(textViewer);
//			styledText.addPaintListener(textRenderer);
			
			ITextEditor textEditor = Adapters.adapt(activeEditor, ITextEditor.class);
			TextCompletionTrigger suggestionTrigger = new TextCompletionTrigger();
			suggestionTrigger.register(textEditor);
			suggestionTriggers.put(textEditor, suggestionTrigger);
		}

	}

	@Override
	public void partClosed(IWorkbenchPartReference part) {
		System.out.println("Part closed: " + part);

		IEditorPart activeEditor = null;
		if (part instanceof IEditorReference) {
			activeEditor = Adapters.adapt(part, IEditorReference.class).getEditor(false);
			System.out.println("activeEditor: " + activeEditor);
		}

		ITextViewer textViewer = null;
		if (activeEditor != null) {
			textViewer = Adapters.adapt(activeEditor, ITextViewer.class);
			
			System.out.println("textViewer: " + textViewer);
			ITextEditor textEditor = Adapters.adapt(activeEditor, ITextEditor.class);
			suggestionTriggers.get(textEditor).unregister(textEditor);
		}


	}
}
