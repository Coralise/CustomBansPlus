package me.coralise.custombansplus.yaml;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;

public class YamlReportCommand extends YamlAbstractCommand {

    YamlReportCommand() {
        super("cbpreport", "custombansplus.report", false);
        ClassGetter.setYamlReportCommand(this);
    }

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //#region Validation
        if (!isValid(sender)) return false;

        Player p = (Player) sender;

        if (m.getReportsBLConfig().getStringList("blacklisted").contains(p.getUniqueId().toString())) {
            sender.sendMessage(m.parseMessage(m.getConfig().getString("messages.report.blacklisted-message")));
            return false;
        }

        if (args.length <= 1) {
            sender.sendMessage("§e/report <player> <report> - Reports a player to the staff team for the specified reason.");
            return false;
        }

        String target = YamlCache.getPlayerIgn(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer " + args[0] + " has never entered the server.");
            return false;
        }
        //#endregion

        UUID tgtUuid = m.getUuid(target);

        new Thread(() -> {

            String report = "";
            for (int i = 1;i < args.length;i++) {
                report = report.concat(args[i] + " ");
            }
            report = report.trim();

            int num = 1;
            if (m.getReportsConfig().isConfigurationSection(tgtUuid.toString()))
                num = m.getReportsConfig().getConfigurationSection(tgtUuid.toString()).getKeys(false).size() + 1;
            String path = tgtUuid.toString() + "." + num;

            m.getReportsConfig().set(path + ".date", formatter.format(new Date()));
            m.getReportsConfig().set(path + ".reporter", m.getUuid(sender).toString());
            m.getReportsConfig().set(path + ".report", report);
            m.getReportsConfig().set(path + ".resolved", false);

            try {
                m.getReportsConfig().save(m.getReportsFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            sender.sendMessage(m.parseMessage(m.getConfig().getString("messages.report.report-successful")));

            String fReport = report;
            Bukkit.getOnlinePlayers().stream().filter(pl -> pl.hasPermission("custombansplus.reports.notify")).forEach(pl -> pl.sendMessage(parseMessage(p.getName(), target, fReport)));

        }).start();

        return true;
    }

    private String parseMessage(String reporter, String reported, String report) {
        String msg = m.parseMessage(m.getConfig().getString("messages.report.staff-notify"));

        msg = msg.replace("%reporter%", reporter);
        msg = msg.replace("%reported%", reported);
        msg = msg.replace("%report%", report);

        return msg;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        ArrayList<String> tc = new ArrayList<String>();
        tc.add ("<report>");

        if (args.length == 1)
            return null;

        if (args.length == 2)
            return tc;

        return null;
    }

	public void addReport(OfflinePlayer offlinePlayer, String[] args) {
        String tgtUuid = offlinePlayer.getUniqueId().toString();
        String report = "";
        for (int i = 1;i < args.length;i++) {
            report = report.concat(args[i] + " ");
        }
        report = report.trim();

        int num = 1;
        if (m.getReportsConfig().isConfigurationSection(args.toString()))
            num = m.getReportsConfig().getConfigurationSection(tgtUuid.toString()).getKeys(false).size() + 1;
        String path = tgtUuid.toString() + "." + num;

        m.getReportsConfig().set(path + ".date", formatter.format(new Date()));
        m.getReportsConfig().set(path + ".reporter", offlinePlayer.getUniqueId().toString());
        m.getReportsConfig().set(path + ".report", report);
        m.getReportsConfig().set(path + ".resolved", false);

        try {
            m.getReportsConfig().save(m.getReportsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
    
}
