package org.kenneh.scripts;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.methods.widget.Lobby;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@Manifest(authors = { "Kenneh" }, description = "AFK's Castle Wars games", name = "CastleWarsAFKer", version = 1.04)
public class CastleWarsAFKer extends ActiveScript implements PaintListener {

    private Image bg = null;
    
    private int gamesTied = 0, gamesWon = 0, gamesLoss = 0, gamesPlayed = 0, ticketsGained = 0, til5k = 0;;

    private final int GUTHIX_PORTAL = 4408;
    private final Area LOBBYY = new Area(new Tile(2457, 3077, 0),new Tile(2434, 3103, 0));
    private final Area SARA_BASE = new Area(new Tile(2423, 3072, 1),new Tile(3432, 3081, 1));
    private final Area ZAMMY_BASE= new Area(new Tile(2368, 3127, 1),new Tile(2377, 3136, 1));
    
    private  final int LADDER[] = {6280, 6281};
    private final Timer timer = new Timer(0);

    private  boolean PlayerInLobby() {
        return LOBBYY.contains(Players.getLocal().getLocation());
    }
    
    private final List<Node> jobsCollection = Collections .synchronizedList(new ArrayList<Node>());

    private Tree jobContainer = null;

    private synchronized final void provide(final Node... jobs) {
        for (final Node job : jobs) {
            if (!jobsCollection.contains(job)) {
                log.info("Providing: " + job.getClass().getSimpleName());
                jobsCollection.add(job);
            }
        }
        jobContainer = new Tree(jobsCollection.toArray(new Node[jobsCollection.size()]));
    }

