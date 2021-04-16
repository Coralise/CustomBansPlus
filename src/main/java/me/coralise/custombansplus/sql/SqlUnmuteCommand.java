package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SqlUnmuteCommand extends SqlAbstractCommand {

    SqlUnmuteCommand() {
        super("cbpunmute", "custombansplus.unmute", true);
    }

    CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        if(args.length == 0){
            sender.sendMessage("§e/unmute <player> - Unmutes a player.");
            return true;
        }

        int s = 0;
        
        if(args[0].equalsIgnoreCase("-s"))
            s = 1;

        target = SqlCache.getPlayerIgn(args[0+s]);
        this.sender = sender;

        if (target.equalsIgnoreCase("")) {
            sender.sendMessage("§cPlayer " + args[0+s] + " has never been in the server.");
            return true;
        }

        if (!SqlCache.isPlayerMuted(target)) {
            sender.sendMessage("§cPlayer " + target + " is not muted.");
            return true;
        }

        Bukkit.getScheduler().runTask(m, () -> {

            SqlCache.removeMute(target);
            SqlMethods.updateHistoryStatus(target, "Mute", "Unmuted", sender);

        });

        if (s == 0) SqlAbstractAnnouncer.getAnnouncer(target, sender.getName(), null, null, "unmute");
        else SqlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), null, null, "unmute");

        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    String target;
    CommandSender sender;
    
}
