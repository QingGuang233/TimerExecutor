package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AllPlayersCondition implements Condition{
    @Override
    public List<CommandSender> getMatches() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }
}
