package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class YamlUnmuteCommand extends YamlAbstractCommand {

    YamlUnmuteCommand() {
        super("cbpunmute", "custombansplus.unmute", true);
    }

    CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    String target;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        int s = 0;
        
        if(args.length != 0 && args[0].equalsIgnoreCase("-s"))
            s = 1;

        if(args.length < 1+s){
            sender.sendMessage("§e/unmute <player> - Unmutes a player.");
            return true;
        }

        target = "";

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if (p.getName().equalsIgnoreCase(args[0+s])) {
                target = p.getName();
                break;
            }
        }

        if (target.equalsIgnoreCase("")) {
            sender.sendMessage("§cPlayer " + args[0+s] + " has never been in the server.");
            return true;
        }

        if (args.length != 1 || !m.getMutesConfig().getKeys(false).contains(m.getUuid(target))) {
            sender.sendMessage("§cPlayer " + target + " is not muted.");
            return true;
        }

        Bukkit.getScheduler().runTask(m, () -> {
            YamlCache.removeMute(m.getUuid(target));
        });

        if (s == 0) YamlAbstractAnnouncer.getAnnouncer(target, sender.getName(), null, null, "unmute");
        else YamlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), null, null, "unmute");

        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
