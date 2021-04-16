package me.coralise.custombansplus;

import me.coralise.custombansplus.yaml.*;
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

    private File severitiesFile = new File(getDataFolder(), "severities.yml");
    private FileConfiguration severitiesConfig = YamlConfiguration.loadConfiguration(severitiesFile);
    private File ociFile = new File(getDataFolder(), "OfflineCI.yml");
    private FileConfiguration ociConfig = YamlConfiguration.loadConfiguration(ociFile);
    private final File altsFile = new File(getDataFolder(), "accounts.yml");
    private final FileConfiguration altsConfig = YamlConfiguration.loadConfiguration(altsFile);
    private final File bansFile = new File(getDataFolder(), "bans.yml");
    private final FileConfiguration bansConfig = YamlConfiguration.loadConfiguration(bansFile);
    private final File historiesFile = new File(getDataFolder(), "histories.yml");
    private final FileConfiguration historiesConfig = YamlConfiguration.loadConfiguration(historiesFile);
    private final File mutesFile = new File(getDataFolder(), "mutes.yml");
    private final FileConfiguration mutesConfig = YamlConfiguration.loadConfiguration(mutesFile);
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

        GetJavaPlugin.setPlugin(this);
        m = (CustomBansPlus) GetJavaPlugin.getPlugin();
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
     
        if (getConfig().getBoolean("toggle-update-notifs")) new UpdateChecker(this);

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
    
        toggleSQL = getConfig().getBoolean("enable-sql");

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
     * Calculates and returns the unban date.
     * 
     * @param value 1h, 2d, perm, etc.
     * @return
     */
    public String calculateUnpunishDate(String value){

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
     * @param unpunishDate
     * @return
     */
    public String getTimeRemaining(String unpunishDate){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date cDate = new Date();
        Date uDate = new Date();
        try {
            uDate = formatter.parse(unpunishDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
     * Returns the IP of the target in yaml format (. to -).
     * <p>
     * Returns null if player's IP is not logged yet.
     * @param ign
     * @return
     */
    public String getYamlIp(String ign){
        if(!getOfflinePlayer(ign).isOnline()) return YamlCache.getYamlIp(m.getUuid(ign));
        String currentIP = Bukkit.getPlayer(ign).getAddress().toString();
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
    public String getSqlIp(String ign){
        if(!getOfflinePlayer(ign).isOnline()) return SqlCache.getSqlIp(ign);
        String currentIP = Bukkit.getPlayer(ign).getAddress().toString();
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
    public void checkSevValues(String target, boolean clearInv, double balDeduct, List<String> cmds){

        OfflinePlayer proTarget = getOfflinePlayer(target);

        if (clearInv) {
            if (proTarget.isOnline()) {
                Player plTarget = (Player) proTarget;
                plTarget.getInventory().clear();
            } else {
                if(sql){
                    SqlCache.getOciCache().add(m.getUuid(target));
                    m.updateSqlOci();
                }else if(yaml){
                    YamlCache.getOciCache().add(m.getUuid(target));
                    m.updateYamlOci();
                }
                    
            }
        }

        if (m.hasVault && balDeduct != 0.0) {
            double deduct = AbstractEconomy.getEconomy().getBalance(proTarget) * balDeduct;
            String command = "eco take " + target + " " + deduct;
            Bukkit.dispatchCommand(cnsl, command);
        }

        if (!cmds.isEmpty()) {
            for (String cmd : cmds) {
                cmd = cmd.replace("%player%", target);
                Bukkit.dispatchCommand(cnsl, cmd);
            }
        }

    }

    /**
     * Gets values of severity and executes them if they are true to the target.
     * @param target
     * @param severity = sN
     */
    public void checkSevValues(String target, String severity){

        OfflinePlayer proTarget = getOfflinePlayer(target);

        int sevNum = Integer.parseInt(severity.substring(1));
        boolean clearInv = m.getSevConfig().getBoolean(sevNum+".clear-inv");
        List<String> cmds = m.getSevConfig().getStringList(sevNum+".console-commands");
        double balDeduct = m.getSevConfig().getDouble(sevNum+".baldeduct");

        if (clearInv) {
            if (proTarget.isOnline()) {
                Player plTarget = (Player) proTarget;
                plTarget.getInventory().clear();
            } else {
                if(sql){
                    SqlCache.getOciCache().add(m.getUuid(target));
                    m.updateSqlOci();
                }else if(yaml){
                    YamlCache.getOciCache().add(m.getUuid(target));
                    m.updateYamlOci();
                }
                    
            }
        }

        if (m.hasVault && balDeduct != 0.0) {
            double deduct = AbstractEconomy.getEconomy().getBalance(proTarget) * balDeduct;
            String command = "eco take " + target + " " + deduct;
            Bukkit.dispatchCommand(cnsl, command);
        }

        if (!cmds.isEmpty()) {
            for (String cmd : cmds) {
                cmd = cmd.replace("%player%", target);
                Bukkit.dispatchCommand(cnsl, cmd);
            }
        }

    }

    public String getUuid(String target){
        return m.getOfflinePlayer(target).getUniqueId().toString();
    }
    public String getUuid(CommandSender target){
        return m.getOfflinePlayer(target.getName()).getUniqueId().toString();
    }
    public OfflinePlayer getOfflinePlayer(String target){
        return Bukkit.getOfflinePlayer(target);
    }
    public String getName(String uuid){
        if(uuid.equalsIgnoreCase("CONSOLE")) return "CONSOLE";
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
    }

    public void updateSqlOci(){

        ArrayList<String> newList = new ArrayList<String>();

        SqlCache.getOciCache().forEach(uuid -> newList.add(uuid));

        m.getOciConfig().set("offline-ci", newList);
        try {
            m.getOciConfig().save(m.getOciFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateYamlOci(){

        ArrayList<String> newList = new ArrayList<String>();

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
    
}