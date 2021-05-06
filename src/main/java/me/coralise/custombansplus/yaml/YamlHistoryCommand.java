package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;

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

public class YamlHistoryCommand extends YamlAbstractCommand implements Listener {

    public YamlHistoryCommand() {
        super("cbphistory", "custombansplus.ban", false);
    }

    static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    static YamlGUIItems item = new YamlGUIItems();
    static int[] slot = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38,
            39, 40, 41, 42, 43 };
    static int slotIndex;

    int banPoint;
    int mutePoint;
    HashMap<String, UUID> t = new HashMap<String, UUID>();
    static HashMap<String, Integer> histNum = new HashMap<String, Integer>();
    HashMap<String, Integer> preHistNum = new HashMap<String, Integer>();
    HashMap<String, Integer> histPage = new HashMap<String, Integer>();

    public boolean checkPoints(UUID tgtUuid, String strPlayer) {

        banPoint = 0;
        mutePoint = 0;

        int banCounter = 0;
        int muteCounter = 0;

        if (!YamlCache.isPlayerBanned(tgtUuid))
            banCounter = 2;

        if (!YamlCache.isPlayerMuted(tgtUuid))
            muteCounter = 2;

        int count = histNum.get(strPlayer);

        while (count > 0) {

            String parentPath = tgtUuid.toString() + "." + count;
            if (muteCounter == 0 && m.getHistConfig().getString(parentPath + ".type").equalsIgnoreCase("Mute") && !YamlCache.isMuteLifted(tgtUuid)) {
                mutePoint = count;
                muteCounter = 2;
            }
            if (banCounter == 0 && !YamlCache.isBanLifted(tgtUuid) && (m.getHistConfig().getString(parentPath + ".type").contains("Ban")
                    || m.getHistConfig().getString(parentPath + ".type").contains("IP Ban"))) {
                banPoint = count;
                banCounter = 2;
            }

            if (banCounter == 2 && muteCounter == 2)
                break;

            count--;

        }

        return true;

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

    public static void setPunishments(Inventory histGUI, UUID tgtUuid, int banPoint, int mutePoint, String strPlayer) {

        slotIndex = 0;

        int hn = histNum.get(strPlayer);

        while (hn > 0) {

            String type = m.getHistConfig().getString(tgtUuid.toString() + "." + hn + ".type");
            Material mat = Material.AIR;

            switch (type) {

                case "Temp Ban":
                    mat = Material.STONE_SWORD;
                    break;
                case "Perm Ban":
                    mat = Material.STONE_SWORD;
                    break;
                case "Temp IP Ban":
                    mat = Material.IRON_SWORD;
                    break;
                case "Perm IP Ban":
                    mat = Material.IRON_SWORD;
                    break;
                case "Warn":
                    mat = Material.ARROW;
                    break;
                case "Mute":
                    mat = Material.STICK;
                    break;
                case "Kick":
                    mat = Material.WOODEN_SWORD;
                    break;

            }

            if (hn == banPoint)
                mat = Material.DIAMOND_SWORD;
            if (hn == mutePoint)
                mat = Material.BLAZE_ROD;

            String parentPath = tgtUuid.toString() + "." + hn;

            ItemStack punishment = new ItemStack(mat, 1);
            ItemMeta meta = punishment.getItemMeta();
            meta.setDisplayName("§r§f" + hn + ": §r§c" + type);
            meta.setLocalizedName("History " + hn);
            List<String> lore = new ArrayList<String>();

            for (String path : m.getHistConfig().getConfigurationSection(parentPath).getKeys(false)) {
                if (path.equalsIgnoreCase("type"))
                    continue;
                String key = path;
                if (key.contains("-"))
                    key = key.replace("-", " ");
                key = key.substring(0, 1).toUpperCase() + key.substring(1) + ": ";
                if((key.equalsIgnoreCase("Banned by: ") || key.equalsIgnoreCase("Muted by: ") || key.equalsIgnoreCase("Kicked by: ") || key.equalsIgnoreCase("Warned by: ")) && !m.getHistConfig().getString(parentPath + "." + path).equalsIgnoreCase("CONSOLE")){
                    lore.add("§a" + key + "§f" + m.getName(m.getHistConfig().getString(parentPath + "." + path)));
                    continue;
                }
                lore.add("§a" + key + "§f" + m.getHistConfig().get(parentPath + "." + path));
            }
            if (hn == banPoint) {
                lore.add("§aBan: §cActive");
                if (!YamlCache.getBannedObject(tgtUuid).getDuration().equalsIgnoreCase("Permanent"))
                    lore.add("§aTime remaining: §c" + m.getTimeRemaining(YamlCache.banCache.get(tgtUuid).getUnbanDate()));
            } else if (hn == mutePoint) {
                lore.add("§aMute: §cActive");
                if (!YamlCache.getMutedObject(tgtUuid).getDuration().equalsIgnoreCase("Permanent"))
                    lore.add("§aTime remaining: §c" + m.getTimeRemaining(YamlCache.muteCache.get(tgtUuid).getUnmuteDate()));
            }

            meta.setLore(lore);
            punishment.setItemMeta(meta);

            histGUI.setItem(slot[slotIndex], punishment);

            slotIndex++;
            hn--;
            histNum.replace(strPlayer, hn);
            

            if (slotIndex == slot.length)
                break;

        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!isValid(sender)) {
            return false;
        }

        Player player = (Player) sender;
        String strPlayer = player.getName();

        YamlCBMenu.player = player;

        if (args.length == 0) {
            sender.sendMessage("§e/hist <player> - Shows specified player's punishment history.");
            return true;
        }

        String target = YamlCache.getPlayerIgn(args[0]);

        if (target == null) {
            sender.sendMessage("§aPlayer " + args[0] + " does not have any history.");
            return true;
        }
        UUID tgtUuid = m.getUuid(target);

        // ----------------------------------//

        Inventory histGUI = Bukkit.createInventory(null, 54, "§8§lC§4§lB§8§lP §8" + target + "'s History");
        setInventory(histGUI);

        t.put(strPlayer, tgtUuid);

        histNum.put(player.getName(), m.getHistConfig().getConfigurationSection(tgtUuid.toString()).getKeys(false).size());
        preHistNum.remove(strPlayer);
        histPage.remove(strPlayer);

        checkPoints(tgtUuid, strPlayer);

        int bp = banPoint;
        int mp = mutePoint;

        setPunishments(histGUI, tgtUuid, bp, mp, strPlayer);
        int hn = histNum.get(strPlayer);

        if(hn != 0) histGUI.setItem(50, item.histNextPage());
        histPage.put(strPlayer, 0);

        Bukkit.getScheduler().runTask(m, () -> player.openInventory(histGUI));

        return true;
    
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){

        String title = event.getView().getTitle();
        
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
        int hn = histNum.get(p.getName());
        Inventory histGUI = Bukkit.createInventory(null, 54, "§8§lC§4§lB§8§lP §8" + t.get(p.getName()) + "'s History");

        String is = event.getCurrentItem().getItemMeta().getLocalizedName();

        switch(is){

            case "hist Next Page":
                preHistNum.put(p.getName(), hn);
                setInventory(histGUI);
                setPunishments(histGUI, t.get(p.getName()), banPoint, mutePoint, p.getName());
                hn = histNum.get(p.getName());
                if(hn != 0) histGUI.setItem(50, item.histNextPage());
                histPage.replace(p.getName(), histPage.get(p.getName())+1);
                histGUI.setItem(48, item.histPrevPage());
                Bukkit.getScheduler().runTask(m, () -> p.openInventory(histGUI));
                return;

            case "hist Prev Page":
                hn = preHistNum.get(p.getName()) + 28;
                histNum.replace(p.getName(), hn);
                preHistNum.put(p.getName(), hn);
                setInventory(histGUI);
                setPunishments(histGUI, t.get(p.getName()), banPoint, mutePoint, p.getName());
                histGUI.setItem(50, item.histNextPage());
                histPage.replace(p.getName(), histPage.get(p.getName())-1);
                if(histPage.get(p.getName()) != 0) histGUI.setItem(48, item.histPrevPage());
                Bukkit.getScheduler().runTask(m, () -> p.openInventory(histGUI));
                return;

        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        return null;

    }
    


}
