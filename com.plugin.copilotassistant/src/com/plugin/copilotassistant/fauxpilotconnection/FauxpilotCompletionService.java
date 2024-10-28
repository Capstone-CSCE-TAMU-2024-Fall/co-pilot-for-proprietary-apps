package com.plugin.copilotassistant.fauxpilotconnection;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;

import com.plugin.copilotassistant.TextRenderer;
import com.plugin.copilotassistant.backendconnection.BackendConnection;
import com.plugin.copilotassistant.backendconnection.TextCompletionService;

// Acts as the controller, calling the TextRenderer when necessary 
// with the responses that this class gets.
public class FauxpilotCompletionService implements TextCompletionService {

	private static TextCompletionService instance = new FauxpilotCompletionService();
	private BackendConnection conn;
	private TextRenderer textRenderer = new TextRenderer();

	@Override
	public void connect() throws URISyntaxException {
		IPreferenceStore preferenceStore = PlatformUI.getPreferenceStore();
		InetSocketAddress socketAddress = new InetSocketAddress(preferenceStore.getString("SERVER_HOST"),
				Integer.parseInt(preferenceStore.getString("SERVER_PORT")));
		conn = new FauxpilotConnection(socketAddress);
	}

	@Override
	public void trigger() {

	}

	public static TextCompletionService getInstance() {
		return instance;
	}
}
