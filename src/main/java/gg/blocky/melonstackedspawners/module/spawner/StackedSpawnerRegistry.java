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
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import gg.blocky.melonstackedspawners.SpawnerPlugin;
import gg.blocky.melonstackedspawners.module.chunk.ChunkPair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class StackedSpawnerRegistry {

	private static final SpawnerPlugin plugin = SpawnerPlugin.getInstance();

	private static final Map<Location, StackedSpawner> stackedSpawners = new ConcurrentHashMap<>();
	private static final Map<ChunkPair, StackedSpawner> chunkCache = new ConcurrentHashMap<>();

	private static final Map<Location, Hologram> hologramCache = new ConcurrentHashMap<>();

	public static void registerStackedSpawner(StackedSpawner stackedSpawner) {
		loadStackedSpawner(stackedSpawner);
		plugin.getSpawnerDatabase().createStackedSpawner(stackedSpawner);
	}

	public static void loadStackedSpawner(StackedSpawner stackedSpawner) {
		stackedSpawners.put(stackedSpawner.getBlock().getLocation(), stackedSpawner);
		loadStackedSpawnerInChunk(stackedSpawner);
	}

	public static void unregisterStackedSpawner(StackedSpawner stackedSpawner) {
		stackedSpawners.remove(stackedSpawner.getBlock().getLocation());
		plugin.getSpawnerDatabase().deleteStackedSpawner(stackedSpawner);
	}

	public static Optional<StackedSpawner> getStackedSpawner(Block block) {
		return Optional.ofNullable(stackedSpawners.get(block.getLocation()));
	}

	public static void isStackedSpawner(Block block, Consumer<StackedSpawner> acceptedConsumer, Consumer<Void> deniedConsumer) {
		final Optional<StackedSpawner> stackedSpawner = getStackedSpawner(block);
		stackedSpawner.ifPresentOrElse(acceptedConsumer, () -> deniedConsumer.accept(null));
	}

	public static boolean isStackedSpawner(Block block) {
		return getStackedSpawner(block).isPresent();
	}

	public static void cacheStackedSpawnerLoop(Consumer<StackedSpawner> consumer) {
		stackedSpawners.values().forEach(consumer);
	}

	public static Optional<StackedSpawner> searchStackedSpawnerInChunk(Chunk chunk, EntityType entityType) {
		final ChunkPair chunkPair = new ChunkPair(chunk, entityType);
		return Optional.ofNullable(chunkCache.get(chunkPair));
	}

	public static StackedSpawner getStackedSpawnerInChunk(ChunkPair chunkPair) {
		return chunkCache.get(chunkPair);
	}

	public static void loadStackedSpawnerInChunk(StackedSpawner stackedSpawner) {
		chunkCache.put(new ChunkPair(stackedSpawner.getBlock().getChunk(), stackedSpawner.getEntityType()), stackedSpawner);
	}

	public static void unregisterStackedSpawnerInChunk(StackedSpawner stackedSpawner) {
		chunkCache.remove(new ChunkPair(stackedSpawner.getBlock().getChunk(), stackedSpawner.getEntityType()));
	}

	public static void registerHologram(Location location, Hologram hologram) {
		hologramCache.put(location, hologram);
	}

	public static void unregisterHologram(Location location) {
		hologramCache.remove(location);
	}

	public static void updateHologram(Location location, String line) {
		Hologram hologram = hologramCache.get(location);
		if (hologram == null) {
			hologram = HologramsAPI.createHologram(SpawnerPlugin.getInstance(), location);
			hologram.appendTextLine(line);
			registerHologram(location, hologram);
		} else {
			((TextLine) hologram.getLine(0)).setText(line);
		}
	}

	public static void filterHologram(Location location, Consumer<Hologram> consumer) {
		final Hologram hologram = hologramCache.get(location);
		if (hologram != null) consumer.accept(hologram);
	}

	public static int getTotalSpawnerCount() {
		return stackedSpawners.values().stream().mapToInt(StackedSpawner::getStackAmount).sum();
	}

	public static int getStackedSpawnerCount() {
		return stackedSpawners.values().size();
	}

	public static int getChunksWithStackedSpawnersCount() {
		return chunkCache.size();
	}
}
