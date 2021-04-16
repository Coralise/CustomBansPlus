package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class YamlAbstractBanCommand {

    public static final CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    static String target;
    static String tgtUuid;
    static String value;
    static String reason;
    static OfflinePlayer proTarget;
    static String type;
    static String sdr;
    static ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String setBanned() {

        String duration = "";
        boolean clearInv = false;
        List<String> cmds = new ArrayList<String>();
        Double balDeduct = 0.0;
        switch (type) {
            case "sev":
                int sevNum = Integer.parseInt(value.substring(1));
                clearInv = m.getSevConfig().getBoolean(sevNum + ".clear-inv");
                cmds = m.getSevConfig().getStringList(sevNum + ".console-commands");
                balDeduct = m.getSevConfig().getDouble(sevNum + ".baldeduct");
                if (m.getSevConfig().getString(sevNum + ".duration").equalsIgnoreCase("Permanent")) {
                    type = "perm";
                    duration = "Permanent";
                    break;
                }else{
                    type = "dura";
                    value = m.getSevConfig().getString(sevNum + ".duration");
                    duration = value;
                    break;
                }
            case "perm":
                duration = "Permanent";
                break;
            case "dura":
                duration = value;
                break;
        }

        m.checkSevValues(target, clearInv, balDeduct, cmds);

        Date cDate = new Date();
        String bannedOn = formatter.format(cDate);

        m.getBansConfig().set(tgtUuid + ".duration", duration);
        m.getBansConfig().set(tgtUuid + ".reason", reason);
        m.getBansConfig().set(tgtUuid + ".banned-by", sdr);
        m.getBansConfig().set(tgtUuid + ".banned-on", bannedOn);

        if (type.equalsIgnoreCase("dura")) {

            String unbanDate = m.calculateUnpunishDate(value);
            m.getBansConfig().set(tgtUuid + ".unban-date", unbanDate);
            m.getBansConfig().set(tgtUuid + ".type", "Temp IP Ban");

        }else{

            m.getBansConfig().set(tgtUuid + ".unban-date", "None");
            m.getBansConfig().set(tgtUuid + ".type", "Perm IP Ban");

        }

        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException ex) {
            // none
        }

        return type;

    }

    public static String getBanMsg(String proTarget, String test) {
        String tgtUuid = m.getUuid(proTarget);
        if (test == null)
            type = m.getBansConfig().getString(tgtUuid + ".duration");
        else if (test.equalsIgnoreCase("temp"))
            type = "dura";
        else
            type = "perm";

        String msg = "placeholder";
        // %staff% %duration% %reason% %unban-date% %player% %timeleft%
        if (type.equalsIgnoreCase("dura") || !type.equalsIgnoreCase("Permanent"))
            msg = m.getConfig().getString("tempban-page");
        if (type.equalsIgnoreCase("perm") || type.equalsIgnoreCase("Permanent"))
            msg = m.getConfig().getString("permban-page");

        String timeleft = "";
        String banner = "CONSOLE";
        String duration;
        String reason;
        String unban;

        if (test == null) {
            String staffUuid = m.getBansConfig().getString(tgtUuid + ".banned-by");
            if(!staffUuid.equalsIgnoreCase("CONSOLE")) banner = m.getName(staffUuid);
            duration = m.getBansConfig().getString(tgtUuid + ".duration");
            reason = m.getBansConfig().getString(tgtUuid + ".reason");
            unban = m.getBansConfig().getString(tgtUuid + ".unban-date");
            if (type.equalsIgnoreCase("dura") || !type.equalsIgnoreCase("Permanent"))
                timeleft = m.getTimeRemaining(m.getYamlUnbanDate(tgtUuid));
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
        msg = msg.replace("%player%", proTarget);
        msg = msg.replace("%staff%", banner);
        msg = msg.replace("%duration%", duration);
        msg = msg.replace("%reason%", reason);
        msg = msg.replace("%unban-date%", unban);
        if (type.equalsIgnoreCase("dura") || !type.equalsIgnoreCase("Permanent"))
            msg = msg.replace("%timeleft%", timeleft);
        msg = msg.replace("&", "§");

        return msg;

    }

    public static boolean banPage() {

        if (proTarget.isOnline()) {
            Player p = Bukkit.getPlayer(target);

            p.kickPlayer(getBanMsg(target, null));
        }

        return true;

    }

    public static boolean addHistory(@NotNull String target, String sender, String type, String reason, String user) {

        String stSender = "CONSOLE";
        tgtUuid = m.getUuid(target);
        if(!sender.equalsIgnoreCase("CONSOLE")) stSender = m.getUuid(sender);

        int spot = 0;
        try {
            if (user == null)
                spot = m.getHistConfig().getConfigurationSection(target).getKeys(false).size() + 1;
            else
                spot = m.getHistConfig().getConfigurationSection(user).getKeys(false).size() + 1;
        } catch (NullPointerException npe) {
            spot = 1;
        }

        String path = "";

        if (user == null)
            path = target + "." + spot;
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

    /**
     * Bans players with the same IP Address. Copies over values from the ip's banned record.
     * @param target
     * @param ip
     * @param value
     * @return
     */
    public static boolean banPlayer(String target, String ip, String value) {

        sdr = m.getBansConfig().getString(ip + ".banned-by");
        reason = m.getBansConfig().getString(ip + ".reason");
        tgtUuid = m.getUuid(target);

        proTarget = m.getOfflinePlayer(target);
        YamlAbstractBanCommand.target = target;
        YamlAbstractBanCommand.value = value;

        type = YamlAbstractBanCommand.getBanType(value);

        type = setBanned();

        banPage();

        YamlCache.setBan(tgtUuid);

        addHistory(target, sdr, "ipban", reason, null);

        return true;

    }

    public static boolean copyIPBan(String target, String targetIP) {

        tgtUuid = m.getUuid(target);

        for (String key : m.getBansConfig().getConfigurationSection(targetIP).getKeys(false)) {
            m.getBansConfig().set(tgtUuid + "." + key, m.getBansConfig().get(targetIP + "." + key));
        }
        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException e) {
            // none
        }
        String senderUuid = m.getBansConfig().getString(tgtUuid + ".banned-by");
        String rsn = m.getBansConfig().getString(tgtUuid + ".reason");

        YamlCache.setBan(tgtUuid);

        String sender = "CONSOLE";
        if (!senderUuid.equalsIgnoreCase("CONSOLE")) sender = m.getName(senderUuid);

        addHistory(target, sender, "ipban", rsn, null);

        return true;

    }

    public static boolean isUnmuted(String target) {

        tgtUuid = m.getUuid(target);

        Date cDate = new Date();
        Date uDate = new Date();
        try {
            uDate = formatter.parse(m.getMutesConfig().getString(tgtUuid + ".unmute-by"));
        } catch (ParseException e) {
            //none
        }

        return cDate.after(uDate);

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
