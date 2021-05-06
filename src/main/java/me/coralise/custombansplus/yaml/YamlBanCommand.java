package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;
import me.coralise.custombansplus.enums.BanType;
import me.coralise.custombansplus.yaml.objects.YamlBanned;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author wayne
 */
public class YamlBanCommand extends YamlAbstractCommand {

    public YamlBanCommand() {
        super("cbpban", "custombansplus.ban", true);
    }
    
    public static final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void setBanned(CommandSender sender, UUID tgtUuid, BanType banType, String reason, String duration){

        if (sender instanceof Player)
            YamlCache.setBan(tgtUuid, YamlCache.playerCache.get(tgtUuid).getIp(), banType, reason, duration, m.getUuid(sender).toString());
        else
            YamlCache.setBan(tgtUuid, YamlCache.playerCache.get(tgtUuid).getIp(), banType, reason, duration);

        YamlBanned yamlBanned = YamlCache.banCache.get(tgtUuid);

        m.getBansConfig().set(tgtUuid.toString() + ".type", yamlBanned.getBanType().toString());
        m.getBansConfig().set(tgtUuid.toString() + ".duration", yamlBanned.getDuration());
        m.getBansConfig().set(tgtUuid.toString() + ".reason", yamlBanned.getReason());
        m.getBansConfig().set(tgtUuid.toString() + ".banned-by", yamlBanned.getBannerUuid());
        m.getBansConfig().set(tgtUuid.toString() + ".ip", yamlBanned.getIp());
        m.getBansConfig().set(tgtUuid.toString() + ".banned-on", formatter.format(yamlBanned.getBanDate()));
        if (yamlBanned.getUnbanDate() != null)
            m.getBansConfig().set(tgtUuid.toString() + ".unban-date", formatter.format(yamlBanned.getUnbanDate()));

        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException ex) {
            Logger.getLogger(YamlBanCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(!isValid(sender)){
            return false;
        }
        
        if(args.length == 0){
            sender.sendMessage("§e/ban [-s] <player> <severity#/duration/permanent> <reason> - Bans specified player.");
            return true;
        }
        
        int s = 0;
        if(args[0].equalsIgnoreCase("-s"))
            s = 1;
        
        if(args.length < 2+s){
            sender.sendMessage(ChatColor.RED + "Please enter a valid ban option.");
            return true;
        }
        
        String target = YamlCache.getPlayerIgn(args[0+s]);
        
        if(target == null){
            sender.sendMessage("§ePlayer " + args[0+s] + " has never been on the server.");
            return false;
        }

        UUID tgtUuid = m.getUuid(target);
        
        String value = args[1+s];
        String reason = "";
        if(args.length > 2+s){
            reason = args[2+s];
            for(int x = 3+s; x < args.length;x++){
                reason = reason.concat(" " + args[x]);
            }
        }

        if(reason.equalsIgnoreCase("") && !m.getConfig().getBoolean("toggle-no-reason"))
            reason = m.parseMessage(m.getConfig().getString("defaults.reason"));

        String annType;
        if(reason.isEmpty())
            annType = "banNoRsn";
        else
            annType = "ban";

        if(m.getType(value) == null){
            sender.sendMessage("§cEnter a valid ban option.");
            return true;
        }

        BanType banType = m.getBanType(value);
        String duration = m.getSevDuration(value);
        String annValue = duration;
        m.checkSevValues(tgtUuid, value);
        
        if(YamlCache.isPlayerBanned(tgtUuid) && !sender.hasPermission("custombansplus.overwrite")){
            sender.sendMessage(ChatColor.RED + "Player " + target + " is already banned and you don't have overwrites permission.");
            return true;
        }
        if(YamlCache.isIpBanned(YamlCache.playerCache.get(tgtUuid).getIp())){
            sender.sendMessage("§cPlayer " + target + " is already IP banned. Overwrite the ban by doing /ipban.");
            return true;
        }

        int fs = s;
        String fReason = reason;

        new Thread(() -> {
            setBanned(sender, tgtUuid, banType, fReason, duration);
            YamlAbstractBanCommand.banPage(tgtUuid);
            YamlAbstractBanCommand.addHistory(YamlCache.getBannedObject(tgtUuid));

            String fAnnType = annType;

            if (value.equalsIgnoreCase("perm")) fAnnType = "perm" + fAnnType;
            else fAnnType = "temp" + fAnnType;

            String fAnnValue = "";
            if (annValue.equalsIgnoreCase("perm")) fAnnValue = "Permanent";
            if (fs == 0) YamlAbstractAnnouncer.getAnnouncer(target, sender.getName(), fAnnValue, fReason, fAnnType);
            else YamlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), fAnnValue, fReason, fAnnType);
        }).start();
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // /ban Coralise 30d Xray
        List<String> tabComplete = new ArrayList<String>();

        if(args.length == 1){
            if(!args[0].contains("-") && args[0].length() != 0) return null;
            tabComplete.add("-s");
            Bukkit.getOnlinePlayers().forEach(p -> {tabComplete.add(p.getName());});
            return tabComplete;
        }
            
        int stab = 0;
        if(args[0].equalsIgnoreCase("-s"))
            stab++;

        if(args.length == 2+stab){

            m.getSevConfig().getKeys(false).forEach(p -> {tabComplete.add("s"+p);});
            
            tabComplete.add("perm");
            tabComplete.add("Xs");
            tabComplete.add("Xm");
            tabComplete.add("Xh");
            tabComplete.add("Xd");
            return tabComplete;

        }

        if(args.length == 3+stab){

            tabComplete.add("<reason>");
            return tabComplete;

        }

        return null;
        
    }
    
}
