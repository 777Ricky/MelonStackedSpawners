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

package gg.blocky.melonstackedspawners;

import co.aikar.commands.PaperCommandManager;
import de.dustplanet.util.SilkUtil;
import gg.blocky.melonstackedspawners.database.SpawnerDatabase;
import gg.blocky.melonstackedspawners.database.implementation.SpawnerDatabaseSQLite;
import gg.blocky.melonstackedspawners.module.spawner.StackedSpawnerCommand;
import gg.blocky.melonstackedspawners.module.spawner.StackedSpawnerListener;
import gg.blocky.melonstackedspawners.module.spawner.StackedSpawnerRegistry;
import gg.blocky.melonstackedspawners.setting.Settings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Getter
public final class SpawnerPlugin extends JavaPlugin {

	private static SpawnerPlugin instance = null;

	private SilkUtil silkUtil;
	private SpawnerDatabase spawnerDatabase;
	private PaperCommandManager commandManager;

	private BukkitTask spawnerSaveTask;

	public SpawnerPlugin() {
		if (instance != null) throw new IllegalStateException("Only one instance can run at the time");
		instance = this;
	}

	public static SpawnerPlugin getInstance() {
		if (instance == null) throw new IllegalStateException("Cannot access instance; instance might be null");
		return instance;
	}

	@Override
	public void onEnable() {
		Settings.IMP.reload(new File(getDataFolder(), "config.yml"));
		this.createBackupOfCurrentDatabase();

		this.silkUtil = SilkUtil.hookIntoSilkSpanwers();

		this.spawnerDatabase = new SpawnerDatabaseSQLite(this);
		this.spawnerDatabase.loadStackedSpawners();

		this.commandManager = new PaperCommandManager(this);
		this.commandManager.registerCommand(new StackedSpawnerCommand());

		this.getServer().getPluginManager().registerEvents(new StackedSpawnerListener(spawnerDatabase), this);

		int fiveMinutesInSeconds = (int) TimeUnit.MINUTES.toSeconds(5);
		this.spawnerSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			StackedSpawnerRegistry.cacheStackedSpawnerLoop(spawnerDatabase::updateStackedSpawnerAmount);
			this.getLogger().info("Saved " + StackedSpawnerRegistry.getStackedSpawnerCount() + " stacked spawners ("
					+ StackedSpawnerRegistry.getTotalSpawnerCount() + " individuals) in "
					+ StackedSpawnerRegistry.getChunksWithStackedSpawnersCount() + " chunks!");
		}, fiveMinutesInSeconds * 20L, fiveMinutesInSeconds * 20L);
	}

	@Override
	public void onDisable() {
		this.spawnerDatabase.disable();
	}

	private void createBackupOfCurrentDatabase() {
		final File dataFolder = this.getDataFolder();
		final File file = new File(dataFolder, "melonstackedspawners.db");
		if (file.exists()) {
			try {
				final File backupDir = new File(dataFolder, "backups");
				if (!backupDir.exists()) backupDir.mkdirs();

				final File backupFile = new File(backupDir, "melonstackedspawners-backup-" + System.currentTimeMillis() + ".db");
				if (!backupFile.exists()) backupFile.createNewFile();

				FileUtils.copyFile(file, backupFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
