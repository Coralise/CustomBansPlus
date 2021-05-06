package me.coralise.custombansplus.sql;
import me.coralise.custombansplus.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SqlHistoryCommand extends SqlAbstractCommand implements Listener {

    public SqlHistoryCommand() {
        super("cbphistory", "custombansplus.ban", false);
    }

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    static SqlGUIItems item = new SqlGUIItems();
    static int[] slot = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38,
            39, 40, 41, 42, 43 };
    static int slotIndex;
    Inventory histGUI;

    HashMap<String, String> t = new HashMap<String, String>();
    static HashMap<String, Integer> histIndex = new HashMap<String, Integer>();
    HashMap<String, Integer> preHistIndex = new HashMap<String, Integer>();
    HashMap<String, Integer> histPage = new HashMap<String, Integer>();
    HashMap<String, String[][]> histTarget = new HashMap<String, String[][]>();
    String target;
    String strPlayer;
    Player player;

    Material getMaterial(String type, String[][] history, int hi, UUID tgtUuid){
        switch(type){

            case "Temp Ban":
            case "Perm Ban":
                if(history[hi][7].equalsIgnoreCase("Active") && !SqlCache.isBanLifted(tgtUuid))
                    return Material.DIAMOND_SWORD;
                else
                    return Material.IRON_SWORD;

            case "Temp IP Ban":
            case "Perm IP Ban":
                if(history[hi][7].equalsIgnoreCase("Active") && !SqlCache.isBanLifted(tgtUuid))
                    return Material.DIAMOND_SWORD;
                else
                    return Material.GOLDEN_SWORD;

            case "Temp Mute":
            case "Perm Mute":
                if(history[hi][7].equalsIgnoreCase("Active") && !SqlCache.isMuteLifted(tgtUuid))
                    return Material.BLAZE_ROD;
                else
                    return Material.STICK;

            case "Kick":
                return Material.WOODEN_SWORD;

            case "Warn":
                return Material.FEATHER;

        }
        return Material.AIR;
    }

    public static void setInventory(Inventory histGUI) {

        histGUI.setItem(0, item.fillerItem());
        histGUI.setItem(1, item.fillerItem());
        histGUI.setItem(2, item.fillerItem());
        histGUI.setItem(3, item.fillerItem());
        histGUI.setItem(4, item.fillerItem());
        histGUI.setItem(5, item.fillerItem());
        histGUI.setItem(6, item.fillerItem());
        histGUI.setItem(7, item.fillerItem());
        histGUI.setItem(8, item.fillerItem());

        histGUI.setItem(9, item.fillerItem());
        histGUI.setItem(10, item.airItem());
        histGUI.setItem(11, item.airItem());
        histGUI.setItem(12, item.airItem());
        histGUI.setItem(13, item.airItem());
        histGUI.setItem(14, item.airItem());
        histGUI.setItem(15, item.airItem());
        histGUI.setItem(16, item.airItem());
        histGUI.setItem(17, item.fillerItem());

        histGUI.setItem(18, item.fillerItem());
        histGUI.setItem(19, item.airItem());
        histGUI.setItem(20, item.airItem());
        histGUI.setItem(21, item.airItem());
        histGUI.setItem(22, item.airItem());
        histGUI.setItem(23, item.airItem());
        histGUI.setItem(24, item.airItem());
        histGUI.setItem(25, item.airItem());
        histGUI.setItem(26, item.fillerItem());

        histGUI.setItem(27, item.fillerItem());
        histGUI.setItem(28, item.airItem());
        histGUI.setItem(29, item.airItem());
        histGUI.setItem(30, item.airItem());
        histGUI.setItem(31, item.airItem());
        histGUI.setItem(32, item.airItem());
        histGUI.setItem(33, item.airItem());
        histGUI.setItem(34, item.airItem());
        histGUI.setItem(35, item.fillerItem());

        histGUI.setItem(36, item.fillerItem());
        histGUI.setItem(37, item.airItem());
        histGUI.setItem(38, item.airItem());
        histGUI.setItem(39, item.airItem());
        histGUI.setItem(40, item.airItem());
        histGUI.setItem(41, item.airItem());
        histGUI.setItem(42, item.airItem());
        histGUI.setItem(43, item.airItem());
        histGUI.setItem(44, item.fillerItem());

        histGUI.setItem(45, item.fillerItem());
        histGUI.setItem(46, item.fillerItem());
        histGUI.setItem(47, item.fillerItem());
        histGUI.setItem(48, item.fillerItem());
        histGUI.setItem(49, item.fillerItem());
        histGUI.setItem(50, item.fillerItem());
        histGUI.setItem(51, item.fillerItem());
        histGUI.setItem(52, item.fillerItem());
        histGUI.setItem(53, item.fillerItem());

    }

    public void setPunishments(Inventory histGUI, UUID tgtUuid, String strPlayer) {

        slotIndex = 0;

        int hi = histIndex.get(strPlayer);
        String[][] history = histTarget.get(target);

        while (slotIndex < slot.length) {

            String type = history[hi][1];
            Material mat = getMaterial(type, history, hi, tgtUuid);

            ItemStack punishment = new ItemStack(mat, 1);
            ItemMeta meta = punishment.getItemMeta();
            int num = hi+1;
            meta.setDisplayName("§r§f" + num + ": §r§c" + type);
            meta.setLocalizedName("History " + hi);
            List<String> lore = new ArrayList<String>();

            lore.add("§aReason: §f" + history[hi][3]);
            lore.add("§aStaff: §f" + m.getName(history[hi][2]));
            lore.add("§aDate: §f" + history[hi][4]);

            if(!history[hi][5].equalsIgnoreCase("None")){
                lore.add("§aDuration: §f" + history[hi][5]);
                if(!history[hi][5].equalsIgnoreCase("Permanent"))
                    lore.add("§aLift by: §f" + history[hi][6]);
            }

            switch(history[hi][7]){

                case "Active":
                    if(SqlCache.isBanLifted(tgtUuid) && SqlCache.isMuteLifted(tgtUuid)){
                        SqlCache.removeBan(tgtUuid, "Lifted", null);
                        SqlCache.removeIpBan(m.getSqlIp(tgtUuid), "Lifted", null);
                        SqlCache.removeMute(tgtUuid, "Lifted", null);
                        try {
                            SqlMethods.updateHistoryStatus(tgtUuid, "Ban", "Lifted", null);
                            SqlMethods.updateHistoryStatus(tgtUuid, "Mute", "Lifted", null);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        lore.add("§aStatus: §cActive");
                        if (!history[hi][5].equalsIgnoreCase("Permanent"))
                            lore.add("§aTime remaining: §c" + m.getTimeRemaining(history[hi][6]));
                        break;
                    }

                case "Lifted":
                    lore.add("§aStatus: §fLifted");
                    break;

                case "Overwritten":
                    lore.add("§aStatus: §eOverwritten");
                    lore.add("§aBy: §e" + m.getName(history[hi][8]));
                    break;

                case "Unbanned":
                    lore.add("§aStatus: §eUnbanned");
                    lore.add("§aBy: §e" + m.getName(history[hi][8]));
                    break;

                case "Unmuted":
                    lore.add("§aStatus: §eUnmuted");
                    lore.add("§aBy: §e" + m.getName(history[hi][8]));
                    break;

            }

            meta.setLore(lore);
            punishment.setItemMeta(meta);

            histGUI.setItem(slot[slotIndex], punishment);

            slotIndex++;
            hi++;
            histIndex.replace(strPlayer, hi);
            if(hi == history.length) break;

        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!isValid(sender)) {
            return false;
        }

        player = (Player) sender;
        strPlayer = player.getName();

        SqlCBMenu.player = player;

        if (args.length == 0) {
            sender.sendMessage("§e/hist <player> - Shows specified player's punishment history.");
            return true;
        }

        target = SqlCache.getPlayerIgn(args[0]);
        UUID tgtUuid = m.getUuid(target);

        if(target == null){
            sender.sendMessage("§cPlayer " + args[0] + " has never entered the server.");
            return false;
        }

        try {
            if (!SqlMethods.playerHasHistory(tgtUuid)) {
                sender.sendMessage("§aPlayer " + target + " does not have any history.");
                return true;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        new Thread(() -> {

            histGUI = Bukkit.createInventory(null, 54, "§8§lC§4§lB§8§lP §8" + t.get(player.getName()) + "'s History");

            histGUI = Bukkit.createInventory(null, 54, "§8§lC§4§lB§8§lP §8" + target + "'s History");
            setInventory(histGUI);

            t.put(strPlayer, target);

            String[][] history = new String[0][0];
            try {
                history = SqlMethods.getHistories(tgtUuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            histTarget.put(target, history);

            histIndex.put(strPlayer, 0);
            preHistIndex.remove(strPlayer);
            histPage.remove(strPlayer);

            setPunishments(histGUI, tgtUuid, strPlayer);
            int hi = histIndex.get(strPlayer);

            if(hi != history.length) histGUI.setItem(50, item.histNextPage());
            histPage.put(strPlayer, 0);

            Bukkit.getScheduler().runTask(m, () -> player.openInventory(histGUI));

        }).start();

        return true;
    
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){

        String title = event.getView().getTitle();
        
        new Thread(() -> {

            try{
                if(!title.substring(title.length()-7).equalsIgnoreCase("History"))
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
            
            Player p = (Player) event.getWhoClicked();
            int hi = histIndex.get(p.getName());
            String[][] history = histTarget.get(t.get(p.getName()));
    
            String is = event.getCurrentItem().getItemMeta().getLocalizedName();
    
            switch(is){
    
                case "hist Next Page":
                    preHistIndex.put(p.getName(), hi);
                    setInventory(histGUI);
                    setPunishments(histGUI, m.getUuid(t.get(p.getName())), p.getName());
                    hi = histIndex.get(p.getName());
                    if(hi != history.length) histGUI.setItem(50, item.histNextPage());
                    histPage.replace(p.getName(), histPage.get(p.getName())+1);
                    histGUI.setItem(48, item.histPrevPage());
                    Bukkit.getScheduler().runTask(m, () -> p.openInventory(histGUI));
                    return;
    
                case "hist Prev Page":
                    hi = preHistIndex.get(p.getName()) - 28;
                    histIndex.replace(p.getName(), hi);
                    preHistIndex.put(p.getName(), hi);
                    setInventory(histGUI);
                    setPunishments(histGUI, m.getUuid(t.get(p.getName())), p.getName());
                    histGUI.setItem(50, item.histNextPage());
                    histPage.replace(p.getName(), histPage.get(p.getName())-1);
                    if(histPage.get(p.getName()) != 0) histGUI.setItem(48, item.histPrevPage());
                    Bukkit.getScheduler().runTask(m, () -> p.openInventory(histGUI));
                    return;
    
            }

        }).start();

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        return null;

    }

}