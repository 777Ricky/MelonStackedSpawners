/*
 * BLOCKY STUDIOS LLC - Cody Lynn
 * cody@blocky.gg
 *
 * [2019] - [2021] Blocky Studios LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Blocky Studios LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Blocky Studios LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Blocky Studios LLC.
 */

package gg.blocky.melonstackedspawners.module.spawner;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import gg.blocky.melonstackedspawners.SpawnerPlugin;
import gg.blocky.melonstackedspawners.util.HexUtil;
import gg.blocky.melonstackedspawners.util.SpawnerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.util.Objects;

@Getter
public final class StackedSpawner {

	private final Block block;
	private final EntityType entityType;

	private int stackAmount;

	public StackedSpawner(String worldName, int x, int y, int z, EntityType entityType, int stackAmount) {
		this.block = Objects.requireNonNull(Bukkit.getWorld(worldName)).getBlockAt(x, y, z);
		this.entityType = entityType;
		this.stackAmount = stackAmount;

		final CreatureSpawner creatureSpawner = this.getCreatureSpawner();
		if (creatureSpawner == null) {
			SpawnerPlugin.getInstance().getSpawnerDatabase().deleteStackedSpawner(this);
			SpawnerPlugin.getInstance().getLogger().info("Deleted stacked spawner because block wasn't a spawner anymore...");
			return;
		}

		this.refreshSpawnerBlock();
		this.updateHologram();
	}

	public StackedSpawner(Block block, EntityType entityType, int stackAmount) {
		this(block.getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ(), entityType, stackAmount);
	}

	public void updateHologram() {
		final String line = "&a❖ &c{type} Spawner &8| &7{amount}x &a❖".replace("{type}", SpawnerUtil.getAppropriateEntityName(this.entityType)).replace("{amount}", this.stackAmount + "");

		StackedSpawnerRegistry.updateHologram(this.getHologramLocation(), HexUtil.colorify(line));
	}

	public void deleteHologram() {
		StackedSpawnerRegistry.filterHologram(this.getHologramLocation(), Hologram::delete);
		StackedSpawnerRegistry.unregisterHologram(this.getHologramLocation());
	}

	public void addStackAmount(int stackAmount) {
		this.stackAmount += stackAmount;
	}

	public void removeStackAmount(int stackAmount) {
		this.stackAmount -= stackAmount;
	}

	public World getWorld() {
		return block.getWorld();
	}

	public void refreshSpawnerBlock() {
		if (this.block.getType() != Material.SPAWNER) block.setType(Material.SPAWNER);

		final CreatureSpawner creatureSpawner = this.getCreatureSpawner();
		if (creatureSpawner == null) return;

		creatureSpawner.setSpawnCount(1);
		creatureSpawner.setSpawnedType(entityType);
		creatureSpawner.update(true);
	}

	private Location getHologramLocation() {
		final Location location = block.getLocation();
		return new Location(getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 1.5, location.getBlockZ() + 0.5);
	}

	public CreatureSpawner getCreatureSpawner() {
		if (this.block.getState() instanceof CreatureSpawner creatureSpawner) {
			return creatureSpawner;
		}
		return null;
	}
}
