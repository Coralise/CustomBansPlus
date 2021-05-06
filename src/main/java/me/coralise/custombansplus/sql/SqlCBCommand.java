/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.sql;

import me.coralise.custombansplus.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author wayne
 */
public class SqlCBCommand extends SqlAbstractCommand {

    SqlCBCommand() {
        super("custombansplus", "custombansplus.admin", true);
    }

    public static final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    protected static Map<String, String> isEditing = new HashMap<String, String>();

    private void reloadConfig(CommandSender sender) {

        m.reloadConfig();
        sender.sendMessage("§aConfig file reloaded.");

    }

    public boolean helpScreen(CommandSender sender) {

        sender.sendMessage("§e/cb help - Opens up this page.");
        sender.sendMessage("§e/cb purge altaccounts - Resets alternate account logs. Use to clean memory.");
        sender.sendMessage("§e/cb purge histories - Resets player histories.");
        sender.sendMessage("§e/cb banpage <temp/perm> set - Sets the temp/perm ban page to your liking.");
        sender.sendMessage("§e/cb banpage <temp/perm> test - Mock bans the sender's connection. Use to test out ban pages.");
        sender.sendMessage("§e/cb kickpage set - Sets the kick page to your liking.");
        sender.sendMessage("§e/cb kickpage test - Mock kicks the sender's connection. Use to test out kick page.");
        sender.sendMessage("§e/cb defaultreason set - Sets a new default reason.");
        sender.sendMessage("§e/cb announcer <ban/ipban/mute/warn/kick> set - Sets a new announcer.");
        sender.sendMessage("§e/cb announcer <ban/ipban/mute/warn/kick> test - Tests the announcer on the sender.");
        return true;

    }

