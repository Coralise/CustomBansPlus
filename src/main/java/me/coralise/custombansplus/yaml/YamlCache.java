package me.coralise.custombansplus.yaml;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;
import me.coralise.custombansplus.enums.BanType;
import me.coralise.custombansplus.enums.MuteType;
import me.coralise.custombansplus.yaml.objects.YamlBanned;
import me.coralise.custombansplus.yaml.objects.YamlMuted;
import me.coralise.custombansplus.yaml.objects.YamlPlayer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class YamlCache {

     static ConcurrentHashMap<UUID, YamlPlayer> playerCache = new ConcurrentHashMap<UUID, YamlPlayer>();
     static ConcurrentHashMap<UUID, YamlBanned> banCache = new ConcurrentHashMap<UUID, YamlBanned>();
     static ConcurrentHashMap<UUID, YamlMuted> muteCache = new ConcurrentHashMap<UUID, YamlMuted>();
     static HashSet<UUID> ociCache = new HashSet<UUID>();

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Saves current player caches to data.
     */
    private static void savePlayerCache(){
        if(m.getConfig().getBoolean("cache.notify-save")) 
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.hasPermission("custombansplus.admin"))
                    p.sendMessage("§e[CBP] §fSaving player cache...");
            });
        long startTime = System.currentTimeMillis();

        playerCache.values().stream().filter(yp -> yp.isUpdate() || yp.isNewPlayer()).forEach(yp -> {
            if (yp.isUpdate()) {
                ArrayList<String> oldList = (ArrayList<String>) m.getAltsConfig().getStringList(yp.getOldIp());
                oldList.remove(yp.getUuid().toString());
                m.getAltsConfig().set(yp.getOldIp(), oldList);
                yp.setOldIp(yp.getIp());
            }
            String ip = yp.getIp();
            List<String> list = m.getAltsConfig().getStringList(ip);
            list.add(yp.getUuid().toString());
            m.getAltsConfig().set(ip, list);
            yp.setNewPlayer(false);
            yp.setUpdate(false);
        });

        try {
            m.getAltsConfig().save(m.getAltsFile());
        } catch (IOException e) {
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
                    p.sendMessage("§e[CBP] §fCache saved! Took " + stTime + " §fto execute action.");
                }
            });
    }

    /**
     * Sets up cache, used on server startup.
     */
    public static void setupCache(){

        for (String ip : m.getAltsConfig().getKeys(false))
            for (String strUuid : m.getAltsConfig().getStringList(ip))
                playerCache.put(UUID.fromString(strUuid), new YamlPlayer(UUID.fromString(strUuid), ip, false, false));

        m.getBansConfig().getKeys(false).stream().filter(k -> k.length() == 36).forEach(strUuid -> {
            banCache.put(UUID.fromString(strUuid), new YamlBanned(
                UUID.fromString(strUuid),
                m.getBansConfig().getString(strUuid + ".type"), 
                m.getBansConfig().getString(strUuid + ".reason"), 
                m.getBansConfig().getString(strUuid + ".duration"), 
                m.getBansConfig().getString(strUuid + ".banned-by"),
                m.getBansConfig().getString(strUuid + ".banned-on"),
                m.getBansConfig().getString(strUuid + ".unban-date"),
                m.getBansConfig().getString(strUuid + ".ip")
            ));
        });
        
        for (String strUuid : m.getMutesConfig().getKeys(false))
            muteCache.put(UUID.fromString(strUuid), new YamlMuted(
                UUID.fromString(strUuid),
                m.getMutesConfig().getString(strUuid + ".type"),
                m.getMutesConfig().getString(strUuid + ".reason"),
                m.getMutesConfig().getString(strUuid + ".duration"),
                m.getMutesConfig().getString(strUuid + ".muted-by"),
                m.getMutesConfig().getString(strUuid + ".muted-on"),
                m.getMutesConfig().getString(strUuid + ".unmute-by")
            ));

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
        for (YamlBanned yamlBanned : banCache.values()) {
            if (yamlBanned.getIp().equalsIgnoreCase(ip) && (yamlBanned.getBanType() == BanType.TEMP_IP_BAN || yamlBanned.getBanType() == BanType.PERM_IP_BAN))
                return true;
        }
        return false;
    }

    /**
     * Checks if player <code>ip</code> is in ban cache.
     * @return True if ip is in cache. False if not.
     */
    public static boolean isIpBanned(UUID uuid){
        YamlBanned yamlBanned = banCache.get(uuid);
        return (yamlBanned.getBanType() == BanType.TEMP_IP_BAN || yamlBanned.getBanType() == BanType.PERM_IP_BAN);
    }

    /**
     * Copies over existing IP Ban to the uuid.
     */
    public static void copyIPBan (UUID uuid) {
        String ip = playerCache.get(uuid).getIp();
        for (YamlBanned yb : banCache.values()) {
            if (yb.getIp().equalsIgnoreCase(ip)) {
                banCache.put(uuid, new YamlBanned(
                    uuid,
                    yb.getBanType().toString(), 
                    yb.getReason(), 
                    yb.getDuration(), 
                    yb.getBannerUuid(),
                    yb.getBanDateString(),
                    yb.getUnbanDateString(),
                    yb.getIp()
                ));
                YamlBanned yamlBanned = YamlCache.getBannedObject(uuid);
                m.getBansConfig().set(uuid.toString() + ".type", yamlBanned.getBanType().toString());
                m.getBansConfig().set(uuid.toString() + ".duration", yamlBanned.getDuration());
                m.getBansConfig().set(uuid.toString() + ".reason", yamlBanned.getReason());
                m.getBansConfig().set(uuid.toString() + ".banned-by", yamlBanned.getBannerUuid());
                m.getBansConfig().set(uuid.toString() + ".ip", yamlBanned.getIp());
                m.getBansConfig().set(uuid.toString() + ".banned-on", formatter.format(yamlBanned.getBanDate()));
                if (yamlBanned.getUnbanDate() != null)
                    m.getBansConfig().set(uuid.toString() + ".unban-date", formatter.format(yamlBanned.getUnbanDate()));
                try {
                    m.getBansConfig().save(m.getBansFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                YamlAbstractBanCommand.addHistory(yamlBanned);
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
        if(playerCache.values().stream().filter(yp -> yp.isUpdate() || yp.isNewPlayer()).collect(Collectors.toList()).size() % amount == 0) savePlayerCache();
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
    public static boolean isBanLifted(UUID uuid){
        if (banCache.get(uuid).getBanType() == BanType.PERM_BAN || banCache.get(uuid).getBanType() == BanType.PERM_IP_BAN) return false;
        Date ubDate = banCache.get(uuid).getUnbanDate();
        Date cDate = new Date();
        return cDate.after(ubDate);
    }

    public static boolean isBanLifted(String ip){
        YamlBanned yb = null;
        for (YamlBanned ban : banCache.values())
            if (ban.getIp().equalsIgnoreCase(ip)) {
                yb = ban;
                break;
            }
        if (yb == null) return false;
        if (yb.getBanType() == BanType.PERM_BAN) return false;
        Date ubDate = yb.getUnbanDate();
        Date cDate = new Date();
        return cDate.after(ubDate);
    }

    /**
     * Checks if unmute date is before current date.
     */
    public static boolean isMuteLifted(UUID uuid){
        if (muteCache.get(uuid).getMuteType() == MuteType.PERM_MUTE) return false;
        Date umDate = muteCache.get(uuid).getUnmuteDate();
        Date cDate = new Date();
        return cDate.after(umDate);
    }

    /**
     * Removes ban of ign in both cache and yaml file.
     * @param ign
     */
    public static void removeBan(UUID uuid){
        String ip = banCache.get(uuid).getIp();
        if(YamlCache.isIpBanned(uuid)){
            m.getBansConfig().set(ip, null);
            for (UUID uuid2 : getSameIps(ip)) {
                banCache.remove(uuid2);
                m.getBansConfig().set(uuid2.toString(), null);
            }
        } else {
            banCache.remove(uuid);
            m.getBansConfig().set(uuid.toString(), null);
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
    public static void removeMute(UUID uuid){
        muteCache.remove(uuid);
        m.getMutesConfig().set(uuid.toString(), null);
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
    public static void setBan(UUID uuid, String ip, BanType banType, String reason, String duration, String bannerUuid){
        banCache.put(uuid, new YamlBanned(uuid, ip, banType, reason, duration, bannerUuid));
    }

    /**
     * Adds or updates the new ban to the cache.
     * <p>
     * value = uuid or ip
     * @param ign
     */
    public static void setBan(UUID uuid, String ip, BanType banType, String reason, String duration){
        banCache.put(uuid, new YamlBanned(uuid, ip, banType, reason, duration, null));
    }

    /**
     * Bans every player in the player cache with the same IP.
     */
    public static void setBan(String ip, BanType banType, String reason, String duration, String bannerUuid){
        for (UUID uuid : getSameIps(ip))
            banCache.put(uuid, new YamlBanned(uuid, ip, banType, reason, duration, bannerUuid));
    }

    /**
     * Bans every player in the player cache with the same IP.
     */
    public static void setBan(String ip, BanType banType, String reason, String duration){
        for (UUID uuid : getSameIps(ip))
            banCache.put(uuid, new YamlBanned(uuid, ip, banType, reason, duration, null));
    }

    /**
     * Adds or updates the new mute to the cache.
     * @param ign
     */
    public static void setMute(UUID uuid, MuteType muteType, String reason, String duration, String muterUuid){
        muteCache.put(uuid, new YamlMuted(uuid, muteType, reason, duration, muterUuid));
    }

    /**
     * Adds or updates the new mute to the cache.
     * @param ign
     */
    public static void setMute(UUID uuid, MuteType muteType, String reason, String duration){
        muteCache.put(uuid, new YamlMuted(uuid, muteType, reason, duration, null));
    }
    
    /**
     * Sets or updates playerIpCache.
     * @param ign
     */
    /*public static void setPlayerIP(String uuid){
        playerIpCache.put(uuid, m.getYamlIp(m.getName(uuid)));
    }*/

    /**
     * Sets or updates playerCache.
     * @param ign
     */
    public static void setPlayer(UUID uuid, String ip){
        if (playerCache.containsKey(uuid)) {
            if (!playerCache.get(uuid).getUsername().equalsIgnoreCase(m.getName(uuid.toString())) || !playerCache.get(uuid).getIp().equalsIgnoreCase(ip)) {
                playerCache.get(uuid).setUpdate(true);
                playerCache.get(uuid).setIp(ip);
                playerCache.get(uuid).setUsername(m.getName(uuid.toString()));
                checkCaches();
            }
            return;
        }
        playerCache.put(uuid, new YamlPlayer(uuid, ip, false, true));
        checkCaches();
    }

    /**
     * Resets all player caches. Used for purges.
     */
    public static void refreshPlayerCaches(){
        playerCache.clear();
        for (String ip : m.getAltsConfig().getKeys(false))
            for (String strUuid : m.getAltsConfig().getStringList(ip))
                playerCache.put(UUID.fromString(strUuid), new YamlPlayer(UUID.fromString(strUuid), ip, false, false));
    }

    /**
     * Prints out all keys and their values in the Bans Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutBanCache(){
        for(UUID key : banCache.keySet())
            System.out.println(key.toString() + " " + banCache.get(key).getUnbanDateString());
    }

    /**
     * Prints out all keys and their values in the Mutes Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutMuteCache(){
        for(UUID key : muteCache.keySet())
            System.out.println(key.toString() + " " + muteCache.get(key).getUuid().toString());
    }

    /**
     * Prints out all keys and their values in the Players Cache to the console.
     * <p>
     * Used for debugging and testing.
     */
    public static void printOutPlayerCache(){
        for(UUID key : playerCache.keySet())
            System.out.println(key.toString() + " " + playerCache.get(key).getUsername());
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

    public static String getYamlIp(UUID uuid){
        return playerCache.get(uuid).getIp();
    }

    /**
     * Returns the list of igns with similar IPs as <code>ip</code>.
     * @param ip
     * @return
     */
    public static Set<UUID> getSameIps(String ip){
        HashSet<UUID> igns = new HashSet<UUID>();
        for(UUID uuid : playerCache.keySet()){
            if(playerCache.get(uuid).getIp().equalsIgnoreCase(ip)) igns.add(uuid);
        }
        return igns;
    }

    /**
     * Lists the IGNs with the same logged IP.
     * <p> Gray shows offline, green online, and red banned.
     */
    public static String listAlts(String ip){
        String ignList = "";
        for(UUID uuid : YamlCache.getSameIps(ip)){
            String ign = playerCache.get(uuid).getUsername();
            if (YamlCache.isPlayerBanned(uuid)) ign = "§c" + ign;
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
    public static YamlPlayer getPlayerObject (UUID uuid) {
        return playerCache.get(uuid);
    }

    /**
     * Gets the banned object of UUID. Returns null if uuid is not banned.
     * @param uuid = UUID of the player.
     */
    public static YamlBanned getBannedObject (UUID uuid) {
        return banCache.get(uuid);
    }

    /**
     * Gets the muted object of UUID. Returns null if uuid is not banned.
     * @param uuid = UUID of the player.
     */
    public static YamlMuted getMutedObject (UUID uuid) {
        return muteCache.get(uuid);
    }

    public static Collection<YamlPlayer> getPlayerObjects() {
        return playerCache.values();
    }

    public static void removeIpBan(String ip) {
        m.getBansConfig().set(ip, null);
        banCache.values().stream().filter(yb -> yb.getIp().equalsIgnoreCase(ip)).forEach(yb -> {
            banCache.remove(yb.getUuid());
            m.getBansConfig().set(yb.getUuid().toString(), null);
        });
        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}