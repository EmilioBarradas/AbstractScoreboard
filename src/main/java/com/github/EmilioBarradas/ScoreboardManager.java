package com.github.EmilioBarradas;

import java.util.HashMap;
import java.util.Map;

public abstract class ScoreboardManager {
    /**
     * The scoreboards stored within the scoreboard manager.
     */
    private static final Map<String, AbstractScoreboard> SCOREBOARDS =
        new HashMap<>();

    /**
     * Sets the scoreboard of the unique identifier
     * within the scoreboard manager.
     * @param <T> any scoreboard type.
     * @param id the unqiue identifier of the scoreboard.
     * @param scoreboard the scoreboard to store.
     */
    public static <T extends AbstractScoreboard> void set(final String id,
                                                   final T scoreboard) {
        SCOREBOARDS.put(id, scoreboard);
    }

    /**
     * @param <T> any scoreboard type.
     * @param id the unique identifier of the scoreboard.
     * @param clazz the scoreboard class.
     * @return the retrieved scoreboard instance.
     * @throws ClassCastException thrown if the scoreboard could
     *         not be parsed to the specified class.
     */
    public static <T extends AbstractScoreboard> T get(final String id,
                                                final Class<T> clazz)
                                                throws ClassCastException {
        return clazz.cast(SCOREBOARDS.get(id));
    }
}
