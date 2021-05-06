/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author wayne
 */
public class YamlSevCommand extends YamlAbstractCommand{
    
    YamlSevCommand(){
        super("severities", null, true);
    }
    
    public static final CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    public int sevTotal;
    public int pageTotal;
    public int page;
    public static CommandSender sender;
    static boolean check = false;
    static int count = 0;

    public static boolean addSeverity() {

        if(m.getSevConfig().getKeys(false).size() == 14){
            return false;
        }

        int sev = 0;

        sev = m.getSevConfig().getKeys(false).stream().map(_item -> 1).reduce(sev, Integer::sum);
        
        sev++;
        
        ArrayList<String> cmds = new ArrayList<String>();
        
        m.getSevConfig().set(sev+".duration", "3d");
        m.getSevConfig().set(sev+".baldeduct", .3);
        m.getSevConfig().set(sev+".clear-inv", false);
        m.getSevConfig().set(sev+".console-commands", cmds);
        try {
            m.getSevConfig().save(m.getSevFile());
        } catch (IOException ex) {
            Logger.getLogger(CustomBansPlus.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
        
    }
    public static boolean deleteSeverity(int severity){
        m.getSevConfig().getKeys(false).forEach(p -> {
            count++;
            if(severity == Integer.parseInt(p))
                check = true;
        });
        
        if(!check){
            sender.sendMessage(ChatColor.RED + "Please enter a valid severity number.");
            return false;
        }
        
        for(int x = 1;x < count+1;x++){
            if(x == count){
                m.getSevConfig().set(String.valueOf(x), null);
                break;
            }
            if(x >= severity){
                int y=x+1;
                m.getSevConfig().set(x+".duration", m.getSevConfig().getString(y+".duration"));
                m.getSevConfig().set(x+".baldeduct", m.getSevConfig().getDouble(y+".baldeduct"));
                m.getSevConfig().set(x+".clear-inv", m.getSevConfig().getBoolean(y+".clear-inv"));
                m.getSevConfig().set(x+".console-commands", m.getSevConfig().getList(y+".console-commands"));
            }
        }
        
        try {
            m.getSevConfig().save(m.getSevFile());
        } catch (IOException ex) {
            Logger.getLogger(CustomBansPlus.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
        
    }
    public boolean showSevs(CommandSender sender){

        int sevStart = 3 * (page - 1) + 1;
        int sevEnd = sevStart + 2;
        if(sevEnd > sevTotal)
            sevEnd = sevTotal;
        
        sender.sendMessage("§a---Severities §f" + sevStart + "-" + sevEnd + "§a/§f" + sevTotal + "§a---");
        
        for(int p = sevStart;p < sevStart + 3;p++){
            
            if(m.getSevConfig().getString(p+".duration") == null)
                break;
            sender.sendMessage("§aSeverity # §e" + p);
            sender.sendMessage("Duration: " + ChatColor.GREEN + m.getSevConfig().getString(p+".duration"));
            Double balDeduct = m.getSevConfig().getDouble(p+".baldeduct") * 100;
            sender.sendMessage("Balance Deduction: " + ChatColor.GREEN + balDeduct + "%");
            sender.sendMessage("Clear Inventory: " + ChatColor.GREEN + m.getSevConfig().getBoolean(p+".clear-inv"));
            sender.sendMessage("Console Commands: " + ChatColor.GREEN + m.getSevConfig().getList(p+".console-commands"));
            
        }
        sender.sendMessage("§aPage §f" + page + "§a/§f" + pageTotal + "§a --- /sev <page>");
        
        return true;
        
    }
    public static boolean editSev(String[] args, CommandSender sender){
        
        int sevNum = Integer.parseInt(args[1]);
        
        boolean check = false;
        
        if(!m.getSevConfig().getKeys(false).contains(String.valueOf(sevNum))){
            if(sender instanceof Player)
                sender.sendMessage(ChatColor.RED + "Please enter a valid severity number.");
            return true;
        }
        
        check = false;
        
        if(args[2].equalsIgnoreCase("duration") || args[2].equalsIgnoreCase("baldeduct") || args[2].equalsIgnoreCase("clearinv") || args[2].equalsIgnoreCase("console-cmds"))
            check = true;
        
        if(!check){
            sender.sendMessage(ChatColor.RED + "Please enter a valid option.");
            return true;
        }
        
        switch(args[2]){
            
            case "duration":
                if(!m.isValueValid(args[3])){
                    sender.sendMessage(ChatColor.RED + "Please enter a valid input.");
                    return false;
                }
                m.getSevConfig().set(args[1]+".duration", args[3]);
                try {
                    m.getSevConfig().save(m.getSevFile());
                } catch (IOException ex) {
                    Logger.getLogger(CustomBansPlus.class.getName()).log(Level.SEVERE, null, ex);
                }
                sender.sendMessage("§aDuration of Severity " + args[1] + " set to " + args[3] + ".");
                return true;
                
            case "baldeduct":
                check = false;
                double test = 0;
                int p = 0;
                if(args[3].substring(args[3].length()-1).equalsIgnoreCase("%"))
                    p++;
                try{
                    test = Double.parseDouble(args[3].substring(0, args[3].length()-p));
                }catch(NumberFormatException nfe){
                    check = true;
                }
                if(test < 0 || test > 100)
                    check = true;
                
                if(check){
                    sender.sendMessage(ChatColor.RED + "Please enter a valid input.");
                    return true;
                }
                
                Double percent = Double.parseDouble(args[3].substring(0,args[3].length()-p));
                percent = percent / 100;
                
                m.getSevConfig().set(args[1]+".baldeduct", percent);
                try {
                    m.getSevConfig().save(m.getSevFile());
                } catch (IOException ex) {
                    Logger.getLogger(CustomBansPlus.class.getName()).log(Level.SEVERE, null, ex);
                }
                sender.sendMessage("§aBalance Deduction of Severity " + args[1] + " set to " + args[3].substring(0,args[3].length()-p) + "%.");
                return true;
            
            case "clearinv":
                if(!args[3].equalsIgnoreCase("true") && !args[3].equalsIgnoreCase("false")){
                    sender.sendMessage("§cPlease enter a valid input.");
                    return true;
                }
                Boolean set = Boolean.parseBoolean(args[3]);
                m.getSevConfig().set(args[1]+".clear-inv", set);
                try {
                    m.getSevConfig().save(m.getSevFile());
                } catch (IOException ex) {
                    Logger.getLogger(CustomBansPlus.class.getName()).log(Level.SEVERE, null, ex);
                }
                sender.sendMessage("§aClear Inventory of Severity " + args[1] + " set to " + args[3] + ".");
                return true;
                
            case "console-cmds":
                String allCmds = "";

                if(args[3].equalsIgnoreCase("clear")){
                m.getSevConfig().set(args[1]+".console-commands", "[]");
                    try {
                        m.getSevConfig().save(m.getSevFile());
                    } catch (IOException ex) {
                        Logger.getLogger(CustomBansPlus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    sender.sendMessage("§aConsole Commands for Severity " + args[1] + " cleared.");
                    return true;
                }

                for(int x = 3;x < args.length;x++){
                    allCmds = allCmds + " " + args[x];
                }
                String[] cmds = allCmds.split(",");
                ArrayList<String> cCmds = new ArrayList<String>();
                cCmds.addAll(Arrays.asList(cmds));
                for(int x = 0;x < cCmds.size();x++)
                    cCmds.set(x, cCmds.get(x).substring(1));
                m.getSevConfig().set(args[1]+".console-commands", cCmds);
                try {
                    m.getSevConfig().save(m.getSevFile());
                } catch (IOException ex) {
                    Logger.getLogger(CustomBansPlus.class.getName()).log(Level.SEVERE, null, ex);
                }
                sender.sendMessage("§aConsole Commands of Severity " + args[1] + " set to: ");
                m.getSevConfig().getStringList(args[1]+".console-commands").forEach(o -> {sender.sendMessage(ChatColor.GREEN + o);});
                return true;

            
        }
        
        return true;

        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){

        if(!isValid(sender)){
            return false;
        }

        YamlSevCommand.sender = sender;

        if(!sender.hasPermission("custombansplus.ban")){
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        sevTotal = m.getSevConfig().getKeys(false).size();
        pageTotal = (sevTotal / 3);
        if(sevTotal % 3 != 0){
            pageTotal++;
        }

        if(args.length == 0){
            if(!(sender instanceof Player)){
                sender.sendMessage("§cYou must be in-game to use this command.");
                System.out.println("Player Cache");
                YamlCache.printOutPlayerCache();
                System.out.println("Ban Cache");
                YamlCache.printOutBanCache();
                System.out.println("Mute Cache");
                YamlCache.printOutMuteCache();
                return true;
            }
            if(sender.hasPermission("custombansplus.admin")){
                YamlCBMenu.openSevsAdminGUI((Player) sender);
            }else{
                YamlCBMenu.openSevsListGUI((Player) sender);
            }
            return true;
        }
        check = false;
        try{
        Integer.parseInt(args[0]);
        check = true;
        }catch(NumberFormatException nfe){
        }

        if(check){
            try{
                if(Integer.parseInt(args[0]) > 0 && Integer.parseInt(args[0]) <= pageTotal){
                    page = Integer.parseInt(args[0]);
                    showSevs(sender);
                    return true;
                }else{
                    sender.sendMessage("§cInvalid input.");
                    return true;
                }
            }catch(NumberFormatException nfe){
                sender.sendMessage("§cInvalid input.");
                return true;
            }
        }

        if(!sender.hasPermission("custombansplus.admin")){
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        switch(args[0]){

            case "help":
                sender.sendMessage(ChatColor.YELLOW + "/sev - List all severities.");
                sender.sendMessage(ChatColor.YELLOW + "/sev add - Adds a new severity with default options.");
                sender.sendMessage(ChatColor.YELLOW + "/sev delete <number> - Deletes an existing severity.");
                sender.sendMessage(ChatColor.YELLOW + "/sev edit <number> <option> <value> - Edits an existing severity's value.");
                sender.sendMessage(ChatColor.YELLOW + "/sev <page> - Shows severities by page.");
                return true;

            case "add":
                if(addSeverity())
                    sender.sendMessage(ChatColor.GREEN + "New severity has been added.");
                else
                    sender.sendMessage(ChatColor.RED + "Reached maximum amount of severities (14).");
                return true;

            case "delete":
                if(args.length==1){
                    sender.sendMessage(ChatColor.RED + "Please enter a valid severity number.");
                    return true;
                }
                if(deleteSeverity(Integer.parseInt(args[1])))
                    sender.sendMessage(ChatColor.GREEN + "Severity #" + args[1] + " successfully deleted.");
                return true;

            case "edit":
                if(args.length<4){
                    sender.sendMessage(ChatColor.RED + "Please enter a valid input.");
                    return true;
                }
                editSev(args, sender);
                return true;

        }
        
        return true;
        
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> tabComplete = new ArrayList<String>();
        
        switch(args.length){
                
            case 1:
                tabComplete.add("help");
                tabComplete.add("add");
                tabComplete.add("delete");
                tabComplete.add("edit");
                for(int p = 1; p <= pageTotal;p++)
                    tabComplete.add(String.valueOf(p));
                return tabComplete;
            case 2:
                if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("edit")){
                    m.getSevConfig().getKeys(false).forEach(p -> {tabComplete.add(p);});
                }
                return tabComplete;
            case 3:
                if(args[0].equalsIgnoreCase("edit")){
                    tabComplete.add("duration");
                    tabComplete.add("baldeduct");
                    tabComplete.add("clearinv");
                    tabComplete.add("console-cmds");
                }
                return tabComplete;
            case 4:
                if(args[2].equalsIgnoreCase("duration")){
                    try{
                    tabComplete.add("Current: " + m.getSevConfig().getString(args[1] + ".duration"));
                    }catch(NullPointerException npe){
                        //none
                    }
                    tabComplete.add("Xs");
                    tabComplete.add("Xm");
                    tabComplete.add("Xh");
                    tabComplete.add("Xd");
                    tabComplete.add("perm");
                }
                if(args[2].equalsIgnoreCase("baldeduct")){
                    try{
                    double current = Double.valueOf(m.getSevConfig().getString(args[1] + ".baldeduct")) * 100;
                    tabComplete.add("Current: " + current + "%");
                    }catch(NullPointerException npe){
                        //none
                    }
                    tabComplete.add("X%");
                }
                if(args[2].equalsIgnoreCase("clearinv")){
                    try{
                    tabComplete.add("Current: " + m.getSevConfig().getString(args[1] + ".clear-inv"));
                    }catch(NullPointerException npe){
                        //none
                    }
                    tabComplete.add("true");
                    tabComplete.add("false");
                }
                if(args[2].equalsIgnoreCase("console-cmds")){
                    tabComplete.add("command without /. Separate each with comma (commandx,commandy). Enter player as \"%player%\" (reset %player%).");
                    tabComplete.add("clear");
                }
                return tabComplete;

        }
        
        return tabComplete;
        
    }
    
}
