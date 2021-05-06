package me.coralise.custombansplus.sql.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;
import me.coralise.custombansplus.enums.MuteType;

public class SqlMuted {

    private final UUID uuid;
    private String duration;
    private String reason;
    private String muterUuid;
    private Date muteDate;
    private Date unmuteDate;
    private MuteType muteType;
    private boolean isInDatabase;

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    public SqlMuted (UUID uuid, MuteType muteType, String reason, String duration, UUID muterUuid) {

        this.uuid = uuid;
        this.setMuteType(muteType);
        this.reason = reason;
        if (duration.equalsIgnoreCase("perm")) duration = "Permanent";
        this.duration = duration;
        this.muteDate = new Date();
        this.muterUuid = "CONSOLE";
        if (muterUuid != null)
            this.muterUuid = muterUuid.toString();
        if (!duration.equalsIgnoreCase("Permanent")) this.unmuteDate = m.calculateUnpunishDateDate(duration);
        else this.unmuteDate = null;
        isInDatabase = false;

    }

    public boolean isInDatabase() {
        return isInDatabase;
    }

    public void setInDatabase(boolean isInDatabase) {
        this.isInDatabase = isInDatabase;
    }

    public SqlMuted (UUID uuid, String strMuteType, String reason, String duration, UUID muterUuid) {

        this.uuid = uuid;
        this.reason = reason;
        if (duration.equalsIgnoreCase("perm")) duration = "Permanent";
        this.duration = duration;
        this.muteDate = new Date();
        this.muterUuid = "CONSOLE";
        if (muterUuid != null)
            this.muterUuid = muterUuid.toString();
        if (!duration.equalsIgnoreCase("Permanent")) this.unmuteDate = m.calculateUnpunishDateDate(duration);
        else this.unmuteDate = null;

        switch (strMuteType) {
            case "Temp Mute":
                this.muteType = MuteType.TEMP_MUTE;
                break;
            case "Perm Mute":
                this.muteType = MuteType.PERM_MUTE;
                break;
        }
        isInDatabase = false;

    }

    public MuteType getMuteType() {
        return muteType;
    }

    public void setMuteType(MuteType muteType) {
        this.muteType = muteType;
    }

    public Date getUnmuteDate() {
        return unmuteDate;
    }
    public String getUnmuteDateString() {
        if (unmuteDate == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(unmuteDate);
    }
    public UUID getUuid() {
        return uuid;
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
    public String getMuterUuid() {
        return muterUuid;
    }
    public void setMuterUuid(String muterUuid) {
        this.muterUuid = muterUuid;
    }
    public Date getMuteDate() {
        return muteDate;
    }
    public String getMuteDateString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(muteDate);
    }
    public void setMuteDate(Date muteDate) {
        this.muteDate = muteDate;
    }
    public void setUnmuteDate(Date unmuteDate) {
        this.unmuteDate = unmuteDate;
    }
}