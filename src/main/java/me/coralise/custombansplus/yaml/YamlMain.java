package me.coralise.custombansplus.yaml;

import com.mysql.cj.x.protobuf.MysqlxCrud.Update;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import me.coralise.custombansplus.*;

public class YamlMain implements Listener{

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();

    @EventHandler
    public boolean onLogin(AsyncPlayerPreLoginEvent event){
        
        String stPlayer = event.getName();
        String uuid = m.getUuid(stPlayer);

        if(YamlCache.isPlayerLogged(uuid) && YamlCache.isPlayerBanned(uuid)){
            if(!YamlCache.isBanLifted(uuid)){
                event.disallow(Result.KICK_BANNED, YamlAbstractBanCommand.getBanMsg(stPlayer, null));
            }else{
                YamlCache.removeBan(uuid);
            }
        }

        return false;
        
    }
    
    @EventHandler
    public boolean onPlayerJoin(PlayerJoinEvent event){
        
        String currentIGN = event.getPlayer().getName();
        String uuid = m.getUuid(currentIGN);
        String currentIP = m.getYamlIp(currentIGN);

        UpdateChecker.checkUpdate(event.getPlayer());

        Bukkit.getScheduler().runTask(m, () -> {
            if(YamlCache.getOciCache().contains(uuid)){
                Bukkit.getPlayer(currentIGN).getInventory().clear();
                YamlCache.getOciCache().remove(uuid);
                m.updateYamlOci();
            }

            if(YamlCache.isPlayerLogged(uuid) && !YamlCache.isIgnDifferent(uuid) && !YamlCache.isIpDifferent(uuid, currentIP)) return;
    
            if(!YamlCache.isPlayerLogged(uuid)){
                YamlCache.setNewPlayer(uuid, "new");
                YamlCache.setPlayerIP(uuid);
            }else if(YamlCache.isIpDifferent(uuid, currentIP))
                YamlCache.setNewPlayer(uuid, "ip");

            YamlCache.setPlayer(uuid);

            if(YamlCache.isPlayerLogged(uuid) && YamlCache.isPlayerBanned(uuid)){
                if(!YamlCache.isBanLifted(uuid)){
                    event.getPlayer().kickPlayer(YamlAbstractBanCommand.getBanMsg(currentIGN, null));
                }else{
                    YamlCache.removeBan(uuid);
                }
            }
    
            if(m.getBansConfig().getKeys(false).contains(currentIP)){
                YamlAbstractBanCommand.copyIPBan(currentIGN, currentIP);
                event.getPlayer().kickPlayer(YamlAbstractBanCommand.getBanMsg(currentIGN, null));
            }
        });
        
        return true;
        
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){

        String target = event.getPlayer().getName();
        String uuid = m.getUuid(target);

        if(YamlCache.isPlayerMuted(uuid)){

            if(!YamlCache.isMuteLifted(uuid)){
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou are muted.");
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
            m.getConfig().set("tempban-page", event.getMessage());
            event.getPlayer().sendMessage("§aTemp Ban Page successfully updated.");
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("perm")){
            m.getConfig().set("permban-page", event.getMessage());
            event.getPlayer().sendMessage("§aPerm Ban Page successfully updated.");
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("kickPage")){
            m.getConfig().set("kick-page", event.getMessage());
            event.getPlayer().sendMessage("§aKick Page successfully updated.");
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
        }
        if(edit.equalsIgnoreCase("defaultreason")){
            m.getConfig().set("default-reason", event.getMessage());
            event.getPlayer().sendMessage("§aDefault Reason successfully updated.");
            YamlCBCommand.isEditing.remove(strPlayer);
            return;
        }

        m.getConfig().set(edit+"-announcer", event.getMessage());
        event.getPlayer().sendMessage("§aThe "+ edit + " announcement is successfully updated.");
        YamlCBCommand.isEditing.remove(strPlayer);
        
        m.saveConfig();
        
    }
    
}