    @Override
    public int loop() {

        if (Game.getClientState() != Game.INDEX_MAP_LOADED) {
            return 1000;
        }

        if (jobContainer != null && Game.isLoggedIn()) {
            try {
                final Node job = jobContainer.state();
                if (job != null) {
                    jobContainer.set(job);
                    getContainer().submit(job);
                    job.join();
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
        }
        return 50;
    }

    private class AntiBan extends Node {
        @Override
        public boolean activate() {
            return !Widgets.get(1188).validate()
                    && !SARA_BASE.contains(Players.getLocal())
                    && !ZAMMY_BASE.contains(Players.getLocal())
                    && !Widgets.get(985).validate()
                    && !LOBBYY.contains(Players.getLocal())
                    && !Widgets.get(1127).validate()
                    && !Widgets.get(1186).validate()
                    && !Lobby.isOpen();
        }

        @Override
        public void execute() {
            log.info("Performing AntiBan.");
            //Max and Min Time
            int minMilliSecond = 500;
            int maxMillisecond = 50000;
            Task.sleep(Random.nextInt(minMilliSecond, maxMillisecond));
            Camera.setPitch(Random.nextInt(-65, -20));

            int i=Random.nextInt(1,200);
            switch (i){
            case 16: Tabs.ATTACK.open();
            break;
            case 42: Tabs.CLAN_CHAT.open();
            break;
            case 98: Tabs.EQUIPMENT.open();
            break;
            case 72: Tabs.FRIENDS.open();
            break;
            case 177: Tabs.INVENTORY.open();
            break;
            case 65: Tabs.NONE.open();
            break;
            case 37: Tabs.OPTIONS.open();
            break;
            case 21: Tabs.STATS.open();
            break;
            case 118: Tabs.ABILITY_BOOK.open();
            break;
            case 140: Tabs.INVENTORY.open();
            break;
            case 133:
                Camera.setNorth();
                break;

            default:
                //randomly generated numbers for mouse
                int x=Random.nextInt(1,450);
                int y=Random.nextInt(1,450);
                int randomX= Random.nextInt(1,300);
                int randomY=Random.nextInt(1,300);
                Mouse.move(x,y,randomX,randomY);

                int ii=Random.nextInt(1,120);
                switch (ii){
                case 23:
                    Camera.setAngle(Random.nextInt(1, 450));
                    break;

                case 52:
                    Camera.setPitch(Random.nextInt(1, 450));
                    break;

                case 82:
                    Camera.setAngle(Random.nextInt(10, 500));
                    Camera.setPitch(Random.nextInt(10, 500));
                    break;

                case 6:
                    Camera.setAngle(Random.nextInt(20, 300));
                    break;

                case 118:
                    Mouse.move(Random.nextInt(Mouse.getLocation().x - 150,
                            Mouse.getLocation().x + 150),
                            Random.nextInt(Mouse.getLocation().y - 150,
                                    Mouse.getLocation().y + 150));
                    break;
                default:
                    break;
                }
                break;
            }
        }    
    }

    private class joinGuthix extends Node {

        @Override
        public boolean activate() {
            SceneObject g = SceneEntities.getNearest(GUTHIX_PORTAL);
            return     PlayerInLobby()
                    && g !=null
                    && !Widgets.get(985).validate()
                    && !Widgets.get(1127).validate();
        }

        @Override
        public void execute() {
            log.info("Joining Guthix.");
            Camera.setPitch(Random.nextInt(70, 98));
            SceneObject g = SceneEntities.getNearest(GUTHIX_PORTAL);
            if(g != null) {
                if(PlayerInLobby()) {
                    if(!g.isOnScreen()) {
                        Walking.walk(g);
                        Camera.turnTo(g);
                        Task.sleep(200, 400);
                    } else {
                        g.interact("Enter");
                        Task.sleep(2000, 2500);
                    }
                }
            }                      
        }
    }

    private class joinEarly extends Node {
        
        @Override
        public boolean activate() {
            return     Widgets.get(1188).validate() && Widgets.get(1188, 3).validate()
                    && !SARA_BASE.contains(Players.getLocal())
                    && !ZAMMY_BASE.contains(Players.getLocal())
                    && !LOBBYY.contains(Players.getLocal());
        }

        @Override
        public void execute() {
            if(Widgets.get(1188).validate()){
                log.info("Joining Early.");
                Keyboard.sendKey('1');
                Task.sleep(Random.nextInt(120, 166));
            } else if (Widgets.get(1186).validate()) {
                Widgets.get(1186).getChild(7).click(true);
                Task.sleep(200, 500);
            }
        }
    }

    private class climbLadder extends Node {

        @Override
        public boolean activate() {
            SceneObject barrier = SceneEntities.getNearest(LADDER);
            return  barrier != null
                    &&    SARA_BASE.contains(Players.getLocal()) ||
                    ZAMMY_BASE.contains(Players.getLocal());
        }

        @Override
        public void execute() {
            log.info("Climbing ladder.");
            SceneObject ladder = SceneEntities.getNearest(LADDER);
            if(ladder != null) {
                if(!ladder.isOnScreen()) {
                    Walking.walk(ladder);
                    Camera.turnTo(ladder);
                } else {
                    ladder.interact("Climb");
                    Task.sleep(4000, 5000);
                }
            }
        }
    }

    private class CloseInterface extends Node {

        @Override
        public boolean activate() {
            return Widgets.get(985).validate() && Widgets.get(985, 77).validate();
        }

        @Override
        public void execute() {
            if (Widgets.get(985).getChild(19).getText().contains("1 ticket")) {
                ticketsGained ++;
                gamesTied ++;
            } else if (Widgets.get(985).getChild(19).getText().contains("2 tickets")) {
                ticketsGained +=2;
                gamesWon ++ ;
            } else if (Widgets.get(985).getChild(19).getText().contains("Nothing")) {
                gamesLoss++;
            }
            til5k = 5000 - Integer.parseInt(Widgets.get(985, 23).getText());
            Widgets.get(985).getChild(77).click(true);
            Task.sleep(1000);
            gamesPlayed++;
        }
    }

    
    public void onStart() {
        try {
            bg = ImageIO.read(new URL("http://puu.sh/1UUWn.png"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Context.setLoginWorld(24);
        provide(new joinGuthix());
        provide(new AntiBan());
        provide(new joinEarly());
        provide(new climbLadder());
        provide(new CloseInterface());
        provide(new HybridArm());
    }

    private class HybridArm extends Node {

        @Override
        public boolean activate() {
            return Widgets.get(1127, 15).validate();
        }

        @Override
        public void execute() {
            log.info("Clamining hood/helm");
            WidgetChild hoods = Widgets.get(1127, Random.nextInt(6, 8));
            if(hoods.validate()) hoods.click(true);
        }

    }

    private String timeTil5000() {
        long gameTime = 1000 * 60 * 25;
        return Time.format(gameTime * til5k);
    }

    @Override
    public void onRepaint(Graphics g) {
        g.setFont(new Font("Calibri", Font.PLAIN, 13));
        g.drawImage(bg, 2, 0, null);
        g.drawString(timer.toElapsedString(), 162, 40);
        g.drawString(String.valueOf(ticketsGained), 620, 40);
        g.drawString(String.valueOf(gamesPlayed) , 268, 40);
        g.drawString(String.valueOf(gamesWon), 362, 40);
        g.drawString(String.valueOf(gamesLoss), 445, 40);
        g.drawString(String.valueOf(gamesTied), 530, 40);
        g.drawString(String.valueOf(timeTil5000()), 674, 40);
    }

}