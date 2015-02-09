package net.sf.taverna.t2.activities.rest.ui.view;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.RESTActivity.HTTP_METHOD;
import net.sf.taverna.t2.activities.rest.RESTActivityConfigurationBean;
import net.sf.taverna.t2.activities.rest.ui.config.RESTActivityConfigureAction;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import uk.org.taverna.commons.services.ServiceRegistry;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;

@SuppressWarnings("serial")
public class RESTActivityMainContextualView extends ContextualView {

	private final Scufl2Tools scufl2Tools = new Scufl2Tools();

	private final Activity activity;

	private JPanel jpMainPanel;
	private JTextField tfHTTPMethod;
	private JTextArea taURLSignature;
	private JTextField tfAcceptHeader;
	private JLabel jlContentType;
	private JTextField tfContentTypeHeader;
	private JLabel jlSendDataAs;
	private JTextField tfSendDataAs;
	private JLabel jlSendHTTPExpectRequestHeader;
	private JTextField tfSendHTTPExpectRequestHeader;

	private final EditManager editManager;
	private final FileManager fileManager;
	private final ActivityIconManager activityIconManager;
	private final ColourManager colourManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;
	private final ServiceRegistry serviceRegistry;

	public RESTActivityMainContextualView(Activity activity, EditManager editManager,
			FileManager fileManager, ActivityIconManager activityIconManager,
			ColourManager colourManager, ServiceDescriptionRegistry serviceDescriptionRegistry,
			ServiceRegistry serviceRegistry) {
		this.activity = activity;
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.colourManager = colourManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
		this.serviceRegistry = serviceRegistry;
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		jpMainPanel = new JPanel(new GridBagLayout());
		jpMainPanel
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 2,
						4, 2), BorderFactory.createLineBorder(
						colourManager.getPreferredColour(RESTActivity.class.getCanonicalName()), 2)));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.weighty = 0;

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		JLabel jlHTTPMethod = new JLabel("HTTP Method:");
		jlHTTPMethod.setFont(jlHTTPMethod.getFont().deriveFont(Font.BOLD));
		jpMainPanel.add(jlHTTPMethod, c);

		c.gridx++;
		c.weightx = 1.0;
		tfHTTPMethod = new JTextField();
		tfHTTPMethod.setEditable(false);
		jpMainPanel.add(tfHTTPMethod, c);
		c.weightx = 0;

		c.gridx = 0;
		c.gridy++;
		JLabel jlURLSignature = new JLabel("URL Template:");
		jlURLSignature.setFont(jlURLSignature.getFont().deriveFont(Font.BOLD));
		jpMainPanel.add(jlURLSignature, c);

		c.gridx++;
		taURLSignature = new JTextArea(3, 30);
		taURLSignature.setEditable(false);
		taURLSignature.setLineWrap(true);
		JScrollPane spURLSignature = new JScrollPane(taURLSignature);
		jpMainPanel.add(spURLSignature, c);

		c.gridx = 0;
		c.gridy++;
		JLabel jlAcceptHeader = new JLabel("'Accept' header:");
		jlAcceptHeader.setFont(jlAcceptHeader.getFont().deriveFont(Font.BOLD));
		jpMainPanel.add(jlAcceptHeader, c);

		c.gridx++;
		tfAcceptHeader = new JTextField();
		tfAcceptHeader.setEditable(false);
		jpMainPanel.add(tfAcceptHeader, c);

		c.gridx = 0;
		c.gridy++;
		jlContentType = new JLabel("'Content-Type' header:");
		jlContentType.setFont(jlContentType.getFont().deriveFont(Font.BOLD));
		jlContentType.setVisible(false);
		jpMainPanel.add(jlContentType, c);

		c.gridx++;
		tfContentTypeHeader = new JTextField();
		tfContentTypeHeader.setEditable(false);
		tfContentTypeHeader.setVisible(false);
		jpMainPanel.add(tfContentTypeHeader, c);

		c.gridx = 0;
		c.gridy++;
		jlSendDataAs = new JLabel("Send data as:");
		jlSendDataAs.setFont(jlSendDataAs.getFont().deriveFont(Font.BOLD));
		jlSendDataAs.setVisible(false);
		jpMainPanel.add(jlSendDataAs, c);

		c.gridx++;
		tfSendDataAs = new JTextField();
		tfSendDataAs.setEditable(false);
		tfSendDataAs.setVisible(false);
		jpMainPanel.add(tfSendDataAs, c);

		c.gridx = 0;
		c.gridy++;
		jlSendHTTPExpectRequestHeader = new JLabel("Send HTTP 'Expect' header:");
		jlSendHTTPExpectRequestHeader.setFont(jlSendHTTPExpectRequestHeader.getFont().deriveFont(
				Font.BOLD));
		jlSendHTTPExpectRequestHeader.setVisible(false);
		jpMainPanel.add(jlSendHTTPExpectRequestHeader, c);

		c.gridx++;
		tfSendHTTPExpectRequestHeader = new JTextField();
		tfSendHTTPExpectRequestHeader.setEditable(false);
		tfSendHTTPExpectRequestHeader.setVisible(false);
		jpMainPanel.add(tfSendHTTPExpectRequestHeader, c);

		// populate the view with values
		refreshView();

		return jpMainPanel;
	}

	@Override
	/**
	 * This is the title of the contextual view - shown in the list of other available
	 * views (even when this contextual view is collapsed).
	 */
	public String getViewTitle() {
		return "REST Service Details";
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView() {
		Configuration configuration = scufl2Tools.configurationFor(activity, activity.getParent());
		RESTActivityConfigurationBean configurationBean = new RESTActivityConfigurationBean(configuration.getJson());

		// toggle visibility of the elements that do not always appear
		HTTP_METHOD httpMethod = configurationBean.getHttpMethod();
		jlContentType.setVisible(httpMethod == HTTP_METHOD.POST || httpMethod == HTTP_METHOD.PUT);
		tfContentTypeHeader.setVisible(httpMethod == HTTP_METHOD.POST || httpMethod == HTTP_METHOD.PUT);
		jlSendDataAs.setVisible(httpMethod == HTTP_METHOD.POST || httpMethod == HTTP_METHOD.PUT);
		tfSendDataAs.setVisible(httpMethod == HTTP_METHOD.POST || httpMethod == HTTP_METHOD.PUT);
		jlSendHTTPExpectRequestHeader.setVisible(httpMethod == HTTP_METHOD.POST || httpMethod == HTTP_METHOD.PUT);
		tfSendHTTPExpectRequestHeader.setVisible(httpMethod == HTTP_METHOD.POST || httpMethod == HTTP_METHOD.PUT);
		jpMainPanel.revalidate();

		tfHTTPMethod.setText("" + configurationBean.getHttpMethod());
		taURLSignature.setText(configurationBean.getUrlSignature());
		tfAcceptHeader.setText(configurationBean.getAcceptsHeaderValue());
		tfContentTypeHeader.setText(configurationBean.getContentTypeForUpdates());
		tfSendDataAs.setText("" + configurationBean.getOutgoingDataFormat());
		tfSendHTTPExpectRequestHeader.setText("" + configurationBean.getSendHTTPExpectRequestHeader());
	}

	/**
	 * View position hint
	 */
	@Override
	public int getPreferredPosition() {
		// want to be on top, as it's the main contextual view for this activity
		return 100;
	}

	@Override
	public Action getConfigureAction(final Frame owner) {
		// "Configure" button appears because of this action being returned
		return new RESTActivityConfigureAction(activity, owner, editManager, fileManager,
				activityIconManager, serviceDescriptionRegistry, serviceRegistry);
	}

}
