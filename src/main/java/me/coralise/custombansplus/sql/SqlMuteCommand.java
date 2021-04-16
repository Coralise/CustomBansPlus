package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SqlMuteCommand extends SqlAbstractCommand {

    SqlMuteCommand() {
        super("cbpmute", "custombansplus.mute", true);
    }

    CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public String target;
    public String duration;
    CommandSender sdr;
    String annType;

    public boolean setMute(String type, String reason) {

        if (reason.equalsIgnoreCase("") && !m.getConfig().getBoolean("toggle-no-reason"))
            reason = m.getConfig().getString("default-mute-reason");

        if(reason.equalsIgnoreCase(""))
            annType = "muteNoRsn";
        else
            annType = "mute";

        if (type.equalsIgnoreCase("Permanent")){
            duration = "Permanent";
        }

        SqlMethods.mutePlayer(target, sdr, reason, duration);

        return true;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        sdr = sender;
        reason = "";
        this.args = args;

        if (args.length == 0) {
            sender.sendMessage("§e/mute [-s] <player> <duration> <reason> - Mutes specified player.");
            return true;
        }

        s = 0;
        if (args.length > 0 && args[0].equalsIgnoreCase("-s"))
            s = 1;

        target = SqlCache.getPlayerIgn(args[0+s]);

        if(target == null){
            sender.sendMessage("§cPlayer " + args[0+s] + " has never played in the server.");
            return true;
        }

        if(SqlCache.isPlayerMuted(target) && !sender.hasPermission("custombansplus.overwrite")){
            sender.sendMessage("§cPlayer " + target + " is already muted and you don't have overwrites permission.");
            return true;
        }

        if(args.length > 2+s){
            for(int i = 2+s; i < args.length; i++){
                reason = reason.concat(args[i] + " ");
            }
            reason = reason.trim();
        }

        Bukkit.getScheduler().runTask(m, () -> {

            SqlMethods.updateHistoryStatus(target, "Mute", "Overwritten", sender);

        if(args.length == 1+s){
            setMute("Permanent", reason);
            SqlMethods.addHistory(target, "Mute", null, null);
            if (s == 0) SqlAbstractAnnouncer.getAnnouncer(target, sdr.getName(), "Permanent", reason, annType);
            else SqlAbstractAnnouncer.getSilentAnnouncer(target, sdr.getName(), "Permanent", reason, annType);
            return;
        }

        if(args.length >= 2+s){

            if(args[1+s].equalsIgnoreCase("perm")){
                setMute("Permanent", reason);
                SqlMethods.addHistory(target, "Mute", null, null);
                if (s == 0) SqlAbstractAnnouncer.getAnnouncer(target, sdr.getName(), "Permanent", reason, annType);
                else SqlAbstractAnnouncer.getSilentAnnouncer(target, sdr.getName(), "Permanent", reason, annType);
                return;
            }
            
            String type = SqlAbstractBanCommand.getBanType(args[1+s]);

            if(type == null){
                sender.sendMessage("§cEnter a valid ban option.");
                return;
            }

            duration = args[1+s];
            setMute("Duration", reason);
            SqlMethods.addHistory(target, "Mute", null, null);
            if (s == 0) SqlAbstractAnnouncer.getAnnouncer(target, sdr.getName(), args[1+s], reason, annType);
            else SqlAbstractAnnouncer.getSilentAnnouncer(target, sdr.getName(), args[1+s], reason, annType);

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
            
        s = 0;
        if(args[0].equalsIgnoreCase("-s"))
            s++;

        if(args.length == 2+s){
            
            tabComplete.add("perm");
            tabComplete.add("Xs");
            tabComplete.add("Xm");
            tabComplete.add("Xh");
            tabComplete.add("Xd");
            return tabComplete;

        }

        if(args.length == 3+s){

            tabComplete.add("<reason>");
            return tabComplete;

        }

        return null;
    }

    CommandSender sender;
    String[] args;
    int s;
    String reason;
    
}
