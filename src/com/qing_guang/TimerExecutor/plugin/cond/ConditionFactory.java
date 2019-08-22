package com.qing_guang.TimerExecutor.plugin.cond;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionFactory {

    private ConditionFactory(){}

    private static Flags flags = null;

    public static void init(Flags flags){
        ConditionFactory.flags = flags;
    }

    public static Condition getCondition(String field){
        if(field.equals(flags.ALL_PLAYER)){
            return new AllPlayersCondition();
        }else if(field.equals(flags.FAKEMAN)){
            return new FakemanCondition();
        }else{
            String[] args = null;
            if(matches(field,flags.AREA + "\\{(.*)\\,((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\,((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\,((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\,((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\}")){
                args = args(field,flags.AREA);
                String world = args[0];
                double p1_x = Double.parseDouble(args[1]);
                double p1_z = Double.parseDouble(args[2]);
                double p2_x = Double.parseDouble(args[3]);
                double p2_z = Double.parseDouble(args[4]);
                return new AreaPlayersCondition(world,p1_x,p1_z,p2_x,p2_z);
            }else if(matches(field,flags.RANGE + "\\{(.*)\\,((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\,((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\,((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\,((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\}")){
                args = args(field,flags.RANGE);
                String world = args[0];
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                double radius = Double.parseDouble(args[4]);
                return new RangePlayersCondition(world,x,y,z,radius);
            }else if(matches(field,flags.MONEY + "\\{((\\d{1,20})|(\\d{1,20}\\.\\d{1,20}))\\,(.*)\\}")){
                args = args(field,flags.MONEY);
                double money = Double.parseDouble(args[0]);
                boolean out = Boolean.parseBoolean(args[1]);
                return new MoneyHasCondition(money,out);
            }else if(matches(field,flags.NEGATION + "\\{(.*)\\}")){
                args = args(field,flags.NEGATION);
                return new NegationCondition(args[0]);
            }else if(matches(field,flags.RANDOM + "\\{(.*)\\}")){
                args = args(field,flags.RANDOM);
                return new RandomPlayerCondition(args[0]);
            }else if(field.startsWith(flags.GROUP)){
                return new PermissionGroupCondition(field.substring(flags.GROUP.length()));
            }else if(field.startsWith(flags.PERMISSION)){
                return new PermissionHasCondition(field.substring(flags.PERMISSION.length()));
            }else if(field.startsWith(flags.WORLD)){
                return new PlayerInWorldCondition(field.substring(flags.WORLD.length()));
            }else if(field.startsWith(flags.CONDS_IN_FILE)){
                return new CondInFileCondition(field.substring(flags.CONDS_IN_FILE.length()));
            }else{
                return new PlayersCondition(field.split(","));
            }
        }
    }

    public static boolean matches(String origin, String regax){
        Pattern pattern = Pattern.compile(regax);
        Matcher matcher = pattern.matcher(origin);
        return matcher.matches();
    }

    private static String[] args(String origin, String name){
        return origin.substring(name.length() + 1,origin.length() - 1).split(",");
    }

    public static class Flags{

        public final String ALL_PLAYER;
        public final String FAKEMAN;

        public final String RANDOM;
        public final String NEGATION;
        public final String AREA;
        public final String RANGE;
        public final String MONEY;

        public final String GROUP;
        public final String WORLD;
        public final String PERMISSION;
        public final String CONDS_IN_FILE;

        public Flags(String ALL_PLAYER, String FAKEMAN, String NEGATION, String RANDOM, String AREA, String RANGE, String MONEY, String GROUP, String WORLD, String PERMISSION, String CONDS_IN_FILE) {
            this.ALL_PLAYER = ALL_PLAYER;
            this.FAKEMAN = FAKEMAN;
            this.NEGATION = NEGATION;
            this.RANDOM = RANDOM;
            this.AREA = AREA;
            this.RANGE = RANGE;
            this.MONEY = MONEY;
            this.GROUP = GROUP;
            this.WORLD = WORLD;
            this.PERMISSION = PERMISSION;
            this.CONDS_IN_FILE = CONDS_IN_FILE;
        }
    }

}
