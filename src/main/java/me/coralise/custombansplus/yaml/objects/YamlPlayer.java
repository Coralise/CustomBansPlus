package me.coralise.custombansplus.yaml.objects;

import java.util.UUID;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;

public class YamlPlayer {

    private final UUID uuid;
    private String username;
    private String ip;
    private String oldIp;
    private boolean isUpdate;
    private boolean isNewPlayer;

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    public YamlPlayer (UUID uuid, String ip, boolean isUpdate, boolean isNewPlayer) {
        this.uuid = uuid;
        this.ip = ip;
        this.setOldIp(ip);
        username = m.getName(uuid.toString());
        this.setUpdate(isUpdate);
        this.setNewPlayer(isNewPlayer);
    }

    public String getOldIp() {
        return oldIp;
    }

    public void setOldIp(String oldIp) {
        this.oldIp = oldIp;
    }

    public boolean isNewPlayer() {
        return isNewPlayer;
    }

    public void setNewPlayer(boolean isNewPlayer) {
        this.isNewPlayer = isNewPlayer;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
}
