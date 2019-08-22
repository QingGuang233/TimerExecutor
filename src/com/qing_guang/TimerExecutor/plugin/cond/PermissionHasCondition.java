package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PermissionHasCondition implements Condition{

    private String pmss;

    public PermissionHasCondition(String pmss){
        this.pmss = pmss;
    }

    @Override
    public List<CommandSender> getMatches() {
        List<CommandSender> list = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.hasPermission(pmss)){
                list.add(player);
            }
        }
        return list;
    }

}
