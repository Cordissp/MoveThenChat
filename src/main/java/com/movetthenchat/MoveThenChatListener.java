package com.movetthenchat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoveThenChatListener implements Listener {

    private final MoveThenChat plugin;
    private final Map<UUID, Long> movedAt = new HashMap<>();

    public MoveThenChatListener(MoveThenChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        movedAt.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        movedAt.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        UUID uuid = event.getPlayer().getUniqueId();
        if (!movedAt.containsKey(uuid)) {
            movedAt.put(uuid, System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("movetthenchat.bypass")) return;

        UUID uuid = player.getUniqueId();
        int required = plugin.getConfig().getInt("required-seconds", 30);

        if (!movedAt.containsKey(uuid)) {
            event.setCancelled(true);
            String msg = color(plugin.getConfig().getString("deny-message",
                    "&cSohbete yazabilmek için &e{time} &csaniye hareket etmelisin.")
                    .replace("{time}", String.valueOf(required)));
            player.sendMessage(msg);
            return;
        }

        long elapsed = (System.currentTimeMillis() - movedAt.get(uuid)) / 1000;
        if (elapsed < required) {
            event.setCancelled(true);
            long remaining = required - elapsed;
            String msg = color(plugin.getConfig().getString("wait-message",
                    "&cSohbete yazabilmek için &e{time} &csaniye daha beklemelisin.")
                    .replace("{time}", String.valueOf(remaining)));
            player.sendMessage(msg);
        }
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
