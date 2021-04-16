package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SqlKickCommand extends SqlAbstractCommand {

    SqlKickCommand() {
        super("cbpkick", "custombansplus.kick", true);
    }

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    static Player plTarget;
    static CommandSender sdr;
    static String rsn;
    String target;
    String[] args;
    CommandSender sender;
    String annType;

    public static String getKickMsg(boolean test){

        // %staff% %reason% %player%
        String tgt;
        String send;
        String reason;
        if(test){
            tgt = "Victim";
            send = "Kicker";
            reason = "Eating pizza with pineapple.";
        }else{
            tgt = plTarget.getName();
            send = sdr.getName();
            reason = rsn;
        }
        
        String msg = m.getConfig().getString("kick-page");

        msg = msg.replace(" /n ", "\n");
        msg = msg.replace("/n ", "\n");
        msg = msg.replace(" /n", "\n");
        msg = msg.replace("/n", "\n");
        msg = msg.replace("%player%", tgt);
        msg = msg.replace("%staff%", send);
        msg = msg.replace("%reason%", reason);
        msg = msg.replace("&", "§");
    
        return msg;
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }
        
        sdr = sender;

        if(args.length == 0){
            sender.sendMessage("§e/kick [-s] <player> <reason> - Kicks specified player.");
            return true;
        }

        int s = 0;
        if(args.length > 1 && args[0].equalsIgnoreCase("-s"))
            s = 1;

        plTarget = Bukkit.getPlayer(args[0+s]);
        target = SqlCache.getPlayerIgn(args[0+s]);

        if(plTarget == null){
            sender.sendMessage("§cPlayer " + args[0+s] + " is not online.");
            return true;
        }
        
        rsn = "";
        if(args.length > 1+s){
            for(int i = 1+s; i < args.length; i++){
                rsn = rsn.concat(args[i] + " ");
            }
            rsn = rsn.trim();
        }else if(!m.getConfig().getBoolean("toggle-no-reason"))
            rsn = m.getConfig().getString("default-reason");

        if(rsn.equalsIgnoreCase(""))
            annType = "kickNoRsn";
        else
            annType = "kick";

        Bukkit.getScheduler().runTask(m, () -> {

            plTarget.kickPlayer(getKickMsg(false));

            SqlMethods.addHistory(target, "Kick", sender, rsn);

        });

        if (s == 0) SqlAbstractAnnouncer.getAnnouncer(plTarget.getName(), sdr.getName(), null, rsn, annType);
        else SqlAbstractAnnouncer.getSilentAnnouncer(plTarget.getName(), sdr.getName(), null, rsn, annType);

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> tabComplete = new ArrayList<String>();

        int s = 0;
        if(args.length > 1 && args[0].equalsIgnoreCase("-s"))
            s = 1;

        if(args.length == 1){
            if(!args[0].contains("-") && args[0].length() != 0) return null;
            tabComplete.add("-s");
            Bukkit.getOnlinePlayers().forEach(p -> {tabComplete.add(p.getName());});
            return tabComplete;
        }

        if(args.length == 2+s){
            tabComplete.add("<reason>");
            return tabComplete;
        }

        return null;
    }
    
}
