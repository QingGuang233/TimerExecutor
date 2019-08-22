package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerInWorldCondition implements Condition{

    private String world;

    public PlayerInWorldCondition(String world){
        this.world = world;
    }

    @Override
    public List<CommandSender> getMatches() {
        List<CommandSender> list = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getWorld().getName().equals(world)){
                list.add(player);
            }
        }
        return list;
    }

}
