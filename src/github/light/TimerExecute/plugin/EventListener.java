package github.light.TimerExecute.plugin;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import github.light.TimerExecute.api.TimerExecuteEvent;

@SuppressWarnings("all")
public class EventListener implements Listener{

	static String DELAY;
	static String SENDMSG;
	
	static String ALL;
	static String FICMAN;
	
	static String RANDOM;
	static String LOCATION;
	static String RANGE;
	static String MONEY;
	
	static String GROUP;
	static String WORLD;
	static String PERMISSION;
	static String COND;
	
	static String CMD_STACK;
	
	static String VAR_PLAYER;
	static String YEAR;
	static String MONTH;
	static String DAY_FOR_MONTH;
	static String DAY_FOR_WEEK;
	static String HOUR;
	static String HOUR_FOR_DAY;
	static String MINUTE;
	static String SECOND;
	
	@EventHandler
	public void join(PlayerJoinEvent e) throws Exception{
		
		if(Main.ficman != null) {
			
			Object npc = Main.ficman.getClass().getMethod("getHandle").invoke(Main.ficman);
			
			Object handle = e.getPlayer().getClass().getMethod("getHandle").invoke(e.getPlayer());
			
			Object connect = handle.getClass().getField("playerConnection").get(handle);
			
			Object array = Array.newInstance(Main.entityp, 1);
			
			Array.set(array, 0, npc);
			
			Object ppopi = Main.packetpopi.getConstructor(Main.epia,Array.newInstance(Main.entityp, 0).getClass()).newInstance(Main.epia.getMethod("valueOf",String.class).invoke(null, "ADD_PLAYER"),array);
			
			Object ppones = Main.packetpones.getConstructor(Class.forName(Main.nms.getName() + ".EntityHuman")).newInstance(npc);
			
			Method send = connect.getClass().getMethod("sendPacket", Main.packet);
			
			send.invoke(connect, ppopi);
			
			send.invoke(connect, ppones);
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void cmdExec(TimerExecuteEvent e){
		
		new Thread() {
			
			public void run() {
				
				if(!e.isCancelled()) {
					
					for(String cmdline : e.getTimer().getCmds()) {
						
						String[] cmdandgroups = cmdline.split(":");
						
						if(cmdandgroups[cmdandgroups.length - 1].startsWith(DELAY)) {
							
							try {
								Thread.sleep(Long.parseLong(cmdandgroups[cmdandgroups.length - 1].substring(DELAY.length())));
							} catch (IllegalArgumentException | InterruptedException e1) {
								continue;
							}
							
						}else if(cmdandgroups.length == 1) {
							
							for(String cmd : getAllCmds(cmdandgroups[0])) {
								
								execUnPmss(Bukkit.getConsoleSender(),cmd,Calendar.getInstance());
								
							}
							
						}else {
							
							String groups = "";
							
							List<String> cmds = getAllCmds(cmdandgroups[cmdandgroups.length - 1]);
							
							for(int i = 0;i < cmdandgroups.length;i++) {
								
								if(i != cmdandgroups.length - 1) {

									if(i != cmdandgroups.length - 2) {
										
										groups = groups.concat(cmdandgroups[i] + ":");
										
									}else {
										
										groups = groups.concat(cmdandgroups[i]);
										
									}
									
								}
								
							}
							
							for(CommandSender player : condsToPlayers(groups)){
								
								for(String cmd : cmds) {
									
									execUnPmss(player,cmd,Calendar.getInstance());
									
								}
								
							}
							
						}
						
					}
					
					e.getTimer().getAPI().removeExecuting(e.getTimer());
					
				}
								
			}
			
		}.start();
		
	}
	
	private void execUnPmss(CommandSender sender,String cmd,Calendar now) {
		
		Bukkit.getScheduler().callSyncMethod(Main.m, new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				
				String var = cmd.replace(YEAR, now.get(Calendar.YEAR) + "")
								.replace(MONTH, now.get(Calendar.MONTH) + "")
								.replace(DAY_FOR_MONTH, now.get(Calendar.DAY_OF_MONTH) + "")
								.replace(DAY_FOR_WEEK, toWeek(now.get(Calendar.DAY_OF_WEEK)))
								.replace(HOUR, addZero(now.get(Calendar.HOUR)))
								.replace(HOUR_FOR_DAY, addZero(now.get(Calendar.HOUR_OF_DAY)))
								.replace(MINUTE, addZero(now.get(Calendar.MINUTE)))
								.replace(SECOND, addZero(now.get(Calendar.SECOND)));
				
				if(var.startsWith(SENDMSG)){
					
					sender.sendMessage(var.substring(SENDMSG.length()).replace("&", "¡ì"));
					
					return null;
					
				}
				
				String handled = var.replace(VAR_PLAYER, sender.getName());
				
				if(sender instanceof Player && !((Player)sender).isOnline()) {
					
					return null;
					
				}
				
				boolean op = sender.isOp();
				
				sender.setOp(true);
				
				Bukkit.dispatchCommand(sender, handled);
				
				sender.setOp(op);
				
				return null;
				
			}
			
		});
		
	}
	
