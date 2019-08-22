package com.qing_guang.TimerExecutor.plugin.cond;

import com.qing_guang.TimerExecutor.plugin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MoneyHasCondition implements Condition{

    private double money;
    private boolean out;

    public MoneyHasCondition(double money,boolean out){
        this.money = money;
        this.out = out;
    }

    @Override
    public List<CommandSender> getMatches() {
        List<CommandSender> list = new ArrayList<>();
        if(Main.economy != null){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(Main.economy.has(player,money)){
                    list.add(player);
                    if(out){
                        Main.economy.withdrawPlayer(player,money);
                    }
                }
            }
        }
        return list;
    }

}
