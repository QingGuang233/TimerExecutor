package github.light.TimerExecute.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class TimerExecuteAPI {
	
	private Map<String,Timer> timers = new HashMap<>();
	private Set<Timer> executings = new HashSet<>();
	
	private TimerExecuteAPI(FileConfiguration config) {
		
		for(String t : config.getKeys(false)) {
			
			ConfigurationSection cs = config.getConfigurationSection(t);
			
			boolean enable = cs.getBoolean("enable",true);
			
			Timer.DayForYear unEna = new Timer.DayForYear(cs.getInt("year",-1), cs.getInt("month",-1), cs.getInt("day",-1));
			
			List<Timer.AEnaTime> enaTimes = new ArrayList<>();
			for(String time : cs.getStringList("timer_timeset")) {
				int hour = 0;
				int minute = 0;
				int second = 0;
				String[] times = time.split(",");
				if(times.length == 1) {
					try {
						hour = Integer.parseInt(time.split(":")[0]);
						minute = Integer.parseInt(time.split(":")[1]);
						second = Integer.parseInt(time.split(":")[2]);
					}catch(NumberFormatException e) {
						continue;
					}
					enaTimes.add(new Timer.AEnaTime(new Timer.DayTime(hour, minute, second)));
				}else {
					try {
						hour = Integer.parseInt(times[1].split(":")[0]);
						minute = Integer.parseInt(times[1].split(":")[1]);
						second = Integer.parseInt(times[1].split(":")[2]);
					}catch(NumberFormatException e) {
						continue;
					}
					try {
						int day = Integer.parseInt(times[0]);
						enaTimes.add(new Timer.AEnaTime(new Timer.DayTime(hour,minute,second),day));
					}catch(NumberFormatException e) {
						if(times[0].contains("/")) {
							try {
								int month = Integer.parseInt(times[0].split("/")[0]);
								int day = Integer.parseInt(times[0].split("/")[1]);
								enaTimes.add(new Timer.AEnaTime(new Timer.DayTime(hour, minute, second), month, day));
							}catch(NumberFormatException e1) {
								continue;
							}
						}else {
							String whatday = time.split(",")[0];
							enaTimes.add(new Timer.AEnaTime(new Timer.DayTime(hour,minute,second),whatday));
						}
					}
				}
			}
			
			List<String> cmds = cs.getStringList("command_set.commands");
			
			timers.put(t,new Timer(t,enable,unEna,enaTimes,cmds,this));
			
		}
		
	}
	
	public static TimerExecuteAPI createAPI(FileConfiguration config) {
		
		return new TimerExecuteAPI(config);
		
	}
	
	public Timer getTimer(String name) {
		
		return timers.get(name);
		
	}
	
	public Collection<Timer> getTimers(){
		
		return timers.values();
		
	}
	
	public boolean isExecuting(Timer timer) {
		
		return executings.contains(timer);
		
	}
	
	public void addExecuting(Timer timer) {
		
		executings.add(timer);
		
	}
	
	public void removeExecuting(Timer timer) {
		
		executings.remove(timer);
		
	}
	
}
