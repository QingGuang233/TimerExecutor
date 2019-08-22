package com.qing_guang.TimerExecutor.plugin.timer;

import com.qing_guang.TimerExecutor.plugin.cond.ConditionFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timer {

    private List<TimeVerifier> unable_times = new ArrayList<>();
    private List<TimeVerifier> enable_times = new ArrayList<>();
    private List<TimerCommand> cmds = new ArrayList<>();

    private boolean isRunning;

    public Timer(ConfigurationSection section){

        if(section.getBoolean("enable",true)) {

            for(String timeset : section.getStringList("enable_times")) {
                TimeVerifier verifier = toVerifier(timeset);
                if(verifier != null){
                    enable_times.add(verifier);
                }
            }

            for(String timeset : section.getStringList("unable_times")){
                TimeVerifier verifier = toVerifier(timeset);
                if(verifier != null){
                    unable_times.add(verifier);
                }
            }

            for(String cmd_line : section.getStringList("commands")){
                cmds.add(new TimerCommand(cmd_line));
            }

        }

    }

    public boolean verify(){
        boolean success = false;
        for(TimeVerifier verifier : unable_times){
            if(verifier.verify()){
                return false;
            }
        }
        for(TimeVerifier verifier : enable_times){
            if(success |= verifier.verify()){
                break;
            }
        }
        return success;
    }

    public void execute(){
        new Thread(() -> {
            isRunning = true;
            for(TimerCommand cmd : cmds){
                if(isRunning)
                    cmd.execute();
                else
                    break;
            }
            isRunning = false;
        }).start();
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void stop(){
        isRunning = false;
    }

    private static TimeVerifier toVerifier(String timeset){
        if (timeset.equalsIgnoreCase("sec")) {
            return new TimeVerifier(false, false, false, false, false, false, false) {
                public boolean verify() {
                    return true;
                }
            };
        } else if (ConditionFactory.matches(timeset, "\\d{1,2}")) {
            return new TimeVerifier(false, false, false, false, false, true, false).setSec(Integer.parseInt(timeset));
        } else {

            Matcher matcher;

            if ((matcher = matches(timeset, "(\\d{1,2}):(\\d{1,2})")) != null) {
                return new TimeVerifier(false, false, false, false, true, true, false).setSec(Integer.parseInt(matcher.group(2)))
                        .setMin(Integer.parseInt(matcher.group(1)));
            } else if ((matcher = matches(timeset, "(\\d{1,2}):(\\d{1,2}):(\\d{1,2})")) != null) {
                return new TimeVerifier(false, false, false, true, true, true, false).setSec(Integer.parseInt(matcher.group(3)))
                        .setMin(Integer.parseInt(matcher.group(2)))
                        .setHour(Integer.parseInt(matcher.group(1)));
            } else if ((matcher = matches(timeset, "(\\d{1,2})\\,(\\d{1,2}):(\\d{1,2}):(\\d{1,2})")) != null) {
                return new TimeVerifier(false, false, true, true, true, true, false).setSec(Integer.parseInt(matcher.group(4)))
                        .setMin(Integer.parseInt(matcher.group(3)))
                        .setHour(Integer.parseInt(matcher.group(2)))
                        .setDay(Integer.parseInt(matcher.group(1)));
            } else if ((matcher = matches(timeset, "(\\d{1,2})/(\\d{1,2})\\,(\\d{1,2}):(\\d{1,2}):(\\d{1,2})")) != null) {
                return new TimeVerifier(false, true, true, true, true, true, false).setSec(Integer.parseInt(matcher.group(5)))
                        .setMin(Integer.parseInt(matcher.group(4)))
                        .setHour(Integer.parseInt(matcher.group(3)))
                        .setDay(Integer.parseInt(matcher.group(2)))
                        .setMonth(Integer.parseInt(matcher.group(1)));
            }else if ((matcher = matches(timeset, "(\\d{1,4})/(\\d{1,2})/(\\d{1,2})\\,(\\d{1,2}):(\\d{1,2}):(\\d{1,2})")) != null){
                return new TimeVerifier(true, true, true, true, true, true, false).setSec(Integer.parseInt(matcher.group(6)))
                        .setMin(Integer.parseInt(matcher.group(5)))
                        .setHour(Integer.parseInt(matcher.group(4)))
                        .setDay(Integer.parseInt(matcher.group(3)))
                        .setMonth(Integer.parseInt(matcher.group(2)))
                        .setYear(Integer.parseInt(matcher.group(1)));
            } else if ((matcher = matches(timeset, "(.*)\\,(\\d{1,2}):(\\d{1,2}):(\\d{1,2})")) != null) {
                try{
                    return new TimeVerifier(false, false, false, true, true, true, true).setSec(Integer.parseInt(matcher.group(4)))
                            .setMin(Integer.parseInt(matcher.group(3)))
                            .setHour(Integer.parseInt(matcher.group(2)))
                            .setWeek(TimeVerifier.Week.valueOf(matcher.group(1).toUpperCase()));
                }catch (IllegalArgumentException e){
                    return new TimeVerifier(false,false,false,false,false,false,false);
                }
            }

        }
        return new TimeVerifier(false,false,false,false,false,false,false);
    }

    private static Matcher matches(String origin,String regax){
        if(ConditionFactory.matches(origin,regax)){
            Matcher matcher = Pattern.compile(regax).matcher(origin);
            matcher.find();
            return matcher;
        }else{
            return null;
        }
    }

}
