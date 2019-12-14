package pl.polskistevek.guard.utils;

import com.maxmind.geoip2.DatabaseReader;
import pl.polskistevek.guard.bukkit.GuardPluginBukkit;
import pl.polskistevek.guard.bungee.GuardPluginBungee;

import java.io.File;
import java.io.IOException;

public class GeoAPI {
    public static DatabaseReader dbReader;
    public static boolean spigot = false;

    public static void registerDatabase(ServerType type) throws IOException {
        File dataFolder = null;
        String dbLocation;
        if (type == ServerType.SPIGOT) {
            dataFolder = GuardPluginBukkit.getPlugin(GuardPluginBukkit.class).getDataFolder();
        }
        if (type == ServerType.BUNGEE) {
            dataFolder = GuardPluginBungee.plugin.getDataFolder();
        }
        dbLocation = dataFolder + "/GeoLite2-Country.mmdb";
        if (!new File(dbLocation).exists()) {
            if (spigot) {
                Logger.info("GeoIP Database not found! Starting download...", false);
            }
            //I need to download it from external site (my cloud) because official site has only tar.zip packed version (plugin don't need to extract it)
            Downloader.download(Mirrors.MIRROR_GEO, dbLocation);
        }
        File database;
        database = new File(dbLocation);
        dbReader = new DatabaseReader.Builder(database).build();
        Logger.info("GeoIP Database succesfully loaded.", false);
    }
}