    public static boolean clearHistories(CommandSender sender) {

        //Deletes every finished bans, mutes, warns, kicks, ip bans.

        sender.sendMessage("§ePlayer Histories has been purged.");

        return true;

    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if(!isValid(sender)){
            return false;
        }

        try{
        
        if(args.length == 0){
            helpScreen(sender);
            return true;
        }
        
        switch(args[0]){
            
            case "help":
                helpScreen(sender);
                return true;

            case "reload":
                reloadConfig(sender);
                return true;
            
            case "purge":
                switch(args[1]){
                    case "altaccounts":
                        if(args.length == 2)
                            sender.sendMessage("§eAre you sure you wish to optimize the Alt Accounts list? Only the accounts.yml will be purged. Type §f/cb purge altaccounts confirm §eto continue.");
                        if(args.length == 3 && args[2].equalsIgnoreCase("confirm")){
                            sender.sendMessage("§eCleaning player list...");
                            SqlMethods.clearAccounts();
                            sender.sendMessage("§aPlayer list cleaned.");
                        }
                        return true;
                    case "histories":
                        if(args.length == 2)
                            sender.sendMessage("§eAre you sure you wish to clear player histories? Only the histories.yml will be purged. Type §f/cb purge histories confirm §eto continue.");
                        if(args.length == 3 && args[2].equalsIgnoreCase("confirm"))
                            clearHistories(sender);
                        return true;
                }
                break;

            case "menu":
                if(!(sender instanceof Player)){
                    sender.sendMessage("§cSorry! You can only use this command in-game.");
                    return true;
                }
                SqlCBMenu.openMainGUI((Player) sender);
                return true;
            
        }

        if(args.length > 2 && args[0].equalsIgnoreCase("announcer")){
            if(!(args[1].equalsIgnoreCase("ban") || args[1].equalsIgnoreCase("ipban") || args[1].equalsIgnoreCase("mute") || args[1].equalsIgnoreCase("kick") || args[1].equalsIgnoreCase("warn"))){
                sender.sendMessage("§Invalid input.");
                return true;
            }
            if(args[2].equalsIgnoreCase("set")){
                isEditing.put(sender.getName(), args[1]);
                sender.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                sender.sendMessage("§eFORMATS: §d%player% %staff% %duration% %reason%");
                return true;
            }
            if(args[2].equalsIgnoreCase("test")){
                String duration = null;
                if(!(args[1].equalsIgnoreCase("kick") || args[1].equalsIgnoreCase("warn"))){
                    duration = "7d";
                }
                SqlAbstractAnnouncer.getSilentAnnouncer("Victim", sender.getName(), duration, "@Reason", args[1]);
            }
        }
        
        if((args[0].equalsIgnoreCase("banpage") || args[0].equalsIgnoreCase("kickpage")) && !(sender instanceof Player)){
            sender.sendMessage("§cSorry! You can only use this command in-game.");
            return true;
        }
        
        if(args.length > 2 && args[0].concat(args[1]).concat(args[2]).equalsIgnoreCase("banpagetempset")){
            isEditing.put(sender.getName(), "temp");
            sender.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
            sender.sendMessage("§eFORMATS: §d%staff% %duration% %reason% %unban-date% %player% %timeleft% /n");
            return true;
        }
        if(args.length > 2 && args[0].concat(args[1]).concat(args[2]).equalsIgnoreCase("banpagetemptest")){
            if(!(sender instanceof Player)){
                sender.sendMessage("§cYou're in the console, silly!");
                return true;
            }
            Bukkit.getPlayer(sender.getName()).kickPlayer(SqlAbstractBanCommand.getBanMsgTest(m.getUuid(sender), "temp"));
            return true;
        }
        if(args.length > 2 && args[0].concat(args[1]).concat(args[2]).equalsIgnoreCase("banpagepermset")){
            isEditing.put(sender.getName(), "perm");
            sender.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
            sender.sendMessage("§eFORMATS: §d%staff% %duration% %reason% %unban-date% %player% %timeleft% /n");
            return true;
        }
        if(args.length > 2 && args[0].concat(args[1]).concat(args[2]).equalsIgnoreCase("banpagepermtest")){
            if(!(sender instanceof Player)){
                sender.sendMessage("§cYou're in the console, silly!");
                return true;
            }
            Bukkit.getPlayer(sender.getName()).kickPlayer(SqlAbstractBanCommand.getBanMsgTest(m.getUuid(sender), "perm"));
            return true;
        }
        if(args[0].concat(args[1]).equalsIgnoreCase("kickpageset")){
            isEditing.put(sender.getName(), "kickPage");
            sender.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
            sender.sendMessage("§eFORMATS: §d%staff% %reason% %player% /n");
            return true;
        }
        if(args[0].concat(args[1]).equalsIgnoreCase("kickpagetest")){
            if(!(sender instanceof Player)){
                sender.sendMessage("§cYou're in the console, silly!");
                return true;
            }
            Bukkit.getPlayer(sender.getName()).kickPlayer(SqlKickCommand.getKickMsg(true));
            return true;
        }
        if(args[0].concat(args[1]).equalsIgnoreCase("defaultreasonset")){
            isEditing.put(sender.getName(), "defaultreason");
            sender.sendMessage("§eType in the new default reason you want down below. Type \"cancel\" to cancel action.");
            return true;
        }
        
        }catch(ArrayIndexOutOfBoundsException arr){
            sender.sendMessage("§cInvalid input.");
        }
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> tabComplete = new ArrayList<String>();
        
        switch(args.length){
            
            case 1:
                tabComplete.add("help");
                tabComplete.add("reload");
                tabComplete.add("purge");
                tabComplete.add("banpage");
                tabComplete.add("kickpage");
                tabComplete.add("defaultreason");
                tabComplete.add("announcer");
                tabComplete.add("menu");
                return tabComplete;
            case 2:
                switch(args[0]){
                    case "purge":
                        tabComplete.add("altaccounts");
                        tabComplete.add("histories");
                        return tabComplete;
                    case "banpage":
                        tabComplete.add("temp");
                        tabComplete.add("perm");
                        return tabComplete;
                    case "kickpage":
                        tabComplete.add("set");
                        tabComplete.add("test");
                        return tabComplete;
                    case "defaultreason":
                        tabComplete.add("set");
                        return tabComplete;
                    case "announcer":
                        tabComplete.add("ban");
                        tabComplete.add("ipban");
                        tabComplete.add("mute");
                        tabComplete.add("warn");
                        tabComplete.add("kick");
                        return tabComplete;
                }
                break;
            case 3:
                if(args[0].equalsIgnoreCase("banpage") && (args[1].equalsIgnoreCase("temp") || args[1].equalsIgnoreCase("perm"))){
                    tabComplete.add("set");
                    tabComplete.add("test");
                    return tabComplete;
                }
                if(args[0].equalsIgnoreCase("announcer")){
                    tabComplete.add("set");
                    tabComplete.add("test");
                    return tabComplete;
                }
                if(args[0].concat(args[1]).equalsIgnoreCase("purgealtaccounts") || args[0].concat(args[1]).equalsIgnoreCase("purgehistories") || args[0].concat(args[1]).equalsIgnoreCase("purgeseverities")){
                    tabComplete.add("confirm");
                    return tabComplete;
                }
            
        }
        
        return tabComplete;
        
    }
    
} // /cb kickpage set