package engine;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.ufp.inf.sd.rmi.project.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.project.server.gamefactory.GameFactoryRI;
import edu.ufp.inf.sd.rmi.project.server.gamesession.GameSessionRI;
import edu.ufp.inf.sd.rmi.project.server.lobby.LobbyRI;
import menus.MenuHandler;
import menus.PlayerSelectionMP;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class Game extends JFrame {
    private static final long serialVersionUID = 1L;

    //Application Settings
    private static final String build = "0";
    private static final String version = "2";
    public static final String name = "Strategy Game";
    public static int ScreenBase = 32;//Bit size for the screen, 16 / 32 / 64 / 128
    public static boolean dev = true;//Is this a dev copy or not... useless? D:

    public static enum State {STARTUP, MENU, PLAYING, EDITOR}

    ;
    public static State GameState = State.STARTUP;

    //Setup the quick access to all of the other class files.
    public static Map map;
    public static Gui gui;
    public static LoadImages load;
    public static InputHandler input;
    public static Editor edit = new Editor();
    public static Battle btl = new Battle();
    public static ErrorHandler error = new ErrorHandler();
    public static Pathfinding pathing = new Pathfinding();
    public static ListData list;
    public static Save save = new Save();
    public static ComputerBrain brain = new ComputerBrain();
    public static FileFinder finder = new FileFinder();
    public static ViewPoint view = new ViewPoint();

    //Image handling settings are as follows
    public int fps;
    public int fpscount;
    public static Image[] img_menu = new Image[5];
    public static Image img_tile;
    public static Image img_char;
    public static Image img_plys;
    public static Image img_city;
    public static Image img_exts;
    public static Boolean readytopaint;

    //This handles the different players and also is used to speed logic arrays (contains a list of all characters they own)
    public static List<players.Base> player = new ArrayList<players.Base>();
    public static List<buildings.Base> builds = new ArrayList<buildings.Base>();
    public static List<units.Base> units = new ArrayList<units.Base>();
    //These are the lists that will hold commander, building, and unit data to use in the menu's
    public static List<players.Base> displayC = new ArrayList<players.Base>();
    public static List<buildings.Base> displayB = new ArrayList<buildings.Base>();
    public static List<units.Base> displayU = new ArrayList<units.Base>();

    public static GameFactoryRI stub;
    public static GameSessionRI session;
    public static LobbyRI lobby;
    public static PlayerSelectionMP playerSelectionMP;
    public static ObserverImpl observer;
    public static Channel channel;
    public static String username;
    public static Game game;
    public static boolean isRabbit = false;
    public static Integer playerID;

    public Game(GameFactoryRI stub) {
        super(name);
        //Default Settings of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(20 * ScreenBase + 6, 12 * ScreenBase + 12));
        setBounds(0, 0, 20 * ScreenBase + 6, 12 * ScreenBase + 12);
        setUndecorated(false);
        setResizable(false);
        setLocationRelativeTo(null);

        Game.stub = stub;
        game = this;

        //Creates all the gui elements and sets them up
        gui = new Gui(this);
        add(gui);
        gui.setFocusable(true);
        gui.requestFocusInWindow();

        //load images, initialize the map, and adds the input settings.
        load = new LoadImages();
        map = new Map();
        input = new InputHandler();
        list = new ListData();

        setVisible(true);//This has been moved down here so that when everything is done, it is shown.
        gui.LoginScreen();
        save.LoadSettings();
        GameLoop();
    }

    public Game(GameFactoryRI stub, Channel channel) {
        super(name);
        //Default Settings of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(20 * ScreenBase + 6, 12 * ScreenBase + 12));
        setBounds(0, 0, 20 * ScreenBase + 6, 12 * ScreenBase + 12);
        setUndecorated(false);
        setResizable(false);
        setLocationRelativeTo(null);

        isRabbit = true;
        Game.stub = stub;
        Game.channel = channel;
        game = this;

        //Creates all the gui elements and sets them up
        gui = new Gui(this);
        add(gui);
        gui.setFocusable(true);
        gui.requestFocusInWindow();

        //load images, initialize the map, and adds the input settings.
        load = new LoadImages();
        map = new Map();
        input = new InputHandler();
        list = new ListData();
        setVisible(true);//This has been moved down here so that when everything is done, it is shown.
        gui.LoginScreen();
        save.LoadSettings();
        GameLoop();
    }


    public static void gameHandler(String message) {
        String[] content = message.split(";");
        playerID = Integer.parseInt(content[4]);
        players.Base ply = Game.player.get(Game.btl.currentplayer);
        System.out.println(content);
        switch (content[1]) {
            case "select":
                ply.selectx = Integer.parseInt(content[2]); // X
                ply.selecty = Integer.parseInt(content[3]); // Y
                Game.btl.Action();
                break;
            case "cancel":
                Game.player.get(Game.btl.currentplayer).Cancle();
                break;
            case "endTurn":
                Game.btl.day = Integer.parseInt(content[11]);
                Game.btl.currentplayer = Integer.parseInt(content[10]);
                Game.btl.EndTurn();

                System.out.println("current " + Game.btl.currentplayer);
                break;
            case "buyUnit":
                Game.btl.Buyunit(Integer.parseInt(content[5]), Integer.parseInt(content[2]), Integer.parseInt(content[3]));
                MenuHandler.CloseMenu();
                break;
        }

    }

    public Game() {
        super(name);
        //Default Settings of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(20 * ScreenBase + 6, 12 * ScreenBase + 12));
        setBounds(0, 0, 20 * ScreenBase + 6, 12 * ScreenBase + 12);
        setUndecorated(false);
        setResizable(false);
        setLocationRelativeTo(null);


        //Creates all the gui elements and sets them up
        gui = new Gui(this);
        add(gui);
        gui.setFocusable(true);
        gui.requestFocusInWindow();

        //load images, initialize the map, and adds the input settings.
        load = new LoadImages();
        map = new Map();
        input = new InputHandler();
        list = new ListData();

        setVisible(true);//This has been moved down here so that when everything is done, it is shown.
        gui.LoginScreen();
        save.LoadSettings();
        GameLoop();
    }

    private void GameLoop() {
        boolean loop = true;
        long last = System.nanoTime();
        long lastCPSTime = 0;
        long lastCPSTime2 = 0;
        @SuppressWarnings("unused")
        int logics = 0;
        logics++;
        while (loop) {
            //Used for logic stuff
            @SuppressWarnings("unused")
            long delta = (System.nanoTime() - last) / 1000000;
            delta++;
            last = System.nanoTime();

            //FPS settings
            if (System.currentTimeMillis() - lastCPSTime > 1000) {
                lastCPSTime = System.currentTimeMillis();
                fpscount = fps;
                fps = 0;
                error.ErrorTicker();
                setTitle(name + " v" + build + "." + version + " : FPS " + fpscount);
                if (GameState == State.PLAYING) {
                    if (player.get(btl.currentplayer).npc && !btl.GameOver) {
                        brain.ThinkDamnYou(player.get(btl.currentplayer));
                    }
                }
            } else fps++;
            //Current Logic and frames per second location (capped at 20 I guess?)
            if (System.currentTimeMillis() - lastCPSTime2 > 100) {
                lastCPSTime2 = System.currentTimeMillis();
                logics = 0;
                if (GameState == State.PLAYING || GameState == State.EDITOR) {
                    view.MoveView();
                }//This controls the view-point on the map
                if (GameState == State.EDITOR) {
                    if (edit.holding && edit.moved) {
                        edit.AssButton();
                    }
                }
                Game.gui.frame++;//This is controlling the current frame of animation.
                if (Game.gui.frame >= 12) {
                    Game.gui.frame = 0;
                }
                gui.repaint();
            } else logics++;

            //Paints the scene then sleeps for a bit.
            try {
                Thread.sleep(30);
            } catch (Exception e) {
            }
            ;
        }
    }

    /**
     * Starts a new game when launched.
     */
    public static void main(String args[]) throws Exception {
        new Game();
    }
}
