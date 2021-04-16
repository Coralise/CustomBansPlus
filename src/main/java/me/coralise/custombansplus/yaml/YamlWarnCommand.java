package me.coralise.custombansplus.yaml;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.GetJavaPlugin;

public class YamlWarnCommand extends YamlAbstractCommand {

    YamlWarnCommand() {
        super("cbpwarn", "custombansplus.warn", true);
    }

    String target;
    String reason;
    public String duration;
    CommandSender sdr;
    CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    String annType;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(!isValid(sender)){
            return false;
        }

        reason = "";

        if (args.length == 0) {
            sender.sendMessage("§e/warn [-s] <player> <duration> <reason> - Warns specified player.");
            return true;
        }

        int s = 0;
        if (args.length > 0 && args[0].equalsIgnoreCase("-s"))
            s = 1;

        Player plTarget = Bukkit.getPlayer(args[0+s]);

        if(plTarget == null){
            sender.sendMessage("§cPlayer " + args[0+s] + " is not online, they wouldn't be able to see their warn.");
            return false;
        }
        target = plTarget.getName();

        if(args.length > 1+s){
            for(int i = 1+s; i < args.length; i++){
                reason = reason.concat(args[i] + " ");
            }
            reason = reason.trim();
        }

        if (reason.equalsIgnoreCase("") && !m.getConfig().getBoolean("toggle-no-reason"))
            reason = m.getConfig().getString("default-warn-reason");
        
        if (reason.equalsIgnoreCase(""))
            annType = "warnNoRsn";
        else
            annType = "warn";

        YamlAbstractBanCommand.addHistory(target, sender.getName(), "warn", reason, null);

        if(m.getConfig().getBoolean("enable-warn-titles")){
            String subtitle = m.getConfig().getString("warn-subtitle");
            if(m.getConfig().getBoolean("custom-subtitle")) subtitle = "§f" + reason;
            plTarget.sendTitle(m.getConfig().getString("warn-title"), subtitle);
        }

        if (s == 0) YamlAbstractAnnouncer.getAnnouncer(target, sender.getName(), null, reason, annType);
        else YamlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), null, reason, annType);

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
            
        int s = 0;
        if(args[0].equalsIgnoreCase("-s"))
            s++;

        if(args.length == 2+s){

            tabComplete.add("<reason>");
            return tabComplete;

        }

        return null;

    }

}
