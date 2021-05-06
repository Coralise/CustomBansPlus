package me.coralise.custombansplus.enums;

public enum MuteType {
    PERM_MUTE("Perm Mute"),
    TEMP_MUTE("Temp Mute");

    private String type;

    private MuteType (String muteType) {
        this.type = muteType;
    }

    @Override
    public String toString() {
        return type;
    }

}