package com.qing_guang.TimerExecutor.plugin.cond;

import com.qing_guang.TimerExecutor.plugin.main.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakemanCondition implements Condition{
    @Override
    public List<CommandSender> getMatches() { return Collections.singletonList(JavaPlugin.getPlugin(Main.class).getFakeman()); }
}
