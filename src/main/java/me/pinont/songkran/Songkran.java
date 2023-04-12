package me.pinont.songkran;

import me.pinont.songkran.events.WaterGun;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Month;
import java.time.MonthDay;
import java.util.Date;

public final class Songkran extends JavaPlugin {

    public static Songkran plugin;

    public Songkran() {
        plugin = this;
    }
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Songkran festival is starting!");

        // Check if it's Songkran festival
        Bukkit.getLogger().info("Checking Calendar...");
//        loadCommands();
//        loadEvents();
        isSongkranFestival();
    }


    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Songkran festival is over!, see you next year!");
    }

    public void loadCommands() {
        getLogger().info("Commands are loading!");

        // Load commands

        getLogger().info("Commands are loaded!");

    }

    public void loadEvents() {
        getLogger().info("Events are loading!");

        // Load events
        getServer().getPluginManager().registerEvents(new WaterGun(), this);

        getLogger().info("Events are loaded!");
    }

    public boolean isSongkranFestival() {
        // Check if it's Songkran festival
        MonthDay monthDay = MonthDay.now();
        if (MonthDay.of(Month.APRIL, 13).isBefore(monthDay) && MonthDay.of(Month.APRIL, 15).isAfter(monthDay)) {
            Bukkit.getLogger().info("It's Songkran festival!");
            Bukkit.getLogger().info("Loading Songkran Minigame...");
            loadCommands();
            loadEvents();
            Bukkit.getLogger().info("Songkran Minigame is loaded!");
        } else {
            Bukkit.getLogger().info("It's not Songkran festival!");
            Bukkit.getLogger().info("Disabling Songkran plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        return true;
    }
}
