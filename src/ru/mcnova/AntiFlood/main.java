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
import java.util.HashSet;
import java.util.Set;

import static jdk.nashorn.internal.objects.NativeString.toLowerCase;

public class main extends JavaPlugin implements Listener {

    Set<String> antiflood = new HashSet();

    long time;

    String version = "AntiFlood v. 1.0";

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
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player)sender;
        if(args.length > 0){
            String argument = toLowerCase(args[0]);
            switch (argument){
                case "reload":
                    Permission reload_permission = new Permission("aflood.reload");
                    if(player.hasPermission(reload_permission)) reloadConfig();
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
        boolean spamhas = this.antiflood.contains(player.getName());
        if (!player.hasPermission("aflood.bypass"))
            if (!spamhas) {
                this.antiflood.add(player.getName());
                getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        main.this.antiflood.remove(player.getName());
                    }
                },  this.time);
            } else {
                e.setCancelled(true);
                player.sendMessage(tools.plugin_message(getConfig().getString("Configuration.prefix"), getConfig().getString("Configuration.message")));
            }
    }

}