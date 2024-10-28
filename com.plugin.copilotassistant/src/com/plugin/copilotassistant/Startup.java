package com.plugin.copilotassistant;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		System.out.println("Starting up.");
		EditorActivationListener listener = new EditorActivationListener();
		listener.registerEditorActivationListener();
		System.out.println("Registered EditorActivationListener.");

	}

}
