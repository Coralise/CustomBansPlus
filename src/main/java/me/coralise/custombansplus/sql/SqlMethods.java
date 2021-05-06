package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;
import me.coralise.custombansplus.sql.objects.SqlBanned;
import me.coralise.custombansplus.sql.objects.SqlMuted;
import me.coralise.custombansplus.sql.objects.SqlPlayer;

import static java.sql.DriverManager.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SqlMethods {

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    private static final String HOST = m.getConfig().getString("sql.host");
    private static final String PORT = m.getConfig().getString("sql.port");
    private static final String USERNAME = m.getConfig().getString("sql.user");
    private static final String PASSWORD = m.getConfig().getString("sql.pass");
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "?useTimezone=true&serverTimezone=UTC";

    private static Statement s;
    private static String sql;
    private static ResultSet rs;
    private static Connection c;

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
                PreparedStatement ps = c.prepareStatement("CREATE DATABASE cbp");
                ps.execute();
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
                    + "`ban_type` VARCHAR(20) NOT NULL, " + "`player_uuid` VARCHAR(45) UNIQUE NOT NULL, "
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
                "`player_uuid` VARCHAR(45) UNIQUE NOT NULL, " +
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
     * Sets player to cbp.active_bans with the required values set. If ban is
     * already in cbp.active_bans, overwrite that record. Otherwise, adds a new
     * record for the player.
     * @throws SQLException
     */
    public static void setBan(SqlBanned sb) throws SQLException {
        PreparedStatement ps = c.prepareStatement("INSERT INTO cbp.active_bans (ban_type, player_uuid, banner_uuid, ban_reason, ban_date, ban_duration, unban_date, banned_ip)\n"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                + "\nON DUPLICATE KEY UPDATE ban_type = ?, banner_uuid = ?, ban_reason = ?, ban_date = ?, ban_duration = ?, banned_ip = ?, unban_date = ?;");

        ps.setString(1, sb.getBanType().toString());
        ps.setString(2, sb.getUuid().toString());
        ps.setString(3, sb.getBannerUuid());
        ps.setString(4, sb.getReason());
        ps.setString(5, sb.getBanDateString());
        ps.setString(6, sb.getDuration());
        ps.setString(7, sb.getUnbanDateString());
        ps.setString(8, sb.getIp());
        ps.setString(9, sb.getBanType().toString());
        ps.setString(10, sb.getBannerUuid());
        ps.setString(11, sb.getReason());
        ps.setString(12, sb.getBanDateString());
        ps.setString(13, sb.getDuration());
        ps.setString(14, sb.getIp());
        ps.setString(15, sb.getUnbanDateString());
        ps.executeUpdate();
    }

    /**
     * Adds ign's latest punishment (type) to cbp.player_histories.
     * <p>
     * type = Ban, Mute, Warn, Kick
     * <p>
     * sender and reason is null unless type is Kick.
     * @throws SQLException
     */
    public static void addHistory(UUID uuid, String type, CommandSender sender, String reason) throws SQLException{
        String ign = m.getName(uuid.toString());
        String punishType = "";
        String stafferUuid = "";
        String punishDate = "";
        String duration = "";
        String unpunishDate = "";
        String status = "";

        switch(type){

            case "Ban":
                SqlBanned sb = SqlCache.getBannedObject(uuid);
                punishType = sb.getBanType().toString();
                stafferUuid = sb.getBannerUuid();
                reason = sb.getReason();
                punishDate = sb.getBanDateString();
                duration = sb.getDuration();
                status = "Active";
                unpunishDate = sb.getUnbanDateString();
                break;

            case "Mute":
                SqlMuted sm = SqlCache.getMutedObject(uuid);
                punishType = sm.getMuteType().toString();
                stafferUuid = sm.getMuterUuid();
                reason = sm.getReason();
                punishDate = sm.getMuteDateString();
                duration = sm.getDuration();
                status = "Active";
                unpunishDate = sm.getUnmuteDateString();
                break;

            case "Kick":
                uuid = m.getUuid(ign);
                punishType = "Kick";
                stafferUuid = "CONSOLE";
                if(sender instanceof Player) stafferUuid = m.getUuid(sender).toString();
                punishDate = formatter.format(new Date());
                duration = "None";
                status = "Kick";
                unpunishDate = null;
                break;

            case "Warn":
                uuid = m.getUuid(ign);
                punishType = "Warn";
                stafferUuid = "CONSOLE";
                if(sender instanceof Player) stafferUuid = m.getUuid(sender).toString();
                punishDate = formatter.format(new Date());
                duration = "None";
                status = "Warn";
                unpunishDate = null;
                break;

        }

        PreparedStatement ps = c.prepareStatement("INSERT INTO cbp.player_histories (player_uuid, punishment_type, staff_uuid, punishment_reason, punishment_date, punishment_duration, unpunish_date, status)\n"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

        ps.setString(1, uuid.toString());
        ps.setString(2, punishType);
        ps.setString(3, stafferUuid);
        ps.setString(4, reason);
        ps.setString(5, punishDate);
        ps.setString(6, duration);
        ps.setString(7, unpunishDate);
        ps.setString(8, status);
        ps.executeUpdate();
    }

    /**
     * Updates the "Active" record in cbp.player_histories.
     * <p>
     * type = Ban, Mute
     * <p>
     * status = Overwritten, Lifted, Unbanned, Unmuted
     * <p>
     * sender is null if status is Lifted.
     * @throws SQLException
     */
    public static void updateHistoryStatus(UUID uuid, String type, String status, CommandSender sender) throws SQLException{

        PreparedStatement ps = c.prepareStatement("UPDATE cbp.player_histories\n"
                    + "SET status = ?\n"
                    + "WHERE player_uuid = ? AND status = 'Active' AND punishment_type LIKE ?");

            ps.setString(1, status);
            ps.setString(2, uuid.toString());
            ps.setString(3, "%" + type + "%");
            ps.executeUpdate();

            if(status.equalsIgnoreCase("Lifted")) return;

            String updater = "CONSOLE";
            if(sender instanceof Player) updater = m.getUuid(sender).toString();

            ps = c.prepareStatement("UPDATE cbp.player_histories\n"
                    + "SET staff_updater_uuid = ?\n"
                    + "WHERE player_uuid = ? AND status IN ('Unbanned', 'Unmuted', 'Overwritten') AND staff_updater_uuid IS NULL");

            ps.setString(1, updater);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
    }

    /**
     * Returns true if player has a history in cbp.player_histories.
     * @param ign
     * @return
     * @throws SQLException
     */
    public static boolean playerHasHistory(UUID uuid) throws SQLException{
        PreparedStatement ps = c.prepareStatement("SELECT * FROM cbp.player_histories WHERE player_uuid = ?;");

        ps.setString(1, uuid.toString());
        rs = ps.executeQuery();

        return rs.next();
    }

    /**
     * Returns the list of histories of ign from cbp.player_histories.
     * @throws SQLException
     */
    public static String[][] getHistories(UUID uuid) throws SQLException{

        PreparedStatement ps = c.prepareStatement("SELECT COUNT(history_id) AS size\n" +
                "FROM cbp.player_histories\n" +
                "WHERE player_uuid = ?\n" +
                "ORDER BY history_id DESC;");

        ps.setString(1, uuid.toString());
        rs = ps.executeQuery();
        rs.next();

        String[][] histories = new String[rs.getInt("size")][9];

        ps = c.prepareStatement("SELECT *\n" +
                "FROM cbp.player_histories\n" +
                "WHERE player_uuid = ?\n" +
                "ORDER BY history_id DESC;");

        ps.setString(1, uuid.toString());

        rs = ps.executeQuery();

        int i = 0;
        while(rs.next()){
            for(int x = 0;x < 9;x++){
                histories[i][x] = rs.getString(x+2);
            }
            i++;
        }
        
        return histories;
    }

    /**
     * Returns true if ign is in cbp.players.
     * @param ign
     * @return
     * @throws SQLException
     */
    public static boolean isPlayerLogged(UUID uuid) throws SQLException{
        PreparedStatement ps = c.prepareStatement("SELECT * FROM cbp.players WHERE player_uuid = ?");
        ps.setString(1, uuid.toString());
        rs = ps.executeQuery();
        return rs.next();
    }

    public static void setPlayer(SqlPlayer sp) throws SQLException {
        if (!isPlayerLogged(sp.getUuid())) {
            PreparedStatement ps = c.prepareStatement("INSERT INTO cbp.players (player_uuid, player_ign, player_ip, join_date)"
                    + "\nVALUES (?, ?, ?, ?);");

            ps.setString(1, sp.getUuid().toString());
            ps.setString(2, sp.getUsername());
            ps.setString(3, sp.getIp());
            ps.setString(4, sp.getJoinDateString());
            ps.executeUpdate();
        } else {
            PreparedStatement ps = c.prepareStatement("UPDATE cbp.players"
                    + "\nSET player_ign = ?, player_ip = ?"
                    + "\nWHERE player_uuid = ?;");
                    
            ps.setString(1, sp.getUsername());
            ps.setString(2, sp.getIp());
            ps.setString(3, sp.getUuid().toString());
            ps.executeUpdate();
        }
    }

    public static void loadPlayerCache() throws SQLException {

        PreparedStatement ps = c.prepareStatement("SELECT * FROM cbp.players;");

        rs = ps.executeQuery();

        while (rs.next()) {
            SqlCache.loadPlayer(UUID.fromString(rs.getString("player_uuid")), rs.getString("player_ip"));
        }

    }

    public static void loadBanCache() throws SQLException {

        PreparedStatement ps = c.prepareStatement("SELECT * FROM cbp.active_bans;");

        rs = ps.executeQuery();

        while (rs.next()) {
            if (rs.getString("player_uuid") == null) continue;
            if (!rs.getString("banner_uuid").equalsIgnoreCase("CONSOLE"))
                SqlCache.setBan(UUID.fromString(rs.getString("player_uuid")), rs.getString("banned_ip"), m.getBanTypeFromString(rs.getString("ban_type")), rs.getString("ban_reason"), rs.getString("ban_duration"));
            else
                SqlCache.setBan(UUID.fromString(rs.getString("player_uuid")), rs.getString("banned_ip"), m.getBanTypeFromString(rs.getString("ban_type")), rs.getString("ban_reason"), rs.getString("ban_duration"), m.getUuid(rs.getString("banner_uuid")));
        }

    }

    public static void loadMuteCache() throws SQLException {

        PreparedStatement ps = c.prepareStatement("SELECT * FROM cbp.active_mutes;");

        rs = ps.executeQuery();

        while (rs.next()) {
            if (!rs.getString("muter_uuid").equalsIgnoreCase("CONSOLE"))
                SqlCache.setMute(UUID.fromString(rs.getString("player_uuid")), m.getMuteTypeFromString(rs.getString("mute_type")), rs.getString("mute_reason"), rs.getString("mute_duration"));
            else
                SqlCache.setMute(UUID.fromString(rs.getString("player_uuid")), m.getMuteTypeFromString(rs.getString("mute_type")), rs.getString("mute_reason"), rs.getString("mute_duration"), m.getUuid(rs.getString("muter_uuid")));
        }

    }

    public static void removeBan(UUID uuid) throws SQLException {
        PreparedStatement ps = c.prepareStatement("DELETE FROM cbp.active_bans WHERE player_uuid = ?");

        ps.setString(1, uuid.toString());
        ps.executeUpdate();
    }

    public static void setNewPlayers(Set<SqlPlayer> collect) throws SQLException {

        PreparedStatement ps = c.prepareStatement("INSERT INTO cbp.players (player_uuid, player_ign, player_ip, join_date)"
                + "\nVALUES (?, ?, ?, ?)"
                + "\nON DUPLICATE KEY UPDATE player_ign = ?, player_ip = ?");

        for (SqlPlayer sp : collect) {
            ps.setString(1, sp.getUuid().toString());
            ps.setString(2, sp.getUsername());
            ps.setString(3, sp.getIp());
            ps.setString(4, sp.getJoinDateString());
            ps.setString(5, sp.getUsername());
            ps.setString(6, sp.getIp());
            ps.executeUpdate();
            sp.setUpdated(false);
        }

    }

    public static void setNewBans(Set<SqlBanned> collect) throws SQLException {

        PreparedStatement ps = c.prepareStatement("INSERT INTO cbp.active_bans (ban_type, player_uuid, banner_uuid, ban_reason, ban_date, ban_duration, unban_date, banned_ip)\n"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                + "\nON DUPLICATE KEY UPDATE ban_type = ?, banner_uuid = ?, ban_reason = ?, ban_date = ?, ban_duration = ?, unban_date = ?, banned_ip = ?");

        for (SqlBanned sb : collect) {
            ps.setString(1, sb.getBanType().toString());
            ps.setString(2, sb.getUuid().toString());
            ps.setString(3, sb.getBannerUuid());
            ps.setString(4, sb.getReason());
            ps.setString(5, sb.getBanDateString());
            ps.setString(6, sb.getDuration());
            ps.setString(7, sb.getUnbanDateString());
            ps.setString(8, sb.getIp());
            ps.setString(9, sb.getBanType().toString());
            ps.setString(10, sb.getBannerUuid());
            ps.setString(11, sb.getReason());
            ps.setString(12, sb.getBanDateString());
            ps.setString(13, sb.getDuration());
            ps.setString(14, sb.getUnbanDateString());
            ps.setString(15, sb.getIp());
            ps.executeUpdate();
            sb.setInDatabase(true);
        }

    }

    public static void setNewMutes(Set<SqlMuted> collect) throws SQLException {

        PreparedStatement ps = c.prepareStatement("INSERT INTO `cbp`.`active_mutes` (`mute_type`, `player_uuid`, `muter_uuid`, `mute_reason`, `mute_date`, `mute_duration`, `unmute_date`)"
                    + "\nVALUES (?, ?, ?, ?, ?, ?, ?)"
                    + "\nON DUPLICATE KEY UPDATE mute_type = ?, muter_uuid = ?, mute_reason = ?, mute_date = ?, mute_duration = ?, unmute_date = ?");

        for (SqlMuted sm : collect) {
            ps.setString(1, sm.getMuteType().toString());
            ps.setString(2, sm.getUuid().toString());
            ps.setString(3, sm.getMuterUuid());
            ps.setString(4, sm.getReason());
            ps.setString(5, sm.getMuteDateString());
            ps.setString(6, sm.getDuration());
            ps.setString(7, sm.getUnmuteDateString());
            ps.setString(8, sm.getMuteType().toString());
            ps.setString(9, sm.getMuterUuid());
            ps.setString(10, sm.getReason());
            ps.setString(11, sm.getMuteDateString());
            ps.setString(12, sm.getDuration());
            ps.setString(13, sm.getUnmuteDateString());
            ps.executeUpdate();
            sm.setInDatabase(true);
        }
        
    }

    public static void removeMute(UUID uuid) throws SQLException {
        PreparedStatement ps = c.prepareStatement("DELETE FROM cbp.active_mutes WHERE player_uuid = ?");

        ps.setString(1, uuid.toString());
        ps.executeUpdate();
    }

    public static void setMute(SqlMuted sm) throws SQLException {
        PreparedStatement ps = c.prepareStatement("INSERT INTO `cbp`.`active_mutes` (`mute_type`, `player_uuid`, `muter_uuid`, `mute_reason`, `mute_date`, `mute_duration`, `unmute_date`)"
                + "\nVALUES (?, ?, ?, ?, ?, ?, ?)"
                + "\nON DUPLICATE KEY UPDATE mute_type = ?, muter_uuid = ?, mute_reason = ?, mute_date = ?, mute_duration = ?, unmute_date = ?");

        ps.setString(1, sm.getMuteType().toString());
        ps.setString(2, sm.getUuid().toString());
        ps.setString(3, sm.getMuterUuid());
        ps.setString(4, sm.getReason());
        ps.setString(5, sm.getMuteDateString());
        ps.setString(6, sm.getDuration());
        ps.setString(7, sm.getUnmuteDateString());
        ps.setString(8, sm.getMuteType().toString());
        ps.setString(9, sm.getMuterUuid());
        ps.setString(10, sm.getReason());
        ps.setString(11, sm.getMuteDateString());
        ps.setString(12, sm.getDuration());
        ps.setString(13, sm.getUnmuteDateString());
        ps.executeUpdate();
    }

    public static void clearPlayerCache() throws SQLException {
        PreparedStatement ps = c.prepareStatement("TRUNCATE cbp.players;");
        ps.execute();
    }

}

/*

public static void name() throws SQLException {

}

*/