package me.coralise.custombansplus.yaml.objects;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import me.coralise.custombansplus.ClassGetter;
import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.yaml.YamlGUIItems;

public class YamlReportsList {

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    private final YamlGUIItems item = new YamlGUIItems();

    private final UUID playerUuid;
    private final Inventory reportsMenu = Bukkit.createInventory(null, 54, "§8§lC§4§lB§8§lP §4Reported Players");
    private int maxPages;
    private int page = 1;
    private int reportsSize;
    private Object[] uuids = m.getReportsConfig().getKeys(false).toArray();

    private static final HashMap<UUID, YamlReportsList> openReportsList = new HashMap<UUID, YamlReportsList>();

    public YamlReportsList (UUID uuid) {
        reportsSize = m.getReportsConfig().getKeys(false).size();
        this.playerUuid = uuid;
        openReportsList.remove(uuid);
        openReportsList.put(uuid, this);
        calculateMaxPages();
    }

    public static YamlReportsList getOpenReportsList (UUID uuid) {
        return openReportsList.get(uuid);
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void calculateMaxPages() {

        reportsSize = m.getReportsConfig().getKeys(false).size();
        int size = reportsSize / 21;
        if (reportsSize % 21 != 0) size++;
        maxPages = size;
        if (page > maxPages) page--;

    }

    public void setupMenu() {

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

        setReports();
        new Thread(() -> setReportsWithSkin()).start();

    }

    public void showMenu () {
        setupMenu();
        Bukkit.getScheduler().runTask(m, () -> Bukkit.getPlayer(playerUuid).openInventory(reportsMenu));
    }

    private void setReports() {

        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
        int si = 0;

        for (int i = (page * 28) - 28;i < reportsSize;i++) {
            reportsMenu.setItem(slots[si], item.getReports(UUID.fromString((String) uuids[i])));
            si++;
            if (si == slots.length) {
                if (i != reportsSize-1)
                    reportsMenu.setItem(53, item.reportsNextPage());
                break;
            }
        }
        if (page != 1) {
            reportsMenu.setItem(51, item.reportsPrevPage());
        }

    }

    private void setReportsWithSkin() {

        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
        int si = 0;

        for (int i = (page * 28) - 28;i < reportsSize;i++) {
            System.out.println(m.getName((String) uuids[i]));
            reportsMenu.setItem(slots[si], item.getReportsWithSkin(UUID.fromString((String) uuids[i])));
            si++;
            if (si == slots.length) {
                break;
            }
        }
        if (page != 1) {
            reportsMenu.setItem(51, item.reportsPrevPage());
        }
        
    }

    public void nextPage() {
        page++;
        if (page > maxPages) {
            page--;
        }
        showMenu();
    }

    public void prevPage() {
        page--;
        if (page == 0) {
            page++;
        }
        showMenu();
    }

    public Inventory getReportsMenu() {
        return reportsMenu;
    }
    
}
