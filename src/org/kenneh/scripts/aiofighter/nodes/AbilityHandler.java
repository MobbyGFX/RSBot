package org.kenneh.scripts.aiofighter.nodes;



import org.kenneh.scripts.aiofighter.FighterGUI;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

import sk.action.Ability;
import sk.action.ActionBar;
import sk.action.book.AbilityType;
import sk.action.book.BookAbility;
import sk.general.TimedCondition;


public class AbilityHandler extends LoopTask {

	public static boolean waitForAbility = false;
	public static boolean waitingForRejuv = false;

	public static boolean barContainsRejuvenation() {
		for(int i = 0; i < 12; i++) {
			if(ActionBar.getAbilityInSlot(i) == BookAbility.REJUVENATE) return true; 
		}
		return false;
	}

	public static boolean rejuvOffCd() {
		WidgetChild rejuvWidget = ActionBar.getReloadChild(ActionBar.findAbility(BookAbility.REJUVENATE));
		return barContainsRejuvenation() && rejuvWidget.validate() && !rejuvWidget.visible();
	}

	public static boolean needsToRejuvenate() {
		return rejuvOffCd() && Players.getLocal().getHealthPercent() < org.kenneh.scripts.aiofighter.Settings.getEatPercent() - 10 && getAdrenalinePercent() == 100;
	}


	public static void sendAbility(final BookAbility ability) {

		BookAbility[] castTimes = {BookAbility.ASPHYXIATE, BookAbility.SNIPE, BookAbility.RAPID_FIRE, BookAbility.UNLOAD, BookAbility.FRENZY,
				BookAbility.ASSAULT, BookAbility.DESTROY, BookAbility.FURY, BookAbility.FLURRY
		};

		//ActionBar.makeReadyForInteract();

		WidgetChild w = Widgets.get(137, 56);
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

		//Logger.log("Using ability: " + ability.name());

		for(BookAbility cast : castTimes) {
			if(ability.equals(cast)) {
				waitForAbility = true;
				Task.sleep(4000);
				waitForAbility = false;
			}
		}
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

	public static int getShieldSlotId() {
		return Players.getLocal().getAppearance()[5];
	}

	public static int getWeaponSlotId() {
		return Players.getLocal().getAppearance()[3];
	}

	public static int getAdrenalinePercent() {
		return ActionBar.getAdrenaline() / 10;
	}

	public static boolean momentumIsEnabled() {
		return Settings.get(1040) != 0;
	}

	@Override
	public int loop() {

		try {	

			if(MonsterKiller.mainWeapon != -1 && getWeaponSlotId() != MonsterKiller.mainWeapon && !waitingForRejuv) {
				Item weapon = Inventory.getItem(MonsterKiller.mainWeapon);
				if(weapon != null) {
					weapon.getWidgetChild().click(true);
					return 1000;
				}
			}

			if(MonsterKiller.offWeapon != -1 && getShieldSlotId() != MonsterKiller.offWeapon && !waitingForRejuv) {
				Item weapon = Inventory.getItem(MonsterKiller.offWeapon);
				if(weapon != null) {
					weapon.getWidgetChild().click(true);
					return 1000;
				}
			}

			if(!momentumIsEnabled()) {
				if(!needsToRejuvenate()) {
					if(Players.getLocal().getInteracting() != null) {
						BookAbility toSend = null;
						if (toSend == null && Players.getLocal().getInteracting().getHealthPercent() > 50) toSend = getAbility(AbilityType.ULTIMATE);
						if (toSend == null && Players.getLocal().getInteracting().getHealthPercent() > 25) toSend = getAbility(AbilityType.THRESHOLD);
						if (toSend == null) toSend = getAbility(AbilityType.BASIC);
						if (toSend != null) sendAbility(toSend);
					}
				} else {
					if(getAdrenalinePercent() == 100) {
						if(getShieldSlotId() != -1 && MonsterKiller.shieldId == -1) {
							waitingForRejuv = true;
							sendAbility(BookAbility.REJUVENATE);
							Task.sleep(5000);
							waitingForRejuv = false;
							return 500;
						}
						if(getShieldSlotId() == MonsterKiller.shieldId) {
							waitingForRejuv = true;
							sendAbility(BookAbility.REJUVENATE);
							Task.sleep(10000);
							waitingForRejuv = false;
							return 500;
						} else {
							Item shield = Inventory.getItem(MonsterKiller.shieldId);
							if(shield != null) {
								waitingForRejuv = true;
								shield.getWidgetChild().click(true);
								return 1000;
							}
						}
					}
				}
			}
		} catch(Exception a) {
			System.out.println("Timer plx fix internal errors!");
		}
		return FighterGUI.abilityDelay;
	}

}
