package com.nhulston.essentials.util;

import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public final class Log {
    private static HytaleLogger logger;

    private Log() {}

    public static void init(@Nonnull HytaleLogger logger) {
        Log.logger = logger;
    }

    public static void info(@Nonnull String message) {
        logger.at(Level.INFO).log(message);
    }

    public static void warning(@Nonnull String message) {
        logger.at(Level.WARNING).log(message);
    }

    public static void error(@Nonnull String message) {
        logger.at(Level.SEVERE).log(message);
    }

    public static void error(@Nonnull String message, @Nonnull Throwable throwable) {
        logger.at(Level.SEVERE).withCause(throwable).log(message);
    }
}
