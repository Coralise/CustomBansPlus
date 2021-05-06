package me.coralise.custombansplus.yaml.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;
import me.coralise.custombansplus.yaml.YamlGUIItems;

public class YamlReports {

    private final UUID uuid;
    private final String username;
    private int reportsSize;
    private ArrayList<YamlReport> reports;
    private final Inventory reportsList;
    private final UUID playerUuid;
    private int unresolveds;

    private int maxPages;
    private int page = 1;

    private static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    private static YamlGUIItems item = new YamlGUIItems();

    private static final HashMap<UUID, YamlReports> openReports = new HashMap<UUID, YamlReports>();
    private static final HashMap<UUID, Inventory> openReportsList = new HashMap<UUID, Inventory>();

    public YamlReports(UUID uuid, UUID playerUuid) {
        this.uuid = uuid;
        this.username = m.getName(uuid.toString());
        reportsSize = m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false).size();
        reports = (ArrayList<YamlReport>) YamlReport.getReports(uuid);
        reportsList = Bukkit.createInventory(null, 45, "§8§lC§4§lB§8§lP §4§l" + username + "'s Reports");
        setUnresolveds(0);
        for (String num : m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false)) {
            if (!m.getReportsConfig().getBoolean(uuid.toString() + "." + num + ".resolved")) {
                setUnresolveds(getUnresolveds() + 1);
            }
        }
        calculateMaxPages();
        setupMenu();

        openReports.remove(playerUuid);
        openReports.put(playerUuid, this);
        this.playerUuid = playerUuid;

    }

    public void calculateMaxPages() {

        reportsSize = m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false).size();
        int size = reportsSize / 21;
        if (reportsSize % 21 != 0) size++;
        maxPages = size;
        if (page > maxPages) page--;

    }

    public int getMaxPages() {
        return maxPages;
    }

    public void setupMenu() {

        reportsList.setItem(0, item.fillerItem());
        reportsList.setItem(1, item.fillerItem());
        reportsList.setItem(2, item.fillerItem());
        reportsList.setItem(3, item.fillerItem());
        reportsList.setItem(4, item.fillerItem());
        reportsList.setItem(5, item.fillerItem());
        reportsList.setItem(6, item.fillerItem());
        reportsList.setItem(7, item.fillerItem());
        reportsList.setItem(8, item.fillerItem());
        
        reportsList.setItem(9, item.fillerItem());
        reportsList.setItem(10, item.airItem());
        reportsList.setItem(11, item.airItem());
        reportsList.setItem(12, item.airItem());
        reportsList.setItem(13, item.airItem());
        reportsList.setItem(14, item.airItem());
        reportsList.setItem(15, item.airItem());
        reportsList.setItem(16, item.airItem());
        reportsList.setItem(17, item.fillerItem());

        reportsList.setItem(18, item.fillerItem());
        reportsList.setItem(19, item.airItem());
        reportsList.setItem(20, item.airItem());
        reportsList.setItem(21, item.airItem());
        reportsList.setItem(22, item.airItem());
        reportsList.setItem(23, item.airItem());
        reportsList.setItem(24, item.airItem());
        reportsList.setItem(25, item.airItem());
        reportsList.setItem(26, item.fillerItem());

        reportsList.setItem(27, item.fillerItem());
        reportsList.setItem(28, item.airItem());
        reportsList.setItem(29, item.airItem());
        reportsList.setItem(30, item.airItem());
        reportsList.setItem(31, item.airItem());
        reportsList.setItem(32, item.airItem());
        reportsList.setItem(33, item.airItem());
        reportsList.setItem(34, item.airItem());
        reportsList.setItem(35, item.fillerItem());

        reportsList.setItem(36, item.backItemReports());
        reportsList.setItem(37, item.fillerItem());
        reportsList.setItem(38, item.fillerItem());
        reportsList.setItem(39, item.fillerItem());
        reportsList.setItem(40, item.fillerItem());
        reportsList.setItem(41, item.fillerItem());
        reportsList.setItem(42, item.fillerItem());
        reportsList.setItem(43, item.fillerItem());
        reportsList.setItem(44, item.fillerItem());

        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34};
        int si = 0;

        for (int i = (page * 21) - 21;i < reportsSize;i++) {
            reportsList.setItem(slots[si], item.getReport(reports.get(i)));
            si++;
            if (si == slots.length) {
                if (i != reportsSize-1)
                    reportsList.setItem(44, item.reportNextPage());
                break;
            }
        }
        if (page != 1) {
            reportsList.setItem(43, item.reportPrevPage());
        }

    }

    public void reloadReports() {
        reports = (ArrayList<YamlReport>) YamlReport.getReports(uuid);
    }

    public int getUnresolveds() {
        return unresolveds;
    }

    public void setUnresolveds(int unresolveds) {
        this.unresolveds = unresolveds;
    }

    public List<YamlReport> getReports() {
        return reports;
    }

    public int getReportsSize() {
        return reportsSize;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void showMenu () {
        setupMenu();
        Bukkit.getScheduler().runTask(m, () -> Bukkit.getPlayer(playerUuid).openInventory(reportsList));
    }

    public void nextPage() {
        page++;
        if (page > maxPages) {
            page--;
        }
        setupMenu();
        Bukkit.getScheduler().runTask(m, () -> Bukkit.getPlayer(playerUuid).openInventory(reportsList));
    }

    public void prevPage() {
        page--;
        if (page == 0) {
            page++;
        }
        setupMenu();
        Bukkit.getScheduler().runTask(m, () -> Bukkit.getPlayer(playerUuid).openInventory(reportsList));
    }

    public static Map<UUID, YamlReports> getOpenReports() {
        return openReports;
    }

    public static void showReportsGUI(Player player) {

        Inventory reportsMenu = Bukkit.createInventory(null, 54, "§8§lC§4§lB§8§lP §4Reported Players");

        reportsMenu.setItem(0, item.fillerItem());
        reportsMenu.setItem(1, item.fillerItem());
        reportsMenu.setItem(2, item.fillerItem());
        reportsMenu.setItem(3, item.fillerItem());
        reportsMenu.setItem(4, item.fillerItem());
        reportsMenu.setItem(5, item.fillerItem());
        reportsMenu.setItem(6, item.fillerItem());
        reportsMenu.setItem(7, item.fillerItem());
        reportsMenu.setItem(8, item.fillerItem());
        
        reportsMenu.setItem(9, item.fillerItem());
        reportsMenu.setItem(10, item.airItem());
        reportsMenu.setItem(11, item.airItem());
        reportsMenu.setItem(12, item.airItem());
        reportsMenu.setItem(13, item.airItem());
        reportsMenu.setItem(14, item.airItem());
        reportsMenu.setItem(15, item.airItem());
        reportsMenu.setItem(16, item.airItem());
        reportsMenu.setItem(17, item.fillerItem());

        reportsMenu.setItem(18, item.fillerItem());
        reportsMenu.setItem(19, item.airItem());
        reportsMenu.setItem(20, item.airItem());
        reportsMenu.setItem(21, item.airItem());
        reportsMenu.setItem(22, item.airItem());
        reportsMenu.setItem(23, item.airItem());
        reportsMenu.setItem(24, item.airItem());
        reportsMenu.setItem(25, item.airItem());
        reportsMenu.setItem(26, item.fillerItem());

        reportsMenu.setItem(27, item.fillerItem());
        reportsMenu.setItem(28, item.airItem());
        reportsMenu.setItem(29, item.airItem());
        reportsMenu.setItem(30, item.airItem());
        reportsMenu.setItem(31, item.airItem());
        reportsMenu.setItem(32, item.airItem());
        reportsMenu.setItem(33, item.airItem());
        reportsMenu.setItem(34, item.airItem());
        reportsMenu.setItem(35, item.fillerItem());

        reportsMenu.setItem(36, item.fillerItem());
        reportsMenu.setItem(37, item.airItem());
        reportsMenu.setItem(38, item.airItem());
        reportsMenu.setItem(39, item.airItem());
        reportsMenu.setItem(40, item.airItem());
        reportsMenu.setItem(41, item.airItem());
        reportsMenu.setItem(42, item.airItem());
        reportsMenu.setItem(43, item.airItem());
        reportsMenu.setItem(44, item.fillerItem());

        reportsMenu.setItem(45, item.fillerItem());
        reportsMenu.setItem(46, item.fillerItem());
        reportsMenu.setItem(47, item.fillerItem());
        reportsMenu.setItem(48, item.fillerItem());
        reportsMenu.setItem(49, item.fillerItem());
        reportsMenu.setItem(50, item.fillerItem());
        reportsMenu.setItem(51, item.fillerItem());
        reportsMenu.setItem(52, item.fillerItem());
        reportsMenu.setItem(53, item.fillerItem());

        setReports(reportsMenu);
        new Thread(() -> setReportsWithSkin(reportsMenu)).start();

        openReportsList.put(player.getUniqueId(), reportsMenu);
        Bukkit.getScheduler().runTask(m, () -> player.openInventory(reportsMenu));

    }

    private static void setReports(Inventory reportsMenu) {

        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
        int si = 0;

        for (String strUuid : m.getReportsConfig().getKeys(false)) {
            UUID uuid = UUID.fromString(strUuid);
            reportsMenu.setItem(slots[si], item.getReports(uuid));
            si++;
            if (si == slots.length) {
                reportsMenu.setItem(53, item.reportsNextPage());
                break;
            }
        }

    }

    private static void setReportsWithSkin(Inventory reportsMenu) {

        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
        int si = 0;

        for (String strUuid : m.getReportsConfig().getKeys(false)) {
            UUID uuid = UUID.fromString(strUuid);
            reportsMenu.setItem(slots[si], item.getReportsWithSkin(uuid));
            si++;
            if (si == slots.length) {
                break;
            }
        }
        
    }
    
}
