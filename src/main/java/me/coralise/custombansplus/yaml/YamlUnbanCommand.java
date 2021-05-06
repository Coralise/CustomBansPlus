package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 *
 * @author Coralise
 */
public class YamlUnbanCommand extends YamlAbstractCommand{

    public YamlUnbanCommand() {
        super("cbpunban", "custombansplus.unban", true);
    }
    
    public final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();

    String target;
    UUID tgtUuid;
    String targetIP;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        int s = 0;
        
        if(args.length != 0 && args[0].equalsIgnoreCase("-s"))
            s = 1;
        
        if(args.length < 1+s){
            sender.sendMessage("§e/unban <player> - Unbans specified player.");
            return true;
        }
        
        target = YamlCache.getPlayerIgn(args[0+s]);

        if(target == null){
            sender.sendMessage("§cPlayer " + args[0+s] + " has never been on the server before.");
            return false;
        }

        tgtUuid = m.getUuid(target);
        
        targetIP = m.getYamlIp(tgtUuid);
        
        if(!YamlCache.isPlayerBanned(tgtUuid)){
            sender.sendMessage("§cPlayer " + target + " is not banned.");
            return true;
        }

        new Thread(() -> YamlCache.removeBan(tgtUuid)).start();
        
        if (s == 0) YamlAbstractAnnouncer.getAnnouncer(target, sender.getName(), null, null, "unban");
        else YamlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), null, null, "unban");
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> tabComplete = new ArrayList<String>();

        tabComplete.add("placeholder");

        return null;
        
    }
    
}
