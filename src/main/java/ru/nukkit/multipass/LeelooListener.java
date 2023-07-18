/*
    Multipass, Nukkit permissions plugin
    Copyright (C) 2016  fromgate, nukkit.ru

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.nukkit.multipass;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerAsyncPreLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import ru.nukkit.multipass.data.Providers;
import ru.nukkit.multipass.permissions.User;
import ru.nukkit.multipass.permissions.Users;

public class LeelooListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLogin(PlayerAsyncPreLoginEvent event) {
        try {
            User user = Users.loadUserSync(event.getName());
            if (!user.inGroup(MultipassPlugin.getCfg().defaultGroup)) {
                MultipassPlugin.getPlugin().getLogger().info("Adding missing default group for " + event.getName());
                user.addGroup(MultipassPlugin.getCfg().defaultGroup);
                Providers.saveUser(user);
            }
        } catch (Exception ex) {
            event.setKickMessage("Failed to load profile.");
            event.setLoginResult(PlayerAsyncPreLoginEvent.LoginResult.KICK);
            MultipassPlugin.getPlugin().getLogger().error("Failed to load profile for " + event.getName(), ex);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        Users.closeUser(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {
        if (!MultipassPlugin.getCfg().enableWorldSupport) return;
        if (event.getFrom().getLevel().equals(event.getTo().getLevel())) return;
        Users.recalculatePermissions(event.getPlayer().getName());
    }
}
