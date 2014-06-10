package net.sf.taverna.t2.wadl.ui;

import net.sf.taverna.t2.workbench.dev.DeveloperWorkbench;

/**
 * Run with parameters:
 * 
 * -Xmx300m -XX:MaxPermSize=140m 
 * -Dsun.swing.enableImprovedDragGesture
 * -Dtaverna.startup=.
 * 
 * NOTE: Do not save any workflows made using this test mode, as the plugin
 * information will be missing from the workflow file, and it will not open
 * in a Taverna run normally.
 * 
 */
public class TavernaWorkbenchWithExamplePlugin {
	public static void main(String[] args) throws Exception {
		DeveloperWorkbench.main(args);
	}
}
