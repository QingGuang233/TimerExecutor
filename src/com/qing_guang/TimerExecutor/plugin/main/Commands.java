package com.qing_guang.TimerExecutor.plugin.main;

import com.qing_guang.TimerExecutor.plugin.timer.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {

        if(!sender.hasPermission("timer_execute.use")){
            sender.sendMessage(ChatColor.RED + "你没有权限使用本插件的指令!");
            return true;
        }

        if(args.length == 1){

            if(args[0].equalsIgnoreCase("help")){

                sender.sendMessage(ChatColor.GREEN + "插件指令帮助信息");
                sender.sendMessage(ChatColor.GREEN + "--------------------------------");
                sender.sendMessage(ChatColor.GREEN + "/timer reload 重载插件");
                sender.sendMessage(ChatColor.GREEN + "/timer test <定时器名> 开始执行一个定时器中的指令(enable为true)");
                sender.sendMessage(ChatColor.GREEN + "/timer isr <定时器名> 检查一个定时器是否在执行指令");
                sender.sendMessage(ChatColor.GREEN + "/timer stop <定时器名> 让一个定时器停止执行指令(如果定时器正在执行指令)");
                sender.sendMessage(ChatColor.GREEN + "--------------------------------");

            }else if(args[0].equalsIgnoreCase("reload")) {

                if (sender.hasPermission("timer_execute.reload")) {

                    Main m = JavaPlugin.getPlugin(Main.class);
                    Bukkit.getPluginManager().disablePlugin(m);
                    Bukkit.getPluginManager().enablePlugin(m);
                    sender.sendMessage(ChatColor.GREEN + "本插件已重载完毕");

                } else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行这个指令!");
                }

            }else{
                Bukkit.dispatchCommand(sender,"timer help");
            }

        }else if(args.length == 2){

            if(args[0].equalsIgnoreCase("test")){

                if(sender.hasPermission("timer_execute.test")){

                    if(Main.TIMERS.containsKey(args[1])){

                        sender.sendMessage(ChatColor.GREEN + "定时器 " + args[1] + " 已开始执行");
                        Main.TIMERS.get(args[1]).execute();

                    }else{
                        sender.sendMessage(ChatColor.RED + "此定时器不存在!如果你在插件运行时更改的配置文件请输入/timer reload来重载本插件,否则请仔细检查后再试!");
                    }

                }else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行这个指令!");
                }

            }else if(args[0].equalsIgnoreCase("isr")){

                if(sender.hasPermission("timer_execute.stop")){

                    if(Main.TIMERS.containsKey(args[1])){

                        Timer timer = Main.TIMERS.get(args[1]);
                        if(timer.isRunning()){
                            sender.sendMessage(ChatColor.GREEN + "此定时器正在执行指令!");
                        }else{
                            sender.sendMessage(ChatColor.GREEN + "此定时器并未正在执行指令!");
                        }

                    }else{
                        sender.sendMessage(ChatColor.RED + "此定时器不存在!如果你在插件运行时更改的配置文件请输入/timer reload来重载本插件,否则请仔细检查后再试!");
                    }

                }else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行这个指令!");
                }

            }else if(args[1].equalsIgnoreCase("stop")){

                if(sender.hasPermission("timer_execute.stop")){

                    if(Main.TIMERS.containsKey(args[1])){

                        Timer timer = Main.TIMERS.get(args[1]);
                        if(timer.isRunning()){
                            timer.stop();
                            sender.sendMessage(ChatColor.GREEN + "此定时器已停止执行指令!");
                        }else{
                            sender.sendMessage(ChatColor.RED + "此定时器并未正在执行指令!");
                        }

                    }else{
                        sender.sendMessage(ChatColor.RED + "此定时器不存在!如果你在插件运行时更改的配置文件请输入/timer reload来重载本插件,否则请仔细检查后再试!");
                    }

                }else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行这个指令!");
                }

            }else{
                Bukkit.dispatchCommand(sender,"timer help");
            }

        }else{
            Bukkit.dispatchCommand(sender,"timer help");
        }

        return true;

    }

}