	private String toWeek(int code) {
		
		if(code == Calendar.SUNDAY) {
			
			return "sunday";
			
		}else if(code == Calendar.MONDAY) {
			
			return "monday";
			
		}else if(code == Calendar.TUESDAY) {
			
			return "tuesday";
			
		}else if(code == Calendar.WEDNESDAY) {
			
			return "wednesday";
			
		}else if(code == Calendar.THURSDAY) {
			
			return "thursday";
			
		}else if(code == Calendar.FRIDAY) {
			
			return "friday";
			
		}else if(code == Calendar.SATURDAY) {
			
			return "saturday";
			
		}
		
		return null;
		
	}
	
	private String addZero(int value) {
		
		return (value + "").length() == 1 ? "0" + value : value + "";
		
	}
	
	private List<CommandSender> condsToPlayers(String conds){
		
		String[] orconds = split(conds,'|');
		
		List<CommandSender>[] orplayers = new ArrayList[orconds.length];
		
		for(int i = 0;i < orconds.length;i++) {
			
			orplayers[i] = orGroupsPlayers(strGroupsToPlayerGroups(split(orconds[i],':')));
			
		}
		
		return addGroupsPlayers(orplayers);
		
	}
	
	private String[] split(String str, char regex){
		List<String> list = new ArrayList();

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
		return (String[])list.toArray(new String[list.size()]);
	}
  
	private A turnTo(String str, int index){
		CharBuffer buffer = CharBuffer.allocate(0);
		for (int i = index + 1; i < str.length(); i++) {
			if (str.charAt(i) == '{'){
			A a = turnTo(str, i);

			buffer = extend(buffer, a.str.length() + 2);

			buffer.put("{" + a.str + "}");

			i = a.index;
			}else{
				if (str.charAt(i) == '}')
				{
				  A a = new A(null);

				  a.str = new String(buffer.array());

				  a.index = i;

				  return a;
				}
				buffer = extend(buffer, 1);

				buffer.put(str.charAt(i));
			}
		}
		A a = new A(null);

		a.str = new String(buffer.array());

		a.index = (str.length() - 1);

		return a;
	}
  
  	private CharBuffer extend(CharBuffer buffer, int length){
	  
		CharBuffer niw = CharBuffer.allocate(buffer.array().length + length);
    
	    	niw.append(new String(buffer.array()));
		
    		return niw;
  	}
	
