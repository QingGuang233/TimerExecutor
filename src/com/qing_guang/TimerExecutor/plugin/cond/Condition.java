package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface Condition {

    List<CommandSender> getMatches();

}
