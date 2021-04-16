package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;

import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class SqlAbstractBanCommand {

    public static final CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getBanMsg(String target, String test) {
        String type;
        if (test == null)
            type = SqlMethods.getBanType(target);
        else if (test.equalsIgnoreCase("temp"))
            type = "dura";
        else
            type = "perm";

        String msg = "placeholder";
        // %staff% %duration% %reason% %unban-date% %player% %timeleft%
        if (type.equalsIgnoreCase("Perm Ban") || type.equalsIgnoreCase("Perm IP Ban"))
            msg = m.getConfig().getString("permban-page");
        else
            msg = m.getConfig().getString("tempban-page");

        String timeleft = "";
        String banner = "";
        String duration = "";
        String reason = "";
        String unban = "";
        String[] getter = new String[5];

        if (test == null) {
            getter = SqlMethods.getActiveBanDetails(target, type);
            banner = getter[0];
            reason = getter[1];
            unban = getter[4];
            timeleft = getter[3];
        } else {
            banner = "@Staff";
            duration = "7d";
            reason = "Use of Hack Client";
            unban = "@mm/dd/yyyy XX:XX:XX";
            timeleft = "Xd Xh Xm Xs";
        }

        msg = msg.replace(" /n ", "\n");
        msg = msg.replace("/n ", "\n");
        msg = msg.replace(" /n", "\n");
        msg = msg.replace("/n", "\n");
        msg = msg.replace("%player%", target);
        msg = msg.replace("%staff%", banner);
        if (!type.equalsIgnoreCase("Perm Ban") && !type.equalsIgnoreCase("Perm IP Ban")) msg = msg.replace("%duration%", duration);
        msg = msg.replace("%reason%", reason);
        if (!type.equalsIgnoreCase("Perm Ban") && !type.equalsIgnoreCase("Perm IP Ban")) msg = msg.replace("%unban-date%", unban);
        if (!type.equalsIgnoreCase("Perm Ban") && !type.equalsIgnoreCase("Perm IP Ban")) msg = msg.replace("%timeleft%", timeleft);
        msg = msg.replace("&", "§");

        return msg;

    }

    public static void banPage(String target){
        
        OfflinePlayer proTarget = m.getOfflinePlayer(target);
        if(proTarget.isOnline()){
            Player p = Bukkit.getPlayer(target);
            
            p.kickPlayer(SqlAbstractBanCommand.getBanMsg(target, null));
        }
        
    }

    public static String getBanType(String value){

        if (value.equalsIgnoreCase("perm"))
            return "perm";
        else if (value.length() >= 2 && value.charAt(0) == 's' && m.getSevConfig().getKeys(false).contains(value.substring(1)))
            return "sev";
        else if (m.isValueValid(value)) {
            return "dura";
        } else {
            return null;
        }

    }
    
}
