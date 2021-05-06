package me.coralise.custombansplus.sql.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;
import me.coralise.custombansplus.enums.BanType;

public class SqlBanned {

    private final UUID uuid;
    private BanType banType;
    private Date banDate;
    private String duration;
    private String reason;
    private String bannerUuid;
    private Date unbanDate;
    private String ip;
    private boolean isInDatabase;

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    public SqlBanned (UUID uuid, String ip, BanType banType, String reason, String duration, UUID bannerUuid) {

        this.uuid = uuid;
        this.banType = banType;
        this.reason = reason;
        if (duration.equalsIgnoreCase("perm")) duration = "Permanent";
        this.duration = duration;
        this.banDate = new Date();
        this.bannerUuid = "CONSOLE";
        if (bannerUuid != null)
            this.bannerUuid = bannerUuid.toString();
        this.ip = ip;
        if (!duration.equalsIgnoreCase("Permanent")) this.unbanDate = m.calculateUnpunishDateDate(duration);
        else this.unbanDate = null;
        setInDatabase(false);

    }

    public boolean isInDatabase() {
        return isInDatabase;
    }

    public void setInDatabase(boolean isInDatabase) {
        this.isInDatabase = isInDatabase;
    }

    public SqlBanned (UUID uuid, String strBanType, String reason, String duration, UUID bannerUuid) {

        this.uuid = uuid;
        this.reason = reason;
        this.duration = duration;
        this.banDate = new Date();
        this.bannerUuid = "CONSOLE";
        if (bannerUuid != null)
            this.bannerUuid = bannerUuid.toString();
        if (!duration.contains("perm")) this.unbanDate = m.calculateUnpunishDateDate(duration);
        else this.unbanDate = null;

        switch (strBanType) {
            case "Temp Ban":
                this.banType = BanType.TEMP_BAN;
                break;
            case "Perm Ban":
                this.banType = BanType.PERM_BAN;
                break;
        }
        setInDatabase(false);

    }

    public SqlBanned(UUID uuid2, String ip2, BanType banType2, String reason2, String duration2, String bannerUuid2) {
        uuid = uuid2;
        ip = ip2;
        banType = banType2;
        reason = reason2;
        duration = duration2;
        bannerUuid = bannerUuid2;
        setInDatabase(false);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public BanType getBanType() {
        return banType;
    }

    public void setBanType(BanType banType) {
        this.banType = banType;
    }

    public Date getUnbanDate() {
        return unbanDate;
    }
    public String getUnbanDateString() {
        if (unbanDate == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(unbanDate);
    }
    public UUID getUuid() {
        return uuid;
    }
    public Date getBanDate() {
        return banDate;
    }
    public String getBanDateString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(banDate);
    }
    public void setBanDate(Date banDate) {
        this.banDate = banDate;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getBannerUuid() {
        return bannerUuid;
    }
    public void setBannerUuid(String bannerUuid) {
        this.bannerUuid = bannerUuid;
    }
    public void setUnbanDate(Date unbanDate) {
        this.unbanDate = unbanDate;
    }
    
}