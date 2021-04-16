package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.GetJavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class SqlCache {

    private static ConcurrentHashMap<String, String> newPlayerCache = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, String> playerCache = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, String> playerIpCache = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, String> muteCache = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, String> banCache = new ConcurrentHashMap<String, String>();
    private static HashSet<String> ociCache = new HashSet<String>();

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat joinDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private static Statement s = SqlMethods.s;
    private static String sql;
    private static ResultSet rs = SqlMethods.rs;

    /**
     * Saves current player caches to data.
     */
    public static void savePlayerCache(){
        if(m.getConfig().getBoolean("notify-cache-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin"))
                    p.sendMessage("§e[CBP] §fSaving player cache...");
            });
        long startTime = System.currentTimeMillis();

        newPlayerCache.keySet().forEach(ign -> {
            String newPlayerValue = newPlayerCache.get(ign);
            String uuid = m.getUuid(ign);
            String ip = playerIpCache.get(ign);

            try{

                if(newPlayerValue.contains(".") || newPlayerCache.get("-" + ign) != null)
                    sql = String.format("UPDATE cbp.players " +
                                        "SET player_ip = '%s', player_ign = '%s' " +
                                        "WHERE player_uuid = '%s'", newPlayerValue, ign, uuid);
                else
                    sql = String.format("INSERT INTO cbp.players (`player_uuid`, `player_ign`, `player_ip`, `join_date`) " +
                                        "VALUES ('%s', '%s', '%s', '%s')", uuid, ign, ip, newPlayerValue);

                s.executeUpdate(sql);

            }catch(SQLException e){
                System.out.println("§e[CBP] §fPlayer already logged, ignoring.");
            }
        });

        newPlayerCache.clear();

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        if(m.getConfig().getBoolean("notify-cache-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin")){
                    String stTime = "";
                    if(time <= 201) stTime = "§a" + time + " ms";
                    if(time > 201) stTime = "§e" + time + " ms";
                    if(time > 501) stTime = "§c" + time + " ms";
                    p.sendMessage("§e[CBP] §fCache saved! Took " + stTime + " §fto execute action.");
                }
            });
    }

    /**
     * Sets up cache, used on server startup.
     */
    public static void setupCache(){

        System.out.println("§e[CBP] §fCaching players...");
        try{

            sql = "SELECT player_uuid, player_ign, player_ip FROM cbp.players";

            rs = s.executeQuery(sql);

            while(rs.next()){
                playerCache.put(rs.getString("player_uuid"), rs.getString("player_ign"));
                playerIpCache.put(rs.getString("player_ign"), rs.getString("player_ip"));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        System.out.println("§e[CBP] §fCaching bans...");
        try{

            sql = "SELECT ban_type, player_uuid, banned_ip, unban_date, ban_duration " +
                    "FROM cbp.active_bans ";

            rs = s.executeQuery(sql);

            while(rs.next()){
                if(!rs.getString("ban_duration").equalsIgnoreCase("Permanent"))
                    if(!rs.getString("ban_type").endsWith(" IP"))
                        banCache.put(rs.getString("player_uuid"), rs.getString("unban_date"));
                    else
                        banCache.put(rs.getString("banned_ip"), rs.getString("unban_date"));
                else
                    if(!rs.getString("ban_type").endsWith(" IP"))
                        banCache.put(rs.getString("player_uuid"), "None");
                    else
                        banCache.put(rs.getString("banned_ip"), "None");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        System.out.println("§e[CBP] §fCaching mutes...");
        try{

            sql = "SELECT mute_type, player_uuid, unmute_date, mute_duration " +
                    "FROM cbp.active_mutes ";

            rs = s.executeQuery(sql);

            while(rs.next()){
                if(!rs.getString("mute_duration").equalsIgnoreCase("Permanent"))
                    muteCache.put(rs.getString("player_uuid"), rs.getString("unmute_date"));
                else
                    muteCache.put(rs.getString("player_uuid"), "None");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        m.getOciConfig().getStringList("offline-ci").forEach(ociCache::add);

    }

    /**
     * Checks if player <code>ign</code> is in player cache.
     * @param ign
     * @return
     */
    public static boolean isPlayerLogged(String ign){
        String uuid = m.getUuid(ign);
        return playerCache.containsKey(uuid);
    }

    /**
     * Checks if player <code>ign</code> is in ban cache.
     * @param ign
     * @return True if player is in cache. False if not.
     */
    public static boolean isPlayerBanned(String ign){
        String uuid = m.getUuid(ign);
        return banCache.containsKey(uuid);
    }

    /**
     * Checks if player <code>ign</code> is in mute cache.
     * @param ign
     * @return True if player is in cache. False if not.
     */
    public static boolean isPlayerMuted(String ign){
        String uuid = m.getUuid(ign);
        return muteCache.containsKey(uuid);
    }

    /**
     * Checks if unban date is before current date.
     */
    public static boolean isBanLifted(String ign){
        String uuid = m.getUuid(ign);
        String ubDate = banCache.get(uuid);
        if(ubDate == null) return true;
        if(ubDate.equalsIgnoreCase("None")) return false;
        Date cDate = new Date();
        Date unbanDate = new Date();
        try {
            unbanDate = formatter.parse(ubDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cDate.after(unbanDate);
    }

    /**
     * Checks if unmute date is before current date.
     */
    public static boolean isMuteLifted(String ign){
        String uuid = m.getUuid(ign);
        String umDate = muteCache.get(uuid);
        if(umDate == null) return true;
        if(umDate.equalsIgnoreCase("None")) return false;
        Date cDate = new Date();
        Date unmuteDate = new Date();
        try {
            unmuteDate = formatter.parse(umDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cDate.after(unmuteDate);
    }

    /**
     * Removes ban of ign in both cache and sql database.
     * <p>
     * Does nothing if player isn't banned.
     * <p>
     * Does not include update of player history.
     * @param ign
     */
    public static void removeBan(String ign){
        String uuid = m.getUuid(ign);

        if(!SqlCache.isPlayerBanned(ign)) return;

        banCache.remove(uuid);
        
        try{

            sql = String.format("DELETE cbp.active_bans " +
                                "FROM cbp.active_bans " +
                                "WHERE player_uuid = '%s' ", uuid);

            s.executeUpdate(sql);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Removes ban of ign in both cache and sql database.
     * <p>
     * Does nothing if player isn't banned.
     * <p>
     * Does not include update of player history.
     * @param ign
     */
    public static void removeMute(String ign){
        String uuid = m.getUuid(ign);

        if(!SqlCache.isPlayerMuted(ign)) return;

        muteCache.remove(uuid);
        
        try{

            sql = String.format("DELETE cbp.active_mutes " +
                                "FROM cbp.active_mutes " +
                                "WHERE player_uuid = '%s' ", uuid);

            s.executeUpdate(sql);

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Removes ban of ip in both cache and sql database, as well as the players banned with the same IP.
     * <p>
     * Does nothing if IP isn't banned.
     * <p>
     * status = Lifted, Unbanned.
     * <p>
     * sender is null if status is Lifted.
     * <p>
     * Does not include update of player history.
     * @param ign
     */
    public static void removeIpBan(String ip, String status, CommandSender sender){

        if(!SqlCache.isIpBanned(ip)) return;

        banCache.remove(ip);
        
        try{

            sql = String.format("DELETE cbp.active_bans " +
                                "FROM cbp.active_bans " +
                                "WHERE banned_ip = '%s' AND ban_type LIKE '%%IP' ", ip);

            s.executeUpdate(sql);

            SqlCache.getSameIps(ip).forEach(ign -> {
                if(status.equalsIgnoreCase("Lifted")) SqlMethods.updateHistoryStatus(ign, "Ban", "Lifted", null);
                else SqlMethods.updateHistoryStatus(ign, "Ban", "Unbanned", sender);
                SqlCache.getOciCache().remove(m.getUuid(ign));
                m.updateSqlOci();
                SqlCache.removeBan(ign);
            });

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Checks if ign's logged IP is different from current IP.
     * @param ign
     * @param currentIp
     * @return
     */
    public static boolean isIpDifferent(String ign, String currentIp){
        String loggedIp = playerIpCache.get(ign);
        return (!loggedIp.equalsIgnoreCase(currentIp));
    }

    /**
     * Sets or updates playerCache.
     * <p>
     * If updates, adds to newPlayerValues.
     * <p>
     * playerCache: &lt;UUID, IGN&gt;
     * <p>
     * playerIpCache: &lt;IGN, IP&gt;
     * @param ign
     */
    public static void setPlayer(String ign){
        String uuid = m.getUuid(ign);
        if(playerCache.get(uuid) != null && !playerCache.get(uuid).equalsIgnoreCase(ign)){
            newPlayerCache.put("-" + ign, "ign");
            playerIpCache.remove(playerCache.get(uuid));
        }
        playerCache.put(uuid, ign);
        playerIpCache.put(ign, m.getSqlIp(ign));
        checkCaches();
    }

    /**
     * Checks if cached player data exceeds specified value.
     * <p>
     * If true, saves cached data to database.
     */
    private static void checkCaches(){
        int amount = m.getConfig().getInt("player-save-cache");
        if(newPlayerCache.size() == amount) savePlayerCache();
    }

    /**
     * Adds player to the new players cache or updates player's IP.
     * <p>
     * Used for first time enters; not yet in database, still in cache.
     * <p>
     * type == "new" if new, type == "ip" if IP update.
     * <p>
     * &lt;IGN, type&gt;
     * <p>
     * &lt;-IGN, "ign"&gt;
     */
    public static void setNewPlayer(String ign, String type){
        if(type.equalsIgnoreCase("new")){
            String joinDate = joinDateFormatter.format(new Date());
            newPlayerCache.put(ign, joinDate);
        }else{
            String ip = m.getSqlIp(ign);
            newPlayerCache.put(ign, ip);
        }
    }

    /**
     * Checks if ign's IP is in ban cache.
     * <p>
     * True if yes, else false.
     * @param ign
     * @return
     */
    public static boolean isIpBanned(String ip){
        if(ip == null) return false;
        return banCache.containsKey(ip);
    }

    /**
     * Returns the proper casing version of the ign.
     * <p>
     * Returns null if player has never entered the server.
     * @param ign
     * @return
     */
    public static String getPlayerIgn(String ign){
        for(OfflinePlayer p : Bukkit.getOfflinePlayers()){
            if(ign.equalsIgnoreCase(p.getName())){
                ign = p.getName();
                return ign;
            }
        }
        return null;
    }

    /**
     * Returns the list of igns with similar IPs as <code>ip</code>.
     * @param ip
     * @return
     */
    public static List<String> getSameIps(String ip){
        List<String> igns = new ArrayList<String>();
        playerIpCache.keySet().forEach(ign -> {
            if(playerIpCache.get(ign).equalsIgnoreCase(ip)) igns.add(ign);
        });
        return igns;
    }

    /**
     * Adds or updates the new ban to the cache.
     * <p>
     * &lt;UUID/IP, Unban Date&gt;
     * @param ign
     */
    public static void setBan(String ign){
        String uuid = m.getUuid(ign);
        banCache.put(uuid, SqlMethods.getUnbanDate(ign));
    }

    /**
     * Adds or updates the new mute to the cache.
     * <p>
     * &lt;UUID/IP, Unban Date&gt;
     * @param ign
     */
    public static void setMute(String ign){
        String uuid = m.getUuid(ign);
        muteCache.put(uuid, SqlMethods.getUnmuteDate(ign));
    }

    /**
     * Adds or updates the new ip to the cache.
     * <p>
     * &lt;UUID/IP, Unban Date&gt;
     * @param ign
     */
    public static void setIpBan(String ip){
        banCache.put(ip, SqlMethods.getUnbanDateIp(ip));
    }

    /**
     * Checks if ign is in New Player Cache, meaning not yet in database.
     * <p>
     * Returns true if yes, else false.
     */
    public static boolean isPlayerNew(String ign){
        return newPlayerCache.containsKey(ign);
    }

    /**
     * Saves caches.
     */
    public static void saveCaches(){
        savePlayerCache();
    }

    /**
     * Prints out all keys and their values in the Bans Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutBanCache(){
        banCache.keySet().forEach(key -> System.out.println(key + " " + banCache.get(key)));
    }

    /**
     * Prints out all keys and their values in the Mutes Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutMuteCache(){
        muteCache.keySet().forEach(key -> System.out.println(key + " " + muteCache.get(key)));
    }

    /**
     * Prints out all keys and their values in the Players Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutPlayerCache(){
        playerCache.keySet().forEach(key -> System.out.println(key + " " + playerCache.get(key)));
    }

    /**
     * Prints out all keys and their values in the New Players Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutNewPlayerCache(){
        newPlayerCache.keySet().forEach(key -> System.out.println(key + " " + newPlayerCache.get(key)));
    }

    /**
     * Prints out all keys and their values in the Player IPs Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutPlayerIpCache(){
        playerIpCache.keySet().forEach(key -> System.out.println(key + " " + playerIpCache.get(key)));
    }

    public static String getSqlIp(String ign){
        return playerIpCache.get(ign);
    }

    public static Set<String> getOciCache(){
        return ociCache;
    }
    
}
