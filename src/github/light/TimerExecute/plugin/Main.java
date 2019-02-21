package github.light.TimerExecute.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

import github.light.TimerExecute.api.Timer;
import github.light.TimerExecute.api.TimerExecuteAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin{
	
	/* config 指令块 条件 计时器 */
	static FileConfiguration config;
	static FileConfiguration cmdStacks;
	static FileConfiguration conds;
	static FileConfiguration timers;
	
	/* getDataFolder */
	static File dataFolder;
	
	/* vault权限和经济 */
	static Permission permission;
	static Economy economy;
	
	/* this */
	static Main m;
	
	/* API */
	static TimerExecuteAPI api;
	
	/* 一些根据版本变包名的反射,例如nms和org.bukkit.craftbukkit */
	static Package nms;
	static Package obce;
	static Class<?> packet;
	static Class<?> server;
	static Class<?> world;
	static Class<?> entityp;
	static Class<?> entityh;
	static Class<?> playerim;
	static Class<?> packetpopi;
	static Class<?> packetpones;
	static Class<?> epia;
	
	/* 假人 */
	static Player ficman;
	
	/* 用于判断时间到的计时器 */
	private TestingTimer timer;
	
	/**
	 * 启动方法
	 */
	public void onEnable() {
		
		this.getLogger().info("正在加载前置插件的依赖部分...");
		
		m = this;
		
		dataFolder = getDataFolder();
		
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
    
		if (permissionProvider != null)
        
			permission = permissionProvider.getProvider();
		
		if (economyProvider != null)
			
			economy = economyProvider.getProvider();
		
		this.getLogger().info("正在加载配置文件...");
		
		timers = load("Timers.yml");
		cmdStacks = load("CmdStacks.yml");
		conds = load("Conds.yml");
		config = load("config.yml");
		
		setFromConfig();
		
		this.getLogger().info("正在加载假人...");
		
		if(config.getBoolean("ena_ficman")) {
			
			for(Package packet : Package.getPackages()) {
				
				String name = packet.getName();
				
				if(name.startsWith("net.minecraft.server")) {

					nms = packet;
					
				}else if(name.startsWith("org.bukkit.craftbukkit") && name.endsWith("entity")) {
					
					obce = packet;
					
				}
				
			}
			
			try {
				
				packet = Class.forName(nms.getName() + ".Packet");
				server = Class.forName(nms.getName() + ".MinecraftServer");
				world = Class.forName(nms.getName() + ".WorldServer");
				entityp = Class.forName(nms.getName() + ".EntityPlayer");
				entityh = Class.forName(nms.getName() + ".EntityHuman");
				playerim = Class.forName(nms.getName() + ".PlayerInteractManager");
				packetpopi = Class.forName(nms.getName() + ".PacketPlayOutPlayerInfo");
				packetpones = Class.forName(nms.getName() + ".PacketPlayOutNamedEntitySpawn");
				
				try {
					epia = Class.forName(nms.getName() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
				}catch(ClassNotFoundException e2) {
					epia = Class.forName(nms.getName() + ".EnumPlayerInfoAction");
				}
			
			} catch (ClassNotFoundException e1) {
				
				this.getLogger().info("版本不配套或者游戏核心损坏!");
				
				e1.printStackTrace();
			
			}
			
			try {
				
				ficman = createFicman(this.getName() + "Ficman");
				
			}catch(Exception e) {
				
				this.getLogger().info("无法初始化假人,请与作者联系!");
				
				e.printStackTrace();
				
			}
			
		}
		
		this.getLogger().info("正在加载最后的部分...");
		
		api = TimerExecuteAPI.createAPI(timers);
		
		this.getCommand("timer").setExecutor(new Commands());
		
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		this.getLogger().info("插件加载完成!");
		
		this.getLogger().info("如果您在本插件的1.6版本之前就使用了本插件,请把config.yml改名为Timers.yml并使用timer reload重载配置文件");
		
		timer = new TestingTimer() {
			
			protected void runMethod() throws Exception{
			
				try {
					testingForTime();
				}catch(Exception e) {
					e.printStackTrace();
				}
			
			}
			
		};
		
		timer.start(0, -1, 1000);
		
	}
	
	/**
	 * 关闭方法
	 */
	public void onDisable() {
		
		timer.stop();
		
		this.getLogger().info("插件已关闭!");
		
	}
	
	static FileConfiguration load(String path) {
		
		File file = new File(dataFolder,path);
		
		if(!dataFolder.exists()) {
			
			dataFolder.mkdirs();
			
		}
		
		if(!file.exists()) {
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		if(m.getResource(path) != null && file.length() == 0) {
			
			m.saveResource(path, true);
			
		}
		
		return YamlConfiguration.loadConfiguration(file);
		
	}
	
	/**
	 * 普通的时间检测
	 * @throws Exception
	 */
	private void testingForTime() throws Exception {
				
		for(String t : timers.getKeys(false)) {
			
			if(isNow(api.getTimer(t),Calendar.getInstance()))
					
				api.getTimer(t).execTimer();
			
		}
		
	}
	
	/**
	 * 一次正常的判断时间是否到了的方法
	 */
	private boolean isNow(Timer timer,Calendar now) {
		
		if(!timer.isEnable())

			return false;
		
		if(timer.getUnEna().getYear() == now.get(Calendar.YEAR)) {
				
			return false;
		
		}else if(timer.getUnEna().getMonth() == now.get(Calendar.YEAR)) {
				
			return false;
		
		}else if(timer.getUnEna().getDay() == now.get(Calendar.YEAR)) {
					
			return false;
				
		}
		
		for(Timer.AEnaTime ena : timer.getEnaTimes()) {
			
			if(ena.getType() == Timer.TimeType.DAY) {
				
				return dayTimeEquals(ena.getTime(), now);
				
			}else if(ena.getType() == Timer.TimeType.MONTH){
				
				return dayTimeEquals(ena.getTime(), now) && now.get(Calendar.DAY_OF_MONTH) == ena.getDayForMonth();
				
			}else if(ena.getType() == Timer.TimeType.WEEK){
				
				return dayTimeEquals(ena.getTime(), now) && now.get(Calendar.DAY_OF_WEEK) == getCalCode(ena.getDayForWeek());
				
			}else if(ena.getType() == Timer.TimeType.DAY_FOR_YEAR) {
				
				return dayTimeEquals(ena.getTime(), now) && now.get(Calendar.MONTH) == ena.getDayForYear().getMonth() && now.get(Calendar.DAY_OF_MONTH) == ena.getDayForYear().getDay();
				
			}
			
		}
		
		return false;
		
	}
	
	/**
	 * 判断给定的时分秒与传入的now的时分秒是否相等
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @param now
	 * @return
	 */
	private boolean dayTimeEquals(Timer.DayTime time,Calendar now) {
		
		return (now.get(Calendar.HOUR_OF_DAY) == time.getHour())
			&& (now.get(Calendar.MINUTE) == time.getMinute())
			&& (now.get(Calendar.SECOND) == time.getSecond());
		
	}
	
	private int getCalCode(String whatday) {
		
		if(whatday.equals("sunday")) {
			
			return Calendar.SUNDAY;
			
		}else if(whatday.equals("monday")) {
			
			return Calendar.MONDAY;
			
		}else if(whatday.equals("tuesday")) {
			
			return Calendar.TUESDAY;
			
		}else if(whatday.equals("wednesday")) {
			
			return Calendar.WEDNESDAY;
			
		}else if(whatday.equals("thursday")) {
			
			return Calendar.THURSDAY;
			
		}else if(whatday.equals("friday")) {
			
			return Calendar.FRIDAY;
			
		}else if(whatday.equals("saturday")) {
			
			return Calendar.SATURDAY;
			
		}
		
		return -1;
		
	}
	
	static void setFromConfig() {
		
		EventListener.DELAY = config.getString("cmd.spl_cmd.delay", "delay") + " ";
		EventListener.SENDMSG = config.getString("cmd.spl_cmd.sendmsg", "sendmsg") + " ";
		
		EventListener.ALL = config.getString("conds.normal_conds.all_player", "all");
		EventListener.FICMAN = config.getString("conds.normal_conds.ficman", "ficman");
		
		EventListener.RANDOM = config.getString("conds.args_conds.random_player", "random") + "{";
		EventListener.LOCATION = config.getString("conds.args_conds.location_players", "location") + "{";
		EventListener.RANGE = config.getString("conds.args_conds.range_players", "range") + "{";
		EventListener.MONEY = config.getString("conds.args_conds.money_has_players", "money") + "{";
		
		EventListener.GROUP = config.getString("conds.prefix_conds.gruop_players", "#");
		EventListener.WORLD = config.getString("conds.prefix_conds.wrold_players", "%");
		EventListener.PERMISSION = config.getString("conds.prefix_conds.permission_has_players", "*");
		EventListener.COND = config.getString("conds.prefix_conds.conds_file_cond", "$");
		
		EventListener.CMD_STACK = config.getString("cmd.cmd_stack", ">");
		
		EventListener.VAR_PLAYER = config.getString("cmd.var.player", "%player%");
		EventListener.YEAR = config.getString("cmd.var.year", "%year%");
		EventListener.MONTH = config.getString("cmd.var.month", "%month%");
		EventListener.DAY_FOR_MONTH = config.getString("cmd.var.day_for_month", "%day_for_month%");
		EventListener.DAY_FOR_WEEK = config.getString("cmd.var.day_for_week", "%day_for_week%");
		EventListener.HOUR = config.getString("cmd.var.hour_twelve", "%hour_for_halfday%");
		EventListener.HOUR_FOR_DAY = config.getString("cmd.var.hour_twenty_four", "%hour_for_day");
		EventListener.MINUTE = config.getString("cmd.var.minute", "%minute%");
		EventListener.SECOND = config.getString("cmd.var.second", "%second%");
		
	}
	
	@SuppressWarnings("deprecation")
	static Player createFicman(String displayname) throws Exception{
		
		Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
	
		Object world = Bukkit.getWorlds().get(0).getClass().getMethod("getHandle").invoke(Bukkit.getWorlds().get(0));
		
		Player target = Bukkit.getServer().getPlayerExact(displayname);
		
		Object pim = Main.playerim.getConstructor(Class.forName(Main.nms.getName() + ".World")).newInstance(world);
		
		Object npc = null;
		
		if(target != null) {
			npc = Main.entityp.getConstructor(Main.server,Main.world,GameProfile.class,Main.playerim).newInstance(server,world,new GameProfile(target.getUniqueId(),target.getName()),pim);
		}else {
			OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(displayname);
			npc = Main.entityp.getConstructor(Main.server,Main.world,GameProfile.class,Main.playerim).newInstance(server,world,new GameProfile(op.getUniqueId(),displayname),pim);
		}
		
		Location loc = new Location(Bukkit.getWorlds().get(0),0,255,0);
		
		Main.entityp.getMethod("setLocation", double.class,double.class,double.class,float.class,float.class).invoke(npc, loc.getX(),loc.getY(),loc.getZ(),loc.getYaw(),loc.getPitch());
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			
			Object connect = handle.getClass().getField("playerConnection").get(handle);
			
			Object array = Array.newInstance(Main.entityp, 1);
			
			Array.set(array, 0, npc);
			
			Object ppopi = Main.packetpopi.getConstructor(Main.epia,Array.newInstance(Main.entityp, 0).getClass()).newInstance(Main.epia.getMethod("valueOf",String.class).invoke(null, "ADD_PLAYER"),array);
			
			Object ppones = Main.packetpones.getConstructor(Class.forName(Main.nms.getName() + ".EntityHuman")).newInstance(npc);
			
			Method send = connect.getClass().getMethod("sendPacket", Main.packet);
			
			send.invoke(connect, ppopi);
			
			send.invoke(connect, ppones);
			
		}
		
		Player ficman = (Player)Class.forName(Main.obce.getName() + ".CraftPlayer").getConstructor(Bukkit.getServer().getClass(),Main.entityp).newInstance(Bukkit.getServer(),npc);
		
		ficman.setOp(true);
		
		return ficman;
		
	}
	
}