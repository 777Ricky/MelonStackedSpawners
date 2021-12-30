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

package gg.blocky.melonstackedspawners.database.implementation;

import gg.blocky.melonstackedspawners.SpawnerPlugin;
import gg.blocky.melonstackedspawners.database.SpawnerDatabase;
import gg.blocky.melonstackedspawners.module.spawner.StackedSpawner;
import gg.blocky.melonstackedspawners.module.spawner.StackedSpawnerRegistry;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public final class SpawnerDatabaseSQLite extends SpawnerDatabase {

	public SpawnerDatabaseSQLite(SpawnerPlugin plugin) {
		super(plugin, "jdbc:sqlite:" + new File(plugin.getDataFolder(), "melonstackedspawners.db").getPath());
	}

	@Override
	public void loadStackedSpawners() {
		try {
			final ResultSet resultSet = executeQuery(Query.LOAD_SPAWNERS.getValue());
			while (resultSet.next()) {
				try {
					final StackedSpawner stackedSpawner = new StackedSpawner(
							resultSet.getString(1),
							resultSet.getInt(2),
							resultSet.getInt(3),
							resultSet.getInt(4),
							EntityType.valueOf(resultSet.getString(6)),
							resultSet.getInt(5)
					);

					StackedSpawnerRegistry.loadStackedSpawner(stackedSpawner);
				} catch (ClassCastException ignored) {
					plugin.getLogger().warning("Invalid spawner block is saved to the database");
				}
			}

			plugin.getLogger().info("Loaded " + StackedSpawnerRegistry.getStackedSpawnerCount() + " stacked spawners ("
					+ StackedSpawnerRegistry.getTotalSpawnerCount() + " individuals) in "
					+ StackedSpawnerRegistry.getChunksWithStackedSpawnersCount() + " chunks!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createStackedSpawner(StackedSpawner stackedSpawner) {
		ForkJoinPool.commonPool().execute(() -> execute(Query.CREATE_SPAWNER.getValue(),
				stackedSpawner.getWorld().getName(),
				stackedSpawner.getBlock().getLocation().getBlockX(),
				stackedSpawner.getBlock().getLocation().getBlockY(),
				stackedSpawner.getBlock().getLocation().getBlockZ(),
				stackedSpawner.getStackAmount(),
				stackedSpawner.getEntityType().name()
		));
	}

	@Override
	public void deleteStackedSpawner(StackedSpawner stackedSpawner) {
		ForkJoinPool.commonPool().execute(() -> execute(Query.DELETE_SPAWNER.getValue(),
				stackedSpawner.getWorld().getName(),
				stackedSpawner.getBlock().getLocation().getBlockX(),
				stackedSpawner.getBlock().getLocation().getBlockY(),
				stackedSpawner.getBlock().getLocation().getBlockZ()
		));
	}

	@Override
	public void updateStackedSpawnerAmount(StackedSpawner stackedSpawner) {
		execute(Query.UPDATE_SPAWNER.getValue(),
				stackedSpawner.getStackAmount(),
				stackedSpawner.getWorld().getName(),
				stackedSpawner.getBlock().getLocation().getBlockX(),
				stackedSpawner.getBlock().getLocation().getBlockY(),
				stackedSpawner.getBlock().getLocation().getBlockZ()
		);
	}
}
