package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;
import me.coralise.custombansplus.sql.objects.SqlBanned;

import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class SqlAbstractBanCommand {

    public static final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getBanMsg(UUID uuid) {

        SqlBanned sb = SqlCache.getBannedObject(uuid);
        
        String banner = m.getName(sb.getBannerUuid());
        String reason = sb.getReason();
        String unban = sb.getUnbanDateString();
        String duration = sb.getDuration();
        String timeleft = "";
        if (!duration.equalsIgnoreCase("Permanent"))
            timeleft = m.getTimeRemaining(sb.getUnbanDate());

        String msg = "placeholder";
        // %staff% %duration% %reason% %unban-date% %player% %timeleft%
        if (duration.equalsIgnoreCase("Permanent"))
            msg = m.parseMessage(m.getConfig().getString("pages.permban"));
        else
            msg = m.parseMessage(m.getConfig().getString("pages.tempban"));

        msg = msg.replace("%player%", SqlCache.getPlayerObject(sb.getUuid()).getUsername());
        msg = msg.replace("%staff%", banner);
        if (!duration.equalsIgnoreCase("Permanent")) msg = msg.replace("%duration%", duration);
        msg = msg.replace("%reason%", reason);
        if (!duration.equalsIgnoreCase("Permanent")) msg = msg.replace("%unban-date%", unban);
        if (!duration.equalsIgnoreCase("Permanent")) msg = msg.replace("%timeleft%", timeleft);

        return msg;

    }

    public static String getBanMsgTest(UUID uuid, String test) {

        SqlBanned sb = SqlCache.getBannedObject(uuid);

        String timeleft = "";
        String banner = "";
        String duration = "";
        String reason = "";
        String unban = "";
        
        if (test == null) {
            banner = m.getName(sb.getBannerUuid());
            reason = sb.getReason();
            unban = sb.getUnbanDateString();
            timeleft = m.getTimeRemaining(sb.getUnbanDate());
            duration = sb.getDuration();
        } else {
            banner = "@Staff";
            duration = "7d";
            reason = "Use of Hack Client";
            unban = "@mm/dd/yyyy XX:XX:XX";
            timeleft = "Xd Xh Xm Xs";
        }

        String type;
        if (test == null)
            type = sb.getBanType().toString();
        else if (test.equalsIgnoreCase("temp"))
            type = "dura";
        else
            type = "perm";

        String msg = "placeholder";
        // %staff% %duration% %reason% %unban-date% %player% %timeleft%
        if (type.equalsIgnoreCase("Perm Ban") || type.equalsIgnoreCase("Perm IP Ban"))
            msg = m.parseMessage(m.getConfig().getString("pages.permban"));
        else
            msg = m.parseMessage(m.getConfig().getString("pages.tempban"));

        msg = msg.replace("%player%", SqlCache.getPlayerObject(sb.getUuid()).getUsername());
        msg = msg.replace("%staff%", banner);
        if (!type.equalsIgnoreCase("Perm Ban") && !type.equalsIgnoreCase("Perm IP Ban")) msg = msg.replace("%duration%", duration);
        msg = msg.replace("%reason%", reason);
        if (!type.equalsIgnoreCase("Perm Ban") && !type.equalsIgnoreCase("Perm IP Ban")) msg = msg.replace("%unban-date%", unban);
        if (!type.equalsIgnoreCase("Perm Ban") && !type.equalsIgnoreCase("Perm IP Ban")) msg = msg.replace("%timeleft%", timeleft);

        return msg;

    }

    public static void banPage(UUID uuid){
        
        OfflinePlayer proTarget = m.getOfflinePlayer(uuid);
        if(proTarget.isOnline()){
            Player p = Bukkit.getPlayer(uuid);
            
            Bukkit.getScheduler().runTask(m, () -> p.kickPlayer(SqlAbstractBanCommand.getBanMsg(uuid)));
        }
        
    }
    
}
