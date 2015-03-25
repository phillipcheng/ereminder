package org.cld.datastore;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/**
 * <LogPatternRecog level="WARN">
 */

@Plugin(name = "LogPatternRecog", category = "Core", elementType = "filter", printObject = true)
public final class LogPatternRecog extends AbstractFilter {
    /**
     * Level of messages to be matched
     */
    private Level level=Level.WARN;

    private LogPatternRecog(final Level level, final Result onMatch,
                        final Result onMismatch) {
        super(onMatch, onMismatch);
        this.level = level;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String msg,
                         final Object... params) {
        return filter(level);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object msg,
                         final Throwable t) {
        return filter(level);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Message msg,
                         final Throwable t) {
        return filter(level);
    }

    @Override
    public Result filter(final LogEvent event) {
        return filter(event.getLevel());
    }

    /**
     * Decide if we're going to log <code>event</code> based on whether the
     * maximum burst of log statements has been exceeded.
     *
     * @param level The log level.
     * @return The onMatch value if the filter passes, onMismatch otherwise.
     */
    private Result filter(final Level level) {
        return onMatch;

    }

    @Override
    public String toString() {
        return "level=" + level.toString();
    }


    /**
     * @param level  The logging level.
     * @param match  The Result to return when the filter matches. Defaults to Result.NEUTRAL.
     * @param mismatch The Result to return when the filter does not match. The default is Result.DENY.
     * @return A BurstFilter.
     */
    @PluginFactory
    public static LogPatternRecog createFilter(
            @PluginAttribute("level") final Level level,
            @PluginAttribute("onMatch") final Result match,
            @PluginAttribute("onMismatch") final Result mismatch) {
        final Result onMatch = match == null ? Result.NEUTRAL : match;
        final Result onMismatch = mismatch == null ? Result.DENY : mismatch;
        final Level actualLevel = level == null ? Level.WARN : level;
        return new LogPatternRecog(actualLevel, onMatch, onMismatch);
    }
}