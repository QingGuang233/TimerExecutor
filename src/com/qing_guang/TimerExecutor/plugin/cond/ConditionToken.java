package com.qing_guang.TimerExecutor.plugin.cond;

import org.bukkit.command.CommandSender;

import java.nio.CharBuffer;
import java.util.*;

public class ConditionToken {

    private String disposed;
    private Map<String,Condition> conds = new HashMap<>();

    public ConditionToken(String origin){

        int count = 0;
        disposed = new String(origin.getBytes());

        for(String addc : split(origin,'|')){
            for(String orc : split(addc,':')){
                String cs = Integer.toString(count);
                Condition cond = ConditionFactory.getCondition(orc);
                conds.put(cs,cond);
                disposed = disposed.replaceFirst(replaceAll(orc),cs);
                count++;
            }
        }

    }

    public ConditionToken(){
        disposed = "0";
        conds.put("0",new ConsoleCondition());
    }

    @SuppressWarnings("unchecked")
    public List<CommandSender> getMatches(){

        String[] addgs = disposed.split("\\|");
        List<CommandSender>[] addgs_c = new List[addgs.length];

        for(int i = 0;i < addgs.length;i++){
            String[] orgs = addgs[i].split(":");
            List<CommandSender>[] orgs_c = new List[orgs.length];
            for(int j = 0;j < orgs.length;j++){
                orgs_c[j] = conds.get(orgs[j]).getMatches();
            }
            addgs_c[i] = orGroupsPlayers(orgs_c);
        }

        return addGroupsPlayers(addgs_c);

    }

    static List<CommandSender> orGroupsPlayers(List<CommandSender>[] groups){

        if(groups.length == 1)
            return groups[0];

        List<CommandSender> player = groups[0];
        List<CommandSender> temp = new ArrayList<>();

        for(int i = 1;i < groups.length;i++) {

            if(groups[i].size() == 0) {
                player.clear();
                break;
            }

            for(int i1 = 0;i1 < player.size();i1++)
                if(groups[i].get(i1).getName().equals(player.get(i1).getName()))
                    temp.add(player.get(i1));

            player = temp;
            temp = new ArrayList<>();

        }
        return player;
    }

    static List<CommandSender> addGroupsPlayers(List<CommandSender>[] lists){

        List<CommandSender> l = new IArrayList<>();

        for(List<CommandSender> list : lists){
            for(CommandSender sender : list){
                if(!l.contains(sender)){
                    l.add(sender);
                }
            }
        }

        return l;

    }

    private String replaceAll(String str){
        return str.replace("$","\\$")
                .replace("(","\\(")
                .replace(")","\\)")
                .replace("*","\\*")
                .replace("+","\\+")
                .replace(".","\\.")
                .replace("[","\\[")
                .replace("]","\\]")
                .replace("?","\\?")
                .replace("\\","\\\\")
                .replace("^","\\^")
                .replace("{","\\{")
                .replace("}","\\}")
                .replace("|","\\|");
    }

    private static String[] split(String str, char regex){

        List<String> list = new ArrayList<>();
        CharBuffer buffer = CharBuffer.allocate(0);

        for (int i = 0; i < str.length(); i++) {

            if (str.charAt(i) == regex){

                list.add(new String(buffer.array()));
                buffer = CharBuffer.allocate(0);

            }else if (str.charAt(i) == '{'){

                A a = turnTo(str, i);
                buffer = extend(buffer, a.str.length() + 2);
                buffer.put("{" + a.str + "}");
                i = a.index;

            }else{

                buffer = extend(buffer, 1);
                buffer.put(str.charAt(i));

            }

        }

        list.add(new String(buffer.array()));
        return list.toArray(new String[0]);

    }

    private static A turnTo(String str, int index){

        CharBuffer buffer = CharBuffer.allocate(0);

        for (int i = index + 1; i < str.length(); i++) {

            if (str.charAt(i) == '{'){

                A a = turnTo(str, i);
                buffer = extend(buffer, a.str.length() + 2);
                buffer.put("{" + a.str + "}");
                i = a.index;

            }else{

                if (str.charAt(i) == '}'){
                    A a = new A();
                    a.str = new String(buffer.array());
                    a.index = i;
                    return a;
                }

                buffer = extend(buffer, 1);
                buffer.put(str.charAt(i));

            }

        }

        A a = new A();
        a.str = new String(buffer.array());
        a.index = (str.length() - 1);

        return a;

    }

    private static CharBuffer extend(CharBuffer buffer, int length){

        CharBuffer niw = CharBuffer.allocate(buffer.array().length + length);
        niw.append(new String(buffer.array()));
        return niw;

    }

    private static class A{

        String str;
        int index;
        private A() {}

    }

    private static class IArrayList<E> extends ArrayList<E>{
        @Override
        public boolean contains(Object o) {
            if(o instanceof CommandSender) {
                for (Object sender : this) {
                    if (((CommandSender) sender).getName().equals(((CommandSender) o).getName())) {
                        return true;
                    }
                }
                return false;
            }else{
                return super.contains(o);
            }
        }
    }

}
