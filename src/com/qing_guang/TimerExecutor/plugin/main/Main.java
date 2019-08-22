package com.qing_guang.TimerExecutor.plugin.main;

import com.mojang.authlib.GameProfile;
import com.qing_guang.TimerExecutor.plugin.cond.ConditionFactory;
import com.qing_guang.TimerExecutor.plugin.timer.Timer;
import com.qing_guang.TimerExecutor.plugin.timer.TimerCommand;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

    public static final Map<String,Timer> TIMERS = new HashMap<>();

    private Player FAKEMAN;
    private YamlConfiguration CMD_STACK;
    private YamlConfiguration CONDS;
    private java.util.Timer verify_timer;
    private TimerTaskImpl taskImpl;

    public static Permission permission;
    public static Economy economy;

    public void onEnable(){

        getLogger().info("正在加载插件所需的依赖环境...");

        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);

        if(permissionProvider != null){
            permission = permissionProvider.getProvider();
        }else{
            getLogger().warning("权限依赖缺失!请检查自己的插件列表是否缺少权限插件");
        }
        if(economyProvider != null){
            economy = economyProvider.getProvider();
        }else{
            getLogger().warning("经济依赖缺失!请检查自己的插件列表是否缺少权限插件");
        }

        getLogger().info("正在创建假人...");
        createFakeman();

        getLogger().info("正在加载插件所需的配置文件...");
        try{
            loadConfig();
            CMD_STACK = load("cmdstacks.yml");
            CONDS = load("conds.yml");
            transferOldFileOfTimers(getDataFolder());
            loadTimers(getDataFolder());
        }catch (IOException e){
            getLogger().warning("配置文件无法加载!请将下面的报错信息反馈给作者!");
            e.printStackTrace();
        }

        getLogger().info("正在开启定时器检测...");
        verify_timer = new java.util.Timer();
        taskImpl = new TimerTaskImpl(TIMERS);
        verify_timer.schedule(taskImpl,0,1000);

        getLogger().info("正在进行最后的准备工作...");
        getCommand("timer").setExecutor(new Commands());

        getLogger().info("本插件加载完毕,版本: " + getDescription().getVersion());

    }

    public void onDisable(){

        getLogger().info("正在关闭定时器检测...");
        verify_timer.cancel();

    }

    public YamlConfiguration load(String path) throws IOException {

        File file = new File(getDataFolder(),path);

        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            saveResource(path, true);
        }

        return YamlConfiguration.loadConfiguration(file);

    }

    public YamlConfiguration getCmdStackConfig(){
        return CMD_STACK;
    }

    public YamlConfiguration getCondsConfig(){
        return CONDS;
    }

    public Player getFakeman(){
        return FAKEMAN;
    }

    private void loadConfig() throws IOException{

        YamlConfiguration config = load("config.yml");

        ConfigurationSection cmd_var = config.getConfigurationSection("cmd.var");
        ConfigurationSection spi_cmd = config.getConfigurationSection("cmd.spi_cmd");

        ConfigurationSection normal_conds = config.getConfigurationSection("conds.normal_conds");
        ConfigurationSection args_conds = config.getConfigurationSection("conds.args_conds");
        ConfigurationSection perfix_conds = config.getConfigurationSection("conds.prefix_conds");

        TimerCommand.init(new com.qing_guang.TimerExecutor.plugin.timer.TimerCommand.Flags(
                cmd_var.getString("player","%player%"),
                cmd_var.getString("year","%year%"),
                cmd_var.getString("month","%month%"),
                cmd_var.getString("day_for_month","%day_for_month%"),
                cmd_var.getString("day_for_week_en","%day_for_week_en%"),
                cmd_var.getString("day_for_week_ch","%day_for_week_ch%"),
                cmd_var.getString("hour_twelve","%hour_for_halfday%"),
                cmd_var.getString("hour_twenty_four","%hour_for_day%"),
                cmd_var.getString("minute","%minute%"),
                cmd_var.getString("second","%second%"),
                spi_cmd.getString("delay","delay"),
                spi_cmd.getString("sendmsg","sendmsg"),
                config.getString("cmd.com_stack",">")
        ));

        ConditionFactory.init(new com.qing_guang.TimerExecutor.plugin.cond.ConditionFactory.Flags(
                normal_conds.getString("all_player","all"),
                normal_conds.getString("fakeman","fakeman"),
                args_conds.getString("negation","ngt"),
                args_conds.getString("random_player","random"),
                args_conds.getString("area_players","area"),
                args_conds.getString("range_players","range"),
                args_conds.getString("money_has_players","money"),
                perfix_conds.getString("group_players","#"),
                perfix_conds.getString("world_players","%"),
                perfix_conds.getString("permission_has_players","*"),
                perfix_conds.getString("conds_in_file","$")
        ));

    }

    private static void transferOldFileOfTimers(File dataFolder) throws IOException{
        File old = new File(dataFolder,"Timers.yml");
        if(old.exists()){
            File now = new File(dataFolder,"timers/timers.yml");
            now.getParentFile().mkdirs();
            InputStream input = new FileInputStream(old);
            OutputStream output = new FileOutputStream(now);
            int len = 0;
            byte[] data = new byte[64];
            while((len = input.read(data)) != -1){
                output.write(data,0,len);
                output.flush();
            }
            input.close();
            output.close();
        }
        old.delete();
    }

    private void loadTimers(File dataFolder) throws IOException{
        if(new File(dataFolder,"timers").exists()) {
            for (File timers_file : new File(dataFolder, "timers").listFiles()) {
                YamlConfiguration timer_c = load("timers/" + timers_file.getName());
                for (String key : timer_c.getKeys(false)) {
                    TIMERS.put(key, new Timer(timer_c.getConfigurationSection(key)));
                }
            }
        }else {
            YamlConfiguration timer_c = load("timers/timer.yml");
            for (String key : timer_c.getKeys(false)) {
                TIMERS.put(key, new Timer(timer_c.getConfigurationSection(key)));
            }
        }
    }

    private void createFakeman(){

        Class<?> minecraft_server = null;
        Class<?> world = null;
        Class<?> world_server = null;
        Class<?> entity_player = null;
        Class<?> player_interact_manager = null;

        Class<?> craft_server = null;
        Class<?> craft_world = null;
        Class<?> craft_player = null;

        Player fakeman = null;

        try{

            for(Package pkae : Package.getPackages()){
                String name = pkae.getName();
                if(name.startsWith("net.minecraft.server")){
                    name += ".";
                    minecraft_server = Class.forName(name + "MinecraftServer");
                    world = Class.forName(name + "World");
                    world_server = Class.forName(name + "WorldServer");
                    entity_player = Class.forName(name + "EntityPlayer");
                    player_interact_manager = Class.forName(name + "PlayerInteractManager");
                }else if(name.startsWith("org.bukkit.craftbukkit")){
                    if(name.split("\\.").length == 4){
                        name += ".";
                        craft_server = Class.forName(name + "CraftServer");
                        craft_world = Class.forName(name + "CraftWorld");
                    }else if(name.endsWith("entity")){
                        name += ".";
                        craft_player = Class.forName(name + "CraftPlayer");
                    }
                }
            }

            Object ms = craft_server.getMethod("getServer").invoke(Bukkit.getServer());
            Object ws = craft_world.getMethod("getHandle").invoke(Bukkit.getWorlds().get(0));

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.randomUUID().toString().replace("-","").substring(0,8));
            GameProfile gp = new GameProfile(offlinePlayer.getUniqueId(),offlinePlayer.getName());

            Constructor<?> epc = entity_player.getConstructor(minecraft_server,world_server,GameProfile.class,player_interact_manager);
            Constructor<?> pimc = player_interact_manager.getConstructor(world);

            Object pim = pimc.newInstance(ws);
            Object ep = epc.newInstance(ms,ws,gp,pim);

            Constructor<?> cpc = craft_player.getConstructor(craft_server,entity_player);
            Object cp = cpc.newInstance(Bukkit.getServer(),ep);

            fakeman = (Player)cp;
            fakeman.teleport(new Location(Bukkit.getWorlds().get(0),0,255,0));
            fakeman.setOp(true);

        }catch (Exception e){
            e.printStackTrace();
        }

        this.FAKEMAN = fakeman;

    }

}
