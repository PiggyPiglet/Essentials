package com.nhulston.essentials.integration.papi;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlaceholderAPI {
    @Nullable
    String setPlaceholders(final PlayerRef ref, @NotNull final String message);
}
