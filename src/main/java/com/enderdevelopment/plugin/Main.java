package com.enderdevelopment.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Main extends JavaPlugin implements Listener {

    private final int MAX_VISIBLE_PLAYERS = 10;
    private final String TEAM_NAME = "playerVisibility";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        updatePlayerVisibility();
    }

    @Override
    public void onDisable() {
        // Clean up the team when the plugin is disabled
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(TEAM_NAME);
        if (team != null) {
            team.unregister();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayerVisibility();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        updatePlayerVisibility();
    }

    private void updatePlayerVisibility() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.registerNewTeam(TEAM_NAME);
        }

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Player> ops = new ArrayList<>();
        List<Player> nonOps = new ArrayList<>();

        for (Player player : players) {
            if (player.isOp()) {
                ops.add(player);
            } else {
                nonOps.add(player);
            }
        }

        Collections.shuffle(nonOps);

        List<Player> visiblePlayers = new ArrayList<>(ops);
        int remainingSlots = MAX_VISIBLE_PLAYERS - ops.size();
        if (remainingSlots > 0) {
            visiblePlayers.addAll(nonOps.subList(0, Math.min(remainingSlots, nonOps.size())));
        }

        team.setCanSeeFriendlyInvisibles(true);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);

        for (Player player : players) {
            if (visiblePlayers.contains(player)) {
                team.addEntry(player.getName());
            } else {
                team.removeEntry(player.getName());
            }
        }
    }
}