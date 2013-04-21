package org.kenneh.core.api.utils;

import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

import sk.action.Ability;
import sk.action.ActionBar;
import sk.action.book.AbilityType;
import sk.action.book.BookAbility;
import sk.general.TimedCondition;

public class AbilityHandler extends LoopTask {
	
	@SuppressWarnings("unused")
	private boolean waitForAbility = false;

	private void sendAbility(final BookAbility ability) {
		
		if(!ActionBar.isExpanded()) {
			ActionBar.setExpanded(true);
		}
		
		final BookAbility[] castTimes = {BookAbility.ASPHYXIATE, BookAbility.SNIPE, BookAbility.RAPID_FIRE, BookAbility.UNLOAD, BookAbility.FRENZY,
				BookAbility.ASSAULT, BookAbility.DESTROY, BookAbility.FURY, BookAbility.FLURRY
		};

		final WidgetChild w = Widgets.get(137, 56);
		if(w.getText().equals("[Press Enter to Chat]")){
			new TimedCondition(1500) {
				@Override
				public boolean isDone() {
					return ActionBar.useAbility(ability);
				}
			}.waitStop();
		} else {
			new TimedCondition(1500) {
				@Override
				public boolean isDone() {
					return ActionBar.getItemChild(ActionBar.findAbility(ability)).click(true);
				}
			}.waitStop();
		}

		for(BookAbility cast : castTimes) {
			if(ability.equals(cast)) {
				waitForAbility = true;
				Task.sleep(4000);
				waitForAbility = false;
			}
		}
	}
	
	@Override
	public int loop() {
		if(Players.getLocal().getInteracting() != null) {
			BookAbility toSend = null;
			if (toSend == null) toSend = getAbility(AbilityType.ULTIMATE);
			if (toSend == null) toSend = getAbility(AbilityType.THRESHOLD);
			if (toSend == null) toSend = getAbility(AbilityType.BASIC);
			if (toSend != null) sendAbility(toSend);
		}
		return 250;
	}
	
	public static BookAbility getAbility(AbilityType type) {
		for(int i = 11; i >= 0; i--) {
			Ability a = ActionBar.getAbilityInSlot(i);
			if(a != null && a instanceof BookAbility && ActionBar.isReady(i)) {
				BookAbility ability = (BookAbility) a;
				if(ability.getType() == type) return ability;
			}
		}
		return null;
	}

}
