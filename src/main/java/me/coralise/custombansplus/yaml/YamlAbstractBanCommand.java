package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;
import me.coralise.custombansplus.yaml.objects.YamlBanned;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class YamlAbstractBanCommand {

    public static final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getBanMsg(UUID uuid) {
        String type = YamlCache.getBannedObject(uuid).getBanType().toString();

        String msg = "placeholder";
        // %staff% %duration% %reason% %unban-date% %player% %timeleft%
        if (type.contains("Temp"))
            msg = m.parseMessage(m.getConfig().getString("pages.tempban"));
        if (type.contains("Perm"))
            msg = m.parseMessage(m.getConfig().getString("pages.permban"));

        String timeleft = "";
        String banner;
        String duration;
        String unban;

        YamlBanned yamlBanned = YamlCache.banCache.get(uuid);

        banner = "CONSOLE";
        if (yamlBanned.getBannerUuid() != null) banner = m.getName(yamlBanned.getBannerUuid());
        duration = yamlBanned.getDuration();
        String reason = yamlBanned.getReason();
        String target = m.getName(uuid.toString());

        msg = msg.replace("%player%", target);
        msg = msg.replace("%staff%", banner);
        msg = msg.replace("%duration%", duration);
        msg = msg.replace("%reason%", reason);

        if (type.contains("Temp")) {
            timeleft = m.getTimeRemaining(yamlBanned.getUnbanDate());
            unban = formatter.format(yamlBanned.getUnbanDate());
            msg = msg.replace("%unban-date%", unban);
            msg = msg.replace("%timeleft%", timeleft);
        }

        return msg;

    }

    public static String getBanMsgTest(UUID uuid, String test) {
        String type;
        if (test == null)
            type = YamlCache.banCache.get(uuid).getBanType().toString();
        else if (test.equalsIgnoreCase("temp"))
            type = "dura";
        else
            type = "perm";

        String msg = "placeholder";
        // %staff% %duration% %reason% %unban-date% %player% %timeleft%
        if (type.contains("temp"))
            msg = m.parseMessage(m.getConfig().getString("pages.tempban"));
        if (type.contains("perm"))
            msg = m.parseMessage(m.getConfig().getString("pages.permban"));

        String timeleft;
        String banner;
        String duration;
        String unban;

        String reason;
        String target = "";
        if (test != null) {
            banner = "@Staff";
            duration = "7d";
            reason = "Use of Hack Client";
            unban = "@mm/dd/yyyy XX:XX:XX";
            timeleft = "Xd Xh Xm Xs";
        } else {
            YamlBanned yamlBanned = YamlCache.banCache.get(uuid);

            timeleft = m.getTimeRemaining(yamlBanned.getUnbanDate());
            banner = "CONSOLE";
            if (yamlBanned.getBannerUuid() != null) banner = m.getName(yamlBanned.getBannerUuid());
            duration = yamlBanned.getDuration();
            reason = yamlBanned.getReason();
            unban = formatter.format(yamlBanned.getUnbanDate());
            target = m.getName(uuid.toString());
        }

        msg = msg.replace("%player%", target);
        msg = msg.replace("%staff%", banner);
        msg = msg.replace("%duration%", duration);
        msg = msg.replace("%reason%", reason);
        msg = msg.replace("%unban-date%", unban);
        if (type.equalsIgnoreCase("dura") || !type.equalsIgnoreCase("Permanent"))
            msg = msg.replace("%timeleft%", timeleft);

        return msg;

    }

    public static void banPage(UUID tgtUuid){
        
        if(m.getOfflinePlayer(tgtUuid).isOnline()){
            Player p = Bukkit.getPlayer(tgtUuid);
            
            Bukkit.getScheduler().runTask(m, () -> p.kickPlayer(YamlAbstractBanCommand.getBanMsg(tgtUuid)));
        }
        
    }

    public static boolean addHistory(UUID tgtUuid, String sender, String type, String reason, String user) {

        UUID stSender = null;
        if(!sender.equalsIgnoreCase("CONSOLE")) stSender = m.getUuid(sender);

        int spot = 0;
        try {
            if (user == null)
                spot = m.getHistConfig().getConfigurationSection(tgtUuid.toString()).getKeys(false).size() + 1;
            else
                spot = m.getHistConfig().getConfigurationSection(user).getKeys(false).size() + 1;
        } catch (NullPointerException npe) {
            spot = 1;
        }

        String path = "";

        if (user == null)
            path = tgtUuid.toString() + "." + spot;
        else
            path = user + "." + spot;

        if (type.equalsIgnoreCase("ban")) {
            m.getHistConfig().set(path + ".type", "Ban");
            m.getHistConfig().set(path + ".duration", m.getBansConfig().getString(tgtUuid + ".duration"));
            m.getHistConfig().set(path + ".reason", m.getBansConfig().getString(tgtUuid + ".reason"));
            m.getHistConfig().set(path + ".banned-by", m.getBansConfig().getString(tgtUuid + ".banned-by"));
            m.getHistConfig().set(path + ".banned-on", m.getBansConfig().getString(tgtUuid + ".banned-on"));
        }
        if (type.equalsIgnoreCase("kick")) {
            m.getHistConfig().set(path + ".type", "Kick");
            m.getHistConfig().set(path + ".kicked-by", stSender);
            m.getHistConfig().set(path + ".reason", reason);
        }
        if (type.equalsIgnoreCase("ipban")) {
            m.getHistConfig().set(path + ".type", "IP Ban");
            m.getHistConfig().set(path + ".duration", m.getBansConfig().getString(tgtUuid + ".duration"));
            m.getHistConfig().set(path + ".reason", m.getBansConfig().getString(tgtUuid + ".reason"));
            m.getHistConfig().set(path + ".banned-by", m.getBansConfig().getString(tgtUuid + ".banned-by"));
            m.getHistConfig().set(path + ".banned-on", m.getBansConfig().getString(tgtUuid + ".banned-on"));
        }
        if (type.equalsIgnoreCase("mute")) {
            m.getHistConfig().set(path + ".type", "Mute");
            m.getHistConfig().set(path + ".duration", m.getMutesConfig().getString(tgtUuid + ".duration"));
            m.getHistConfig().set(path + ".reason", m.getMutesConfig().getString(tgtUuid + ".reason"));
            m.getHistConfig().set(path + ".muted-by", m.getMutesConfig().getString(tgtUuid + ".muted-by"));
            m.getHistConfig().set(path + ".muted-on", m.getMutesConfig().getString(tgtUuid + ".muted-on"));
            m.getHistConfig().set(path + ".unmute-by", m.getMutesConfig().getString(tgtUuid + ".unmute-by"));
        }
        if (type.equalsIgnoreCase("warn")) {
            m.getHistConfig().set(path + ".type", "Warn");
            m.getHistConfig().set(path + ".reason", reason);
            m.getHistConfig().set(path + ".warned-by", stSender);
        }

        try {
            m.getHistConfig().save(m.getHistFile());
        } catch (IOException ex) {
            // none
        }

        return true;

    }

    public static void addHistory(YamlBanned yb) {

        UUID tgtUuid = yb.getUuid();

        int spot = 0;
        try {
            spot = m.getHistConfig().getConfigurationSection(tgtUuid.toString()).getKeys(false).size() + 1;
        } catch (NullPointerException npe) {
            spot = 1;
        }

        String path = tgtUuid.toString() + "." + spot;

        m.getHistConfig().set(path + ".type", yb.getBanType().toString());
        m.getHistConfig().set(path + ".duration", yb.getDuration());
        m.getHistConfig().set(path + ".reason", yb.getReason());
        m.getHistConfig().set(path + ".banned-by", yb.getBannerUuid());
        m.getHistConfig().set(path + ".banned-on", yb.getBanDateString());
        if (yb.getBanType().toString().contains("Temp")) {
            m.getHistConfig().set(path + ".unban-date", yb.getUnbanDateString());
        }

        try {
            m.getHistConfig().save(m.getHistFile());
        } catch (IOException ex) {
            // none
        }

    }
    
}
