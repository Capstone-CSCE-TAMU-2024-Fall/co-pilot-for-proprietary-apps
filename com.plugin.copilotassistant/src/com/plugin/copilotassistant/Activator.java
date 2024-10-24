package com.plugin.copilotassistant;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "com.plugin.copilotassistant";

	// The shared instance
	private static Activator plugin;
	
	public static Activator getDefault() {
		return plugin;
	}
	
	
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		System.out.println("Plugin starting");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Plugin Stopping");
		plugin = null;
		super.stop(context);
	}
}