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

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import gg.blocky.melonstackedspawners.util.HexUtil;
import gg.blocky.melonstackedspawners.util.SpawnerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

@CommandAlias("melonstackedspawners|stackedspawners|stackedspawner|mss|mspawners|mstackedspawners")
public final class StackedSpawnerCommand extends BaseCommand {

	@Default
	@CatchUnknown
	public void onCommand(CommandSender sender) {
		sender.sendMessage(HexUtil.colorify("<g:#FB8801:#FDE696>MelonStackedSpawners: Product by &lBlocky Studios, LLC"));
		if (sender.hasPermission("melonstackedspawners.admin")) {
			sender.sendMessage(HexUtil.colorify("<g:#FB8801:#FDE696>- /" + this.getExecCommandLabel() + " give <player> <type> <amount>"));
		}
	}

	@Subcommand("give|givespawner")
	@CommandPermission("melonstackedspawners.admin")
	public void onGive(CommandSender sender, OnlinePlayer target, EntityType entityType, @Default("1") int amount) {
		final Player player = target.getPlayer();

		final PlayerInventory inventory = player.getInventory();
		if (inventory.firstEmpty() == -1) {
			sender.sendMessage("<g:#FB8801:#FDE696> MelonStackedSpawners: Their inventory is full, cannot give the spawners!");
			return;
		}

		final ItemStack itemStack = SpawnerUtil.createSpawnerItemStack(entityType, amount);
		inventory.addItem(itemStack);

		sender.sendMessage(HexUtil.colorify("<g:#FB8801:#FDE696>MelonStackedSpawners: Given " + amount + "x " + Objects.requireNonNull(entityType.getEntityClass()).getSimpleName() + " Spawner to " + player.getName()));
	}

	@Subcommand("stats|info")
	@CommandPermission("melonstackedspawners.admin")
	public void onStats(CommandSender sender) {
		final int stackedSpawnerCount = StackedSpawnerRegistry.getStackedSpawnerCount();
		final int chunksWithStackedSpawnersCount = StackedSpawnerRegistry.getChunksWithStackedSpawnersCount();
		final int totalSpawnerCount = StackedSpawnerRegistry.getTotalSpawnerCount();

		sender.sendMessage(HexUtil.colorify("<g:#FB8801:#FDE696>MelonStackedSpawners: Internal Statistics"));
		sender.sendMessage(HexUtil.colorify("<g:#FB8801:#FDE696> - Spawners: " + stackedSpawnerCount + " (" + totalSpawnerCount + " individuals)"));
		sender.sendMessage(HexUtil.colorify("<g:#FB8801:#FDE696> - Chunks: " + chunksWithStackedSpawnersCount));
	}
//	@Subcommand("convert")
//	@Syntax("<worldName> <password>")
//	@CommandPermission("melonstackedspawners.convert")
//	public void onConvert(Player player, String worldName, int one, int two, String password) {
//		if (!password.equals("8913467026")) {
//			player.sendMessage("§cIncorrect password, please try again!");
//			return;
//		}
//
//		final World world = Bukkit.getWorld(worldName);
//		if (world == null) {
//			player.sendMessage("§cInvalid world, try again noob...");
//			return;
//		}
//
//		final Map<ChunkPair, StackedSpawner> chunkSpawnerMap = new ConcurrentHashMap<>();
//
//		int firstX = one / 16;
//		int firstZ = two / 16;
//
//		for (int x = (-1 * firstX); x < firstX; x++) {
//			for (int z = (-1 * firstZ); z < firstZ; z++) {
//
//				final Chunk chunk = world.getChunkAt(x, z);
//				if (!chunk.isLoaded()) chunk.load();
//
//				for (int i = 0; i < 16; i++) {
//					for (int j = 0; j < 16; j++) {
//						for (int k = 0; k < 256; k++) {
//
//							final Block block = chunk.getBlock(i, k, j);
//							if (block.getType() == Material.SPAWNER && block.getState() instanceof CreatureSpawner creatureSpawner) {
//
//								final EntityType entityType = creatureSpawner.getSpawnedType();
//								final ChunkPair chunkPair = new ChunkPair(chunk, entityType);
//
//								// If there already was a lonely spawner, use that one
//								if (chunkSpawnerMap.containsKey(chunkPair)) {
//									final StackedSpawner stackedSpawner = chunkSpawnerMap.get(chunkPair);
//
//									stackedSpawner.addStackAmount(1);
//									block.setType(Material.AIR);
//
//								} else {
//									// If the lonely spawner is already stacked, use that one
//									if (StackedSpawnerRegistry.searchStackedSpawnerInChunk(chunk, entityType).isPresent()) {
//										final StackedSpawner stackedSpawner = StackedSpawnerRegistry.getStackedSpawnerInChunk(chunkPair);
//										chunkSpawnerMap.put(chunkPair, stackedSpawner);
//
//										// otherwise, convert that lonely spawner into a stacked spawner
//									} else {
//										final StackedSpawner stackedSpawner = new StackedSpawner(block, entityType, 1);
//										chunkSpawnerMap.put(chunkPair, stackedSpawner);
//
//										StackedSpawnerRegistry.registerStackedSpawner(stackedSpawner);
//										StackedSpawnerRegistry.loadStackedSpawnerInChunk(stackedSpawner);
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//
//		final Collection<StackedSpawner> stackedSpawners = chunkSpawnerMap.values();
//		stackedSpawners.forEach(stackedSpawner -> {
//			stackedSpawner.updateHologram();
//
//			ForkJoinPool.commonPool().execute(() -> {
//				SpawnerPlugin.getInstance().getSpawnerDatabase().updateStackedSpawnerAmount(stackedSpawner);
//			});
//		});
//
//		player.sendMessage("§aConverted " + chunkSpawnerMap.values().stream()
//				.mapToInt(StackedSpawner::getStackAmount).sum() + " spawners into " + chunkSpawnerMap.values().size()
//				+ " stacked spawners!");
//	}
}