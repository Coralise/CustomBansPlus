package me.coralise.custombansplus.sql;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import me.coralise.custombansplus.*;

public class SqlMain implements Listener{

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();

    String currentIP;
    String currentIGN;
    PlayerJoinEvent event;

    @EventHandler
    public boolean onLogin(AsyncPlayerPreLoginEvent event){
        
        String stPlayer = event.getName();

        if(SqlCache.isPlayerBanned(stPlayer)){
            if(!SqlCache.isBanLifted(stPlayer)){
                event.disallow(Result.KICK_BANNED, SqlAbstractBanCommand.getBanMsg(stPlayer, null));
            }else{
                if(SqlCache.isIpBanned(m.getSqlIp(stPlayer)))
                    SqlCache.removeIpBan(m.getSqlIp(stPlayer), "Lifted", null);
                else{
                    SqlMethods.updateHistoryStatus(stPlayer, "Ban", "Lifted", null);
                    SqlCache.removeBan(stPlayer);
                }
            }
        }

        return false;
        
    }

    @EventHandler
    public boolean onPlayerJoin(PlayerJoinEvent event){
        
        currentIGN = event.getPlayer().getName();
        currentIP = m.getSqlIp(currentIGN);
        this.event = event;

        UpdateChecker.checkUpdate(event.getPlayer());

        if (SqlCache.getOciCache().contains(m.getUuid(currentIGN))) {
            Bukkit.getPlayer(currentIGN).getInventory().clear();
            SqlCache.getOciCache().remove(m.getUuid(currentIGN));
            m.updateSqlOci();
        }

        if (!SqlCache.isPlayerLogged(currentIGN)) {
            SqlCache.setNewPlayer(currentIGN, "new");
        } else if (SqlCache.isIpDifferent(currentIGN, currentIP))
            SqlCache.setNewPlayer(currentIGN, "ip");
        else
            return true;
            
        SqlCache.setPlayer(currentIGN);

        Bukkit.getScheduler().runTask(m, () -> {

            if (SqlCache.isIpBanned(currentIP)) {
                SqlMethods.banPlayer(currentIGN);
                SqlMethods.addHistory(currentIGN, "Ban", null, null);
                event.getPlayer().kickPlayer(SqlAbstractBanCommand.getBanMsg(currentIGN, null));
            }
    
            if (SqlCache.isPlayerNew("-" + currentIGN) && SqlCache.isPlayerBanned(currentIGN)) {
                if (!SqlCache.isBanLifted(currentIGN)) {
                    event.getPlayer().kickPlayer(SqlAbstractBanCommand.getBanMsg(currentIGN, null));
                } else {
                    if (SqlCache.isIpBanned(m.getSqlIp(currentIGN)))
                        SqlCache.removeIpBan(m.getSqlIp(currentIGN), "Lifted", null);
                    else {
                        SqlMethods.updateHistoryStatus(currentIGN, "Ban", "Lifted", null);
                        SqlCache.removeBan(currentIGN);
                    }
                }
            }

        });
        
        return true;
        
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        String strPlayer = event.getPlayer().getName();
        Player player = Bukkit.getPlayer(strPlayer);

        if (SqlCache.isPlayerMuted(strPlayer)) {

            if (!SqlCache.isMuteLifted(strPlayer)) {
                String message = m.getConfig().getString("muted-player-message");
                player.sendMessage(message);
                event.setCancelled(true);
            } else {
                SqlCache.removeMute(strPlayer);
                SqlMethods.updateHistoryStatus(strPlayer, "Mute", "Lifted", null);
            }

        }
        
        if (!SqlCBCommand.isEditing.containsKey(event.getPlayer().getName()))
            return;

        //-------------------------------------------------//

        event.setCancelled(true);
        
        if(event.getMessage().equals("cancel")){
            SqlCBCommand.isEditing.remove(strPlayer);
            event.getPlayer().sendMessage("§eAction cancelled.");
            return;
        }

        String edit = SqlCBCommand.isEditing.get(strPlayer);

        if(edit.substring(edit.length()-3).equalsIgnoreCase("GUI")){

            String[] args = new String[4];
            int sevLength = 0;
            args[0] = "edit";
            try{
                Integer.parseInt(edit.substring(0, 2));
                sevLength = 2;
            }catch(NumberFormatException nfe){
                sevLength = 1;
            }
            args[1] = edit.substring(0, sevLength);
            args[2] = edit.substring(sevLength, edit.length()-3);
            args[3] = event.getMessage();
            SqlSevCommand.editSev(args, event.getPlayer());
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
            
        }
        
        if(edit.equalsIgnoreCase("temp")){
            m.getConfig().set("tempban-page", event.getMessage());
            event.getPlayer().sendMessage("§aTemp Ban Page successfully updated.");
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("perm")){
            m.getConfig().set("permban-page", event.getMessage());
            event.getPlayer().sendMessage("§aPerm Ban Page successfully updated.");
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("kickPage")){
            m.getConfig().set("kick-page", event.getMessage());
            event.getPlayer().sendMessage("§aKick Page successfully updated.");
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("defaultreason")){
            m.getConfig().set("default-reason", event.getMessage());
            event.getPlayer().sendMessage("§aDefault Reason successfully updated.");
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
        }

        m.getConfig().set(edit+"-announcer", event.getMessage());
        event.getPlayer().sendMessage("§aThe "+ edit + " announcement is successfully updated.");
        SqlCBCommand.isEditing.remove(strPlayer);
        
        m.saveConfig();
        
    }
    
}
