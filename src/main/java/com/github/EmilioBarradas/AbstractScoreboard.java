package com.github.EmilioBarradas;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public abstract class AbstractScoreboard {
    /**
     * The pattern used to split entry lines.
     */
    private static final Pattern LINE_SPLITTER =
        Pattern.compile(".{1,16}(?<!&)");

    /**
     * The underlying bukkit scoreboard used for the abstract scoreboard.
     */
    private final Scoreboard scoreboard =
        Bukkit.getScoreboardManager().getNewScoreboard();
    /**
     * The scoreboard objective used for the abstract scoreboard.
     */
    @SuppressWarnings("deprecation") // displayName is set in constructor.
    private final Objective objective =
        scoreboard.registerNewObjective("...", "dummy");
    /**
     * The lines of the underlying scoreboard.
     */
    private final HashMap<Integer, Team> lines = new HashMap<>();
    /**
     * The entries of the underlying scoreboard.
     */
    private final HashMap<Integer, String> entries = new HashMap<>();

    /**
     * Constructs a new AbstractScoreboard.
     */
    protected AbstractScoreboard() {
        initializeLines();
        initializeDefaults();

        objective.setDisplayName(getName());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Creates the scoreboard's underlying line entries.
     */
    private void initializeLines() {
        String[] entries = {"&0", "&1", "&2", "&3", "&4", "&5", "&6",
                            "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e"};

        // CHECKSTYLE.OFF: MagicNumber
        // 15 is the maximum amount of lines on a scoreboard.
        for (int i = 1; i <= 15; i++) {
            String teamID = RandomStringUtils.random(16);
            String entry =
                ChatColor.translateAlternateColorCodes('&', entries[i - 1]);

            Team team = scoreboard.registerNewTeam(teamID);
            team.addEntry(entry);

            this.lines.put(i, team);
            this.entries.put(i, entry);
        }
        // CHECKSTYLE.ON: MagicNumber
    }

    /**
     * Sets the entry at the specified {@code lineNum} to {@code value}.
     * @param lineNum - the line number of the entry
     * @param value - the new value of the entry
     */
    public void setLine(final int lineNum, final String value) {
        setScore(lineNum);

        Team team = lines.get(lineNum);

        // CHECKSTYLE.OFF: MagicNumber
        // 16 is maximum string length for a scoreboard entry
        // without a prefix and a suffix.
        if (value.length() <= 16) {
            team.setPrefix(color(value));
            team.setSuffix("");
            return;
        }
        // CHECKSTYLE.ON: MagicNumber

        Matcher matcher = LINE_SPLITTER.matcher(value);

        matcher.find();

        String prefix = color(matcher.group());
        String lastCode = ChatColor.getLastColors(prefix);

        matcher.find();

        String suffix = color(lastCode + matcher.group());

        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }

    /**
     * Sets the score of the entry at the
     * specified {@code lineNum} to {@code lineNum}.
     * @param lineNum - the line number of the entry to set the score of
     */
    private void setScore(final int lineNum) {
        Score score = objective.getScore(entries.get(lineNum));
        if (!score.isScoreSet()) {
            score.setScore(lineNum);
        }
    }

    /**
     * Deletes the line at the specified {@code lineNum} from the scoreboard.
     * @param lineNum - the line number of the entry to delete the line of
     */
    public void deleteLine(final int lineNum) {
        scoreboard.resetScores(entries.get(lineNum));
    }

    /**
     * Applys the scoreboard to the specified player.
     * @param player - the player to apply the scoreboard to
     */
    public void apply(final Player player) {
        player.setScoreboard(scoreboard);

        Team team = scoreboard.getTeam("collisions");

        if (team != null) {
            team.addEntry(player.getName());
        }
    }

    /**
     * @return the translated display name of the scoreboard
     */
    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', getDisplayName());
    }

    /**
     * Sets whether the players of this scoreboard
     * should collide with other players.
     * @param collisions {@code true} if the players should
     *                   collide, otherwise {@code false}.
     */
    public void setCollisions(final boolean collisions) {
        Team team = scoreboard.getTeam("collisions");

        if (team == null) {
            team = scoreboard.registerNewTeam("collisions");
        }

        team.setOption(
            Option.COLLISION_RULE,
            collisions ? OptionStatus.ALWAYS : OptionStatus.NEVER
        );
    }

    /**
     * Translates the string into a colored string.
     * @param value the value to translate.
     * @return the translated string.
     */
    public String color(final String value) {
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    /**
     * Sets the default text of the scoreboard.
     */
    public abstract void initializeDefaults();

    /**
     * @return the display name of the scoreboard
     */
    public abstract String getDisplayName();
}
