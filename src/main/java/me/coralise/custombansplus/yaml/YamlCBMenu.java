package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;
import me.coralise.custombansplus.yaml.objects.YamlReports;
import me.coralise.custombansplus.yaml.objects.YamlReportsList;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class YamlCBMenu implements Listener {

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    static YamlGUIItems item = new YamlGUIItems();
    public static int sevNum;
    public static Player player;

    public static boolean openMainGUI(Player p) {

        player = p;
        
        Inventory mainMenu = Bukkit.createInventory(null, 27, "§8§lC§4§lB§8§lP Menu");

        mainMenu.setItem(0, item.fillerItem());
        mainMenu.setItem(1, item.fillerItem());
        mainMenu.setItem(2, item.fillerItem());
        mainMenu.setItem(3, item.fillerItem());
        mainMenu.setItem(4, item.fillerItem());
        mainMenu.setItem(5, item.fillerItem());
        mainMenu.setItem(6, item.fillerItem());
        mainMenu.setItem(7, item.fillerItem());
        mainMenu.setItem(8, item.fillerItem());

        mainMenu.setItem(9, item.fillerItem());
        mainMenu.setItem(10, item.sevItem());
        mainMenu.setItem(11, item.fillerItem());
        mainMenu.setItem(12, item.editPageItem());
        mainMenu.setItem(13, item.fillerItem());
        mainMenu.setItem(14, item.editAnnouncersItem());
        mainMenu.setItem(15, item.fillerItem());
        mainMenu.setItem(16, item.miscItem());
        mainMenu.setItem(17, item.fillerItem());

        mainMenu.setItem(18, item.fillerItem());
        mainMenu.setItem(19, item.fillerItem());
        mainMenu.setItem(20, item.fillerItem());
        mainMenu.setItem(21, item.fillerItem());
        mainMenu.setItem(22, item.fillerItem());
        mainMenu.setItem(23, item.fillerItem());
        mainMenu.setItem(24, item.fillerItem());
        mainMenu.setItem(25, item.fillerItem());
        mainMenu.setItem(26, item.fillerItem());

        Bukkit.getScheduler().runTask(m, () -> player.openInventory(mainMenu));

        return true;

    }

    public static boolean openMiscGUI(Player p) {

        player = p;
        
        Inventory miscMenu = Bukkit.createInventory(null, 27, "§8§lC§4§lB§8§lP §3§lMiscellaneous");
        
        miscMenu.setItem(0, item.fillerItem());
        miscMenu.setItem(1, item.fillerItem());
        miscMenu.setItem(2, item.fillerItem());
        miscMenu.setItem(3, item.fillerItem());
        miscMenu.setItem(4, item.fillerItem());
        miscMenu.setItem(5, item.fillerItem());
        miscMenu.setItem(6, item.fillerItem());
        miscMenu.setItem(7, item.fillerItem());
        miscMenu.setItem(8, item.fillerItem());

        miscMenu.setItem(9, item.fillerItem());
        miscMenu.setItem(10, item.fillerItem());
        miscMenu.setItem(11, item.defReasonItem());
        miscMenu.setItem(12, item.fillerItem());
        miscMenu.setItem(13, item.purgeHistItem());
        miscMenu.setItem(14, item.fillerItem());
        miscMenu.setItem(15, item.purgeAltsItem());
        miscMenu.setItem(16, item.fillerItem());
        miscMenu.setItem(17, item.fillerItem());

        miscMenu.setItem(18, item.backItemMain());
        miscMenu.setItem(19, item.fillerItem());
        miscMenu.setItem(20, item.fillerItem());
        miscMenu.setItem(21, item.fillerItem());
        miscMenu.setItem(22, item.fillerItem());
        miscMenu.setItem(23, item.fillerItem());
        miscMenu.setItem(24, item.fillerItem());
        miscMenu.setItem(25, item.fillerItem());
        miscMenu.setItem(26, item.fillerItem());

        Bukkit.getScheduler().runTask(m, () -> player.openInventory(miscMenu));

        return true;

    }

    public static boolean openEditPageGUI(Player p) {

        player = p;
        
        Inventory editPageMenu = Bukkit.createInventory(null, 27, "§8§lC§4§lB§8§lP §2§lEdit Pages");

        editPageMenu.setItem(0, item.fillerItem());
        editPageMenu.setItem(1, item.fillerItem());
        editPageMenu.setItem(2, item.fillerItem());
        editPageMenu.setItem(3, item.fillerItem());
        editPageMenu.setItem(4, item.fillerItem());
        editPageMenu.setItem(5, item.fillerItem());
        editPageMenu.setItem(6, item.fillerItem());
        editPageMenu.setItem(7, item.fillerItem());
        editPageMenu.setItem(8, item.fillerItem());

        editPageMenu.setItem(9, item.fillerItem());
        editPageMenu.setItem(10, item.fillerItem());
        editPageMenu.setItem(11, item.tempBanPageItem());
        editPageMenu.setItem(12, item.fillerItem());
        editPageMenu.setItem(13, item.permBanPageItem());
        editPageMenu.setItem(14, item.fillerItem());
        editPageMenu.setItem(15, item.kickPageItem());
        editPageMenu.setItem(16, item.fillerItem());
        editPageMenu.setItem(17, item.fillerItem());

        editPageMenu.setItem(18, item.backItemMain());
        editPageMenu.setItem(19, item.fillerItem());
        editPageMenu.setItem(20, item.fillerItem());
        editPageMenu.setItem(21, item.fillerItem());
        editPageMenu.setItem(22, item.fillerItem());
        editPageMenu.setItem(23, item.fillerItem());
        editPageMenu.setItem(24, item.fillerItem());
        editPageMenu.setItem(25, item.fillerItem());
        editPageMenu.setItem(26, item.fillerItem());

        Bukkit.getScheduler().runTask(m, () -> player.openInventory(editPageMenu));

        return true;

    }

    public static boolean openEditAnnouncersGUI(Player p) {

        player = p;
        
        Inventory editAnnouncersMenu = Bukkit.createInventory(null, 27, "§8§lC§4§lB§8§lP §d§lEdit Announcers");

        editAnnouncersMenu.setItem(0, item.fillerItem());
        editAnnouncersMenu.setItem(1, item.fillerItem());
        editAnnouncersMenu.setItem(2, item.fillerItem());
        editAnnouncersMenu.setItem(3, item.fillerItem());
        editAnnouncersMenu.setItem(4, item.fillerItem());
        editAnnouncersMenu.setItem(5, item.fillerItem());
        editAnnouncersMenu.setItem(6, item.fillerItem());
        editAnnouncersMenu.setItem(7, item.fillerItem());
        editAnnouncersMenu.setItem(8, item.fillerItem());

        editAnnouncersMenu.setItem(9, item.fillerItem());
        editAnnouncersMenu.setItem(10, item.fillerItem());
        editAnnouncersMenu.setItem(11, item.banAnnouncerItem());
        editAnnouncersMenu.setItem(12, item.ipBanAnnouncerItem());
        editAnnouncersMenu.setItem(13, item.muteAnnouncerItem());
        editAnnouncersMenu.setItem(14, item.warnAnnouncerItem());
        editAnnouncersMenu.setItem(15, item.kickAnnouncerItem());
        editAnnouncersMenu.setItem(16, item.fillerItem());
        editAnnouncersMenu.setItem(17, item.fillerItem());

        editAnnouncersMenu.setItem(18, item.backItemMain());
        editAnnouncersMenu.setItem(19, item.fillerItem());
        editAnnouncersMenu.setItem(20, item.fillerItem());
        editAnnouncersMenu.setItem(21, item.fillerItem());
        editAnnouncersMenu.setItem(22, item.fillerItem());
        editAnnouncersMenu.setItem(23, item.fillerItem());
        editAnnouncersMenu.setItem(24, item.fillerItem());
        editAnnouncersMenu.setItem(25, item.fillerItem());
        editAnnouncersMenu.setItem(26, item.fillerItem());

        Bukkit.getScheduler().runTask(m, () -> player.openInventory(editAnnouncersMenu));

        return true;

    }

    public static boolean openSevsAdminGUI(Player player) {

        YamlCBMenu.player = player;
        
        Inventory sevsMenu = Bukkit.createInventory(null, 36, "§8§lC§4§lB§8§lP §6§lSeverity List");

        int sevTotal = m.getSevConfig().getKeys(false).size();
        sevNum = 1;

        sevsMenu.setItem(0, item.fillerItem());
        sevsMenu.setItem(1, item.fillerItem());
        sevsMenu.setItem(2, item.fillerItem());
        sevsMenu.setItem(3, item.fillerItem());
        sevsMenu.setItem(4, item.fillerItem());
        sevsMenu.setItem(5, item.fillerItem());
        sevsMenu.setItem(6, item.fillerItem());
        sevsMenu.setItem(7, item.fillerItem());
        sevsMenu.setItem(8, item.fillerItem());

        sevsMenu.setItem(9, item.fillerItem());
        sevsMenu.setItem(10, item.airItem());
        sevsMenu.setItem(11, item.airItem());
        sevsMenu.setItem(12, item.airItem());
        sevsMenu.setItem(13, item.airItem());
        sevsMenu.setItem(14, item.airItem());
        sevsMenu.setItem(15, item.airItem());
        sevsMenu.setItem(16, item.airItem());
        sevsMenu.setItem(17, item.fillerItem());

        sevsMenu.setItem(18, item.fillerItem());
        sevsMenu.setItem(19, item.airItem());
        sevsMenu.setItem(20, item.airItem());
        sevsMenu.setItem(21, item.airItem());
        sevsMenu.setItem(22, item.airItem());
        sevsMenu.setItem(23, item.airItem());
        sevsMenu.setItem(24, item.airItem());
        sevsMenu.setItem(25, item.airItem());
        sevsMenu.setItem(26, item.fillerItem());

        sevsMenu.setItem(27, item.backItemMain());
        sevsMenu.setItem(28, item.fillerItem());
        sevsMenu.setItem(29, item.fillerItem());
        sevsMenu.setItem(30, item.fillerItem());
        sevsMenu.setItem(31, item.addSevItem());
        sevsMenu.setItem(32, item.fillerItem());
        sevsMenu.setItem(33, item.fillerItem());
        sevsMenu.setItem(34, item.fillerItem());
        sevsMenu.setItem(35, item.fillerItem());

        for (int i = 10; i < 17 && sevNum <= sevTotal; i++) {
            sevsMenu.setItem(i, item.getSevItem(sevNum));
        }
        for (int i = 19; i < 26 && sevNum <= sevTotal; i++) {
            sevsMenu.setItem(i, item.getSevItem(sevNum));
        }

        Bukkit.getScheduler().runTask(m, () -> player.openInventory(sevsMenu));

        return true;

    }

    public static boolean openSevsListGUI(Player player) {

        YamlCBMenu.player = player;
        
        Inventory sevsMenu = Bukkit.createInventory(null, 36, "§8§lC§4§lB§8§lP §6§lSeverity List");

        int sevTotal = m.getSevConfig().getKeys(false).size();
        sevNum = 1;
        int leftOff = 0;

        sevsMenu.setItem(0, item.fillerItem());
        sevsMenu.setItem(1, item.fillerItem());
        sevsMenu.setItem(2, item.fillerItem());
        sevsMenu.setItem(3, item.fillerItem());
        sevsMenu.setItem(4, item.fillerItem());
        sevsMenu.setItem(5, item.fillerItem());
        sevsMenu.setItem(6, item.fillerItem());
        sevsMenu.setItem(7, item.fillerItem());
        sevsMenu.setItem(8, item.fillerItem());

        sevsMenu.setItem(9, item.fillerItem());

        for (int i = 10; i < 17 && sevNum <= sevTotal; i++) {
            sevsMenu.setItem(i, item.getSevItemList(sevNum));
            leftOff = i + 1;
        }
        for (int i = leftOff; i < 17; i++)
            sevsMenu.setItem(i, item.airItem());

        sevsMenu.setItem(17, item.fillerItem());

        sevsMenu.setItem(18, item.fillerItem());

        for (int i = 19; i < 26 && sevNum <= sevTotal; i++) {
            sevsMenu.setItem(i, item.getSevItemList(sevNum));
            leftOff = i + 1;
        }
        for (int i = leftOff; i < 26; i++)
            sevsMenu.setItem(i, item.airItem());

        sevsMenu.setItem(26, item.fillerItem());

        sevsMenu.setItem(27, item.fillerItem());
        sevsMenu.setItem(28, item.fillerItem());
        sevsMenu.setItem(29, item.fillerItem());
        sevsMenu.setItem(30, item.fillerItem());
        sevsMenu.setItem(31, item.fillerItem());
        sevsMenu.setItem(32, item.fillerItem());
        sevsMenu.setItem(33, item.fillerItem());
        sevsMenu.setItem(34, item.fillerItem());
        sevsMenu.setItem(35, item.fillerItem());

        Bukkit.getScheduler().runTask(m, () -> player.openInventory(sevsMenu));

        return true;

    }

    public boolean openSevEditGUI(int sevNum) {

        Inventory sevsEditMenu = Bukkit.createInventory(null, 27, "§8§lC§4§lB§8§lP Severity #" + sevNum);

        sevsEditMenu.setItem(0, item.fillerItem());
        sevsEditMenu.setItem(1, item.fillerItem());
        sevsEditMenu.setItem(2, item.fillerItem());
        sevsEditMenu.setItem(3, item.fillerItem());
        sevsEditMenu.setItem(4, item.fillerItem());
        sevsEditMenu.setItem(5, item.fillerItem());
        sevsEditMenu.setItem(6, item.fillerItem());
        sevsEditMenu.setItem(7, item.fillerItem());
        sevsEditMenu.setItem(8, item.fillerItem());

        sevsEditMenu.setItem(9, item.fillerItem());
        sevsEditMenu.setItem(10, item.duraItem(sevNum));
        sevsEditMenu.setItem(11, item.fillerItem());
        sevsEditMenu.setItem(12, item.balDeductItem(sevNum));
        sevsEditMenu.setItem(13, item.fillerItem());
        sevsEditMenu.setItem(14, item.clearInvItem(sevNum));
        sevsEditMenu.setItem(15, item.fillerItem());
        sevsEditMenu.setItem(16, item.cnslCmdItem(sevNum));
        sevsEditMenu.setItem(17, item.fillerItem());

        sevsEditMenu.setItem(18, item.backItemSevs());
        sevsEditMenu.setItem(19, item.fillerItem());
        sevsEditMenu.setItem(20, item.fillerItem());
        sevsEditMenu.setItem(21, item.fillerItem());
        sevsEditMenu.setItem(22, item.fillerItem());
        sevsEditMenu.setItem(23, item.fillerItem());
        sevsEditMenu.setItem(24, item.fillerItem());
        sevsEditMenu.setItem(25, item.fillerItem());
        sevsEditMenu.setItem(26, item.fillerItem());

        Bukkit.getScheduler().runTask(m, () -> player.openInventory(sevsEditMenu));

        return true;

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        try{
        if(!event.getView().getTitle().substring(0, 15).equalsIgnoreCase("§8§lC§4§lB§8§lP"))
            return;
        }catch(StringIndexOutOfBoundsException exc){
            return;
        }

        event.setResult(Result.DENY);

        Inventory inv = event.getClickedInventory();
        if (inv == null || inv.getType().toString().equalsIgnoreCase("PLAYER")) {
            return;
        }

        if (event.getCurrentItem() == null)
            return;

        //----------------------------------------------------------------------//

        new Thread(() -> {

            Player p = (Player) event.getWhoClicked();

            YamlCBMenu.player = p;

            String is = event.getCurrentItem().getItemMeta().getLocalizedName();

            if (is.startsWith("report ")) {

                YamlReports reports = YamlReports.getOpenReports().get(player.getUniqueId());

                if (is.equalsIgnoreCase("report next page")) {
                    reports.nextPage();
                    return;
                } else if (is.equalsIgnoreCase("report prev page")) {
                    reports.prevPage();
                    return;
                }

                String[] values = is.split(" ");
                UUID uuid = UUID.fromString(values[1]);
                int reportNum = Integer.parseInt(values[2]);
                String path = uuid.toString() + "." + reportNum;

                if (event.getClick() == ClickType.LEFT) {
                    if (m.getReportsConfig().getBoolean(path + ".resolved")) {
                        m.getReportsConfig().set(path + ".resolved", false);
                        m.getReportsConfig().set(path + ".resolver", null);
                    } else {
                        m.getReportsConfig().set(path + ".resolved", true);
                        m.getReportsConfig().set(path + ".resolver", p.getName());
                    }
                } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    for (String strNum : m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false)) {
                        int num = Integer.parseInt(strNum);
                        int nextNum = num + 1;
                        if (num < reportNum) continue;
                        if (!m.getReportsConfig().contains(uuid.toString() + "." + nextNum)) {
                            m.getReportsConfig().set(uuid.toString() + "." + num, null);
                            break;
                        }
                        for (String key : m.getReportsConfig().getConfigurationSection(uuid.toString() + "." + num).getKeys(false)) {
                            m.getReportsConfig().set(uuid.toString() + "." + num + "." + key, m.getReportsConfig().get(uuid.toString() + "." + nextNum + "." + key));
                        }
                    }
                    reports.calculateMaxPages();
                    if (reports.getMaxPages() == 0) {
                        m.getReportsConfig().set(uuid.toString(), null);
                        YamlReports.getOpenReports().remove(p.getUniqueId());
                        ClassGetter.getYamlReportsCommand().listCommand(player);
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        try {
                            m.getReportsConfig().save(m.getReportsFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }

                try {
                    m.getReportsConfig().save(m.getReportsFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                reports.reloadReports();
                reports.setupMenu();

                return;

            }

            if (is.startsWith("reports ")) {

                YamlReportsList reportsList = YamlReportsList.getOpenReportsList(player.getUniqueId());

                if (is.equalsIgnoreCase("reports next page")) {
                    reportsList.nextPage();
                    return;
                } else if (is.equalsIgnoreCase("reports prev page")) {
                    reportsList.prevPage();
                    return;
                }
                String[] args = {"show", m.getName(is.split(" ")[1])};
                ClassGetter.getYamlReportsCommand().showCommand(args, player);
                return;
            }

            switch(is){

                case "SEVERITIES":
                case "Back to Severity List":
                    openSevsAdminGUI(player);
                    return;

                case "Back to Main Menu":
                    openMainGUI(player);
                    return;

                case "Back to Reports List":
                    ClassGetter.getYamlReportsCommand().listCommand(player);
                    return;

                case "Add a Severity":
                    YamlSevCommand.addSeverity();
                    openSevsAdminGUI(player);
                    return;

                case "EDIT PAGES":
                    openEditPageGUI(player);
                    return;

                case "EDIT ANNOUNCERS":
                    openEditAnnouncersGUI(player);
                    return;

                case "Temp Ban Announcer":
                    if(event.getClick() == ClickType.LEFT){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), "tempban");
                        player.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eFORMATS: §d%player% %staff% %duration% %reason%");
                    }else if(event.getClick() == ClickType.MIDDLE){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlAbstractAnnouncer.getSilentAnnouncer("Victim", player.getName(), "7d", "@Reason", "tempban");
                    }
                    return;

                case "IP Ban Announcer":
                    if(event.getClick() == ClickType.LEFT){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), "ipban");
                        player.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eFORMATS: §d%player% %staff% %duration% %reason%");
                    }else if(event.getClick() == ClickType.MIDDLE){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlAbstractAnnouncer.getSilentAnnouncer("Victim", player.getName(), "@7d", "@Reason", "ipban");
                    }
                    return;

                case "Mute Announcer":
                    if(event.getClick() == ClickType.LEFT){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), "mute");
                        player.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eFORMATS: §d%player% %staff% %duration% %reason%");
                    }else if(event.getClick().toString().equalsIgnoreCase("MIDDLE")){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlAbstractAnnouncer.getSilentAnnouncer("Victim", player.getName(), "@7d", "@Reason", "mute");
                    }
                    return;

                case "Warn Announcer":
                    if(event.getClick() == ClickType.LEFT){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), "warn");
                        player.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eFORMATS: §d%player% %staff% %reason%");
                    }else if(event.getClick() == ClickType.MIDDLE){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlAbstractAnnouncer.getSilentAnnouncer("Victim", player.getName(), null, "@Reason", "warn");
                    }
                    return;

                case "Kick Announcer":
                    if(event.getClick() == ClickType.LEFT){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), "kick");
                        player.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eFORMATS: §d%player% %staff% %reason%");
                    }else if(event.getClick() == ClickType.MIDDLE){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlAbstractAnnouncer.getSilentAnnouncer("Victim", player.getName(), null, "@Reason", "kick");
                    }
                    return;

                case "Temp Ban Page":
                    if(event.getClick() == ClickType.LEFT){
                        YamlCBCommand.isEditing.put(player.getName(), "temp");
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        player.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eFORMATS: §d%staff% %duration% %reason% %unban-date% %player% %timeleft% /n");
                    }else if(event.getClick() == ClickType.MIDDLE){
                        player.kickPlayer(YamlAbstractBanCommand.getBanMsgTest(m.getUuid(player), "temp"));
                    }
                    return;

                case "Perm Ban Page":
                    if(event.getClick() == ClickType.LEFT){
                        YamlCBCommand.isEditing.put(player.getName(), "perm");
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        player.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eFORMATS: §d%staff% %duration% %reason% %unban-date% %player% %timeleft% /n");
                    }else if(event.getClick() == ClickType.MIDDLE){
                        player.kickPlayer(YamlAbstractBanCommand.getBanMsgTest(m.getUuid(player), "perm"));
                    }
                    return;

                case "Kick Page":
                    if(event.getClick() == ClickType.LEFT){
                        YamlCBCommand.isEditing.put(player.getName(), "kickPage");
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        player.sendMessage("§eType in the new format you want down below. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eFORMATS: §d%staff% %reason% %player% /n");
                    }else if(event.getClick() == ClickType.MIDDLE){
                        player.kickPlayer(YamlKickCommand.getKickMsg(true));
                    }
                    return;

                case "misc":
                    openMiscGUI(player);
                    return;

                case "default reason":
                    if(event.getClick() == ClickType.LEFT){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), "defaultreason");
                        player.sendMessage("§eType in the new default reason you want down below. Type \"cancel\" to cancel action.");
                        return;
                    }

                case "punishment histories":
                    if(event.getClick() == ClickType.SHIFT_RIGHT){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.clearHistories((CommandSender) player);
                        return;
                    }

                case "alts histories":
                    if(event.getClick() == ClickType.SHIFT_RIGHT){
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.clearAccounts((CommandSender) player);
                        return;
                    }

            }

            //------------------SEVERITY-------------------//

            String sevNum2 = "";

            switch(is){

                case "1":
                case "1 Duration":
                case "1 Balance Deduction":
                case "1 Clear Inventory":
                case "1 Console Commands": sevNum2 = "1"; break;
                case "2":
                case "2 Duration":
                case "2 Balance Deduction":
                case "2 Clear Inventory":
                case "2 Console Commands": sevNum2 = "2"; break;
                case "3":
                case "3 Duration":
                case "3 Balance Deduction":
                case "3 Clear Inventory":
                case "3 Console Commands": sevNum2 = "3"; break;
                case "4":
                case "4 Duration":
                case "4 Balance Deduction":
                case "4 Clear Inventory":
                case "4 Console Commands": sevNum2 = "4"; break;
                case "5":
                case "5 Duration":
                case "5 Balance Deduction":
                case "5 Clear Inventory":
                case "5 Console Commands": sevNum2 = "5"; break;
                case "6":
                case "6 Duration":
                case "6 Balance Deduction":
                case "6 Clear Inventory":
                case "6 Console Commands": sevNum2 = "6"; break;
                case "7":
                case "7 Duration":
                case "7 Balance Deduction":
                case "7 Clear Inventory":
                case "7 Console Commands": sevNum2 = "7"; break;
                case "8":
                case "8 Duration":
                case "8 Balance Deduction":
                case "8 Clear Inventory":
                case "8 Console Commands": sevNum2 = "8"; break;
                case "9":
                case "9 Duration":
                case "9 Balance Deduction":
                case "9 Clear Inventory":
                case "9 Console Commands": sevNum2 = "9"; break;
                case "10":
                case "10 Duration":
                case "10 Balance Deduction":
                case "10 Clear Inventory":
                case "10 Console Commands": sevNum2 = "10"; break;
                case "11":
                case "11 Duration":
                case "11 Balance Deduction":
                case "11 Clear Inventory":
                case "11 Console Commands": sevNum2 = "11"; break;
                case "12":
                case "12 Duration":
                case "12 Balance Deduction":
                case "12 Clear Inventory":
                case "12 Console Commands": sevNum2 = "12"; break;
                case "13":
                case "13 Duration":
                case "13 Balance Deduction":
                case "13 Clear Inventory":
                case "13 Console Commands": sevNum2 = "13"; break;
                case "14":
                case "14 Duration":
                case "14 Balance Deduction":
                case "14 Clear Inventory":
                case "14 Console Commands": sevNum2 = "14"; break;

            }

            if (is.length() == 1 || is.length() == 2){
                if (event.getClick() == ClickType.LEFT) {
                    openSevEditGUI(Integer.parseInt(sevNum2));
                }
                if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    YamlSevCommand.deleteSeverity(Integer.parseInt(sevNum2));
                    openSevsAdminGUI(player);
                }
                return;
            }

            if (is.length() > 4) switch (is.substring(is.indexOf(' ')+1)) {

                case "Duration":
                    if (event.getClick() == ClickType.LEFT) {
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), sevNum2 + "durationGUI");
                        player.sendMessage("§eAccepted values: §dperm or Xd Xh Xm Xs (You may combine them, i.e. 1d6h30m)");
                        player.sendMessage("§eEnter new duration:");
                    }
                    if (event.getClick() == ClickType.RIGHT) {
                        m.getSevConfig().set(sevNum2 + ".duration", "3d");
                        try {
                            m.getSevConfig().save(m.getSevFile());
                        } catch (IOException e) {
                        }
                        openSevEditGUI(Integer.parseInt(sevNum2));
                    }
                    return;

                case "Balance Deduction":
                    if (event.getClick() == ClickType.LEFT) {
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), sevNum2 + "baldeductGUI");
                        player.sendMessage("§eAccepted values: §dX%");
                        player.sendMessage("§eEnter new Balance Deduction Percentage:");
                    }
                    if (event.getClick() == ClickType.RIGHT) {
                        m.getSevConfig().set(sevNum2 + ".baldeduct", .3);
                        try {
                            m.getSevConfig().save(m.getSevFile());
                        } catch (IOException e) {
                        }
                        openSevEditGUI(Integer.parseInt(sevNum2));
                    }
                    return;

                case "Clear Inventory":
                    if (event.getClick() == ClickType.LEFT) {
                        m.getSevConfig().set(sevNum2 + ".clear-inv", true);
                        try {
                            m.getSevConfig().save(m.getSevFile());
                        } catch (IOException e) {
                        }
                        openSevEditGUI(Integer.parseInt(sevNum2));
                    }
                    if (event.getClick() == ClickType.RIGHT) {
                        m.getSevConfig().set(sevNum2 + ".clear-inv", false);
                        try {
                            m.getSevConfig().save(m.getSevFile());
                        } catch (IOException e) {
                        }
                        openSevEditGUI(Integer.parseInt(sevNum2));
                    }
                    return;

                case "Console Commands":
                    if (event.getClick() == ClickType.LEFT) {
                        Bukkit.getScheduler().runTask(m, () -> player.closeInventory());
                        YamlCBCommand.isEditing.put(player.getName(), sevNum2 + "console-cmdsGUI");
                        player.sendMessage("§eCommand without /. Separate each with comma (commandx,commandy). Enter player as \"§d%player%§e\" (reset %player%). Enter \"clear\" to clear commands. Type \"cancel\" to cancel action.");
                        player.sendMessage("§eEnter new Console Commands:");
                    }
                    if (event.getClick() == ClickType.RIGHT) {
                        m.getSevConfig().set(sevNum2 + ".console-commands", "[]");
                        try {
                            m.getSevConfig().save(m.getSevFile());
                        } catch (IOException e) {
                        }
                        openSevEditGUI(Integer.parseInt(sevNum2));
                    }

            }

        }).start();

        //-------------------SEVERITY--------------------//

    }
    
}
