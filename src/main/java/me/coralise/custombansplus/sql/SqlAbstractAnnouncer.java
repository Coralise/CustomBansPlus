package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;

import org.bukkit.Bukkit;

public abstract class SqlAbstractAnnouncer {

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    static String target;
    static String staff;
    static String duration;
    static String reason;
    static String message;

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

        message = parseMessage(m.getConfig().getString("announcers." + type));

        System.out.println(message);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));

    }

    public static void getSilentAnnouncer(String t, String s, String d, String r, String type){

        target = t;
        staff = s;
        duration = d;
        reason = r;

        String message = parseMessage(m.getConfig().getString("announcers." + type));

        if(Bukkit.getPlayer(staff) != null)
            Bukkit.getPlayer(staff).sendMessage("§a§lSilent: §r" + message);
        else
            System.out.println("§a§lSilent: §r" + message);

        if(Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline())
            Bukkit.getPlayer(target).sendMessage(message);
        
    }
    
}