	private List<CommandSender>[] strGroupsToPlayerGroups(String[] groups){
		
		List<CommandSender>[] pgroups = new List[groups.length];
		
		for(int i = 0;i < groups.length;i++){
			
			if(groups[i].equals(ALL)) {
				
				List<CommandSender> group = new ArrayList<>();
				
				for(Player player : Bukkit.getOnlinePlayers())
					
					group.add(player);
				
				pgroups[i] = group;
				
			}else if(groups[i].equals(FICMAN)){
				
				if(Main.ficman != null) {
					
					List<CommandSender> temp = new ArrayList<>();
					
					temp.add(Main.ficman);
					
					pgroups[i] = temp;
					
				}
				
			}else if(groups[i].startsWith(MONEY) && groups[i].endsWith("}")){
				
				String[] sets = groups[i].substring(MONEY.length(), groups[i].length() - 1).split(",");
				
				if(sets.length != 2) {
					
					continue;
					
				}
				
				int money = 0;
				
				boolean out = false;
				
				try {
					
					money = Integer.parseInt(sets[0]);
					
					out = Boolean.parseBoolean(sets[1]);
					
				}catch(NumberFormatException e) {
					
					continue;
					
				}
				
				List<CommandSender> players = new ArrayList<>();
				
				for(Player player : Bukkit.getOnlinePlayers()) {
					
					if(Main.economy.has(player, money)) {
						
						players.add(player);
						
						if(out) {
							
							Main.economy.withdrawPlayer(player, money);
							
						}
						
					}
					
				}
				
				pgroups[i] = players;
				
			}else if(groups[i].startsWith(RANDOM) && groups[i].endsWith("}")) {
				
				List<CommandSender> all = condsToPlayers(groups[i].substring(RANDOM.length(), groups[i].length() - 1));
				
				List<CommandSender> temp = new ArrayList<>();
				
				temp.add(all.get(new Random().nextInt(all.size() - 1)));
				
				pgroups[i] = temp;
				
			}else if(groups[i].startsWith(LOCATION) && groups[i].endsWith("}")) {
				
				String[] location = groups[i].substring(LOCATION.length(), groups[i].length() - 1).split(",");
				
				if(location.length != 5) {
					
					continue;
					
				}
					
				List<CommandSender> players = new ArrayList<>();
				
				for(Player player : Bukkit.getOnlinePlayers()) {
					
					try {
						
						if(player.getLocation().getWorld().getName().equals(location[4]) && onSquare(
								Integer.parseInt(location[0])
							   ,Integer.parseInt(location[1])
							   ,Integer.parseInt(location[2])
							   ,Integer.parseInt(location[3])
							   ,player))

							players.add(player);
						
					}catch(NumberFormatException e) {
						
						break;
						
					}
					
				}
				
				pgroups[i] = players;
				
			}else if(groups[i].startsWith(RANGE) && groups[i].endsWith("}")){
				
				String[] location = groups[i].substring(RANGE.length(), groups[i].length() - 1).split(",");
				
				List<CommandSender> players = new ArrayList<>();
				
				if(location.length != 5) {
					
					continue;
					
				}
				
				Location center = null;
				
				try {
					
					if(location.length == 5) {
						
						World world = Bukkit.getWorld(location[0]);
						
						world.canGenerateStructures();
						
						center = new Location(world,
											  Double.parseDouble(location[1]),
											  Double.parseDouble(location[2]),
											  Double.parseDouble(location[3]));
						
						Double.parseDouble(location[4]);
						
					}
					
				}catch(NumberFormatException | NullPointerException e) {
					 
					continue;
					
				}
				
				for(Player player : Bukkit.getOnlinePlayers()) {
					
					if(range(center,player.getLocation()) <= Double.parseDouble(location[4]) && center.getWorld().getName().equals(player.getLocation().getWorld().getName())) {
						
						players.add(player);
						
					}
					
				}
				
				pgroups[i] = players;
				
			}else if(groups[i].startsWith(GROUP)) {
					
				List<CommandSender> temp = new ArrayList<>();
				
				for(Player player : Bukkit.getServer().getOnlinePlayers())
						
					if(onGroup(player,groups[i].substring(GROUP.length())))
							
						temp.add(player);
				
				pgroups[i] = temp;
				
			}else if(groups[i].startsWith(WORLD)) {
				
				List<CommandSender> temp = new ArrayList<>();
				
				for(Player player : Bukkit.getServer().getOnlinePlayers())
						
					if(player.getLocation().getWorld().getName().equals(groups[i].substring(WORLD.length())))
						
						temp.add(player);
				
				pgroups[i] = temp;
				
			}else if(groups[i].startsWith(PERMISSION)){
				
				List<CommandSender> temp = new ArrayList<>();
				
				for(Player player : Bukkit.getOnlinePlayers()) {
					
					if(player.hasPermission(groups[i].substring(PERMISSION.length()))) {
						
						temp.add(player);
						
					}
					
				}
				
				pgroups[i] = temp;
				
			}else if(groups[i].startsWith(COND)) {
				
				if(Main.conds.contains(groups[i].substring(COND.length()))) {
					
					pgroups[i] = condsToPlayers(Main.conds.getString(groups[i].substring(COND.length())));
					
				}else {
					
					continue;
					
				}
				
			}else {
				
				pgroups[i] = toPlayers(groups[i].split(":"));
				
			}
			
		}
		
		List<List<CommandSender>> pg = new ArrayList<>();
		
		for(List<CommandSender> group : pgroups) {
			
			if(group != null) {
				
				pg.add(group);
				
			}
			
		}
		
		pgroups = pg.toArray(new List[pg.size()]);
		
		if(allIsNull(pgroups)) {
			
			pgroups = new ArrayList[1];
			
			List<CommandSender> group = new ArrayList<>();
			
			group.add(Bukkit.getConsoleSender());
			
			pgroups[1] = group;
			
		}
		
		return pgroups;
		
	}
	
