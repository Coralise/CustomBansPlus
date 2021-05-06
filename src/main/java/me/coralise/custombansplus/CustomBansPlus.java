package me.coralise.custombansplus;

import me.coralise.custombansplus.yaml.*;
import me.coralise.custombansplus.enums.BanType;
import me.coralise.custombansplus.enums.MuteType;
import me.coralise.custombansplus.sql.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomBansPlus extends JavaPlugin implements Listener {

    //#region File Instantiations
    private File severitiesFile = new File(getDataFolder(), "severities.yml");
    private FileConfiguration severitiesConfig = YamlConfiguration.loadConfiguration(severitiesFile);
    private File ociFile = new File(getDataFolder(), "OfflineCI.yml");
    private FileConfiguration ociConfig = YamlConfiguration.loadConfiguration(ociFile);
    private final File reportsFile = new File(getDataFolder(), "reports.yml");
    private final FileConfiguration reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
    private final File reportsBLFile = new File(getDataFolder(), "reportsblacklist.yml");
    private final FileConfiguration reportsBLConfig = YamlConfiguration.loadConfiguration(reportsBLFile);
    private final File altsFile = new File(getDataFolder(), "accounts.yml");
    private final FileConfiguration altsConfig = YamlConfiguration.loadConfiguration(altsFile);
    private final File bansFile = new File(getDataFolder(), "bans.yml");
    private final FileConfiguration bansConfig = YamlConfiguration.loadConfiguration(bansFile);
    private final File historiesFile = new File(getDataFolder(), "histories.yml");
    private final FileConfiguration historiesConfig = YamlConfiguration.loadConfiguration(historiesFile);
    private final File mutesFile = new File(getDataFolder(), "mutes.yml");
    private final FileConfiguration mutesConfig = YamlConfiguration.loadConfiguration(mutesFile);
    //#endregion

    static CustomBansPlus m;
    public boolean hasVault = false;
    private boolean yaml = false;
    private boolean sql = false;
    static ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();
    PluginManager pm;

    boolean toggleSQL;
    String update;

    @Override
    public void onEnable() {

        ClassGetter.setPlugin(this);
        m = (CustomBansPlus) ClassGetter.getPlugin();
        pm = getServer().getPluginManager();

        int pluginId = 10668;
        new Metrics(this, pluginId);

        String[] f = new String[1];
        if(this.getDataFolder().exists()) f = this.getDataFolder().list();

        if(!this.getDataFolder().exists() || !Arrays.toString(f).contains("config.yml")){
            this.saveDefaultConfig();
            System.out.println("");
            System.out.println("          §e[CBP] §aConfig file created!");
            System.out.println("          §aYou may setup your SQL Database there, if you choose to.");
            System.out.println("          §aRestart again to enable plugin.");
            System.out.println("");
            this.setEnabled(false);
            return;
        }
     
        if (getConfig().getBoolean("updates.notify")) new UpdateChecker(this);

        if (pm.getPlugin("Vault") == null) {
            System.out.println("§e[CBP]§c Vault plugin not found, baldeduct function is disabled.");
            } else {
            AbstractEconomy.setupEconomy();
            hasVault = true;
        }

        this.saveDefaultConfig();

        if(!severitiesFile.exists()){
            saveResource("severities.yml", false);
            severitiesFile = new File(getDataFolder(), "severities.yml");
            severitiesConfig = YamlConfiguration.loadConfiguration(severitiesFile);
        }
        if(!ociFile.exists()){
            saveResource("OfflineCI.yml", false);
            ociFile = new File(getDataFolder(), "OfflineCI.yml");
            ociConfig = YamlConfiguration.loadConfiguration(ociFile);
        }
        if(!reportsFile.exists()){
            saveResource("reports.yml", false);
        }
        if(!reportsBLFile.exists()){
            saveResource("reportsblacklist.yml", false);
        }
    
        toggleSQL = getConfig().getBoolean("sql.enable");

        if(toggleSQL && SqlMethods.getConn()){
            System.out.println("§e[CBP] §aDatabase connection acquired.");
            SqlMethods.checkDB();
            System.out.println("§e[CBP] §fCaching database asynchronously...");
            new Thread(() -> {
                SqlCache.setupCache();
                System.out.println("§e[CBP] §fCache Complete.");
                System.out.println("§e[CBP] §aSetup Complete.");
                registerSQL();
            }).start();
        }else if (toggleSQL){
            System.out.println("§e[CBP] §cDatabase connection failed. Please make sure you inputted the details correctly.");
            this.setEnabled(false);
        }else{
            System.out.println("§e[CBP] §aSQL is not enabled, proceeding to local version.");
            System.out.println("§e[CBP] §fCaching data asynchronously...");
            new Thread(() -> {
                YamlCache.setupCache();
                System.out.println("§e[CBP] §fCache Complete.");
                System.out.println("§e[CBP] §aSetup Complete.");
                registerYAML();
            }).start();
        }

    }

    @Override
    public void onDisable(){

        if(yaml) YamlCache.saveCaches();
        if(sql) SqlCache.saveCaches();
        
        System.out.println("§e[CBP] §cDisabled.");
        
    }
    
    public String getUpdate(){
        return update;
    }
    
    //#region File Getters
    public FileConfiguration getSevConfig(){
        return severitiesConfig;
    } 
    public File getSevFile(){
        return severitiesFile;
    }
    public FileConfiguration getOciConfig(){
        return ociConfig;
    } 
    public File getOciFile(){
        return ociFile;
    }
    public FileConfiguration getReportsConfig(){
        return reportsConfig;
    } 
    public File getReportsFile(){
        return reportsFile;
    }
    public FileConfiguration getReportsBLConfig(){
        return reportsBLConfig;
    } 
    public File getReportsBLFile(){
        return reportsBLFile;
    }
    public FileConfiguration getAltsConfig(){
        return altsConfig;
    }
    public File getAltsFile(){
        return altsFile;
    }
    public FileConfiguration getBansConfig(){
        return bansConfig;
    } 
    public File getBansFile(){
        return bansFile;
    }
    public FileConfiguration getHistConfig(){
        return historiesConfig;
    } 
    public File getHistFile(){
        return historiesFile;
    }
    public FileConfiguration getMutesConfig(){
        return mutesConfig;
    } 
    public File getMutesFile(){
        return mutesFile;
    }
    //#endregion

    public void registerYAML(){

        if(!altsFile.exists()){
            saveResource("accounts.yml", false);
        }
        if(!bansFile.exists()){
            saveResource("bans.yml", false);
        }
        if(!historiesFile.exists()){
            saveResource("histories.yml", false);
        }
        if(!mutesFile.exists()){
            saveResource("mutes.yml", false);
        }

        pm.registerEvents(new YamlMain(), this);
        pm.registerEvents(new YamlCBMenu(), this);
        YamlAbstractCommand.registerCommands(this);
        pm.registerEvents(new YamlHistoryCommand(), this);
        yaml = true;

    }

    public void registerSQL(){

        pm.registerEvents(new SqlMain(), this);
        pm.registerEvents(new SqlCBMenu(), this);
        SqlAbstractCommand.registerCommands(this);
        pm.registerEvents(new SqlHistoryCommand(), this);
        sql = true;

    }

    /**
     * Calculates and returns the unpunish date.
     * 
     * @param value 1h, 2d, perm, etc.
     * @return string value of unpunish date.
     */
    public String calculateUnpunishDateString(String value){

        value = m.getSevDuration(value);

        if (value.contains("perm")) return null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date cDate = new Date();
        long cDateMilli = cDate.getTime();
        long addMillis = 0;
        String[] loValues = value.split("(?<=[smhd])");
        for(String v : loValues){
            long loValue = Long.parseLong(v.substring(0,v.length()-1));
            switch(v.charAt(v.length()-1)){
                case 's':
                    addMillis += loValue * 1000;
                    break;
                case 'm':
                    addMillis += loValue * 60000;
                    break;
                case 'h':
                    addMillis += loValue * 3600000;
                    break;
                case 'd':
                    addMillis += loValue * 86400000;
                    break;
            }
        }

        Date uDate = new Date(cDateMilli + addMillis);

        return formatter.format(uDate);

    }

    /**
     * Calculates and returns the unpunish date.
     * 
     * @param value 1h, 2d, perm, etc.
     * @return date value of unpunish date.
     */
    public Date calculateUnpunishDateDate(String value){

        Date cDate = new Date();
        long cDateMilli = cDate.getTime();
        long addMillis = 0;
        String[] loValues = value.split("(?<=[smhd])");
        for(String v : loValues){
            long loValue = Long.parseLong(v.substring(0,v.length()-1));
            switch(v.charAt(v.length()-1)){
                case 's':
                    addMillis += loValue * 1000;
                    break;
                case 'm':
                    addMillis += loValue * 60000;
                    break;
                case 'h':
                    addMillis += loValue * 3600000;
                    break;
                case 'd':
                    addMillis += loValue * 86400000;
                    break;
            }
        }

        Date uDate = new Date(cDateMilli + addMillis);

        return uDate;

    }

    public boolean isValueValid(String value){
        if(value.equalsIgnoreCase("perm")) return true;
        String[] valChars = new String[0];
        boolean num = false;
        valChars = value.split("");
        for(String c : valChars){
            if (c.matches("[0-9]"))
                num = true;
            else if (c.matches("[smhd]") && num)
                num = false;
            else{
                return false;
            }
        }
        return !num;
    }

    /**
     * Returns the string time remaining from the current date until the specified date.
     * @param date
     * @return
     */
    public String getTimeRemaining(Date uDate){

        Date cDate = new Date();

        long cDateMilli = cDate.getTime();
        long uDateMilli = uDate.getTime();

        long difference = uDateMilli - cDateMilli;

        long days = difference / 86400000;
        difference = difference % 86400000;
        long hours = difference / 3600000;
        difference = difference % 3600000;
        long minutes = difference / 60000;
        difference = difference % 60000;
        long seconds = difference / 1000;

        String timeRemaining = days + "d " + hours + "h " + minutes + "m " + seconds + "s";

        return timeRemaining;

    }

    /**
     * Returns the string time remaining from the current date until the specified date.
     * @param date
     * @return
     */
    public String getTimeRemaining(String uDate){

        Date cDate = new Date();

        long cDateMilli = cDate.getTime();
        long uDateMilli = new Date().getTime();
        try {
            uDateMilli = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(uDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = uDateMilli - cDateMilli;

        long days = difference / 86400000;
        difference = difference % 86400000;
        long hours = difference / 3600000;
        difference = difference % 3600000;
        long minutes = difference / 60000;
        difference = difference % 60000;
        long seconds = difference / 1000;

        String timeRemaining = days + "d " + hours + "h " + minutes + "m " + seconds + "s";

        return timeRemaining;

    }

    /**
     * Returns the IP of the target in yaml format (. to -).
     * <p>
     * Returns null if player's IP is not logged yet.
     * @return
     */
    public String getYamlIp(UUID uuid){
        if(!getOfflinePlayer(uuid).isOnline()) return YamlCache.getYamlIp(uuid);
        String currentIP = Bukkit.getPlayer(uuid).getAddress().toString();
        currentIP = currentIP.replace('.', '-');
        currentIP = currentIP.substring(1, currentIP.indexOf(":"));
        return currentIP;
    }

    /**
     * Returns the IP of the target in SQL format.
     * <p>
     * Returns null if player's IP is not logged yet.
     * @param ign
     * @return
     */
    public String getSqlIp(UUID uuid){
        if(!getOfflinePlayer(uuid).isOnline()) return SqlCache.getSqlIp(uuid);
        String currentIP = Bukkit.getPlayer(uuid).getAddress().toString();
        currentIP = currentIP.substring(1, currentIP.indexOf(":"));
        return currentIP;
    }

    /**
     * Gets values of sevs and executes them if they are true to the target.
     * @param target
     * @param clearInv
     * @param balDeduct
     * @param cmds
     */
    public void checkSevValues(UUID uuid, boolean clearInv, double balDeduct, List<String> cmds){

        OfflinePlayer proTarget = getOfflinePlayer(uuid);

        if (clearInv) {
            if (proTarget.isOnline()) {
                Player plTarget = (Player) proTarget;
                plTarget.getInventory().clear();
            } else {
                if(sql){
                    SqlCache.getOciCache().add(uuid);
                    m.updateSqlOci();
                }else if(yaml){
                    YamlCache.getOciCache().add(uuid);
                    m.updateYamlOci();
                }
                    
            }
        }

        if (m.hasVault && balDeduct != 0.0) {
            double deduct = AbstractEconomy.getEconomy().getBalance(proTarget) * balDeduct;
            AbstractEconomy.getEconomy().withdrawPlayer(proTarget, deduct);
        }

        if (!cmds.isEmpty()) {
            for (String cmd : cmds) {
                cmd = cmd.replace("%player%", getName(uuid.toString()));
                Bukkit.dispatchCommand(cnsl, cmd);
            }
        }

    }

    /**
     * Gets values of severity and executes them if they are true to the target.
     * @param target
     * @param severity = sN
     */
    public void checkSevValues(UUID uuid, String value){

        if (!(value.length() >= 2 && value.charAt(0) == 's' && m.getSevConfig().getKeys(false).contains(value.substring(1))))
            return;

        OfflinePlayer proTarget = getOfflinePlayer(uuid);

        int sevNum = Integer.parseInt(value.substring(1));
        boolean clearInv = m.getSevConfig().getBoolean(sevNum+".clear-inv");
        List<String> cmds = m.getSevConfig().getStringList(sevNum+".console-commands");
        double balDeduct = m.getSevConfig().getDouble(sevNum+".baldeduct");

        if (clearInv) {
            if (proTarget.isOnline()) {
                Player plTarget = (Player) proTarget;
                plTarget.getInventory().clear();
            } else {
                if(sql){
                    SqlCache.getOciCache().add(uuid);
                    m.updateSqlOci();
                }else if(yaml){
                    YamlCache.getOciCache().add(uuid);
                    m.updateYamlOci();
                }
                    
            }
        }

        if (m.hasVault && balDeduct != 0.0) {
            double deduct = AbstractEconomy.getEconomy().getBalance(proTarget) * balDeduct;
            AbstractEconomy.getEconomy().withdrawPlayer(proTarget, deduct);
        }

        if (!cmds.isEmpty()) {
            for (String cmd : cmds) {
                cmd = cmd.replace("%player%", getName(uuid.toString()));
                String fCmd = cmd;
                Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(cnsl, fCmd));
            }
        }

    }

    public UUID getUuid(String target){
        return Bukkit.getOfflinePlayer(target).getUniqueId();
    }
    public UUID getUuid(CommandSender target){
        return Bukkit.getOfflinePlayer(target.getName()).getUniqueId();
    }
    public OfflinePlayer getOfflinePlayer(UUID uuid){
        return Bukkit.getOfflinePlayer(uuid);
    }
    public String getName(String uuid){
        if(uuid.equalsIgnoreCase("CONSOLE")) return "CONSOLE";
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
    }

    public void updateSqlOci(){

        ArrayList<UUID> newList = new ArrayList<UUID>();

        SqlCache.getOciCache().forEach(uuid -> newList.add(uuid));

        m.getOciConfig().set("offline-ci", newList);
        try {
            m.getOciConfig().save(m.getOciFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateYamlOci(){

        ArrayList<UUID> newList = new ArrayList<UUID>();

        YamlCache.getOciCache().forEach(uuid -> newList.add(uuid));

        m.getOciConfig().set("offline-ci", newList);
        try {
            m.getOciConfig().save(m.getOciFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the unban date of the target's ban.
     * <p>
     * Returns "None" if target is permanently banned, and null if target is not banned.
     */
    public String getYamlUnbanDate(String uuid){
        String strDate = m.getBansConfig().getString(uuid+".unban-date");
        return strDate;
    }

    /**
     * Returns the unban date of the target's ban.
     * <p>
     * Returns "None" if target is permanently banned, and null if target is not banned.
     */
    public String getYamlUnmuteDate(String uuid){
        String strDate = m.getMutesConfig().getString(uuid+".unmute-by");
        return strDate;
    }

    public String getSevDuration(String value) {

        if (!(value.length() >= 2 && value.charAt(0) == 's' && m.getSevConfig().getKeys(false).contains(value.substring(1))))
            return value;

        return m.getSevConfig().getString(value.substring(1) + ".duration");

    }

    public String getType(String value){

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

    public BanType getBanTypeIP (String value) {

        value = getSevDuration(value);

        if (value.contains("perm"))
            return BanType.PERM_IP_BAN;
        else
            return BanType.TEMP_IP_BAN;

    }

    public BanType getBanType (String value) {

        value = getSevDuration(value);

        if (value.contains("perm"))
            return BanType.PERM_BAN;
        else
            return BanType.TEMP_BAN;

    }

    public BanType getBanTypeFromString (String value) {

        value = getSevDuration(value);
        BanType banType;

        if (value.contains("perm")) {
            banType = BanType.PERM_BAN;
            if (value.contains("IP"))
                banType = BanType.PERM_IP_BAN;
        } else {
            banType = BanType.TEMP_BAN;
            if (value.contains("IP"))
                banType = BanType.TEMP_IP_BAN;
        }
        return banType;

    }

    public MuteType getMuteType (String value) {

        value = getSevDuration(value);

        if (value.contains("perm"))
            return MuteType.PERM_MUTE;
        else
            return MuteType.TEMP_MUTE;

    }

    public MuteType getMuteTypeFromString (String value) {

        value = getSevDuration(value);
        MuteType muteType;

        if (value.contains("perm")) {
            muteType = MuteType.PERM_MUTE;
        } else {
            muteType = MuteType.TEMP_MUTE;
        }
        return muteType;
        
    }

    public String parseMessage (String msg) {
        String newMsg = msg.replace("&", "§");
        newMsg = newMsg.replace(" /n ", "\n");
        newMsg = newMsg.replace("/n ", "\n");
        newMsg = newMsg.replace(" /n", "\n");
        newMsg = newMsg.replace("/n", "\n");
        return newMsg;
    }
    
}