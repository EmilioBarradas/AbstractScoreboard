package com.github.EmilioBarradas;

import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public abstract class AbstractScoreboard {
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    @SuppressWarnings("deprecation") // displayName is set in constructor.
    private final Objective objective = scoreboard.registerNewObjective("...", "dummy");
    private final HashMap<Integer, Team> lines = new HashMap<>();
    private final HashMap<Integer, String> entries = new HashMap<>();

    /**
     * Constructs a new AbstractScoreboard.
     */
    AbstractScoreboard() {
        initializeLines();
        initializeDefaults();

        objective.setDisplayName(getName());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Creates the scoreboard's underlying line entries.
     */
    private void initializeLines() {
        String[] entries = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e"};

        for (int i = 1; i <= 15; i++) {
            String teamID = RandomStringUtils.random(16);
            String entry = ChatColor.translateAlternateColorCodes('&', entries[i - 1]);

            Team team = scoreboard.registerNewTeam(teamID);
            team.addEntry(entry);

            lines.put(i, team);
            this.entries.put(i, entry);
        }
    }

    /**
     * Sets the entry at the specified {@code lineNum} to {@code value}.
     * @param lineNum - the line number of the entry
     * @param value - the new value of the entry
     */
    public void setLine(int lineNum, String value) {
        setScore(lineNum);

        String translatedValue = ChatColor.translateAlternateColorCodes('&', value);
        Team team = lines.get(lineNum);

        if (translatedValue.length() <= 16) {
            team.setPrefix(translatedValue);
            return;
        }

        String prefix = translatedValue.substring(0, 16);

        String lastCode = ChatColor.getLastColors(prefix);

        String suffix = lastCode + translatedValue.substring(16);

        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }

    /**
     * Sets the score of the entry at the specified {@code lineNum} to {@code lineNum}.
     * @param lineNum - the line number of the entry to set the score of
     */
    private void setScore(int lineNum) {
        Score score = objective.getScore(entries.get(lineNum));
        if (!score.isScoreSet()) score.setScore(lineNum);
    }

    /**
     * Applys the scoreboard to the specified player
     * @param player - the player to apply the scoreboard to
     */
    public void apply(Player player) {
        player.setScoreboard(scoreboard);
    }

    /**
     * @return the translated display name of the scoreboard
     */
    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', getDisplayName());
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