package com.qing_guang.TimerExecutor.plugin.main;

import com.qing_guang.TimerExecutor.plugin.timer.Timer;

import java.util.Map;
import java.util.TimerTask;

public class TimerTaskImpl extends TimerTask {

    private Map<String,Timer> timers;

    public TimerTaskImpl(Map<String,Timer> timers){
        this.timers = timers;
    }

    public void run() {
        for(Timer timer : timers.values()){
            if(timer.verify())
                timer.execute();
        }
    }

    public Map<String,Timer> getTimers(){
        return timers;
    }

}
