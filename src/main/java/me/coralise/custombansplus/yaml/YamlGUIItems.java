package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;
import me.coralise.custombansplus.yaml.objects.YamlReport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class YamlGUIItems {

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    
    public ItemStack airItem(){

        return new ItemStack(Material.AIR, 1);

    }

    public ItemStack fillerItem(){

        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§f");
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack backItemMain(){

        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§c§lBack to Main Menu");
        meta.setLocalizedName("Back to Main Menu");
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack backItemSevs(){

        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§c§lBack to Severity List");
        meta.setLocalizedName("Back to Severity List");
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack backItemReports(){

        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§c§lBack to Reports List");
        meta.setLocalizedName("Back to Reports List");
        item.setItemMeta(meta);

        return item;

    }

    //-------------------------SEVERITY--------------------------//

    public ItemStack sevItem(){

        ItemStack item = new ItemStack(Material.GOLDEN_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e§lSEVERITIES");
        meta.setLocalizedName("SEVERITIES");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§fList of all your severities.");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack addSevItem(){

        ItemStack item = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if(m.getSevConfig().getKeys(false).size() != 14){
            meta.setDisplayName("§r§e§lAdd a Severity");
            meta.setLocalizedName("Add a Severity");
            lore.add("§r§fAdds a new severity");
            lore.add("§r§fwith default values.");
        }else{
            meta.setDisplayName("§r§c§lSeverity List Full!");
            lore.add("§r§fCan't add more!");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack getSevItem(int sevNum){

        ItemStack item = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e§l" + sevNum);
        meta.setLocalizedName(String.valueOf(sevNum));
        List<String> lore = new ArrayList<String>();

        m.getSevConfig().getConfigurationSection(String.valueOf(sevNum)).getKeys(false).forEach(key -> {
            String desc = key.replace("-", " ");
            String value = "";
            desc = desc.substring(0, 1).toUpperCase() + desc.substring(1);
            if(desc.equalsIgnoreCase("baldeduct")){
                value = String.valueOf(m.getSevConfig().getDouble(sevNum+"."+key) * 100);
                lore.add("§r§e" + desc + ": §f" + value + "%");
            }else if(desc.equalsIgnoreCase("Console commands")){
                lore.add("§r§e" + desc + ":");
                m.getSevConfig().getStringList(sevNum+".console-commands").forEach(cmd -> lore.add("§f" + cmd));
            }else{
                value = m.getSevConfig().get(sevNum+"."+key).toString();
                lore.add("§r§e" + desc + ": §f" + value);
            }
        });

        lore.add("§r");
        lore.add("§r§eLeft Click: §fEdit Severity");
        lore.add("§r§eShift+Right Click: §fDelete Severity");

        meta.setLore(lore);
        item.setItemMeta(meta);
        YamlCBMenu.sevNum++;
        
        return item;

    }

    public ItemStack getSevItemList(int sevNum){

        ItemStack item = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e§l" + sevNum);
        meta.setLocalizedName(String.valueOf(sevNum));
        List<String> lore = new ArrayList<String>();

        m.getSevConfig().getConfigurationSection(String.valueOf(sevNum)).getKeys(false).forEach(key -> {
            String desc = key.replace("-", " ");
            String value = "";
            desc = desc.substring(0, 1).toUpperCase() + desc.substring(1);
            if(!desc.equalsIgnoreCase("baldeduct")){
                value = m.getSevConfig().get(sevNum+"."+key).toString();
                lore.add("§r§e" + desc + ": §f" + value);
            }else{
                value = String.valueOf(m.getSevConfig().getDouble(sevNum+"."+key) * 100);
                lore.add("§r§e" + desc + ": §f" + value + "%");
            }
        });

        meta.setLore(lore);
        item.setItemMeta(meta);
        YamlCBMenu.sevNum++;
        
        return item;

    }

    public ItemStack duraItem(int intSevNum){

        String sevNum = String.valueOf(intSevNum);
        ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e§lDuration");
        meta.setLocalizedName(sevNum + " Duration");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§eCurrent: §f" + m.getSevConfig().getString(sevNum+".duration"));
        lore.add("§r");
        lore.add("§r§eLeft Click: §fEdit");
        lore.add("§r§eRight Click: §fReset to Default");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack balDeductItem(int intSevNum){

        String sevNum = String.valueOf(intSevNum);
        ItemStack item = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e§lBalance Deduction");
        meta.setLocalizedName(sevNum + " Balance Deduction");
        List<String> lore = new ArrayList<String>();
        double current = m.getSevConfig().getDouble(sevNum+".baldeduct") * 100;
        lore.add("§r§eCurrent: §f" + current + "%");
        lore.add("§r");
        lore.add("§r§eLeft Click: §fEdit");
        lore.add("§r§eRight Click: §fReset to Default");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack clearInvItem(int intSevNum){

        String sevNum = String.valueOf(intSevNum);
        ItemStack item = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e§lClear Inventory");
        meta.setLocalizedName(sevNum + " Clear Inventory");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§eCurrent: §f" + m.getSevConfig().getString(sevNum+".clear-inv"));
        lore.add("§r");
        lore.add("§r§eLeft Click: §fSet to True");
        lore.add("§r§eRight Click: §fSet to False");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack cnslCmdItem(int intSevNum){

        String sevNum = String.valueOf(intSevNum);
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§e§lConsole Commands");
        meta.setLocalizedName(sevNum + " Console Commands");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§eCurrent: §f");
        m.getSevConfig().getStringList(sevNum+".console-commands").forEach(cmd -> lore.add("§f" + cmd));
        lore.add("§r");
        lore.add("§r§eLeft Click: §fEdit");
        lore.add("§r§eRight Click: §fClear Commands");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    //----------------SEVERITY---------------EDIT PAGE----------------//

    public ItemStack editPageItem(){

        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§a§lEDIT PAGES");
        meta.setLocalizedName("EDIT PAGES");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§fEdit your ban and");
        lore.add("§r§fkick pages here.");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack tempBanPageItem(){

        ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§c§lTemp Ban Page");
        meta.setLocalizedName("Temp Ban Page");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§f");
        lore.add("§r§eLeft Click: §fEdit Temp Ban Page");
        lore.add("§r§eMiddle Click: §fTest Temp Ban Page");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack permBanPageItem(){

        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§c§lPerm Ban Page");
        meta.setLocalizedName("Perm Ban Page");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§f");
        lore.add("§r§eLeft Click: §fEdit Perm Ban Page");
        lore.add("§r§eMiddle Click: §fTest Perm Ban Page");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack kickPageItem(){

        ItemStack item = new ItemStack(Material.WOODEN_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§c§lKick Page");
        meta.setLocalizedName("Kick Page");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§f");
        lore.add("§r§eLeft Click: §fEdit Kick Page");
        lore.add("§r§eMiddle Click: §fTest Kick Page");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    //-----------------------EDIT PAGE-------------------EDIT ANNOUCNERS----------------------//

    public ItemStack editAnnouncersItem(){

        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§d§lEDIT ANNOUNCERS");
        meta.setLocalizedName("EDIT ANNOUNCERS");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§fEdit your punishment");
        lore.add("§r§fannouncers here.");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    //ban ipban warn mute kick

    public ItemStack banAnnouncerItem(){

        ItemStack item = new ItemStack(Material.GOLDEN_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§d§lTemp Ban Announcer");
        meta.setLocalizedName("Temp Ban Announcer");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§f");
        lore.add("§r§eLeft Click: §fEdit Ban Announcer");
        lore.add("§r§eMiddle Click: §fTest Ban Announcer");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack ipBanAnnouncerItem(){

        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§d§lIP Ban Announcer");
        meta.setLocalizedName("IP Ban Announcer");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§f");
        lore.add("§r§eLeft Click: §fEdit IP Ban Announcer");
        lore.add("§r§eMiddle Click: §fTest IP Ban Announcer");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack warnAnnouncerItem(){

        ItemStack item = new ItemStack(Material.FEATHER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§d§lWarn Announcer");
        meta.setLocalizedName("Warn Announcer");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§f");
        lore.add("§r§eLeft Click: §fEdit Warn Announcer");
        lore.add("§r§eMiddle Click: §fTest Warn Announcer");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack muteAnnouncerItem(){

        ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§d§lMute Announcer");
        meta.setLocalizedName("Mute Announcer");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§f");
        lore.add("§r§eLeft Click: §fEdit Mute Announcer");
        lore.add("§r§eMiddle Click: §fTest Mute Announcer");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack kickAnnouncerItem(){

        ItemStack item = new ItemStack(Material.WOODEN_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§d§lKick Announcer");
        meta.setLocalizedName("Kick Announcer");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§f");
        lore.add("§r§eLeft Click: §fEdit Kick Announcer");
        lore.add("§r§eMiddle Click: §fTest Kick Announcer");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    //--------------------------ANNOUNCERS------------------------------HIST GUI----------------------------//

    public ItemStack histNextPage(){

        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§a§lNext Page");
        meta.setLocalizedName("hist Next Page");
        List<String> lore = new ArrayList<String>();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack histPrevPage(){

        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§a§lPrevious Page");
        meta.setLocalizedName("hist Prev Page");
        List<String> lore = new ArrayList<String>();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    //---------------------------HIST GUI------------------------------MISCELLANEOUS-----------------------//

    public ItemStack miscItem(){

        ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§3§lMISCELLANEOUS");
        meta.setLocalizedName("misc");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§fEdit miscellaneous");
        lore.add("§r§foptions here.");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack defReasonItem(){

        ItemStack item = new ItemStack(Material.FEATHER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§3§lEdit Default Reason");
        meta.setLocalizedName("default reason");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§eCurrent:");
        lore.add("§r§f" + m.parseMessage(m.getConfig().getString("defaults.reason")));
        lore.add("§r§e");
        lore.add("§r§eLeft Click: §fEdit");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack purgeHistItem(){

        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§3§lPurge Punishment Histories");
        meta.setLocalizedName("punishment histories");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§eUse to clear memory.");
        lore.add("§r§e");
        lore.add("§r§eShift+Right Click: §fPurge");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack purgeAltsItem(){

        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§3§lPurge Alt Account Histories");
        meta.setLocalizedName("alts histories");
        List<String> lore = new ArrayList<String>();
        lore.add("§r§eUse to clear memory.");
        lore.add("§r§e");
        lore.add("§r§eShift+Right Click: §fPurge");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack getReport(YamlReport report) {
        
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();

        if (!report.isResolved()) {
            meta.addEnchant(Enchantment.MENDING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            lore.add("§aStatus: §cNot Resolved");
            item.setType(Material.RED_WOOL);
        } else {
            lore.add("§aStatus: §eResolved");
            lore.add("§aResolved by: §f" + report.getResolverUuid());
            item.setType(Material.GREEN_WOOL);
        }
        meta.setDisplayName("§a§l" + report.getReportNum());
        meta.setLocalizedName("report " + report.getUuid().toString() + " " + report.getReportNum());

        lore.add("§aDate: §f" + report.getDate());
        lore.add("§aReporter: §f" + m.getName(report.getReporterUuid()));

        lore.add("§aReport:");
        String[] words = report.getReport().split(" ");
        String sentence = "";

        for (int i = 0;i < words.length;i++) {
            sentence += words[i] + " ";
            if (i != 0 && i % 4 == 0) {
                lore.add("§f" + sentence);
                sentence = "";
            }
        }
        lore.add("§f" + sentence);
        lore.add("");
        lore.add("§eLeft Click: §fToggle Resolved");
        lore.add("§eShift+Right Click: §fDelete");

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;

    }

    public ItemStack getReports(UUID uuid) {

        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName("§e§l" + m.getName(uuid.toString()));
        meta.setLocalizedName("reports " + uuid.toString());

        int unresolveds = 0;
        for (String num : m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false)) {
            if (!m.getReportsConfig().getBoolean(uuid.toString() + "." + num + ".resolved")) {
                unresolveds++;
            }
        }
        
        String u = "";
        if (unresolveds == 0) u = "§a0";
        else u = "§c" + unresolveds;
        
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add("§fReports: §f" + m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false).size());
        lore.add("§fUnresolved: " + u);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack getReportsWithSkin(UUID uuid) {

        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(m.getOfflinePlayer(uuid));
        meta.setDisplayName("§e§l" + m.getName(uuid.toString()));
        meta.setLocalizedName("reports " + uuid.toString());

        int unresolveds = 0;
        for (String num : m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false)) {
            if (!m.getReportsConfig().getBoolean(uuid.toString() + "." + num + ".resolved")) {
                unresolveds++;
            }
        }
        
        String u = "";
        if (unresolveds == 0) u = "§a0";
        else u = "§c" + unresolveds;
        
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add("§fReports: §f" + m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false).size());
        lore.add("§fUnresolved: " + u);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;

    }

    public ItemStack reportNextPage() {

        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§a§lNext Page");
        meta.setLocalizedName("report Next Page");
        List<String> lore = new ArrayList<String>();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

	public ItemStack reportPrevPage() {

        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§a§lPrevious Page");
        meta.setLocalizedName("report Prev Page");
        List<String> lore = new ArrayList<String>();
        meta.setLore(lore);
        item.setItemMeta(meta);

		return item;
	}

    public ItemStack reportsNextPage() {

        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§a§lNext Page");
        meta.setLocalizedName("reports Next Page");
        List<String> lore = new ArrayList<String>();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

	public ItemStack reportsPrevPage() {

        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§a§lPrevious Page");
        meta.setLocalizedName("reports Prev Page");
        List<String> lore = new ArrayList<String>();
        meta.setLore(lore);
        item.setItemMeta(meta);

		return item;
	}
    
}
