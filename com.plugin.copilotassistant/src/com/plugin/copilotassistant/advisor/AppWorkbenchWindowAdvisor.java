package com.plugin.copilotassistant.advisor;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;

public class AppWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private Timer delayTimer = new Timer();
	private final int delay = 5000;
	public AppWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}
	
	@Override
	public void preWindowOpen() {
		Display.getDefault().addFilter(SWT.KeyDown, event -> resetTimer());
		resetTimer();
	}

	private void resetTimer() {
		delayTimer.cancel();
		delayTimer = new Timer();
		delayTimer.schedule(new TimerTask() {
			public void run() {
				executeCodeInsert();
			}
		}, delay);
	}

	protected void executeCodeInsert() {
		Display.getDefault().asyncExec(() -> {
			try {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
				handlerService.executeCommand("com.plugin.copilotassistant.commands.insert", null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