	private <T> boolean allIsNull(T[] array) {
		
		if(array == null) {
			
			return true;
			
		}
		
		for(T value : array)
			
			if(value != null)
				
				return false;
		
		return true;
		
	}
	
	private List<CommandSender> orGroupsPlayers(List<CommandSender>[] groups){
		
		if(groups.length == 1)
			
			return groups[0];
			
		List<CommandSender> player = groups[0];
		
		List<CommandSender> temp = new ArrayList<>();
		
		for(int i = 1;i < groups.length;i++) {
			
			if(groups[i].size() == 0) {
			
				player.removeAll(player);
				
				break;
				
			}
			
			for(int i1 = 0;i1 < player.size();i1++) {
				
				if(groups[i].get(i1).getName().equals(player.get(i1).getName()))
					
					temp.add(player.get(i1));
				
			}
			
			player = temp;
			
			temp = new ArrayList<>();
			
		}
		
		return player;
		
	}
	
	private List<CommandSender> addGroupsPlayers(List<CommandSender>[] groups){
		
		if(groups.length == 1)
			
			return groups[0];
			
		List<CommandSender> players = groups[0];
		
		List<CommandSender> temp = new ArrayList<>();
		
		for(int i = 1;i < groups.length;i++) {
			
			if(groups[i].size() == 0) {
			
				continue;
				
			}
			
			for(int i1 = 0;i1 < groups[i].size();i1++) {
				
				if(!onCollection(groups[i].get(i1),players))
					
					temp.add(players.get(i1));
				
			}
			
			players.addAll(temp);
			
			temp = new ArrayList<>();
			
		}
		
		return players;
		
	}
	
	private List<CommandSender> toPlayers(String[] players){
		
		List<CommandSender> list = new ArrayList<>();
		
		for(String player : players) {
			
			Player p = Bukkit.getPlayerExact(player);
			
			if(p != null)
			
				list.add(p);
			
		}
		
		return list;
		
	}
	
	private boolean onGroup(Player player,String group) {
		
		for(String g : Main.permission.getPlayerGroups(player))
			
			if(group.equals(g))
				
				return true;
		
		return false;
		
	}
	
	private boolean onSquare(int x,int z,int x1,int z1,Player player) {
		
		return isBetween(x,x1,player.getLocation().getBlockX()) && isBetween(z,z1,player.getLocation().getBlockZ());
		
	}
	
	private boolean onCollection(CommandSender player,List<CommandSender> players) {
		
		for(CommandSender decide : players)
			
			if(player.getName().equals(decide.getName()))
				
				return true;
		
		return false;
		
	}
	
	private boolean isBetween(int num1,int num2,int num) {
		
		if((num1 <= num) && (num <= num2))
				
			return true;
		
		if((num1 >= num) && (num >= num2))
				
			return true;
		
		return false;
		
	}
	
	private double range(Location loc1,Location loc2) {
		
		double sqrt = Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2)
							  + Math.pow(loc1.getY() - loc2.getY(), 2)
							  + Math.pow(loc1.getZ() - loc2.getZ(), 2));
		
        return Math.abs(sqrt);
		
	}
	
	private List<String> getAllCmds(String cmd) {
		
		List<String> cmds = new ArrayList<String>();
		
		if(!cmd.startsWith(CMD_STACK)){
			
			cmds.add(cmd);
		
		}else {
			
			if(!Main.cmdStacks.contains(cmd.substring(CMD_STACK.length()))) {
				
				cmds.add(cmd);
				
				return cmds;
				
			}
			
			for(String cmdSs : Main.cmdStacks.getStringList(cmd.substring(CMD_STACK.length()))) {
				
				cmds.addAll(getAllCmds(cmdSs));
				
			}
			
		}
		
		return cmds;
		
	}
	
	private class A{
		String str;
		int index;

		private A() {}
	}
	
}
