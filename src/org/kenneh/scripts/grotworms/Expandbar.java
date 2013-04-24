package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

import sk.action.ActionBar;

public class Expandbar implements KNode {

	private static final int BAR_WIDGET = 640;
	private static final int MAIN_BAR_CHILD = 4;
	private static final int EXPAND_BUTTON = 3, MINIMIZE_BUTTON = 30;

	public static boolean isExpanded() {
		return Widgets.get(BAR_WIDGET, MAIN_BAR_CHILD).visible();
	}

	public static WidgetChild getExpandButton() {
		return Widgets.get(BAR_WIDGET, (isExpanded()) ? MINIMIZE_BUTTON : EXPAND_BUTTON);
	}



	public static boolean setExpanded(final boolean expanded) {
		if (isExpanded() == expanded)
			return true;
		WidgetChild tc = getExpandButton();
		return tc.visible() && tc.click(true) && new sk.general.TimedCondition(1500) {

			@Override
			public boolean isDone() {
				return isExpanded() == expanded;
			}
		}.waitStop();
	}
	
	public static boolean wrongIndex() {
		return ActionBar.getCurrentBar() != Settings.getBar();
	}

	public static boolean isInvVisible() {
		return Widgets.get(746, 110).visible();
	}

	@Override
	public boolean canActivate() {
		return !isExpanded() || wrongIndex();
	}
	
	public void setToIndex(int index) {
		int curr = ActionBar.getCurrentBar();
		if(curr == index) {
			return;
		}
		if(index < curr) {
			ActionBar.getPrevButton().click(true);
		} else {
			ActionBar.getNextButton().click(true);
		}
		Task.sleep(500);
	}

	@Override
	public void activate() {
		if(wrongIndex()) {
			setToIndex(Settings.getBar());
		}
		if(!isExpanded() && !Bank.isOpen()) {
			setExpanded(true);
		}
	}

}
