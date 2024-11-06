package com.plugin.copilotassistant.backendconnection;

import java.net.URISyntaxException;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;

import com.plugin.copilotassistant.TextRenderer;

public interface TextCompletionService {
	public void registerRenderer(ITextViewer textViewer, TextRenderer textRenderer);

	public void unregisterRenderer(ITextViewer textViewer);

	void connect() throws URISyntaxException;

	void trigger();
	
	void dismiss();
}
