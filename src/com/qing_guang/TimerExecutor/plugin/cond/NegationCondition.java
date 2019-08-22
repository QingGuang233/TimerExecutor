package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class NegationCondition implements Condition{

    private ConditionToken token;

    public NegationCondition(String cond_t){ token = new ConditionToken(); }

    @Override
    @SuppressWarnings("unchecked")
    public List<CommandSender> getMatches() {
        return ConditionToken.orGroupsPlayers(new List[]{new ArrayList<>(Bukkit.getOnlinePlayers()),token.getMatches()});
    }

}
