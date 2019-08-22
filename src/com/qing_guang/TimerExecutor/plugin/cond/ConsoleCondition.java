package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ConsoleCondition implements Condition{
    @Override
    public List<CommandSender> getMatches() {
        return Collections.singletonList(Bukkit.getConsoleSender());
    }
}
