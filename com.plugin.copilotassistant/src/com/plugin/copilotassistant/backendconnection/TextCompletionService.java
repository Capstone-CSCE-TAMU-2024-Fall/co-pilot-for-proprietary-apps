package com.plugin.copilotassistant.backendconnection;

import java.net.URISyntaxException;

import org.eclipse.jface.text.ITextViewer;

import com.plugin.copilotassistant.TextRenderer;

public interface TextCompletionService {
	public void registerRenderer(ITextViewer textViewer, TextRenderer textRenderer);

	public void unregisterRenderer(ITextViewer textViewer);

	void connect() throws URISyntaxException;

	void trigger();

	boolean accept();

	void dismiss();
}
