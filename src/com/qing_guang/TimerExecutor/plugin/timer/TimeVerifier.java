package com.qing_guang.TimerExecutor.plugin.timer;

import java.util.Calendar;

public class TimeVerifier {

    private int year;
    private int month;
    private int day;

    private int hour;
    private int min;
    private int sec;

    private Week week;

    private boolean aff_year;
    private boolean aff_month;
    private boolean aff_day;

    private boolean aff_hour;
    private boolean aff_min;
    private boolean aff_sec;

    private boolean aff_week;

    public TimeVerifier(boolean aff_year, boolean aff_month, boolean aff_day, boolean aff_hour, boolean aff_min, boolean aff_sec, boolean aff_week) {
        this.aff_year = aff_year;
        this.aff_month = aff_month;
        this.aff_day = aff_day;
        this.aff_hour = aff_hour;
        this.aff_min = aff_min;
        this.aff_sec = aff_sec;
        this.aff_week = aff_week;
    }

    public TimeVerifier setYear(int year) {
        this.year = year;
        return this;
    }

    public TimeVerifier setMonth(int month) {
        this.month = month;
        return this;
    }

    public TimeVerifier setDay(int day) {
        this.day = day;
        return this;
    }

    public TimeVerifier setHour(int hour) {
        this.hour = hour;
        return this;
    }

    public TimeVerifier setMin(int min) {
        this.min = min;
        return this;
    }

    public TimeVerifier setSec(int sec) {
        this.sec = sec;
        return this;
    }

    public TimeVerifier setWeek(Week week) {
        this.week = week == null ? Week.SUNDAY : week;
        return this;
    }

    public boolean verify(){
        Calendar c = Calendar.getInstance();
        if(!aff_year && !aff_month && !aff_day && ! aff_hour && !aff_min && !aff_sec && !aff_week){
            return false;
        }
        return (!aff_year || c.get(Calendar.YEAR) == year)
            && (!aff_month || c.get(Calendar.MONTH) == month)
            && (!aff_day || c.get(Calendar.DAY_OF_MONTH) == day)
            && (!aff_hour || c.get(Calendar.HOUR_OF_DAY) == hour)
            && (!aff_min || c.get(Calendar.MINUTE) == min)
            && (!aff_sec || c.get(Calendar.SECOND) == sec)
            && (!aff_week || week.is(c.get(Calendar.DAY_OF_WEEK)));
    }

    public enum Week{

        SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY;

        public static Week getField(int week){
            if(week == 1){
                return SUNDAY;
            }else if(week == 2){
                return MONDAY;
            }else if(week == 3){
                return TUESDAY;
            }else if(week == 4){
                return WEDNESDAY;
            }else if(week == 5){
                return THURSDAY;
            }else if(week == 6){
                return FRIDAY;
            }else if(week == 7){
                return SATURDAY;
            }else{
                return null;
            }
        }

        public boolean is(int week){
            if(week < 1 || week > 7){
                return false;
            }
            return this == SUNDAY && week == 1
                || this == MONDAY && week == 2
                || this == TUESDAY && week == 3
                || this == WEDNESDAY && week == 4
                || this == THURSDAY && week == 5
                || this == FRIDAY && week == 6
                || this == SATURDAY && week == 7;
        }

        public String getZH_CN(){
            if(this == SUNDAY){
                return "星期天";
            }else if(this == MONDAY){
                return "星期一";
            }else if(this == TUESDAY){
                return "星期二";
            }else if(this == WEDNESDAY){
                return "星期三";
            }else if(this == THURSDAY){
                return "星期四";
            }else if(this == FRIDAY){
                return "星期五";
            }else {
                return "星期六";
            }
        }
    }

}
