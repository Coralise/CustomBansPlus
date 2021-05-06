package me.coralise.custombansplus.yaml;
import me.coralise.custombansplus.*;
import me.coralise.custombansplus.enums.MuteType;
import me.coralise.custombansplus.yaml.objects.YamlMuted;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YamlMuteCommand extends YamlAbstractCommand {

    YamlMuteCommand() {
        super("cbpmute", "custombansplus.mute", true);
    }

    CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!isValid(sender)){
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("§e/mute [-s] <player> <duration> <reason> - Mutes specified player.");
            return false;
        }

        int s = 0;
        if (args.length > 0 && args[0].equalsIgnoreCase("-s"))
            s = 1;

        String target = YamlCache.getPlayerIgn(args[0+s]);

        if(target == null){
            sender.sendMessage("§cPlayer " + args[0+s] + " has never played in the server.");
            return false;
        }

        UUID tgtUuid = m.getUuid(target);

        if (m.getType(args[1+s]) == null) {
            sender.sendMessage("§cPlease enter a valid option.");
            return false;
        }

        String value = args[1+s];
        String duration = m.getSevDuration(value);
        int fs = s;

        new Thread(() -> {

            String reason = "";
            if(args.length > 2+fs){
                for(int i = 2+fs; i < args.length; i++){
                    reason = reason.concat(args[i] + " ");
                }
                reason = reason.trim();
            }
            if (!m.getConfig().getBoolean("toggle-no-reason") && reason.isEmpty())
                reason = m.parseMessage(m.getConfig().getString("defaults.mute-reason"));

            String annType;
            if (reason.isEmpty())
                annType = "muteNoRsn";
            else
                annType = "mute";

            MuteType muteType = m.getMuteType(value);

            if (sender instanceof Player)
                YamlCache.setMute(tgtUuid, muteType, reason, duration, m.getUuid(sender).toString());
            else
                YamlCache.setMute(tgtUuid, muteType, reason, duration);

            YamlMuted ym = YamlCache.getMutedObject(tgtUuid);

            m.getMutesConfig().set(tgtUuid + ".type", ym.getMuteType().toString());
            m.getMutesConfig().set(tgtUuid + ".duration", ym.getDuration());
            m.getMutesConfig().set(tgtUuid + ".reason", ym.getReason());
            m.getMutesConfig().set(tgtUuid + ".muted-by", ym.getMuterUuid());
            m.getMutesConfig().set(tgtUuid + ".muted-on", ym.getMuteDateString());
            m.getMutesConfig().set(tgtUuid + ".unmute-by", ym.getUnmuteDateString());

            try {
                m.getMutesConfig().save(m.getMutesFile());
            } catch (IOException e) {
                //none
            }

            YamlAbstractBanCommand.addHistory(tgtUuid, sender.getName(), "mute", reason, null);
            if (fs == 0) YamlAbstractAnnouncer.getAnnouncer(target, sender.getName(), args[1+fs], reason, annType);
            else YamlAbstractAnnouncer.getSilentAnnouncer(target, sender.getName(), args[1+fs], reason, annType);

        }).start();

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> tabComplete = new ArrayList<String>();

        if(args.length == 1){
            if(!args[0].contains("-") && args[0].length() != 0) return null;
            tabComplete.add("-s");
            Bukkit.getOnlinePlayers().forEach(p -> {tabComplete.add(p.getName());});
            return tabComplete;
        }
            
        int stab = 0;
        if(args[0].equalsIgnoreCase("-s"))
            stab++;

        if(args.length == 2+stab){
            
            tabComplete.add("perm");
            tabComplete.add("Xs");
            tabComplete.add("Xm");
            tabComplete.add("Xh");
            tabComplete.add("Xd");
            return tabComplete;

        }

        if(args.length == 3+stab){

            tabComplete.add("<reason>");
            return tabComplete;

        }

        return null;
    }
    
}
