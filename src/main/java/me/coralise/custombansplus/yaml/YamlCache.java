package me.coralise.custombansplus.yaml;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.GetJavaPlugin;

import java.io.IOException;
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

public class YamlCache {

    private static ConcurrentHashMap<String, String> newPlayerCache = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, String> playerCache = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, String> playerIpCache = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, String> banCache = new ConcurrentHashMap<String, String>();
    private static ConcurrentHashMap<String, String> muteCache = new ConcurrentHashMap<String, String>();
    private static HashSet<String> ociCache = new HashSet<String>();

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // newPlayerCache: <uuid, type>
    // playerCache: <uuid, ign>
    // playerIpCache: <uuid, ip>
    // banCache: <uuid, unbanDate>
    // muteCache: <uuid, unmuteDate>

    // AltsConfig
    // ip:
    //  uuid

    /**
     * Saves current player caches to data.
     */
    private static void savePlayerCache(){
        if(m.getConfig().getBoolean("notify-cache-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin"))
                    p.sendMessage("§e[CBP] §fSaving player cache...");
            });
        long startTime = System.currentTimeMillis();

        newPlayerCache.keySet().forEach(uuid -> {
            if(newPlayerCache.get(uuid).endsWith(" ip")){
                List<String> uuids = m.getAltsConfig().getStringList(playerIpCache.get(uuid));
                uuids.remove(uuid);
                m.getAltsConfig().set(playerIpCache.get(uuid), uuids);
            }
            List<String> uuids = m.getAltsConfig().getStringList(playerIpCache.get(uuid));
            uuids.add(uuid);
            m.getAltsConfig().set(playerIpCache.get(uuid), uuids);
        });

        newPlayerCache.clear();

        try {
            m.getAltsConfig().save(m.getAltsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        
        for(String ip : m.getAltsConfig().getKeys(false))
            for(String uuid : m.getAltsConfig().getStringList(ip))
                playerCache.put(uuid, m.getName(uuid));

        for(String ip : m.getAltsConfig().getKeys(false))
            for(String uuid : m.getAltsConfig().getStringList(ip))
                playerIpCache.put(uuid, ip);
        
        for(String uuid : m.getBansConfig().getKeys(false)){
                banCache.put(uuid, m.getBansConfig().getString(uuid+".unban-date"));
        }

        for(String muteUuid : m.getMutesConfig().getKeys(false)){
            muteCache.put(muteUuid, m.getMutesConfig().getString(muteUuid+".unmute-by"));
        }

        for(String uuid : m.getOciConfig().getStringList("offline-ci")){
            ociCache.add(uuid);
        }

    }

    /**
     * Checks if ign's logged IP is different from current IP.
     * @param ign
     * @param currentIp
     * @return
     */
    public static boolean isIpDifferent(String uuid, String currentIp){
        String loggedIp = playerIpCache.get(uuid);
        return (!loggedIp.equalsIgnoreCase(currentIp));
    }

     /**
     * Checks if uuid's logged IGN is different from current IGN.
     * @param ign
     * @return
     */
    public static boolean isIgnDifferent(String uuid){
        String loggedIgn = playerCache.get(uuid);
        return (!loggedIgn.equalsIgnoreCase(m.getName(uuid)));
    }

    /**
     * Checks if player <code>ign</code> is in player cache.
     * @param ign
     * @return
     */
    public static boolean isPlayerLogged(String uuid){
        return playerCache.containsKey(uuid);
    }

    /**
     * Checks if player <code>uuid</code> is in ban cache.
     * @param ign
     * @return True if player is in cache. False if not.
     */
    public static boolean isPlayerBanned(String uuid){
        return banCache.containsKey(uuid);
    }

    /**
     * Checks if player <code>ip</code> is in ban cache.
     * @param ign
     * @return True if ip is in cache. False if not.
     */
    public static boolean isIpBanned(String ip){
        return banCache.containsKey(ip);
    }

    /**
     * Checks if player <code>ign</code> is in mute cache.
     * @param ign
     * @return True if player is in cache. False if not.
     */
    public static boolean isPlayerMuted(String uuid){
        return muteCache.containsKey(uuid);
    }

    /**
     * Checks if cached player data exceeds specified value.
     * <p>
     * If true, saves cached data to database/ymls.
     */
    private static void checkCaches(){
        int amount = m.getConfig().getInt("player-save-cache");
        if(playerCache.size() >= amount) savePlayerCache();
    }

    /**
     * Saves all current caches. Used when plugin is disabled.
     */
    public static void saveCaches(){
        savePlayerCache();
    }

    /**
     * Checks if unban date is before current date.
     */
    public static boolean isBanLifted(String uuid){
        String ubDate = banCache.get(uuid);
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
    public static boolean isMuteLifted(String uuid){
        String umDate = muteCache.get(uuid);
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
     * Removes ban of ign in both cache and yaml file.
     * @param ign
     */
    public static void removeBan(String uuid){
        banCache.remove(uuid);
        m.getBansConfig().set(uuid, null);
        String ip = m.getYamlIp(m.getName(uuid));
        if(YamlCache.isIpBanned(ip)){
            m.getBansConfig().set(ip, null);
            banCache.remove(ip);
        }
        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes mute of ign in both cache and yaml file.
     * @param ign
     */
    public static void removeMute(String uuid){
        muteCache.remove(uuid);
        m.getMutesConfig().set(uuid, null);
        try {
            m.getMutesConfig().save(m.getMutesFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds or updates the new ban to the cache.
     * <p>
     * value = uuid or ip
     * @param ign
     */
    public static void setBan(String value){
        banCache.put(value, m.getBansConfig().getString(value+".unban-date"));
    }

    /**
     * Adds or updates the new mute to the cache.
     * @param ign
     */
    public static void setMute(String uuid){
        muteCache.put(uuid, m.getMutesConfig().getString(uuid+".unmute-by"));
    }

    /**
     * Sets or updates playerIpCache.
     * @param ign
     */
    public static void setPlayerIP(String uuid){
        playerIpCache.put(uuid, m.getYamlIp(m.getName(uuid)));
    }

    /**
     * Sets or updates playerCache.
     * @param ign
     */
    public static void setPlayer(String uuid){
        playerCache.put(uuid, m.getName(uuid));
        checkCaches();
    }

    /**
     * Adds player to the new players cache or updates player's IP.
     * <p>
     * Used for first time enters; not yet in database, still in cache.
     * <p>
     * &lt;IGN, type&gt;
     * <p>
     * type = new, ip, ign
     */
    public static void setNewPlayer(String uuid, String type){
        if(type.equalsIgnoreCase("new")){
            newPlayerCache.put(uuid, "new");
        }else if(type.equalsIgnoreCase("ip")){
            String ip = m.getYamlIp(m.getName(uuid));
            newPlayerCache.put(uuid, ip + " ip");
        }
    }

    /**
     * Resets all player caches. Used for purges.
     */
    public static void refreshPlayerCaches(){
        playerCache.clear();
        playerIpCache.clear();
        setupCache();
    }

    /**
     * Prints out all keys and their values in the Bans Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutBanCache(){
        for(String key : banCache.keySet())
            System.out.println(key + " " + banCache.get(key));
    }

    /**
     * Prints out all keys and their values in the Mutes Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutMuteCache(){
        for(String key : muteCache.keySet())
            System.out.println(key + " " + muteCache.get(key));
    }

    /**
     * Prints out all keys and their values in the Players Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutPlayerCache(){
        for(String key : playerCache.keySet())
            System.out.println(key + " " + playerCache.get(key));
    }

    /**
     * Prints out all keys and their values in the Player IPs Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutPlayerIpCache(){
        for(String key : playerIpCache.keySet())
            System.out.println(key + " " + playerIpCache.get(key));
    }

    /**
     * Prints out all keys and their values in the Player IPs Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutNewPlayerCache(){
        for(String key : newPlayerCache.keySet())
            System.out.println(key + " " + newPlayerCache.get(key));
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
                if(YamlCache.isPlayerLogged(m.getUuid(ign)))
                    return ign;
            }
        }
        return null;
    }

    public static String getYamlIp(String uuid){
        return playerIpCache.get(uuid);
    }

    /**
     * Returns the list of igns with similar IPs as <code>ip</code>.
     * @param ip
     * @return
     */
    public static List<String> getSameIps(String ip){
        List<String> igns = new ArrayList<String>();
        for(String uuid : playerIpCache.keySet()){
            if(playerIpCache.get(uuid).equalsIgnoreCase(ip)) igns.add(m.getName(uuid));
        }
        return igns;
    }

    /**
     * Lists the IGNs with the same logged IP.
     * <p> Gray shows offline, green online, and red banned.
     */
    public static String listAlts(String ip){
        String ignList = "";
        for(String ign : YamlCache.getSameIps(ip)){
            if (YamlCache.isPlayerBanned(m.getUuid(ign))) ign = "§c" + ign;
            else if(m.getOfflinePlayer(ign).isOnline()) ign = "§a" + ign;
            else ign = "§7" + ign;
            ignList = ignList.concat(ign + "§f, ");
        }
        ignList = ignList.substring(0, ignList.length()-2);
        return ignList;
    }

    public static Set<String> getOciCache(){
        return ociCache;
    }
    
}