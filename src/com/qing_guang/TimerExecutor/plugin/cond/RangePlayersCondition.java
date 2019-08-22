package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RangePlayersCondition implements Condition{

    private Location cpoint;
    private double range;

    public RangePlayersCondition(String world,double cx,double cy,double cz,double range){
        System.out.println(world);
        this.cpoint = new Location(Bukkit.getWorld(world),cx,cy,cz);
        this.range = range;
    }

    @Override
    public List<CommandSender> getMatches() {
        List<CommandSender> list = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getWorld().getName().equals(cpoint.getWorld().getName())
            && distance(player.getLocation(),cpoint) <= range){
                list.add(player);
            }
        }
        return list;
    }

    private static double distance(Location loc1,Location loc2){
        return Math.sqrt(
                Math.pow(Math.abs(loc1.getX() - loc2.getX()),2) +
                Math.pow(Math.abs(loc1.getY() - loc2.getY()),2) +
                Math.pow(Math.abs(loc1.getZ() - loc2.getZ()),2)
        );
    }

}
