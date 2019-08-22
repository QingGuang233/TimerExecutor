package com.qing_guang.TimerExecutor.plugin.cond;

import com.qing_guang.TimerExecutor.plugin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PermissionGroupCondition implements Condition{

    private String group;

    public PermissionGroupCondition(String group){
        this.group = group;
    }

    @Override
    public List<CommandSender> getMatches() {
        List<CommandSender> list = new ArrayList<>();
        if(Main.permission != null){
            for(Player player : Bukkit.getOnlinePlayers()){
                for(String g : Main.permission.getPlayerGroups(player)){
                    if(g.equals(group)){
                        list.add(player);
                        break;
                    }
                }
            }
        }
        return list;
    }
}
