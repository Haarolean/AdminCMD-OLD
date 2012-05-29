/************************************************************************
 * This file is part of AdminCmd.									
 *																		
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * AdminCmd is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Listeners.Features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACNoDropListener implements Listener {
	private final Map<UUID, List<ItemStack>> itemsDrops = new HashMap<UUID, List<ItemStack>>();
	private final Map<String, List<ItemStack>> itemsOfDeadDisconnected = new HashMap<String, List<ItemStack>>();

	@EventHandler(ignoreCancelled = true)
	public void onDrop(final PlayerDropItemEvent event) {
		if (ACPlayer.getPlayer(event.getPlayer()).hasPower(Type.NO_DROP)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDeath(final EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		final Player p = (Player) event.getEntity();
		final ACPlayer player = ACPlayer.getPlayer(p);
		if (!player.hasPower(Type.NO_DROP)
				&& !PermissionManager.hasPerm(p, "admincmd.spec.noloss", false)) {
			return;
		}
		final List<ItemStack> items = new ArrayList<ItemStack>();
		for (final ItemStack item : event.getDrops()) {
			items.add(item.clone());
			item.setAmount(0);
		}
		itemsDrops.put(p.getUniqueId(), items);
	}

	@EventHandler
	public void onRespawn(final PlayerRespawnEvent event) {
		final Player p = event.getPlayer();
		final ACPlayer player = ACPlayer.getPlayer(p);
		if (!player.hasPower(Type.NO_DROP)
				&& !PermissionManager.hasPerm(p, "admincmd.spec.noloss", false)) {
			itemsDrops.remove(p.getUniqueId());
			return;
		}
		final List<ItemStack> items = itemsDrops.get(p);
		if (items == null) {
			return;
		}
		p.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
		itemsDrops.remove(p.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final List<ItemStack> itemStacks = itemsDrops.remove(player.getUniqueId());
		if (itemStacks != null && player.isDead()) {
			itemsOfDeadDisconnected.put(player.getName(), itemStacks);
		}
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		final ACPlayer player = ACPlayer.getPlayer(p);
		final String name = p.getName();
		if (!player.hasPower(Type.NO_DROP)
				&& !PermissionManager.hasPerm(p, "admincmd.spec.noloss", false)) {
			itemsOfDeadDisconnected.remove(name);
			return;
		}
		final List<ItemStack> itemStacks = itemsOfDeadDisconnected.get(name);
		if (itemStacks == null) {
			return;
		}
		p.getInventory().addItem(itemStacks.toArray(new ItemStack[itemStacks.size()]));
		itemsOfDeadDisconnected.remove(name);
	}
}
