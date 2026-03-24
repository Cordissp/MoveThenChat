package com.movetthenchat;

import org.bukkit.plugin.java.JavaPlugin;

public class MoveThenChat extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new MoveThenChatListener(this), this);
    }

    @Override
    public void onDisable() {}
}
