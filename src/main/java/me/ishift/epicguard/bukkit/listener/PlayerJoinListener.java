/*
 * EpicGuard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EpicGuard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.ishift.epicguard.bukkit.listener;

import me.ishift.epicguard.bukkit.GuardBukkit;
import me.ishift.epicguard.bukkit.user.User;
import me.ishift.epicguard.bukkit.user.UserManager;
import me.ishift.epicguard.bukkit.util.misc.Notificator;
import me.ishift.epicguard.bukkit.util.server.Updater;
import me.ishift.epicguard.common.Config;
import me.ishift.epicguard.common.Messages;
import me.ishift.epicguard.common.StorageManager;
import me.ishift.epicguard.common.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        try {
            final Player player = event.getPlayer();
            UserManager.addUser(player);
            final User user = UserManager.getUser(player);
            final String address = user.getAddress();

            // AntiBypass
            if (StorageManager.isBlacklisted(address)) {
                event.setJoinMessage("");
                player.kickPlayer(Messages.messageKickBlacklist.stream().map(s -> ChatUtil.fix(s) + "\n").collect(Collectors.joining()));
                return;
            }

            Updater.notify(player);

            if (Config.autoWhitelist) {
                Bukkit.getScheduler().runTaskLater(GuardBukkit.getInstance(), () -> {
                    if (player.isOnline()) StorageManager.whitelist(address);
                }, Config.autoWhitelistTime);
            }

            // IP History manager
            if (Config.ipHistoryEnable) {
                final List<String> history = StorageManager.getFile().getStringList("address-history." + player.getName());
                if (!history.contains(address)) {
                    if (!history.isEmpty()) {
                        Notificator.broadcast(Messages.historyNew.replace("{NICK}", player.getName()).replace("{IP}", address));
                    }
                    history.add(address);
                }
                StorageManager.getFile().set("address-history." + player.getName(), history);
                user.setAddressList(history);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
