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

package gg.blocky.melonstackedspawners.database;

import com.zaxxer.hikari.HikariDataSource;
import gg.blocky.melonstackedspawners.SpawnerPlugin;
import gg.blocky.melonstackedspawners.module.spawner.StackedSpawner;
import gg.blocky.melonstackedspawners.module.spawner.StackedSpawnerRegistry;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SpawnerDatabase {

	private static final String TABLE_NAME = "melonstackedspawners";

	protected final SpawnerPlugin plugin;
	protected final HikariDataSource hikari;

	public SpawnerDatabase(SpawnerPlugin plugin, String url) {
		this.plugin = plugin;
		this.hikari = new HikariDataSource();

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		hikari.setJdbcUrl(url);
		hikari.setMaximumPoolSize(20);
		hikari.addDataSourceProperty("characterEncoding", "utf8");
		hikari.addDataSourceProperty("useUnicode", "true");
		hikari.validate();

		execute(Query.CREATE_TABLE.value);
		plugin.getLogger().info("Successfully created database connection!");
	}

	public void disable() {
		// Save all cached stacked spawners
		StackedSpawnerRegistry.cacheStackedSpawnerLoop(this::updateStackedSpawnerAmount);

		// Close the connection
		hikari.close();
		plugin.getLogger().info("Closed database connection!");
	}

	protected void execute(String query, Object... parameters) {
		try (Connection connection = hikari
				.getConnection(); PreparedStatement statement = connection
				.prepareStatement(query)) {

			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					statement.setObject(i + 1, parameters[i]);
				}
			}

			statement.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	protected ResultSet executeQuery(String query, Object... parameters) {
		try (Connection connection = hikari.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					statement.setObject(i + 1, parameters[i]);
				}
			}

			CachedRowSet resultCached = RowSetProvider.newFactory().createCachedRowSet();
			ResultSet resultSet = statement.executeQuery();

			resultCached.populate(resultSet);
			resultSet.close();

			return resultCached;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public abstract void loadStackedSpawners();

	public abstract void createStackedSpawner(StackedSpawner stackedSpawner);

	public abstract void deleteStackedSpawner(StackedSpawner stackedSpawner);

	public abstract void updateStackedSpawnerAmount(StackedSpawner stackedSpawner);

	public abstract void updateAllStackAmounts();

	public enum Query {
		CREATE_TABLE("CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (" +
				"world VARCHAR(36), " +
				"x INT(6), " +
				"y INT(6), " +
				"z INT(6), " +
				"stack_amount INT(6), " +
				"entity_type VARCHAR(36) " +
				");"),

		LOAD_SPAWNERS("SELECT * FROM `" + TABLE_NAME + "`"),
		CREATE_SPAWNER("INSERT INTO `" + TABLE_NAME + "` (world, x, y, z, stack_amount, entity_type) VALUES (?, ?, ?, ?, ?, ?);"),
		UPDATE_SPAWNER("UPDATE `" + TABLE_NAME + "` SET stack_amount=? WHERE world=? AND x=? AND y=? AND z=?;"),
		DELETE_SPAWNER("DELETE FROM `" + TABLE_NAME + "` WHERE world=? AND x=? AND y=? AND z=?;");

		private final String value;

		Query(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
