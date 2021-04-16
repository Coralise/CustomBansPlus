package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class YamlAbstractAnnouncer {

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    static String target;
    static String staff;
    static String duration;
    static String reason;

    // %player% %staff% %duration% %reason%

    public static String parseMessage(String message){

        message = message.replace("%player%", target);
        message = message.replace("%staff%", staff);
        if(duration != null) message = message.replace("%duration%", duration);
        if(reason != null) message = message.replace("%reason%", reason);
        message = message.replace("&", "§");
        
        return message;

    }
    
    public static void getAnnouncer(String t, String s, String d, String r, String type){

        target = t;
        staff = s;
        duration = d;
        reason = r;

        String message = parseMessage(m.getConfig().getString(type + "-announcer"));

        System.out.println(message);
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(message);
        }

    }

    public static void getSilentAnnouncer(String t, String s, String d, String r, String type){

        target = t;
        staff = s;
        duration = d;
        reason = r;

        String message = parseMessage(m.getConfig().getString(type + "-announcer"));

        if(Bukkit.getPlayer(staff) != null)
            Bukkit.getPlayer(staff).sendMessage("§a§lSilent: §r" + message);
        else
            System.out.println("§a§lSilent: §r" + message);

        if(Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline())
            Bukkit.getPlayer(target).sendMessage(message);
        
    }
    
}
