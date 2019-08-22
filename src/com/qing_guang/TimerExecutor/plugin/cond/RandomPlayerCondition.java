package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.command.CommandSender;

import java.util.*;

public class RandomPlayerCondition implements Condition{

    private ConditionToken token;
    private Random random = new Random();

    public RandomPlayerCondition(String cond_t){
        token = new ConditionToken(cond_t);
    }

    @Override
    public List<CommandSender> getMatches() {
        List<CommandSender> list = token.getMatches();
        return Collections.singletonList(list.get(random.nextInt(list.size())));
    }

}
