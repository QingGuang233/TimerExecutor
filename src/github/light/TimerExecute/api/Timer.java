package github.light.TimerExecute.api;

import java.util.List;

import org.bukkit.Bukkit;

public class Timer {

	private String name;
	private boolean enable;
	private DayForYear unEna;
	private List<AEnaTime> enaTimes;
	private List<String> cmds;
	
	private TimerExecuteAPI group;
	
	Timer(String name,boolean enable, DayForYear unEna, List<AEnaTime> enaTimes, List<String> cmds,TimerExecuteAPI group) {
		this.name = name;
		this.enable = enable;
		this.unEna = unEna;
		this.enaTimes = enaTimes;
		this.cmds = cmds;
		this.group = group;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isEnable() {
		return enable;
	}

	public DayForYear getUnEna() {
		return unEna;
	}

	public List<AEnaTime> getEnaTimes() {
		return enaTimes;
	}

	public List<String> getCmds() {
		return cmds;
	}
	
	public TimerExecuteAPI getAPI() {
		return group;
	}
	
	public void execTimer() {
		Bukkit.getServer().getPluginManager().callEvent(new TimerExecuteEvent(this));
		group.addExecuting(this);
	}

	@Override
	public String toString() {
		return "Timer [name=" + name + ", enable=" + enable + ", unEna=" + unEna + ", enaTimes=" + enaTimes + ", cmds="
				+ cmds + ", group=" + group + "]";
	}

	public enum TimeType{
		DAY,
		WEEK,
		MONTH,
		DAY_FOR_YEAR
	}
	
	public static class AEnaTime{
		private TimeType type;
		private DayTime time;

		private String day_for_week;
		private int day_for_month;
		private DayForYear day_for_year;
		
		AEnaTime(DayTime time) {
			this.type = TimeType.DAY;
			this.time = time;
		}
		AEnaTime(DayTime time, String whatday){
			this.type = TimeType.WEEK;
			this.time = time;
			this.day_for_week = whatday;
		}
		AEnaTime(DayTime time, int dayForMonth){
			this.type = TimeType.MONTH;
			this.time = time;
			this.day_for_month = dayForMonth;
		}
		AEnaTime(DayTime time, int month,int day_for_month){
			this.type = TimeType.DAY_FOR_YEAR;
			this.time = time;
			this.day_for_year = new DayForYear(-1,month,day_for_month);
		}
		
		public TimeType getType() {
			return type;
		}
		public DayTime getTime() {
			return time;
		}

		public int getDayForMonth() {
			return day_for_month;
		}
		public String getDayForWeek() {
			return day_for_week;
		}
		public DayForYear getDayForYear() {
			return day_for_year;
		}
		
		@Override
		public String toString() {
			return "AEnaTime [type=" + type + ", time=" + time + ", day_for_week=" + day_for_week + ", day_for_month="
					+ day_for_month + ", day_for_year=" + day_for_year + "]";
		}
	}
	
	public static class DayForYear{
		private int year;
		private int month;
		private int day;
		
		DayForYear(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}
		
		public int getYear() {
			return year;
		}
		public int getMonth() {
			return month;
		}
		public int getDay() {
			return day;
		}

		@Override
		public String toString() {
			return "DayForYear [year=" + year + ", month=" + month + ", day=" + day + "]";
		}
	}
	
	public static class DayTime{
		private int hour;
		private int minute;
		private int second;
		DayTime(int hour, int minute, int second) {
			this.hour = hour;
			this.minute = minute;
			this.second = second;
		}
		public int getHour() {
			return hour;
		}
		public int getMinute() {
			return minute;
		}
		public int getSecond() {
			return second;
		}
		
		@Override
		public String toString() {
			return "DayTime [hour=" + hour + ", minute=" + minute + ", second=" + second + "]";
		}
	}
	
}
