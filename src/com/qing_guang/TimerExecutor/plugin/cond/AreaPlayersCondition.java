package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AreaPlayersCondition implements Condition{

    private String world;
    private Point point1;
    private Point point2;

    public AreaPlayersCondition(String world,double p1_x,double p1_z,double p2_x,double p2_z){
        this.world = world;
        point1 = new Point(p1_x,p1_z);
        point2 = new Point(p2_x,p2_z);
    }

    @Override
    public List<CommandSender> getMatches() {
        List<CommandSender> list = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getWorld().getName().equals(world)
            && middle(point1.x,point2.x,player.getLocation().getX())
            && middle(point1.z,point2.z,player.getLocation().getZ())){
                list.add(player);
            }
        }
        return null;
    }

    private static boolean middle(double bound1,double bound2,double num) {
        return bound1 <= num && num <= bound2 || num >= bound1 && bound2 >= num;
    }

    class Point{
        private double x;
        private double z;
        public Point(double x, double z) {
            this.x = x;
            this.z = z;
        }
    }

}
