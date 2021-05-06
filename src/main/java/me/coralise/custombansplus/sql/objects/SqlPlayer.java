package me.coralise.custombansplus.sql.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;

public class SqlPlayer {

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    private final UUID uuid;
    private String username;
    private String ip;
    private boolean isUpdated;
    private final Date joinDate;

    public SqlPlayer (UUID uuid, String ip, boolean isUpdated) {
        this.uuid = uuid;
        this.ip = ip;
        this.setUpdated(isUpdated);
        username = m.getName(uuid.toString());
        joinDate = new Date();
    }

    public Date getJoinDate() {
        return joinDate;
    }
    public String getJoinDateString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(joinDate);
    }
    public boolean isUpdated() {
        return isUpdated;
    }
    public void setUpdated(boolean updated) {
        this.isUpdated = updated;
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
