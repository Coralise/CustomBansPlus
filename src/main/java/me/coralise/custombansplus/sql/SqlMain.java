package me.coralise.custombansplus.sql;

import java.util.UUID;

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

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    PlayerJoinEvent event;

    @EventHandler
    public boolean onLogin(AsyncPlayerPreLoginEvent event){
        
        String stPlayer = event.getName();
        UUID uuid = m.getUuid(stPlayer);
        String ip = event.getAddress().toString();
        ip = ip.substring(1);

        SqlCache.setPlayer(uuid, ip);

        if (SqlCache.isPlayerBanned(uuid)) {
            if (!SqlCache.isBanLifted(uuid)) {
                event.disallow(Result.KICK_BANNED, SqlAbstractBanCommand.getBanMsg(uuid));
                return false;
            } else {
                SqlCache.removeBan(uuid, "Lifted", null);
            }
        }

        if (SqlCache.isIpBanned(ip)) {
            if (!SqlCache.isBanLifted(ip)) {
                SqlCache.copyIPBan(uuid);
                event.disallow(Result.KICK_BANNED, SqlAbstractBanCommand.getBanMsg(uuid));
                return false;
            } else {
                SqlCache.removeIpBan(ip, "Lifted", null);
            }
        }

        return true;
        
    }

    @EventHandler
    public boolean onPlayerJoin(PlayerJoinEvent event){
        
        String currentIGN = event.getPlayer().getName();
        UUID uuid = event.getPlayer().getUniqueId();

        UpdateChecker.checkUpdate(event.getPlayer());

        new Thread(() -> {

            if(SqlCache.getOciCache().contains(uuid)){
                Bukkit.getPlayer(currentIGN).getInventory().clear();
                SqlCache.getOciCache().remove(uuid);
                m.updateSqlOci();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (SqlCache.isPlayerBanned(uuid)) {
                if (!SqlCache.isBanLifted(uuid)) {
                    Bukkit.getScheduler().runTask(m, () -> event.getPlayer().kickPlayer(SqlAbstractBanCommand.getBanMsg(uuid)));
                } else {
                    SqlCache.removeBan(uuid, "Lifted", null);
                }
            }

        }).start();
        
        return true;
        
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        String strPlayer = event.getPlayer().getName();
        Player player = Bukkit.getPlayer(strPlayer);
        UUID uuid = player.getUniqueId();

        if (SqlCache.isPlayerMuted(uuid)) {

            if (!SqlCache.isMuteLifted(uuid)) {
                String message = m.parseMessage(m.getConfig().getString("messages.muted-player"));
                player.sendMessage(message);
                event.setCancelled(true);
            } else {
                SqlCache.removeMute(uuid, "Lifted", null);
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
            m.getConfig().set("pages.tempban", event.getMessage());
            event.getPlayer().sendMessage("§aTemp Ban Page successfully updated.");
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("perm")){
            m.getConfig().set("pages.permban", event.getMessage());
            event.getPlayer().sendMessage("§aPerm Ban Page successfully updated.");
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("kickPage")){
            m.getConfig().set("pages.kick", event.getMessage());
            event.getPlayer().sendMessage("§aKick Page successfully updated.");
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("defaultreason")){
            m.getConfig().set("defaults.reason", event.getMessage());
            event.getPlayer().sendMessage("§aDefault Reason successfully updated.");
            SqlCBCommand.isEditing.remove(strPlayer);
            return;
        }

        m.getConfig().set("announcers." + edit, event.getMessage());
        event.getPlayer().sendMessage("§aThe "+ edit + " announcement is successfully updated.");
        SqlCBCommand.isEditing.remove(strPlayer);
        
        m.saveConfig();
        
    }
    
}
