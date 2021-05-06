package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;
import me.coralise.custombansplus.enums.MuteType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SqlMuteCommand extends SqlAbstractCommand {

    SqlMuteCommand() {
        super("cbpmute", "custombansplus.mute", true);
    }

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        CommandSender sdr = sender;
        reason = "";
        this.args = args;

        s = 0;
        if (args.length > 0 && args[0].equalsIgnoreCase("-s"))
            s = 1;

        if (args.length < 2+s) {
            sender.sendMessage("§e/mute [-s] <player> <duration> <reason> - Mutes specified player.");
            return false;
        }

        String target = SqlCache.getPlayerIgn(args[0+s]);

        if(target == null){
            sender.sendMessage("§cPlayer " + args[0+s] + " has never played in the server.");
            return true;
        }

        UUID tgtUuid = m.getUuid(target);

        if(SqlCache.isPlayerMuted(tgtUuid) && !sender.hasPermission("custombansplus.overwrite")){
            sender.sendMessage("§cPlayer " + target + " is already muted and you don't have overwrites permission.");
            return true;
        }

        if(args.length > 2+s){
            for(int i = 2+s; i < args.length; i++){
                reason = reason.concat(args[i] + " ");
            }
            reason = reason.trim();
        }

        if (reason.equalsIgnoreCase("") && !m.getConfig().getBoolean("toggle-no-reason"))
            reason = m.parseMessage(m.getConfig().getString("defaults.mute-reason"));

        String annType;
        if(reason.equalsIgnoreCase(""))
            annType = "muteNoRsn";
        else
            annType = "mute";

        String value = args[1+s];
        if (m.getType(value) == null) {
            sender.sendMessage("§cEnter a valid mute option.");
            return false;
        }
        String duration = m.getSevDuration(value);
        MuteType muteType = m.getMuteType(duration);

        new Thread(() ->{
            try {
                SqlMethods.updateHistoryStatus(tgtUuid, "Mute", "Overwritten", sender);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            if (sender instanceof Player)
                SqlCache.setMute(tgtUuid, muteType, reason, duration, m.getUuid(sender));
            else
                SqlCache.setMute(tgtUuid, muteType, reason, duration);

            try {
                SqlMethods.addHistory(tgtUuid, "Mute", null, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String annValue = duration;
            if (duration.equalsIgnoreCase("perm")) annValue = "Permanent";
            
            if (s == 0) SqlAbstractAnnouncer.getAnnouncer(target, sdr.getName(), annValue, reason, annType);
            else SqlAbstractAnnouncer.getSilentAnnouncer(target, sdr.getName(), annValue, reason, annType);
        }).start();

        return true;
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
