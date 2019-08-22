package com.qing_guang.TimerExecutor.plugin.cond;

import com.qing_guang.TimerExecutor.plugin.main.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CondInFileCondition implements Condition{

    private ConditionToken token;

    public CondInFileCondition(String cond_name){
        token = new ConditionToken(JavaPlugin.getPlugin(Main.class).getCondsConfig().getString(cond_name,""));
    }

    @Override
    public List<CommandSender> getMatches() {
        return token.getMatches();
    }

}
