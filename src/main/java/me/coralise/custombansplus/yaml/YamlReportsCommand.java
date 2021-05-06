package me.coralise.custombansplus.yaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;
import me.coralise.custombansplus.yaml.objects.YamlReports;
import me.coralise.custombansplus.yaml.objects.YamlReportsList;

public class YamlReportsCommand extends YamlAbstractCommand {

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    static YamlReportCommand yrc;

    YamlReportsCommand() {
        super("cbpreports", "custombansplus.reports", false);
        ClassGetter.setYamlReportsCommand(this);
    }

    public void showCommand(String[] args, Player sender) {

        if (args.length == 1) {
            sender.sendMessage("§cPlease enter a username.");
            return;
        }

        String target = YamlCache.getPlayerIgn(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer " + args[1] + " has never entered the server.");
            return;
        }
        UUID tgtUuid = m.getUuid(target);

        new Thread(() -> {

            YamlReports reports = new YamlReports(tgtUuid, sender.getUniqueId());

            if (reports.getReports() == null) {
                sender.sendMessage("§aPlayer " + target + " does not have any reports made against them.");
                return;
            }

            reports.showMenu();

        }).start();

    }

    public void listCommand(Player player) {

        if (m.getReportsConfig().getKeys(false).size() == 0) {
            player.sendMessage("§aNo reports so far!");
            return;
        }

        YamlReportsList reportsList = new YamlReportsList(player.getUniqueId());

        reportsList.showMenu();

    }

    private void blacklistCommand(String[] args, Player player) {
        
        //#region Validation
        if (args.length != 3) {
            player.sendMessage("§cInvalid input, please try again.");
            return;
        }

        String target = YamlCache.getPlayerIgn(args[2]);
        if (target == null) {
            player.sendMessage("§cPlayer " + args[2] + " has never entered the server.");
            return;
        }
        //#endregion

        String strUuid = m.getUuid(target).toString();

        if (args[1].equalsIgnoreCase("add")) {
            if (m.getReportsBLConfig().getStringList("blacklisted").contains(strUuid)) {
                player.sendMessage("§c" + target + " is already blacklisted.");
            } else {
                List<String> list = m.getReportsBLConfig().getStringList("blacklisted");
                list.add(strUuid);
                m.getReportsBLConfig().set("blacklisted", list);
                player.sendMessage("§a" + target + " is now blocked from sending reports.");
            }
        } else if (args[1].equalsIgnoreCase("remove")) {
            if (m.getReportsBLConfig().getStringList("blacklisted").contains(strUuid)) {
                List<String> list = m.getReportsBLConfig().getStringList("blacklisted");
                list.remove(strUuid);
                m.getReportsBLConfig().set("blacklisted", list);
                player.sendMessage("§a" + target + " is removed from the reports blacklist.");
            } else {
                player.sendMessage("§c" + target + " is not blacklisted.");
            }
        }

        try {
            m.getReportsBLConfig().save(m.getReportsBLFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)) return false;

        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("§e/reports show <player> - Opens a GUI containing all reports made to the player."
                    + "\n/reports list - Opens a menu showing all reports made."
                    + "\n/reports blacklist <add/remove> <player> - Adds or removes the player to and from the reports blacklist.");
            return false;
        }

        switch (args[0]) {

            case "list":
                listCommand(player);
                return true;

            case "show":
                showCommand(args, player);
                return true;

            case "blacklist":
                blacklistCommand(args, player);
                return true;

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        ArrayList<String> tc = new ArrayList<String>();

        if (args.length == 1) {
            tc.add("show");
            tc.add("list");
            tc.add("blacklist");
            return tc;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("blacklist")) {
            tc.add("add");
            tc.add("remove");
            return tc;
        }

        return null;
    }


    
}