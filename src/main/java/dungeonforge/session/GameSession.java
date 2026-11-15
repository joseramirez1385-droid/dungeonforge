package dungeonforge.session;

import dungeonforge.commands.Command;
import dungeonforge.commands.CommandHistory;
import dungeonforge.commands.CommandParser;
import dungeonforge.commands.GameContext;

import dungeonforge.core.Combat;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Player;

import dungeonforge.events.AchievementSystem;
import dungeonforge.events.CombatLog;
import dungeonforge.events.DamageMeter;
import dungeonforge.events.EventBus;
import dungeonforge.events.GameEvent;
import dungeonforge.events.GameEventListener;
import dungeonforge.events.QuestTracker;

import dungeonforge.factory.MonsterDef;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.MonsterSource;

import dungeonforge.items.BasicWeapon;
import dungeonforge.items.Hourglass;
import dungeonforge.items.Potion;

import dungeonforge.legacy.LegacyBestiary;
import dungeonforge.legacy.LegacyBestiaryAdapter;

import java.util.ArrayList;
import java.util.List;

public final class GameSession {

    private final Player player;
    private final GameWorld world;
    private final EventBus bus;
    private final CommandParser parser;
    private final CommandHistory history;
    private final GameContext ctx;

    private final List<String> transcript = new ArrayList<>();
    private final List<String> sourceReport = new ArrayList<>();

    private GameSession(Player player, GameWorld world, EventBus bus, CommandParser parser, CommandHistory history,
                        GameContext ctx) {
        this.player = player;
        this.world = world;
        this.bus = bus;
        this.parser = parser;
        this.history = history;
        this.ctx = ctx;
    }

    public static GameSession start(String playerName, GameEventListener... views) {
        Player player = new Player(playerName);
        player.addItem(new Potion("Small Healing Draught", 0.3, 20, 22));
        player.addItem(new Hourglass(2));
        player.addItem(new BasicWeapon("Notched Shortsword", 2.5, 25, 5));

        // ORDER MATTERS: Every monster source BEFORE generating the world.
        MonsterFactory factory = new MonsterFactory();
        List<String> sourceReport = new ArrayList<>();
        sourceReport.add(registerInto(factory,
                new LegacyBestiaryAdapter(
                        new LegacyBestiary("bestiary.csv"), "bestiary.csv (0.4 export)")
        ));

        GameWorld world = new GameWorld(player, factory);

        EventBus bus = new EventBus();
        QuestTracker quests = new QuestTracker(bus);
        AchievementSystem achievements = new AchievementSystem(bus);
        CombatLog log = new CombatLog(200);
        DamageMeter meter = new DamageMeter(bus, player, 0.25);

        CommandHistory history = new CommandHistory();
        GameContext ctx = new GameContext(world, player, bus, new Combat(bus), history);
        CommandParser parser = new CommandParser(quests, achievements);

        GameSession session = new GameSession(player, world, bus, parser, history, ctx);
        session.sourceReport.addAll(sourceReport);

        bus.subscribe(quests);
        bus.subscribe(achievements);
        bus.subscribe(log);
        bus.subscribe(meter);
        bus.subscribe(session.new TranscriptRecorder());

        for (GameEventListener view : views) bus.subscribe(view);

        bus.message("Type 'help' for what you can do. " + "You are carrying an hourglass -- "
                + "'use hourglass' rewinds a turn.");
        session.runLine("look");
        return session;
    }

    public void runLine(String line) {
        Command command = parser.parse(line, ctx);
        command.execute();
        history.push(command);
        ctx.syncState();
    }

    public static GameWorld buildWorldOnly(String playerName) {
        MonsterFactory factory = new MonsterFactory();
        registerInto(factory, new LegacyBestiaryAdapter(new LegacyBestiary("bestiary.csv"),
                "bestiary.csv (0.4 export)"));
        return new GameWorld(new Player(playerName), factory);
    }

    public boolean isRunning() { return ctx.isRunning(); }

    public String prompt() {
        return "\n[" + ctx.getState().name() + " | HP " + ctx.getPlayer().getHp() + "/"
                + ctx.getPlayer().getMaxHp() + "] > ";
    }

    public String epilogue() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(player.describe()).append("\n");
        if (!player.isAlive()) sb.append("  You die in the dark.\n");
        sb.append("  Actions this session: ").append(history.replayLog().size()).append("  |  undo stack depth: ")
                .append(history.depth());
        return sb.toString();
    }

    private static String registerInto(MonsterFactory factory, MonsterSource source) {
        int before = factory.blueprintCount();
        for (MonsterDef def : source.definitions()) factory.register(def);
        return source.sourceName() + ": " + (factory.blueprintCount() - before) + " blueprints";
    }

    public List<String> transcript() { return List.copyOf(transcript); }
    public List<String> sourceReport() { return List.copyOf(sourceReport); }
    public Player getPlayer() { return player; }
    public GameWorld getWorld() { return world; }
    public GameContext getContext() { return ctx; }
    public CommandHistory getHistory() { return history; }
    public EventBus getBus() { return bus; }
    public int listenerCount() { return bus.listenerCount(); }

    private final class TranscriptRecorder implements GameEventListener {
        private final CombatLog formatter = new CombatLog(1);

        @Override
        public void onEvent(GameEvent event) {
            formatter.onEvent(event);
            transcript.addAll(formatter.getLines());
        }
    }
}