package com.qing_guang.TimerExecutor.plugin.timer;

import com.qing_guang.TimerExecutor.plugin.cond.ConditionFactory;
import com.qing_guang.TimerExecutor.plugin.cond.ConditionToken;
import com.qing_guang.TimerExecutor.plugin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TimerCommand {

    private static Flags flags;

    public static void init(Flags flags){ TimerCommand.flags = flags; }

    private List<String> cmds;
    private ConditionToken cond_t;

    public TimerCommand(String cmd_line){

        if(!cmd_line.contains(":")){
            cond_t = new ConditionToken();
            cmds.add(cmd_line);
        }else{

            String[] strs = cmd_line.split(":");
            String origin_c = strs[strs.length - 1];
            strs[strs.length - 1] = null;
            String type = toString(strs,":");

            cond_t = new ConditionToken(type);
            cmds = getCommands(origin_c);

        }

    }

    private static List<String> getCommands(String origin_c){
        boolean is_stack = false;
        String stack_name = null;
        if(origin_c.startsWith(flags.SENDMSG)){
            origin_c = origin_c.replace("&","¡ì");
        }else if(origin_c.startsWith(flags.START_OF_CMDS_IN_FILE)){
            stack_name = origin_c.substring(flags.START_OF_CMDS_IN_FILE.length());
            is_stack = true;
        }
        if(is_stack){
            Main m = JavaPlugin.getPlugin(Main.class);
            List<String> o_cmds = m.getCmdStackConfig().getStringList(stack_name);
            List<String> cmds = new ArrayList<>();
            if(o_cmds != null){
                for(String cmd : o_cmds){
                    cmds.addAll(getCommands(cmd));
                }
            }
            return cmds;
        }else{
            return Collections.singletonList(origin_c);
        }
    }

    public void execute(){
        BukkitScheduler scheduler = Bukkit.getScheduler();
        Main m = JavaPlugin.getPlugin(Main.class);
        for(CommandSender sender : cond_t.getMatches()){
            for(String cmd : cmds){
                if(ConditionFactory.matches(cmd,flags.DELAY + " \\d{1,20}")){
                    try {
                        Thread.sleep(Long.parseLong(cmd.replace(flags.DELAY + " ","")));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {

                    Calendar c = Calendar.getInstance();
                    TimeVerifier.Week week = TimeVerifier.Week.getField(c.get(Calendar.DAY_OF_WEEK));

                    String processed = cmd.replace(flags.VAR_PLAYER,sender.getName())
                            .replace(flags.VAR_YEAR,Integer.toString(c.get(Calendar.YEAR)))
                            .replace(flags.VAR_MONTH,Integer.toString(c.get(Calendar.MONTH)))
                            .replace(flags.VAR_DAY_FOR_MONTH,Integer.toString(c.get(Calendar.DAY_OF_MONTH)))
                            .replace(flags.VAR_DAY_FOR_WEEK_EN,week.name().toLowerCase())
                            .replace(flags.VAR_DAY_FOR_WEEK_CH,week.getZH_CN())
                            .replace(flags.VAR_HOUR_TWELVE,Integer.toString(c.get(Calendar.HOUR)))
                            .replace(flags.VAR_HOUR_TWENTY_FOUR,addZero(c.get(Calendar.HOUR_OF_DAY)))
                            .replace(flags.VAR_MONUTE,addZero(c.get(Calendar.MINUTE)))
                            .replace(flags.VAR_SECOND,addZero(c.get(Calendar.SECOND)));

                    if(ConditionFactory.matches(cmd,flags.SENDMSG + " (.*)")){
                        scheduler.callSyncMethod(m,() -> {
                            sender.sendMessage(processed.replace(flags.SENDMSG + " ",""));
                            return null;
                        });
                    }else{
                        scheduler.callSyncMethod(m,() -> {
                            System.out.println(processed);
                            dispatchWithOp(sender,processed);
                            return null;
                        });
                    }

                }
            }
        }
    }

    private static void dispatchWithOp(CommandSender sender,String cmd){
        boolean isop = sender.isOp();
        sender.setOp(true);
        Bukkit.dispatchCommand(sender,cmd);
        sender.setOp(isop);
    }

    private static String toString(String[] strs,String placeholder){
        String str = "";
        for(int i = 0;i < strs.length;i++){
            if(strs[i] != null){
                str += strs[i] + placeholder;
            }
        }
        return str.substring(0,str.length() - placeholder.length());
    }

    private static String addZero(int num){
        return num < 10 ? "0" + num : Integer.toString(num);
    }

    public static class Flags{

        public final String VAR_PLAYER;
        public final String VAR_YEAR;
        public final String VAR_MONTH;
        public final String VAR_DAY_FOR_MONTH;
        public final String VAR_DAY_FOR_WEEK_EN;
        public final String VAR_DAY_FOR_WEEK_CH;
        public final String VAR_HOUR_TWELVE;
        public final String VAR_HOUR_TWENTY_FOUR;
        public final String VAR_MONUTE;
        public final String VAR_SECOND;

        public final String DELAY;
        public final String SENDMSG;
        public final String START_OF_CMDS_IN_FILE;

        public Flags(String VAR_PLAYER, String VAR_YEAR, String VAR_MONTH, String VAR_DAY_FOR_MONTH, String VAR_DAY_FOR_WEEK_EN, String VAR_DAY_FOR_WEEK_CH, String VAR_HOUR_TWELVE, String VAR_HOUR_TWENTY_FOUR, String VAR_MONUTE, String VAR_SECOND, String DELAY, String SENDMSG, String START_OF_CMDS_IN_FILE) {
            this.VAR_PLAYER = VAR_PLAYER;
            this.VAR_YEAR = VAR_YEAR;
            this.VAR_MONTH = VAR_MONTH;
            this.VAR_DAY_FOR_MONTH = VAR_DAY_FOR_MONTH;
            this.VAR_DAY_FOR_WEEK_EN = VAR_DAY_FOR_WEEK_EN;
            this.VAR_DAY_FOR_WEEK_CH = VAR_DAY_FOR_WEEK_CH;
            this.VAR_HOUR_TWELVE = VAR_HOUR_TWELVE;
            this.VAR_HOUR_TWENTY_FOUR = VAR_HOUR_TWENTY_FOUR;
            this.VAR_MONUTE = VAR_MONUTE;
            this.VAR_SECOND = VAR_SECOND;
            this.DELAY = DELAY;
            this.SENDMSG = SENDMSG;
            this.START_OF_CMDS_IN_FILE = START_OF_CMDS_IN_FILE;
        }

    }

}
