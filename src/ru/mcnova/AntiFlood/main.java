package ru.mcnova.AntiFlood;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

import static jdk.nashorn.internal.objects.NativeString.toLowerCase;

public class main extends JavaPlugin implements Listener {

    ArrayList<String> antiflood_list = new ArrayList<String>();

    boolean compatibility_enable;

    ArrayList<String> compatibility_channel = new ArrayList<String>();

    long time;

    String version = "AntiFlood v. 1.1";

    String help = version + "\n" + "Доступные команды:\n" + "/aflood reload - перезагрузка конфигурации\n" + "/aflood version - версия плагина";

    public void onEnable(){
        getLogger().info("Запуск...");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("aflood").setExecutor(this);
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            getLogger().info("Создание config.yml...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();

            String[] config_channels = getConfig().getString("UltimateChat_Compatibility.channels").split(";");
            for(int i = 0; i < config_channels.length; i++){
                compatibility_channel.add(config_channels[i]);
            }
            compatibility_enable = getConfig().getBoolean("UltimateChat_Compatibility.enable");
        }
        else{
            String[] config_channels = getConfig().getString("UltimateChat_Compatibility.channels").split(";");
            for(int i = 0; i < config_channels.length; i++){
                compatibility_channel.add(config_channels[i]);
            }
            compatibility_enable = getConfig().getBoolean("UltimateChat_Compatibility.enable");
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player)sender;
        if(args.length > 0){
            String argument = toLowerCase(args[0]);
            switch (argument){
                case "reload":
                    Permission reload_permission = new Permission("aflood.reload");
                    if(player.hasPermission(reload_permission)){
                        reloadConfig();
                        String[] config_channels = getConfig().getString("UltimateChat_Compatibility.channels").split(";");
                        for(int i = 0; i < config_channels.length; i++){
                            compatibility_channel.add(config_channels[i]);
                        }
                        compatibility_enable = getConfig().getBoolean("UltimateChat_Compatibility.enable");
                    }
                    else{player.sendMessage(tools.plugin_message(getConfig().getString("Configuration.prefix"), getConfig().getString("Configuration.no_permission")));}
                    break;
                case "version":
                    Permission version_permission = new Permission("aflood.version");
                    if(player.hasPermission(version_permission)) player.sendMessage(tools.plugin_message(getConfig().getString("Configuration.prefix"), version));
                    else{player.sendMessage(tools.plugin_message(getConfig().getString("Configuration.prefix"), getConfig().getString("Configuration.no_permission")));}
            }
        }
        else if(args.length == 0){
            Permission help_permission = new Permission("aflood.help");
            if(player.hasPermission(help_permission)){
                player.sendMessage(tools.plugin_message(getConfig().getString("Configuration.prefix"), help));
            }
            else{player.sendMessage(tools.plugin_message(getConfig().getString("Configuration.prefix"), getConfig().getString("Configuration.no_permission")));}
        }
        return false;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        final Player player = e.getPlayer();
        this.time = getConfig().getLong("Configuration.time");
        this.time *= 20L;
        boolean check_player = this.antiflood_list.contains(player.getName());
        if (!player.hasPermission("aflood.bypass"))
            if (!check_player) {
                if(compatibility_enable && !compatibility_channel.contains(Character.toString(e.getMessage().charAt(0)))){
                    this.antiflood_list.add(player.getName());
                    getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        public void run() {
                            main.this.antiflood_list.remove(player.getName());
                        }
                    },  this.time);
                }
            } else {
                e.setCancelled(true);
                player.sendMessage(tools.plugin_message(getConfig().getString("Configuration.prefix"), getConfig().getString("Configuration.message")));
            }
    }
}