package me.coralise.custombansplus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker extends Thread{

    private JavaPlugin plugin;

    static CustomBansPlus m = (CustomBansPlus) GetJavaPlugin.getPlugin();

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.start();
    }

    public void getVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=89075").openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    String ver = scanner.next();
                    consumer.accept(ver);
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    public static void checkUpdate(Player p) {
        if(m.getConfig().getBoolean("toggle-update-notifs") && m.update != null && p.isOp()) Bukkit.getScheduler().runTaskLater(m, () -> p.sendMessage(m.getUpdate()), 100);
    }

    @Override
    public void run() {
        while(true) {
            System.out.println("§e[CBP] §fChecking for updates...");
            getVersion(version -> {
                if (plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                    m.update = null;
                } else {
                    m.update = "§e[CBP] §fA new update is available!\n§fYour Version: §e" + plugin.getDescription().getVersion() + "\n§fNew Version: §a" + version + "\n§fYou can get it here:\n§ahttps://www.spigotmc.org/resources/custom-bans-plus-fully-customizable-ban-manager.89075/";
                    System.out.println(m.update);
                }
            });
            try {
                long millis = 0;
                String[] loValues = m.getConfig().getString("update-interval").split("(?<=[smhd])");
                for(String v : loValues){
                    long loValue = Long.parseLong(v.substring(0,v.length()-1));
                    switch(v.charAt(v.length()-1)){
                        case 's':
                            millis += loValue * 1000;
                            break;
                        case 'm':
                            millis += loValue * 60000;
                            break;
                        case 'h':
                            millis += loValue * 3600000;
                            break;
                        case 'd':
                            millis += loValue * 86400000;
                            break;
                    }
                }
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}