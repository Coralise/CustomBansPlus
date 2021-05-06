package me.coralise.custombansplus.enums;

public enum BanType {

    PERM_BAN("Perm Ban"),
    TEMP_BAN("Temp Ban"),
    TEMP_IP_BAN("Temp IP Ban"),
    PERM_IP_BAN("Perm IP Ban");

    private String type;

    private BanType (String banType) {
        this.type = banType;
    }

    @Override
    public String toString() {
        return type;
    }

}