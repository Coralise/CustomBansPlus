package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;
import me.coralise.custombansplus.enums.BanType;
import me.coralise.custombansplus.enums.MuteType;
import me.coralise.custombansplus.sql.objects.SqlBanned;
import me.coralise.custombansplus.sql.objects.SqlMuted;
import me.coralise.custombansplus.sql.objects.SqlPlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class SqlCache {

     static ConcurrentHashMap<UUID, SqlPlayer> playerCache = new ConcurrentHashMap<UUID, SqlPlayer>();
     static ConcurrentHashMap<UUID, SqlBanned> banCache = new ConcurrentHashMap<UUID, SqlBanned>();
     static ConcurrentHashMap<UUID, SqlMuted> muteCache = new ConcurrentHashMap<UUID, SqlMuted>();
     static HashSet<UUID> ociCache = new HashSet<UUID>();

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    /**
     * Saves current player caches to data.
     */
    public static void savePlayerCache(){
        if(m.getConfig().getBoolean("cache.notify-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin"))
                    p.sendMessage("§e[CBP] §fSaving player cache...");
            });
        long startTime = System.currentTimeMillis();

        try {
            SqlMethods.setNewPlayers(playerCache.values().stream().filter(sp -> sp.isUpdated()).collect(Collectors.toSet()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        if(m.getConfig().getBoolean("cache.notify-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin")){
                    String stTime = "";
                    if(time <= 201) stTime = "§a" + time + " ms";
                    if(time > 201) stTime = "§e" + time + " ms";
                    if(time > 501) stTime = "§c" + time + " ms";
                    p.sendMessage("§e[CBP] §fPlayer Cache saved! Took " + stTime + " §fto execute action.");
                }
            });
    }

    /**
     * Saves current ban caches to data.
     */
    public static void saveBanCache(){
        if(m.getConfig().getBoolean("cache.notify-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin"))
                    p.sendMessage("§e[CBP] §fSaving player cache...");
            });
        long startTime = System.currentTimeMillis();

        try {
            SqlMethods.setNewBans(banCache.values().stream().filter(sp -> !sp.isInDatabase()).collect(Collectors.toSet()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        if(m.getConfig().getBoolean("cache.notify-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin")){
                    String stTime = "";
                    if(time <= 201) stTime = "§a" + time + " ms";
                    if(time > 201) stTime = "§e" + time + " ms";
                    if(time > 501) stTime = "§c" + time + " ms";
                    p.sendMessage("§e[CBP] §fBan Cache saved! Took " + stTime + " §fto execute action.");
                }
            });
    }

    /**
     * Saves current mute caches to data.
     */
    public static void saveMuteCache(){
        if(m.getConfig().getBoolean("cache.notify-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin"))
                    p.sendMessage("§e[CBP] §fSaving player cache...");
            });
        long startTime = System.currentTimeMillis();

        try {
            SqlMethods.setNewMutes(muteCache.values().stream().filter(sm -> !sm.isInDatabase()).collect(Collectors.toSet()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        if(m.getConfig().getBoolean("cache.notify-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin")){
                    String stTime = "";
                    if(time <= 201) stTime = "§a" + time + " ms";
                    if(time > 201) stTime = "§e" + time + " ms";
                    if(time > 501) stTime = "§c" + time + " ms";
                    p.sendMessage("§e[CBP] §fMute Cache saved! Took " + stTime + " §fto execute action.");
                }
            });
    }

    /**
     * Sets up cache, used on server startup.
     */
    public static void setupCache(){

        try {
            SqlMethods.loadPlayerCache();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(String strUuid : m.getOciConfig().getStringList("offline-ci")){
            ociCache.add(UUID.fromString(strUuid));
        }

    }

    /**
     * Checks if ign's logged IP is different from current IP.
     * @param ign
     * @param currentIp
     * @return
     */
    public static boolean isIpDifferent(UUID uuid, String currentIp){
        String loggedIp = playerCache.get(uuid).getIp();
        return (!loggedIp.equalsIgnoreCase(currentIp));
    }

     /**
     * Checks if uuid's logged IGN is different from current IGN.
     * @param ign
     * @return
     */
    public static boolean isIgnDifferent(UUID uuid, String newIgn){
        String loggedIgn = playerCache.get(uuid).getUsername();
        return (!loggedIgn.equalsIgnoreCase(newIgn));
    }

    /**
     * Checks if player <code>ign</code> is in player cache.
     * @param ign
     * @return
     */
    public static boolean isPlayerLogged(UUID uuid){
        return playerCache.containsKey(uuid);
    }

    /**
     * Checks if player <code>uuid</code> is in ban cache.
     * @param ign
     * @return True if player is in cache. False if not.
     */
    public static boolean isPlayerBanned(UUID uuid){
        return banCache.containsKey(uuid);
    }

    /**
     * Checks if player <code>ip</code> is in ban cache.
     * @param ign
     * @return True if ip is in cache. False if not.
     */
    public static boolean isIpBanned(String ip){
        for (SqlBanned SqlBanned : banCache.values()) {
            if (SqlBanned.getIp().equalsIgnoreCase(ip) && (SqlBanned.getBanType() == BanType.TEMP_IP_BAN || SqlBanned.getBanType() == BanType.PERM_IP_BAN))
                return true;
        }
        return false;
    }

    /**
     * Checks if player <code>ip</code> is in ban cache.
     * @return True if ip is in cache. False if not.
     */
    public static boolean isIpBanned(UUID uuid){
        SqlBanned sqlBanned = banCache.get(uuid);
        return (sqlBanned.getBanType() == BanType.TEMP_IP_BAN || sqlBanned.getBanType() == BanType.PERM_IP_BAN);
    }

    /**
     * Copies over existing IP Ban to the uuid.
     */
    public static void copyIPBan (UUID uuid) {
        String ip = playerCache.get(uuid).getIp();
        for (SqlBanned sb : banCache.values()) {
            if (sb.getIp().equalsIgnoreCase(ip)) {
                setBan(uuid, ip, sb.getBanType(), sb.getReason(), sb.getDuration(), UUID.fromString(sb.getBannerUuid()));
                break;
            }
        }
    }

    /**
     * Checks if player <code>ign</code> is in mute cache.
     * @param ign
     * @return True if player is in cache. False if not.
     */
    public static boolean isPlayerMuted(UUID uuid){
        return muteCache.containsKey(uuid);
    }

    /**
     * Checks if cached player data exceeds specified value.
     * <p>
     * If true, saves cached data to database/ymls.
     */
    private static void checkCaches(){
        int amount = m.getConfig().getInt("cache.save-at");
        if(playerCache.values().stream().filter(sp -> sp.isUpdated()).collect(Collectors.toList()).size() % amount == 0) savePlayerCache();
    }

    /**
     * Saves all current caches. Used when plugin is disabled.
     */
    public static void saveCaches(){
        savePlayerCache();
        saveBanCache();
        saveMuteCache();
    }

    /**
     * Checks if unban date is before current date.
     */
    public static boolean isBanLifted(UUID uuid){
        if (banCache.get(uuid) == null) {
            try {
                SqlMethods.updateHistoryStatus(uuid, "Ban", "Lifted", null);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (banCache.get(uuid).getBanType() == BanType.PERM_BAN || banCache.get(uuid).getBanType() == BanType.PERM_IP_BAN) return false;
        Date ubDate = banCache.get(uuid).getUnbanDate();
        Date cDate = new Date();
        return cDate.after(ubDate);
    }

    /**
     * Checks if unban date is before current date.
     */
    public static boolean isBanLifted(String ip){
        UUID uuid = null;
        for (SqlBanned sb : banCache.values()) {
            if (sb.getIp().equalsIgnoreCase(ip)) {
                uuid = sb.getUuid();
                break;
            }
        }
        if (banCache.get(uuid).getBanType() == BanType.PERM_BAN || banCache.get(uuid).getBanType() == BanType.PERM_IP_BAN) return false;
        Date ubDate = banCache.get(uuid).getUnbanDate();
        Date cDate = new Date();
        return cDate.after(ubDate);
    }

    /**
     * Checks if unmute date is before current date.
     */
    public static boolean isMuteLifted(UUID uuid){
        if (muteCache.get(uuid) == null) {
            try {
                SqlMethods.updateHistoryStatus(uuid, "Mute", "Lifted", null);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (muteCache.get(uuid).getMuteType() == MuteType.PERM_MUTE) return false;
        Date umDate = muteCache.get(uuid).getUnmuteDate();
        Date cDate = new Date();
        return cDate.after(umDate);
    }

    /**
     * Removes ban of ign in both cache and database.
     * <p>
     * If player is ip banned, removes every other ip ban and the ip as well.
     * @param ign
     */
    public static void removeBan(UUID uuid, String histStatus, CommandSender sender){
        if (!SqlCache.isIpBanned(uuid)) {
            banCache.remove(uuid);
            try {
                SqlMethods.removeBan(uuid);
                SqlMethods.updateHistoryStatus(uuid, "Ban", histStatus, sender);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            SqlCache.getSameIps(SqlCache.getBannedObject(uuid).getIp()).forEach(u -> {
                banCache.remove(u);
                try {
                    SqlMethods.removeBan(u);
                    SqlMethods.updateHistoryStatus(u, "Ban", histStatus, sender);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Removes mute of ign in both cache and Sql file.
     * @param ign
     */
    public static void removeMute(UUID uuid, String histStatus, CommandSender sender) {
        muteCache.remove(uuid);
        try {
            SqlMethods.removeMute(uuid);
            SqlMethods.updateHistoryStatus(uuid, "Mute", histStatus, sender);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds or updates the new ban to the cache and database.
     * <p>
     * value = uuid or ip
     * @param ign
     */
    public static void setBan(UUID uuid, String ip, BanType banType, String reason, String duration, UUID bannerUuid){
        banCache.put(uuid, new SqlBanned(uuid, ip, banType, reason, duration, bannerUuid));
        try {
            SqlMethods.setBan(banCache.get(uuid));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds or updates the new ban to the cache and database.
     * <p>
     * value = uuid or ip
     * @param ign
     */
    public static void setBan(UUID uuid, String ip, BanType banType, String reason, String duration){
        banCache.put(uuid, new SqlBanned(uuid, ip, banType, reason, duration, (UUID) null));
        try {
            SqlMethods.setBan(banCache.get(uuid));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bans every player in the player cache with the same IP plus the IP itself and adds to database.
     */
    public static void setBan(String ip, BanType banType, String reason, String duration, UUID bannerUuid){
        for (UUID uuid : getSameIps(ip)) {
            banCache.put(uuid, new SqlBanned(uuid, ip, banType, reason, duration, bannerUuid));
            try {
                SqlMethods.setBan(banCache.get(uuid));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds or updates the new ban to the cache and database.
     * <p>
     * value = uuid or ip
     * @param ign
     */
    public static void setBan(SqlPlayer sp, SqlBanned banToCopy){
        banCache.put(sp.getUuid(), new SqlBanned(sp.getUuid(), sp.getIp(), banToCopy.getBanType(), banToCopy.getReason(), banToCopy.getDuration(), banToCopy.getBannerUuid()));
        try {
            SqlMethods.setBan(banCache.get(sp.getUuid()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bans every player in the player cache with the same IP plus the IP itself and adds to database.
     */
    public static void setBan(String ip, BanType banType, String reason, String duration){
        for (UUID uuid : getSameIps(ip)) {
            banCache.put(uuid, new SqlBanned(uuid, ip, banType, reason, duration, (UUID) null));
            try {
                SqlMethods.setBan(banCache.get(uuid));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds or updates the new mute to the cache.
     * @param ign
     */
    public static void setMute(UUID uuid, MuteType muteType, String reason, String duration, UUID muterUuid){
        muteCache.put(uuid, new SqlMuted(uuid, muteType, reason, duration, muterUuid));
        try {
            SqlMethods.setMute(muteCache.get(uuid));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds or updates the new mute to the cache.
     * @param ign
     */
    public static void setMute(UUID uuid, MuteType muteType, String reason, String duration){
        muteCache.put(uuid, new SqlMuted(uuid, muteType, reason, duration, null));
        try {
            SqlMethods.setMute(muteCache.get(uuid));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets or updates playerIpCache.
     * @param ign
     */
    /*public static void setPlayerIP(String uuid){
        playerIpCache.put(uuid, m.getSqlIp(m.getName(uuid)));
    }*/

    /**
     * Sets or updates playerCache.
     * @param ign
     */
    public static void setPlayer(UUID uuid, String ip) {
        if (playerCache.containsKey(uuid)) {
            SqlPlayer sp = SqlCache.getPlayerObject(uuid);
            if (!sp.getUsername().equalsIgnoreCase(m.getName(uuid.toString())) || !sp.getIp().equalsIgnoreCase(ip)) {
                sp.setUpdated(true);
                sp.setUsername(m.getName(uuid.toString()));
                sp.setIp(ip);
                checkCaches();
            }
            return;
        }
        playerCache.put(uuid, new SqlPlayer(uuid, ip, true));
        checkCaches();
    }

    public static void loadPlayer(UUID uuid, String ip) {
        playerCache.put(uuid, new SqlPlayer(uuid, ip, false));
    }

    /**
     * Sets a new player in playerCache.
     * @param ign
     */
    public static void setNewPlayer(UUID uuid, String ip, boolean isNewPlayer){
        playerCache.put(uuid, new SqlPlayer(uuid, ip, isNewPlayer));
        checkCaches();
    }

    /**
     * Resets all player caches. Used for purges.
     */
    public static void refreshPlayerCaches(){
        playerCache.clear();
        try {
            SqlMethods.clearPlayerCache();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTask(m, () -> Bukkit.getOnlinePlayers().forEach(p -> SqlCache.setNewPlayer(p.getUniqueId(), m.getSqlIp(p.getUniqueId()), true)));
    }

    /**
     * Prints out all keys and their values in the Bans Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutBanCache(){
        for(UUID key : banCache.keySet())
            System.out.println(key.toString() + " " + banCache.get(key));
    }

    /**
     * Prints out all keys and their values in the Mutes Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutMuteCache(){
        for(UUID key : muteCache.keySet())
            System.out.println(key.toString() + " " + muteCache.get(key));
    }

    /**
     * Prints out all keys and their values in the Players Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutPlayerCache(){
        for(UUID key : playerCache.keySet())
            System.out.println(key.toString() + " " + playerCache.get(key));
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
                if(SqlCache.isPlayerLogged(m.getUuid(ign)))
                    return ign;
            }
        }
        return null;
    }

    public static String getSqlIp(UUID uuid){
        return playerCache.get(uuid).getIp();
    }

    /**
     * Returns the list of igns with similar IPs as <code>ip</code>.
     * @param ip
     * @return
     */
    public static List<UUID> getSameIps(String ip){
        List<UUID> igns = new ArrayList<UUID>();
        playerCache.values().stream().filter(sp -> sp.getIp().equalsIgnoreCase(ip)).forEach(sp -> igns.add(sp.getUuid()));
        return igns;
    }

    /**
     * Lists the IGNs with the same logged IP.
     * <p> Gray shows offline, green online, and red banned.
     */
    public static String listAlts(String ip){
        String ignList = "";
        for(UUID uuid : SqlCache.getSameIps(ip)){
            String ign = playerCache.get(uuid).getUsername();
            if (SqlCache.isPlayerBanned(uuid)) ign = "§c" + ign;
            else if(m.getOfflinePlayer(uuid).isOnline()) ign = "§a" + ign;
            else ign = "§7" + ign;
            ignList = ignList.concat(ign + "§f, ");
        }
        ignList = ignList.substring(0, ignList.length()-2);
        return ignList;
    }

    public static Set<UUID> getOciCache(){
        return ociCache;
    }

    /**
     * Gets the player object of UUID. Returns null if player is not logged.
     * @param uuid = UUID of the player.
     */
    public static SqlPlayer getPlayerObject (UUID uuid) {
        return playerCache.get(uuid);
    }

    /**
     * Gets the banned object of UUID. Returns null if uuid is not banned.
     * @param uuid = UUID of the player.
     */
    public static SqlBanned getBannedObject (UUID uuid) {
        return banCache.get(uuid);
    }

    /**
     * Gets the muted object of UUID. Returns null if uuid is not banned.
     * @param uuid = UUID of the player.
     */
    public static SqlMuted getMutedObject (UUID uuid) {
        return muteCache.get(uuid);
    }

    public static void removeIpBan (String ip, String histStatus, CommandSender sender) {

        banCache.values().stream().filter(sb -> sb.getIp().equalsIgnoreCase(ip)).forEach(sb -> removeBan(sb.getUuid(), histStatus, sender));

    }

    public static void copyBan(UUID uuid) {
        SqlPlayer sp = SqlCache.getPlayerObject(uuid);
        SqlBanned banToCopy = banCache.values().stream().filter(sb -> sb.getIp().equalsIgnoreCase(sp.getIp())).iterator().next();
        SqlCache.setBan(sp, banToCopy);
    }
    
}