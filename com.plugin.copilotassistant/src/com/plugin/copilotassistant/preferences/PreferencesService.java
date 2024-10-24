package com.plugin.copilotassistant.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
//import org.eclipse.jface.util.IPropertyChangeListener;
//import org.eclipse.jface.util.PropertyChangeEvent;
//import org.eclipse.lsp4j.DidChangeConfigurationParams;
//import org.eclipse.lsp4j.services.WorkspaceService;

import com.plugin.copilotassistant.Activator;

public class PreferencesService {
	
	private static class LazyHolder {
		private static final PreferencesService INSTANCE = new PreferencesService();
	}
	
	public static PreferencesService getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public void init() {
		getPreferenceStore().setDefault("ENABLE_INSERTION", true);
		//getPreferenceStore().setDefault(KEY_INLINE_COMPLETION_TRIGGER_AUTO, true);

//		getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
//			@Override
//			public void propertyChange(PropertyChangeEvent event) {
//				logger.info("Syncing configuration.");
//				LanguageServerService.getInstance().getServer().execute((server) -> {
//					WorkspaceService workspaceService = ((ILanguageServer) server).getWorkspaceService();
//					DidChangeConfigurationParams params = new DidChangeConfigurationParams();
//					params.setSettings(buildClientProvidedConfig());
//					workspaceService.didChangeConfiguration(params);
//					return null;
//				});
//			}
//		});
	}
	
	public IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	public boolean getEnableInsertion() {
		return getPreferenceStore().getBoolean("ENABLE_INSERTION");
	}
}
