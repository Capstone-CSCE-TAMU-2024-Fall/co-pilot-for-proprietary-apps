package com.plugin.copilotassistant.preferences;


import java.net.URISyntaxException;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.plugin.copilotassistant.TextCompletionService;

public class PropertyChangeListener implements IPropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if ("SCHEME".equals(event.getProperty())) {
	        try {
	            TextCompletionService.getInstance().connect();
	        } catch (URISyntaxException e) {
	            e.printStackTrace();
	        }
	    }
		
	}

}
