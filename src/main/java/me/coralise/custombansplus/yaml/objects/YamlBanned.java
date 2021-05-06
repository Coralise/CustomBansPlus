package me.coralise.custombansplus.yaml.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;
import me.coralise.custombansplus.enums.BanType;

public class YamlBanned {

    private final UUID uuid;
    private BanType banType;
    private Date banDate;
    private String duration;
    private String reason;
    private String bannerUuid;
    private Date unbanDate;
    private String ip;

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public YamlBanned(UUID uuid, String ip, BanType banType, String reason, String duration, String bannerUuid) {

        this.uuid = uuid;
        this.banType = banType;
        this.reason = reason;
        if (duration.equalsIgnoreCase("perm")) duration = "Permanent";
        this.duration = duration;
        this.banDate = new Date();
        this.bannerUuid = bannerUuid;
        this.ip = ip;
        if (!duration.contains("perm")) this.unbanDate = m.calculateUnpunishDateDate(duration);
        else this.unbanDate = null;

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Used for cache loading.
     */
    public YamlBanned(UUID uuid, String strBanType, String reason, String duration, String bannerUuid, String banDate, String unbanDate, String ip) {

        this.uuid = uuid;
        this.reason = reason;
        if (duration.equalsIgnoreCase("perm")) duration = "Permanent";
        this.duration = duration;
        this.ip = ip;
        try {
            this.banDate = formatter.parse(banDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.bannerUuid = bannerUuid;
        if (!duration.equalsIgnoreCase("Permanent"))
            try {
                this.unbanDate = formatter.parse(unbanDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        else this.unbanDate = null;

        switch (strBanType) {
            case "Temp Ban":
                this.banType = BanType.TEMP_BAN;
                break;
            case "Perm Ban":
                this.banType = BanType.PERM_BAN;
                break;
            case "Temp IP Ban":
                this.banType = BanType.TEMP_IP_BAN;
                break;
            case "Perm IP Ban":
                this.banType = BanType.PERM_IP_BAN;
                break;
        }

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
    public UUID getUuid() {
        return uuid;
    }
    public Date getBanDate() {
        return banDate;
    }
    public String getUnbanDateString() {
        if (unbanDate == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(unbanDate);
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