package org.kenneh.core.api;

import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
 
/**
 * Lodestone teleportation method
 * 
 * @Author Mortemer2112
 */
public class Lodestone {
 
    public static final int AL_KHARID = 40;
    public static final int ARDOUGNE = 41;
    public static final int BANDIT_CAMP = 7;
    public static final int BURTHOPE = 42;
    public static final int CATHERBY = 43;
    public static final int DRAYNOR_VILLAGE = 44;
    public static final int EDGEVILLE = 45;
    public static final int FALADOR = 46;
    public static final int LUMBRIDGE = 47;
    public static final int LUNAR_ISLE = 39;
    public static final int PORT_SARIM = 48;
    public static final int SEERS_VILLAGE = 49;
    public static final int TAVERLY = 50;
    public static final int VARROCK = 51;
    public static final int YANILE = 52;
 
    /**
     * Teleports to a specificed location via lodestones
     * 
     * @param widgetId
     *            of desired teleport location
     */
    public static void teleport(int widgetId) {
        final WidgetChild SKILL_TAB = Widgets.get(275, 16);
        if (SKILL_TAB.validate() && !SKILL_TAB.visible()) {
            final WidgetChild OPEN_SKILL_TAB = Widgets.get(548, 114);
            if (OPEN_SKILL_TAB.validate()
                    && OPEN_SKILL_TAB.interact("Ability Book"))
                Task.sleep(300, 600);
        }
        final WidgetChild MAGIC_TAB = Widgets.get(275, 62);
        if (MAGIC_TAB.validate() && !MAGIC_TAB.visible()) {
            final WidgetChild OPEN_MAGIC_TAB = Widgets.get(275, 40);
            if (OPEN_MAGIC_TAB.validate() && OPEN_MAGIC_TAB.interact("Magic"))
                Task.sleep(300, 600);
        }
        final WidgetChild TELEPORT_TAB = Widgets.get(275, 38);
        if (TELEPORT_TAB.validate() && !TELEPORT_TAB.visible()) {
            final WidgetChild OPEN_TELEPORT_TAB = Widgets.get(275, 46);
            if (OPEN_TELEPORT_TAB.validate()
                    && OPEN_TELEPORT_TAB.interact("Teleport-spells"))
                Task.sleep(300, 600);
        }
        final WidgetChild LODESTONE_BUTTON = Widgets.get(275, 16).getChild(155);
        if (LODESTONE_BUTTON.validate() && LODESTONE_BUTTON.visible())
            if (LODESTONE_BUTTON.interact("Cast"))
                Task.sleep(800, 1200);
        final WidgetChild DESTINATION_CHOOSE = Widgets.get(1092, 0);
        if (DESTINATION_CHOOSE.validate() && DESTINATION_CHOOSE.visible()) {
            final WidgetChild DESTINATION_BUTTON = Widgets.get(1092, widgetId);
            if (DESTINATION_BUTTON.validate() && DESTINATION_BUTTON.visible()) {
                if (DESTINATION_BUTTON.interact("Teleport")) {
                    Task.sleep(2000, 3000);
                    final Timer TIMEOUT = new Timer(15000);
                    while (TIMEOUT.isRunning() && !Players.getLocal().isIdle()) {
                        Task.sleep(50, 100);
                    }
                }
            }
        }
    }
}