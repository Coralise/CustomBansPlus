package me.coralise.custombansplus.yaml;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import me.coralise.custombansplus.*;

public class YamlMain implements Listener{

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    @EventHandler
    public boolean onLogin(AsyncPlayerPreLoginEvent event){
        
        String stPlayer = event.getName();
        UUID uuid = m.getUuid(stPlayer);
        String currentIP = event.getAddress().toString();
        currentIP = currentIP.substring(1);
        currentIP = currentIP.replace('.', '-');

        YamlCache.setPlayer(uuid, currentIP);

        if (YamlCache.isPlayerBanned(uuid)) {
            if (!YamlCache.isBanLifted(uuid)) {
                event.disallow(Result.KICK_BANNED, YamlAbstractBanCommand.getBanMsg(uuid));
                return false;
            } else {
                YamlCache.removeBan(uuid);
                return true;
            }
        }

        if (YamlCache.isIpBanned(currentIP)) {
            if (!YamlCache.isBanLifted(currentIP)) {
                YamlCache.copyIPBan(uuid);
                event.disallow(Result.KICK_BANNED, YamlAbstractBanCommand.getBanMsg(uuid));
                return false;
            } else {
                YamlCache.removeIpBan(currentIP);
            }
        }

        return true;
        
    }
    
    @EventHandler
    public boolean onPlayerJoin(PlayerJoinEvent event){
        
        String currentIGN = event.getPlayer().getName();
        UUID uuid = m.getUuid(currentIGN);

        UpdateChecker.checkUpdate(event.getPlayer());

        new Thread(() -> {

            if(YamlCache.getOciCache().contains(uuid)){
                Bukkit.getPlayer(currentIGN).getInventory().clear();
                YamlCache.getOciCache().remove(uuid);
                m.updateYamlOci();
            }

            if(YamlCache.isPlayerBanned(uuid)){
                if(!YamlCache.isBanLifted(uuid)){
                    Bukkit.getScheduler().runTask(m, () -> event.getPlayer().kickPlayer(YamlAbstractBanCommand.getBanMsg(uuid)));
                }else{
                    YamlCache.removeBan(uuid);
                }
            }

        }).start();
        
        return true;
        
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){

        String target = event.getPlayer().getName();
        UUID uuid = m.getUuid(target);

        if(YamlCache.isPlayerMuted(uuid)){

            if(!YamlCache.isMuteLifted(uuid)){
                System.out.println("Inside mute not lifted");
                event.setCancelled(true);
                event.getPlayer().sendMessage(m.parseMessage(m.getConfig().getString("messages.muted-player")));
            }else{
                YamlCache.removeMute(uuid);
            }
            
            return;

        }
        
        if(!YamlCBCommand.isEditing.containsKey(event.getPlayer().getName()))
            return;

        //-------------------------------------------------//

        String strPlayer = event.getPlayer().getName();

        event.setCancelled(true);
        
        if(event.getMessage().equals("cancel")){
            YamlCBCommand.isEditing.remove(strPlayer);
            event.getPlayer().sendMessage("§eAction cancelled.");
            return;
        }

        String edit = YamlCBCommand.isEditing.get(strPlayer);

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
            YamlSevCommand.editSev(args, event.getPlayer());
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
            
        }
        
        if(edit.equalsIgnoreCase("temp")){
            m.getConfig().set("pages.tempban", event.getMessage());
            event.getPlayer().sendMessage("§aTemp Ban Page successfully updated.");
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("perm")){
            m.getConfig().set("pages.permban", event.getMessage());
            event.getPlayer().sendMessage("§aPerm Ban Page successfully updated.");
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("kickPage")){
            m.getConfig().set("pages.kick", event.getMessage());
            event.getPlayer().sendMessage("§aKick Page successfully updated.");
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("defaultreason")){
            m.getConfig().set("defaults.reason", event.getMessage());
            event.getPlayer().sendMessage("§aDefault Reason successfully updated.");
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
        }

        m.getConfig().set("announcers." + edit, event.getMessage());
        event.getPlayer().sendMessage("§aThe "+ edit + " announcement is successfully updated.");
        YamlCBCommand.isEditing.remove(strPlayer);
        
        m.saveConfig();
        
    }
    
}
