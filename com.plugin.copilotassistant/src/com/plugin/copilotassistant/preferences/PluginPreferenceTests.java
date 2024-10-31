package com.plugin.copilotassistant.preferences;

import static org.junit.Assert.*;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;

public class PluginPreferenceTests {
    
    private static final String PLUGIN_ID = "com.plugin.copilotassistant";
    
    @Mock
    private IWorkbench workbench;
    
    @Mock
    private Composite parent;
    
    private PluginPreferencePage preferencePage;
    private PreferenceInitializer initializer;
    private ScopedPreferenceStore preferenceStore;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, PLUGIN_ID);
        
        preferencePage = new PluginPreferencePage();
        initializer = new PreferenceInitializer();
    }
    
    @Test
    public void testPreferencePageInitialization() {
        preferencePage.init(workbench);
        assertNotNull("Preference store should not be null", preferencePage.getPreferenceStore());
    }
    
    @Test
    public void testDefaultPreferenceInitialization() {

        initializer.initializeDefaultPreferences();
        

        IPreferenceStore store = preferenceStore;
        assertEquals("localhost", store.getDefaultString("SERVER_HOST"));
        assertEquals("5000", store.getDefaultString("SERVER_PORT"));
        assertEquals("200", store.getDefaultString("MAX_TOKENS"));
        assertEquals("20", store.getDefaultString("MAX_LINES"));
        assertEquals("davinci", store.getDefaultString("ENGINE"));
        assertEquals("text-davinci-003", store.getDefaultString("MODEL"));
        assertEquals("0.1", store.getDefaultString("TEMPERATURE"));
        assertEquals("500", store.getDefaultString("SUGGESTION_DELAY"));
        assertFalse(store.getDefaultBoolean("DEBUG_MODE"));
        assertTrue(store.getDefaultBoolean("ENABLE_INSERTION"));
    }
    
    @Test
    public void testPreferenceValuePersistence() {

        preferenceStore.setValue("SERVER_HOST", "test-host");
        preferenceStore.setValue("SERVER_PORT", "8080");
        preferenceStore.setValue("DEBUG_MODE", true);
        
        assertEquals("test-host", preferenceStore.getString("SERVER_HOST"));
        assertEquals("8080", preferenceStore.getString("SERVER_PORT"));
        assertTrue(preferenceStore.getBoolean("DEBUG_MODE"));
    }
    
    @Test
    public void testPreferenceReset() {
        preferenceStore.setValue("SERVER_HOST", "custom-host");
        preferenceStore.setValue("SERVER_PORT", "9000");
        preferenceStore.setToDefault("SERVER_HOST");
        preferenceStore.setToDefault("SERVER_PORT");
        assertEquals("localhost", preferenceStore.getString("SERVER_HOST"));
        assertEquals("5000", preferenceStore.getString("SERVER_PORT"));
    }
    
    @Test
    public void testInvalidPortValue() {
        // Test handling of invalid port number
        preferenceStore.setValue("SERVER_PORT", "invalid");
        assertEquals("invalid", preferenceStore.getString("SERVER_PORT"));
    }
}