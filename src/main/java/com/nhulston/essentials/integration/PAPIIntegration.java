package com.nhulston.essentials.integration;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.nhulston.essentials.integration.papi.PlaceholderAPI;
import com.nhulston.essentials.managers.HomeManager;
import com.nhulston.essentials.managers.KitManager;
import com.nhulston.essentials.util.Log;
import com.nhulston.essentials.util.StorageManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public final class PAPIIntegration {
    private static PlaceholderAPI placeholderapi = null;
    private static boolean available = false;

    private PAPIIntegration() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void register(@NotNull final HomeManager homeManager, @NotNull final KitManager kitManager,
                                @NotNull final StorageManager storageManager) {
        try {
            final Class<?> papiClass = Class.forName("at.helpch.placeholderapi.PlaceholderAPI");
            placeholderapi = ((Class<PlaceholderAPI>) Class.forName("com.nhulston.essentials.integration.papi.PAPIImplementation")).getConstructor().newInstance();
            available = true;

            final Class<?> expansionClass = Class.forName("com.nhulston.essentials.integration.papi.EssentialsExpansion");
            final Object expansion = expansionClass.getConstructor(HomeManager.class, KitManager.class, StorageManager.class).newInstance(homeManager, kitManager, storageManager);
            final Method register = expansionClass.getMethod("register");
            register.invoke(expansion);
            Log.info("[PlaceholderAPI] Found, placeholders will be replaced in chat.");
        } catch (Exception e) {
            available = false;
        }
    }

    public static boolean available() {
        return available;
    }

    @NotNull
    public static PlaceholderAPI get() {
        // if for whatever reason this method gets called even when papi isn't available, we'll provide a null impl so it doesn't error out
        return placeholderapi == null || !available ? NullPlaceholderAPI.INSTANCE : placeholderapi;
    }

    private static class NullPlaceholderAPI implements PlaceholderAPI {
        private static final PlaceholderAPI INSTANCE = new NullPlaceholderAPI();

        @Override
        public @NotNull String setPlaceholders(final PlayerRef ref, final @NotNull String message) {
            return message;
        }

        @Override
        public @NotNull String setRelationalPlaceholders(final PlayerRef one, final PlayerRef two, final @NotNull String message) {
            return message;
        }
    }
}
