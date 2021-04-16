/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public final CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();
    ConsoleCommandSender cnsl = Bukkit.getServer().getConsoleSender();

    String target;
    String tgtUuid;
    String targetIP;

    public void removeBans(){
        if(m.getBansConfig().getKeys(false).contains(targetIP)){
            YamlCache.getSameIps(targetIP).forEach(ign -> {
                YamlCache.getOciCache().remove(tgtUuid);
                m.updateYamlOci();
                YamlCache.removeBan(m.getUuid(ign));
            });
            m.getBansConfig().set(targetIP, null);
        }else if(m.getBansConfig().getKeys(false).contains(tgtUuid)){
            YamlCache.getOciCache().remove(tgtUuid);
            m.updateYamlOci();
            YamlCache.removeBan(tgtUuid);
        }

        try {
            m.getBansConfig().save(m.getBansFile());
        } catch (IOException ex) {
            Logger.getLogger(YamlUnbanCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
        
        targetIP = m.getYamlIp(target);
        
        if(!m.getBansConfig().getKeys(false).contains(tgtUuid) && !m.getBansConfig().getKeys(false).contains(targetIP)){
            sender.sendMessage("§cPlayer " + target + " is not banned.");
            return true;
        }
        
        Bukkit.getScheduler().runTask(m, () -> {
            removeBans();
        });
        
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
