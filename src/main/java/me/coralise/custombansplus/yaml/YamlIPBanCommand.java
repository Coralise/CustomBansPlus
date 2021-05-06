package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;
import me.coralise.custombansplus.enums.BanType;
import me.coralise.custombansplus.yaml.objects.YamlBanned;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YamlIPBanCommand extends YamlAbstractCommand {

    YamlIPBanCommand() {
        super("cbpipban", "custombansplus.ban", true);
    }

    public final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    public boolean banPage(String targetIP){
        
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> m.getYamlIp(p.getUniqueId()).equalsIgnoreCase(targetIP))
            .forEach(p -> Bukkit.getScheduler().runTask(m, () -> p.kickPlayer(YamlAbstractBanCommand.getBanMsg(p.getUniqueId()))));
        
        return true;
        
    }

    public void setIpBanned(String value, String targetIP, CommandSender sender, String reason, String duration, UUID tgtUuid){

        BanType banType = m.getBanTypeIP(value);
        YamlCache.getSameIps(targetIP).forEach(uuid -> Bukkit.getScheduler().runTask(m, () -> m.checkSevValues(uuid, value)));

        if (sender instanceof Player)
            YamlCache.getPlayerObjects().stream()
                .filter(yp -> yp.getIp().equalsIgnoreCase(targetIP))
                .forEach(yp -> {
                    YamlCache.setBan(yp.getUuid(), yp.getIp(), banType, reason, duration, m.getUuid(sender).toString());
                    YamlAbstractBanCommand.addHistory(YamlCache.getBannedObject(yp.getUuid()));
                });
        else 
            YamlCache.getPlayerObjects().stream()
                .filter(yp -> yp.getIp().equalsIgnoreCase(targetIP))
                .forEach(yp -> {
                    YamlCache.setBan(yp.getUuid(), yp.getIp(), banType, reason, duration);
                    YamlAbstractBanCommand.addHistory(YamlCache.getBannedObject(yp.getUuid()));
                });

        YamlCache.banCache.values().stream()
            .filter(yb -> yb.getIp().equalsIgnoreCase(targetIP))
            .forEach(yb -> {
                m.getBansConfig().set(tgtUuid.toString() + ".type", yb.getBanType().toString());
                m.getBansConfig().set(tgtUuid.toString() + ".duration", yb.getDuration());
                m.getBansConfig().set(tgtUuid.toString() + ".reason", yb.getReason());
                m.getBansConfig().set(tgtUuid.toString() + ".banned-by", yb.getBannerUuid());
                m.getBansConfig().set(tgtUuid.toString() + ".banned-on", yb.getBanDateString());
                m.getBansConfig().set(tgtUuid.toString() + ".unban-date", yb.getUnbanDateString());
                m.getBansConfig().set(tgtUuid.toString() + ".ip", yb.getIp());
            });

        for (YamlBanned yb : YamlCache.banCache.values()) {
            if (yb.getIp().equalsIgnoreCase(targetIP)) {
                m.getBansConfig().set(targetIP + ".type", yb.getBanType().toString());
                m.getBansConfig().set(targetIP + ".duration", yb.getDuration());
                m.getBansConfig().set(targetIP + ".reason", yb.getReason());
                m.getBansConfig().set(targetIP + ".banned-by", yb.getBannerUuid());
                m.getBansConfig().set(targetIP + ".banned-on", yb.getBanDateString());
                m.getBansConfig().set(targetIP + ".unban-date", yb.getUnbanDateString());
                break;
            }
        }
        
        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException ex) {
            //none
        }
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }
        
        if(args.length == 0){
            sender.sendMessage("§e/ipban [-s] <player> <snum/duration/permanent> <reason> - Bans specified player.");
            return true;
        }
        
        int s = 0;
            
        if(args[0].equalsIgnoreCase("-s"))
            s = 1;
        
        if(args.length < 2+s){
            sender.sendMessage("§cPlease enter a valid ban option.");
            return true;
        }
        
        String target = YamlCache.getPlayerIgn(args[0+s]);
        
        if(target == null){
            sender.sendMessage("§ePlayer " + args[0+s] + " has never been on the server.");
            return false;
        }
        
        UUID tgtUuid = m.getUuid(target);
        String targetIP = m.getYamlIp(tgtUuid);

        if(YamlCache.isPlayerBanned(tgtUuid) && !sender.hasPermission("custombansplus.overwrite")){
            sender.sendMessage("§cPlayer " + target + " is already banned and you don't have overwrites permission.");
            return true;
        }

        String value = args[1+s];
        if(m.getType(value) == null){
            sender.sendMessage("§cEnter a valid ban option.");
            return true;
        }
        String duration = m.getSevDuration(value);

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
        if (reason.equalsIgnoreCase(""))
            annType = "ipbanNoRsn";
        else
            annType = "ipban";

        if (duration.equalsIgnoreCase("perm")) annType = "perm" + annType;
        else annType = "temp" + annType;

        String fReason = reason;
        int fs = s;
        String fAnnType = annType;

        new Thread(() -> {
            setIpBanned(value, targetIP, sender, fReason, duration, tgtUuid);
            banPage(targetIP);
        
            String annValue = args[1+fs];
            if(annValue.equalsIgnoreCase("perm")) annValue = "Permanent";
    
            if (fs == 0) YamlAbstractAnnouncer.getAnnouncer(target, sender.getName(), annValue, fReason, fAnnType);
            else YamlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), annValue, fReason, fAnnType);
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
