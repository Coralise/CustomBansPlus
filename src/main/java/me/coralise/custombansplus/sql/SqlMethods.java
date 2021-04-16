package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;

import static java.sql.DriverManager.getConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SqlMethods {

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();

    static final String HOST = m.getConfig().getString("host");
    static final String PORT = m.getConfig().getString("port");
    static final String USERNAME = m.getConfig().getString("user");
    static final String PASSWORD = m.getConfig().getString("pass");
    static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "?useTimezone=true&serverTimezone=UTC";

    static Statement s;
    static String sql;
    static ResultSet rs;
    static Connection c;

    static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean getConn() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("§e[CBP] §fGetting SQL Connection at: " + HOST + ":" + PORT + " ...");
            c = getConnection(URL, USERNAME, PASSWORD);
            s = c.createStatement();

            return true;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void checkDB() {
        try {

            rs = c.getMetaData().getCatalogs();

            boolean check = true;

            while (rs.next()) {
                String catalogs = rs.getString(1);

                if (catalogs.equalsIgnoreCase("cbp")) {
                    check = false;
                    break;
                }
            }

            if (check) {
                sql = "CREATE DATABASE cbp";
                s.executeUpdate(sql);
                System.out.println("[CBP] Created CustomBansPlus schematic for the first time.");
                System.out.println("[CBP] Preparing tables...");
                createTables();
            }

        } catch (SQLException e) {
        }
    }

    public static void createTables() {
        try {

            s.executeUpdate("USE cbp;");

            sql = "CREATE TABLE players (" + "player_id INT NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,"
                    + "player_uuid VARCHAR(36) NOT NULL UNIQUE," + "player_ign VARCHAR(30) NOT NULL,"
                    + "player_ip VARCHAR(20) NOT NULL," + "join_date DATE NOT NULL" + ")";

            s.executeUpdate(sql);

            sql = "CREATE TABLE `cbp`.`active_bans` ( " + "`ban_id` INT NOT NULL AUTO_INCREMENT, "
                    + "`ban_type` VARCHAR(20) NOT NULL, " + "`player_uuid` VARCHAR(45) NULL, "
                    + "`banned_ip` VARCHAR(45) NULL, " + "`banner_uuid` VARCHAR(45) NOT NULL, "
                    + "`ban_reason` VARCHAR(256) NULL, " + "`ban_date` DATETIME NOT NULL, "
                    + "`ban_duration` VARCHAR(20) NULL, " + "`unban_date` DATETIME NULL, " + "PRIMARY KEY (`ban_id`)) ";

            s.executeUpdate(sql);

            sql = "CREATE TABLE `cbp`.`player_histories` ( " + "`history_id` INT NOT NULL AUTO_INCREMENT, "
                    + "`player_uuid` VARCHAR(45) NOT NULL, " + "`punishment_type` VARCHAR(20) NOT NULL, "
                    + "`staff_uuid` VARCHAR(45) NOT NULL, " + "`punishment_reason` VARCHAR(256) NULL, "
                    + "`punishment_date` DATETIME NOT NULL, " + "`punishment_duration` VARCHAR(20) NULL, `unpunish_date` DATETIME NULL, `status` VARCHAR(11) NOT NULL, `staff_updater_uuid` VARCHAR(45), "
                    + "PRIMARY KEY (`history_id`)); ";

            s.executeUpdate(sql);

            sql = "CREATE TABLE `cbp`.`active_mutes` ( " +
                "`mute_id` INT NOT NULL AUTO_INCREMENT, " +
                "`mute_type` VARCHAR(20) NOT NULL, " +
                "`player_uuid` VARCHAR(45) NOT NULL, " +
                "`muter_uuid` VARCHAR(45) NOT NULL, " +
                "`mute_reason` VARCHAR(256) NULL, " +
                "`mute_date` DATETIME NOT NULL, " +
                "`mute_duration` VARCHAR(20) NULL, " +
                "`unmute_date` DATETIME NULL, " +
                "PRIMARY KEY (`mute_id`));";

            s.executeUpdate(sql);

            System.out.println("[CBP] Tables successfully set.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cleans cbp.players table of offline players and those without bans and mutes.
     */
    public static void clearAccounts() {
        try {

            // Clear players except for those with existing bans and mutes.

            SqlCache.savePlayerCache();

            String online = "(";
            for (Player p : Bukkit.getOnlinePlayers()) {
                online = online.concat("'" + p.getName() + "', ");
            }

            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                online = online.substring(0, online.length() - 2).concat(")");
                online = "AND player_ign NOT IN " + online;
            } else
                online = "";

            sql = "DELETE FROM cbp.players " + "WHERE is_muted = 'False' AND is_banned = 'False' " + online;

            s.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the player's latest ban type in cbp.active_bans.
     */
    public static String getBanType(String ign) {
        // assuming ign is banned
        try {

            String uuid = m.getUuid(ign);

            sql = String.format("SELECT ban_type FROM cbp.active_bans WHERE player_uuid = '%s' ORDER BY ban_id DESC",
                    uuid);

            rs = s.executeQuery(sql);

            while (rs.next()) {
                if (!rs.getString("ban_type").endsWith("IP"))
                    break;
            }

            return rs.getString("ban_type");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets player to cbp.active_bans with the required values set. If ban is
     * already in cbp.active_bans, overwrite that record. Otherwise, adds a new
     * record for the player.
     */
    public static void setBan(String targetUuid, CommandSender sender, String reason, String duration) {
        try {

            String cDate = formatter.format(new Date());
            String unbanDate = null;
            String banType = "Perm Ban";
            if (!duration.equalsIgnoreCase("Permanent")) {
                unbanDate = "'" + m.calculateUnpunishDate(duration) + "'";
                banType = "Temp Ban";
            }

            reason = reason.replace("'", "''");

            String ign = Bukkit.getOfflinePlayer(UUID.fromString(targetUuid)).getName();
            String ip = m.getSqlIp(ign);
            String bannerUuid = "CONSOLE";
            if(sender instanceof Player) bannerUuid = m.getUuid(sender);

            if (!SqlCache.isPlayerBanned(ign))
                sql = String.format(
                        "INSERT INTO `cbp`.`active_bans` (`ban_type`, `player_uuid`, `banner_uuid`, `ban_reason`, `ban_date`, `ban_duration`, `unban_date`, banned_ip) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s, '%s');",
                        banType, targetUuid, bannerUuid, reason, cDate, duration, unbanDate, ip);
            else
                sql = String.format(
                        "UPDATE `cbp`.`active_bans` " + "SET `ban_type` = '%s', " + "`banner_uuid` = '%s', "
                                + "`ban_reason` = '%s', " + "`ban_date` = '%s', " + "`ban_duration` = '%s', " + "banned_ip = '%s', "
                                + "`unban_date` = %s " + "WHERE player_uuid = '%s'",
                        banType, bannerUuid, reason, cDate, duration, ip, unbanDate, targetUuid);

            s.executeUpdate(sql);

            SqlCache.setBan(ign);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all values from a player's latest active ban in cbp.active_bans.
     * 
     * @param ign
     * @param banType
     * @return
     */
    public static String[] getActiveBanDetails(String ign, String banType) {
        try {

            String uuid = m.getUuid(ign);

            sql = String.format(
                    "SELECT * FROM cbp.active_bans WHERE ban_type = '%s' AND  player_uuid = '%s' ORDER BY ban_id DESC",
                    banType, uuid);

            rs = s.executeQuery(sql);

            rs.next();

            String[] out = new String[5];
            out[0] = "CONSOLE";
            if(!rs.getString("banner_uuid").equalsIgnoreCase("CONSOLE")) out[0] = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("banner_uuid"))).getName();
            out[1] = rs.getString("ban_reason");
            out[2] = rs.getString("ban_duration");
            out[3] = null;
            if(!rs.getString("ban_type").contains("Perm")) out[3] = m.getTimeRemaining(rs.getString("unban_date"));
            out[4] = rs.getString("unban_date");

            return out;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds IP to cbp.active_bans with the required values set.
     * <p>
     * If IP is already in
     * cbp.active_bans, overwite that record. Otherwise, create a new one.
     */
    public static void setIpBan(String targetIp, String bannerUuid, String reason, String duration) {
        try {

            String cDate = formatter.format(new Date());
            String unbanDate = null;
            String banType = "Perm IP Ban IP";
            if (!duration.equalsIgnoreCase("Permanent")) {
                unbanDate = "'" + m.calculateUnpunishDate(duration) + "'";
                banType = "Temp IP Ban IP";
            }

            reason = reason.replace("'", "''");

            if (!SqlCache.isIpBanned(targetIp))
                sql = String.format(
                        "INSERT INTO `cbp`.`active_bans` (`ban_type`, `banned_ip`, `banner_uuid`, `ban_reason`, `ban_date`, `ban_duration`, `unban_date`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s);",
                        banType, targetIp, bannerUuid, reason, cDate, duration, unbanDate);
            else
                sql = String.format(
                        "UPDATE `cbp`.`active_bans` " + "SET `ban_type` = '%s', " + "`banner_uuid` = '%s', "
                                + "`ban_reason` = '%s', " + "`ban_date` = '%s', " + "`ban_duration` = '%s', "
                                + "`unban_date` = %s " + "WHERE banned_ip = '%s' AND player_uuid IS NULL",
                        banType, bannerUuid, reason, cDate, duration, unbanDate, targetIp);

            s.executeUpdate(sql);

            SqlCache.setIpBan(targetIp);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns details from the IP's record in cbp.active_bans.
     */
    public static String[] getIpBanDetails(String ip) {
        String[] out = new String[5];
        try {

            String banType = SqlMethods.getIpBanType(ip, "ip");

            sql = String.format(
                    "SELECT * FROM cbp.active_bans WHERE ban_type = '%s' AND  banned_ip = '%s' ORDER BY ban_id DESC",
                    banType, ip);

            rs = s.executeQuery(sql);

            rs.next();

            out[0] = "CONSOLE";
            if(!rs.getString("banner_uuid").equalsIgnoreCase("CONSOLE")) out[0] = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("banner_uuid"))).getName();
            out[1] = rs.getString("ban_reason");
            out[2] = rs.getString("ban_duration");
            out[3] = null;
            if(!rs.getString("ban_type").contains("Perm")) out[3] = m.getTimeRemaining(rs.getString("unban_date"));
            out[4] = rs.getString("unban_date");

            return out;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Gets the Ban Type of an IP in cbp.active_bans.
     * <p>
     * Returns: 
     * <p>
     * Temp IP Ban or Perm IP Ban if option == player
     * Temp IP Ban IP or Perm IP Ban IP if option == ip
     */
    public static String getIpBanType(String ip, String option) {
        try {

            sql = String.format("SELECT ban_type FROM cbp.active_bans WHERE banned_ip = '%s' AND ban_type LIKE '%%IP'", ip);

            rs = s.executeQuery(sql);

            rs.next();

            if(option.equalsIgnoreCase("ip")) return rs.getString("ban_type");

            if (rs.getString("ban_type").equalsIgnoreCase("Temp IP Ban IP"))
                return "Temp IP Ban";
            else
                return "Perm IP Ban";

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Bans player using IP's record values in cbp.active_bans.
     * <p>
     * Used for when a new player enters the server and IP is banned.
     * 
     * @param target
     */
    public static void banPlayer(String target) {
        try {

            String ip = m.getSqlIp(target);
            String banType = SqlMethods.getIpBanType(ip, "player");
            String uuid = m.getUuid(target);

            if(!SqlCache.isPlayerBanned(target))
                sql = String.format(
                    "INSERT INTO cbp.active_bans (ban_type, player_uuid, banned_ip, banner_uuid, ban_reason, ban_date, ban_duration, unban_date) " +
                    "SELECT '%s' AS 'ban_type', '%s' AS player_uuid, banned_ip, banner_uuid, ban_reason, ban_date, ban_duration, unban_date " +
                    "FROM cbp.active_bans " +
                    "WHERE banned_ip = '%s' AND ban_type LIKE '%%IP'", banType, uuid, ip);
            else
                sql = String.format("UPDATE cbp.active_bans pl " +
                                    "INNER JOIN cbp.active_bans ip ON pl.banned_ip = ip.banned_ip " +
                                    "SET pl.ban_type = '%s', " +
                                    "pl.banner_uuid = ip.banner_uuid, " +
                                    "pl.ban_reason = ip.ban_reason, " +
                                    "pl.ban_date = ip.ban_date, " +
                                    "pl.ban_duration = ip.ban_duration, " +
                                    "pl.unban_date = ip.unban_date " +
                                    "WHERE pl.player_uuid = '%s' AND ip.ban_type LIKE '%%IP'", banType, uuid);


            s.executeUpdate(sql);

            SqlCache.setBan(target);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the unban date of ign's ban.
     * @param ign
     * @return
     */
    public static String getUnbanDate(String ign){
        try{

            String uuid = m.getUuid(ign);
    
            sql = String.format("SELECT unban_date, ban_duration " +
                                "FROM cbp.active_bans " +
                                "WHERE player_uuid = '%s'", uuid);

            rs = s.executeQuery(sql);

            rs.next();

            if(!rs.getString("ban_duration").equalsIgnoreCase("Permanent"))
                return rs.getString("unban_date");
            else
                return "None";
    
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the unmute date of ign's unmute.
     * @param ign
     * @return
     */
    public static String getUnmuteDate(String ign){
        try{

            String uuid = m.getUuid(ign);
    
            sql = String.format("SELECT unmute_date, mute_duration " +
                                "FROM cbp.active_mutes " +
                                "WHERE player_uuid = '%s'", uuid);

            rs = s.executeQuery(sql);

            rs.next();

            if(!rs.getString("mute_duration").equalsIgnoreCase("Permanent"))
                return rs.getString("unmute_date");
            else
                return "None";
    
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the unban date of ip's ban.
     * @param ign
     * @return
     */
    public static String getUnbanDateIp(String ip){
        try{

            sql = String.format("SELECT unban_date, ban_duration " +
                                "FROM cbp.active_bans " +
                                "WHERE banned_ip = '%s' AND ban_type LIKE '%%IP'", ip);

            rs = s.executeQuery(sql);

            rs.next();

            if(!rs.getString("ban_duration").equalsIgnoreCase("Permanent"))
                return rs.getString("unban_date");
            else
                return "None";
    
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds ign's latest punishment (type) to cbp.player_histories.
     * <p>
     * type = Ban, Mute, Warn, Kick
     * <p>
     * sender and reason is null unless type is Kick.
     */
    public static void addHistory(String ign, String type, CommandSender sender, String reason){
        try{

            Map<String, String> values = new HashMap<String, String>();
            String uuid = "";
            String punishType = "";
            String stafferUuid = "";
            String punishDate = "";
            String duration = "";
            String unpunishDate = "";
            String status = "";

            switch(type){

                case "Ban":
                    values = SqlMethods.getBanDetails(ign);
                    uuid = values.get("player_uuid");
                    punishType = values.get("ban_type");
                    stafferUuid = values.get("banner_uuid");
                    reason = values.get("ban_reason");
                    punishDate = values.get("ban_date");
                    duration = values.get("ban_duration");
                    status = "Active";
                    if(values.get("unban_date") == null)
                        unpunishDate = values.get("unban_date");
                    else
                        unpunishDate = "'" + values.get("unban_date") + "'";
                    break;

                case "Mute":
                    values = SqlMethods.getMuteDetails(ign);
                    uuid = values.get("player_uuid");
                    punishType = values.get("mute_type");
                    stafferUuid = values.get("muter_uuid");
                    reason = values.get("mute_reason");
                    punishDate = values.get("mute_date");
                    duration = values.get("mute_duration");
                    status = "Active";
                    if(values.get("unmute_date") == null)
                        unpunishDate = values.get("unmute_date");
                    else
                        unpunishDate = "'" + values.get("unmute_date") + "'";
                    break;

                case "Kick":
                    uuid = m.getUuid(ign);
                    punishType = "Kick";
                    stafferUuid = "CONSOLE";
                    if(sender instanceof Player) stafferUuid = m.getUuid(sender);
                    punishDate = formatter.format(new Date());
                    duration = "None";
                    status = "Kick";
                    unpunishDate = null;
                    break;

                case "Warn":
                    uuid = m.getUuid(ign);
                    punishType = "Warn";
                    stafferUuid = "CONSOLE";
                    if(sender instanceof Player) stafferUuid = m.getUuid(sender);
                    punishDate = formatter.format(new Date());
                    duration = "None";
                    status = "Warn";
                    unpunishDate = null;
                    break;

            }

            reason = reason.replace("'", "''");
    
            sql = String.format("INSERT INTO cbp.player_histories (player_uuid, punishment_type, staff_uuid, punishment_reason, punishment_date, punishment_duration, unpunish_date, status) " +
                                "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s, '%s')", uuid, punishType, stafferUuid, reason, punishDate, duration, unpunishDate, status);

            s.executeUpdate(sql);
    
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns all values from ign's existing ban.
     * @param ign
     * @return
     */
    public static Map<String, String> getBanDetails(String ign){
        HashMap<String, String> values = new HashMap<String, String>();
        try{
    
            String uuid = m.getUuid(ign);

            sql = String.format("SELECT * FROM cbp.active_bans WHERE player_uuid = '%s'", uuid);

            rs = s.executeQuery(sql);
            rs.next();

            ResultSetMetaData md = rs.getMetaData();

            for(int i = 2;i < 10;i++){
                values.put(md.getColumnName(i), rs.getString(i));
            }

            return values;
    
        }catch (SQLException e){
            e.printStackTrace();
        }
        return values;
    }

    /**
     * Returns all values from ign's existing mute.
     * @param ign
     * @return
     */
    public static Map<String, String> getMuteDetails(String ign){
        HashMap<String, String> values = new HashMap<String, String>();
        try{
    
            String uuid = m.getUuid(ign);

            sql = String.format("SELECT * FROM cbp.active_mutes WHERE player_uuid = '%s'", uuid);

            rs = s.executeQuery(sql);
            rs.next();

            ResultSetMetaData md = rs.getMetaData();

            for(int i = 2;i <= md.getColumnCount();i++){
                values.put(md.getColumnName(i), rs.getString(i));
            }

            return values;
    
        }catch (SQLException e){
            e.printStackTrace();
        }
        return values;
    }

    /**
     * Updates the "Active" record in cbp.player_histories.
     * <p>
     * type = Ban, Mute
     * <p>
     * status = Overwritten, Lifted, Unbanned, Unmuted
     * <p>
     * sender is null if status is Lifted.
     */
    public static void updateHistoryStatus(String ign, String type, String status, CommandSender sender){
        try{
    
            String uuid = m.getUuid(ign);

            sql = String.format("UPDATE cbp.player_histories " +
                                "SET status = '%s' " +
                                "WHERE player_uuid = '%s' AND status = 'Active' AND punishment_type LIKE '%%%s%%'", status, uuid, type);

            s.executeUpdate(sql);

            if(status.equalsIgnoreCase("Lifted")) return;

            String updater = "CONSOLE";
            if(sender instanceof Player) updater = m.getUuid(sender.getName());

            sql = String.format("UPDATE cbp.player_histories " +
                                "SET staff_updater_uuid = '%s' " +
                                "WHERE player_uuid = '%s' AND status IN ('Unbanned', 'Unmuted', 'Overwritten') AND staff_updater_uuid IS NULL", updater, uuid);

            s.executeUpdate(sql);
    
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void mutePlayer(String ign, CommandSender sender, String reason, String duration){
        try{
    
            String cDate = formatter.format(new Date());
            String umDate = null;
            String muteType = "Perm Mute";
            if (!duration.equalsIgnoreCase("Permanent")){
                umDate = "'" + m.calculateUnpunishDate(duration) + "'";
                muteType = "Temp Mute";
            }

            String uuid = m.getUuid(ign);
            reason = reason.replace("'", "''");

            String muterUuid = "CONSOLE";
            if (sender instanceof Player) muterUuid = m.getUuid(sender.getName());

            if (!SqlCache.isPlayerMuted(ign))
                sql = String.format(
                        "INSERT INTO `cbp`.`active_mutes` (`mute_type`, `player_uuid`, `muter_uuid`, `mute_reason`, `mute_date`, `mute_duration`, `unmute_date`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', %s);",
                        muteType, uuid, muterUuid, reason, cDate, duration, umDate);
            else
                sql = String.format(
                        "UPDATE `cbp`.`active_mutes` " + "SET " + "`muter_uuid` = '%s', " + "`mute_type` = '%s', "
                                + "`mute_reason` = '%s', " + "`mute_date` = '%s', " + "`mute_duration` = '%s', "
                                + "`unmute_date` = %s " + "WHERE player_uuid = '%s'",
                        muterUuid, muteType, reason, cDate, duration, umDate, uuid);

            s.executeUpdate(sql);

            SqlCache.setMute(ign);
    
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns true if player has a history in cbp.player_histories.
     * @param ign
     * @return
     */
    public static boolean playerHasHistory(String ign){
        try{
    
            String uuid = m.getUuid(ign);

            sql = "SELECT * FROM cbp.player_histories WHERE player_uuid = '" + uuid + "'";

            rs = s.executeQuery(sql);

            return rs.next();
    
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the list of histories of ign from cbp.player_histories.
     */
    public static String[][] getHistories(String ign){
        try{

            String uuid = m.getUuid(ign);

            sql = String.format("SELECT COUNT(history_id) AS size " +
                                "FROM cbp.player_histories " +
                                "WHERE player_uuid = '%s' " +
                                "ORDER BY history_id DESC ", uuid);

            rs = s.executeQuery(sql);
            rs.next();

            String[][] histories = new String[rs.getInt("size")][9];
    
            sql = String.format("SELECT * " +
                                "FROM cbp.player_histories " +
                                "WHERE player_uuid = '%s' " +
                                "ORDER BY history_id DESC ", uuid);

            rs = s.executeQuery(sql);

            int i = 0;
            while(rs.next()){
                for(int x = 0;x < 9;x++){
                    histories[i][x] = rs.getString(x+2);
                }
                i++;
            }
            
            return histories;
    
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns true if ign is in cbp.players.
     * @param ign
     * @return
     */
    public static boolean isPlayerLogged(String ign){
        try{
    
            String uuid = m.getUuid(ign);

            sql = String.format("SELECT * FROM cbp.players WHERE player_uuid = '%s'", uuid);

            rs = s.executeQuery(sql);

            return rs.next();
    
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}

/*

public static void name(){
    try{

        

    }catch (SQLException e){
        e.printStackTrace();
    }
}

*/