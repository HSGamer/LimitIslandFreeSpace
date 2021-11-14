package me.hsgamer.limitislandfreespace;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.events.island.IslandCreateEvent;
import world.bentobox.bentobox.api.events.island.IslandResetEvent;
import world.bentobox.bentobox.util.Util;

import java.util.Optional;
import java.util.UUID;

public class LimitIslandFreeSpace extends Addon implements Listener {
    private long limitFreeBytes = 8589934592L;
    private String errorMessage = "&cYou can't create the island as the system is low disk space. Ask an admin to expand the system.";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        registerListener(this);
    }

    @Override
    public void onDisable() {
        // EMPTY
    }

    @Override
    public void onReload() {
        reloadConfig();
        loadConfig();
    }

    private void loadConfig() {
        limitFreeBytes = getConfig().getLong("limit-free-bytes", limitFreeBytes);
        errorMessage = getConfig().getString("error-message", errorMessage);
        errorMessage = Util.translateColorCodes(errorMessage);
    }

    private boolean isLowDiskSpace() {
        return getFile().getFreeSpace() < limitFreeBytes;
    }

    private void sendErrorMessage(UUID uuid) {
        Optional.ofNullable(uuid)
                .map(Bukkit::getPlayer)
                .ifPresent(player -> player.sendMessage(errorMessage));
        getLogger().warning("Cannot create islands as the system is in low memory. Please expand the disk space.");
    }

    @EventHandler
    public void onCreate(IslandCreateEvent event) {
        if (isLowDiskSpace()) {
            event.setCancelled(true);
            sendErrorMessage(event.getPlayerUUID());
        }
    }

    @EventHandler
    public void onReset(IslandResetEvent event) {
        if (isLowDiskSpace()) {
            event.setCancelled(true);
            sendErrorMessage(event.getPlayerUUID());
        }
    }
}
