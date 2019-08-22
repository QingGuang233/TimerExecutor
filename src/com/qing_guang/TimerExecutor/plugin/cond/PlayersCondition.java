package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersCondition implements Condition{

    private String[] players = null;

    public PlayersCondition(String[] players){
        this.players = players;
    }

    @Override
    public List<CommandSender> getMatches() {
        List<CommandSender> list = new ArrayList<>();
        for(String name : players){
            Player player = Bukkit.getPlayerExact(name);
            if(player != null){
                list.add(player);
            }
        }
        return list;
    }

}
