package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class YamlMuteCommand extends YamlAbstractCommand {

    YamlMuteCommand() {
        super("cbpmute", "custombansplus.mute", true);
    }

    CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public String target;
    public String tgtUuid;
    public String duration;
    public String annType;
    CommandSender sdr;

    int s;

    public boolean mutePlayer(String type, String reason) {

        Date cDate = new Date();
        String date = formatter.format(cDate);

        if (reason.equalsIgnoreCase("") && !m.getConfig().getBoolean("toggle-no-reason"))
            reason = m.getConfig().getString("default-mute-reason");

        if(reason.equalsIgnoreCase(""))
            annType = "muteNoRsn";
        else
            annType = "mute";

        if (type.equalsIgnoreCase("Permanent")){
            duration = "Permanent";
        }

        m.getMutesConfig().set(tgtUuid + ".duration", duration);
        m.getMutesConfig().set(tgtUuid + ".reason", reason);
        if(!sdr.getName().equalsIgnoreCase("CONSOLE")) m.getMutesConfig().set(tgtUuid + ".muted-by", m.getUuid(sdr.getName()));
        else m.getMutesConfig().set(tgtUuid + ".muted-by", "CONSOLE");
        m.getMutesConfig().set(tgtUuid + ".muted-on", date);
        m.getMutesConfig().set(tgtUuid + ".unmute-by", "None");

        if (type.equalsIgnoreCase("Duration")) {
            String unmuteDate = m.calculateUnpunishDate(duration);
            m.getMutesConfig().set(tgtUuid + ".unmute-by", unmuteDate);
        }else m.getMutesConfig().set(tgtUuid + ".unmute-by", "None");

        try {
            m.getMutesConfig().save(m.getMutesFile());
        } catch (IOException e) {
            //none
        }

        YamlCache.setMute(m.getUuid(target));

        return true;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        sdr = sender;

        if (args.length == 0) {
            sender.sendMessage("§e/mute [-s] <player> <duration> <reason> - Mutes specified player.");
            return true;
        }

        s = 0;
        if (args.length > 0 && args[0].equalsIgnoreCase("-s"))
            s = 1;

        target = YamlCache.getPlayerIgn(args[0+s]);

        if(target == null){
            sender.sendMessage("§cPlayer " + args[0+s] + " has never played in the server.");
            return true;
        }

        tgtUuid = m.getUuid(target);

        Bukkit.getScheduler().runTask(m, () -> {
            String reason = "";
            if(args.length > 2+s){
                for(int i = 2+s; i < args.length; i++){
                    reason = reason.concat(args[i] + " ");
                }
                reason = reason.trim();
            }
    
            if(args.length == 1+s){
                mutePlayer("Permanent", reason);
                YamlAbstractBanCommand.addHistory(target, sender.getName(), "mute", reason, null);
                if (s == 0) YamlAbstractAnnouncer.getAnnouncer(target, sdr.getName(), "Permanent", reason, annType);
                else YamlAbstractAnnouncer.getSilentAnnouncer(target, sdr.getName(), "Permanent", reason, annType);
                return;
            }
    
            if(args.length >= 2+s){
    
                if(args[1+s].equalsIgnoreCase("perm")){
                    mutePlayer("Permanent", reason);
                    YamlAbstractBanCommand.addHistory(target, sender.getName(), "mute", reason, null);
                    if (s == 0) YamlAbstractAnnouncer.getAnnouncer(target, sdr.getName(), "Permanent", reason, annType);
                    else YamlAbstractAnnouncer.getSilentAnnouncer(target, sdr.getName(), "Permanent", reason, annType);
                    return;
                }
                String type = YamlAbstractBanCommand.getBanType(args[1+s]);

                if(type == null){
                    sender.sendMessage("§cEnter a valid ban option.");
                    return;
                }
                duration = args[1+s];
                mutePlayer("Duration", reason);
                YamlAbstractBanCommand.addHistory(target, sender.getName(), "mute", reason, null);
                if (s == 0) YamlAbstractAnnouncer.getAnnouncer(target, sdr.getName(), args[1+s], reason, annType);
                else YamlAbstractAnnouncer.getSilentAnnouncer(target, sdr.getName(), args[1+s], reason, annType);
                return;
    
            }
        });

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
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
