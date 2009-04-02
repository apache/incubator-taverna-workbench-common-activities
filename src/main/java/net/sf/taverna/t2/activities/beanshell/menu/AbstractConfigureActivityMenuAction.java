package net.sf.taverna.t2.activities.beanshell.menu;

import java.awt.Frame;

import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;
import net.sf.taverna.t2.ui.menu.DefaultContextualMenu;
import net.sf.taverna.t2.workbench.ui.Utils;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

public abstract class AbstractConfigureActivityMenuAction<ActivityClass extends Activity<?>>
		extends AbstractContextualMenuAction {

	protected final Class<ActivityClass> activityClass;

	public AbstractConfigureActivityMenuAction(
			Class<ActivityClass> activityClass) {
		super(DefaultContextualMenu.DEFAULT_CONTEXT_MENU, 40);
		this.activityClass = activityClass;
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && findActivity() != null;
	}

	protected ActivityClass findActivity() {
		if (getContextualSelection() == null) {
			return null;
		}
		Object selection = getContextualSelection().getSelection();
		if (activityClass.isInstance(selection)) {
			return activityClass.cast(selection);
		}
		if (selection instanceof Processor) {
			Processor processor = (Processor) selection;
			for (Activity<?> activity : processor.getActivityList()) {
				if (activityClass.isInstance(activity)) {
					return activityClass.cast(activity);
				}
			}
		}
		return null;
	}

	protected Frame getParentFrame() {
		return Utils.getParentFrame(getContextualSelection()
				.getRelativeToComponent());
	}

}