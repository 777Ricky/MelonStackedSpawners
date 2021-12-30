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

import gg.blocky.melonstackedspawners.database.SpawnerDatabase;
import gg.blocky.melonstackedspawners.module.event.SpawnerBreakEvent;
import gg.blocky.melonstackedspawners.module.event.SpawnerStackEvent;
import gg.blocky.melonstackedspawners.module.event.SpawnerUnstackEvent;
import gg.blocky.melonstackedspawners.util.HexUtil;
import gg.blocky.melonstackedspawners.util.SpawnerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.entity.StackEntity;

import java.util.concurrent.ForkJoinPool;

public record StackedSpawnerListener(SpawnerDatabase spawnerDatabase) implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		final Player player = event.getPlayer();

		final ItemStack itemInHand = event.getItemInHand();
		if (itemInHand.getType() != Material.SPAWNER) return;

		if (event.getHand() != EquipmentSlot.HAND) {
			player.sendMessage(HexUtil.colorify("&cSpawners are not allowed in off hand!"));
			event.setCancelled(true);
			return;
		}

		final EntityType spawnerType = SpawnerUtil.getSpawnerTypeFromItemStack(itemInHand);
		if (spawnerType == null) throw new IllegalStateException("This isn't suppose to happen....");

		final Block blockPlaced = event.getBlockPlaced();
		if (blockPlaced.getType() != Material.SPAWNER) return;

		final int itemAmount = itemInHand.getAmount();

		final int maxStackSize = 9999;
		final boolean isSneaking = player.isSneaking();

		final Block blockAgainst = event.getBlockAgainst();
		boolean existingSpawner = blockAgainst.getType() == Material.SPAWNER;
		if (existingSpawner) {
			StackedSpawnerRegistry.isStackedSpawner(blockAgainst, stackedSpawner -> {
				if (stackedSpawner.getEntityType() != spawnerType) {
					player.sendMessage(HexUtil.colorify("&cThis is not the same entity type, please use a "
							+ SpawnerUtil.getAppropriateEntityName(spawnerType) + " spawner instead!"));
					return;
				}

				event.setCancelled(true);

				final int previousStackAmount = stackedSpawner.getStackAmount();
				int newStackAmount = previousStackAmount + (isSneaking ? itemAmount : 1);

				if (newStackAmount > maxStackSize) newStackAmount = newStackAmount - maxStackSize;

				stackedSpawner.addStackAmount(isSneaking ? itemAmount : 1);
				stackedSpawner.updateHologram();
				ForkJoinPool.commonPool().execute(() -> spawnerDatabase.updateStackedSpawnerAmount(stackedSpawner));

				SpawnerUtil.takeSpawnerFromInventory(player, isSneaking ? itemAmount : 1);

				Bukkit.getServer().getPluginManager().callEvent(new SpawnerStackEvent(player, stackedSpawner,
						previousStackAmount, newStackAmount));
			}, unused -> {
				final Chunk chunk = blockAgainst.getLocation().getChunk();
				StackedSpawnerRegistry.searchStackedSpawnerInChunk(chunk, spawnerType).ifPresentOrElse(stackedSpawner -> {
					final Location location = stackedSpawner.getBlock().getLocation();
					player.sendMessage(HexUtil.colorify("&cYou are not allowed to place more than 1 spawner stack of the same entity type in the same chunk! &oThe stacked spawner is placed here X: " +
							location.getX() + ", Y: " + location.getY() + ", Z: " + location.getZ()));
					event.setCancelled(true);
				}, () -> {
					CreatureSpawner creatureSpawner = (CreatureSpawner) blockAgainst.getState();
					final EntityType entityType = creatureSpawner.getSpawnedType();
					if (spawnerType != entityType) {
						player.sendMessage(HexUtil.colorify("&cThat's not the same mob type, please use a " +
								SpawnerUtil.getAppropriateEntityName(spawnerType) + " spawner to stack this spawner!"));
						event.setCancelled(true);
						return;
					}

					final int finalAmount = 1 + (isSneaking ? itemAmount : 1);

					final StackedSpawner stackedSpawner = new StackedSpawner(blockAgainst, spawnerType, finalAmount);
					SpawnerUtil.takeSpawnerFromInventory(player, finalAmount);

					Bukkit.getServer().getPluginManager().callEvent(new SpawnerStackEvent(player, stackedSpawner, 0, finalAmount));

					StackedSpawnerRegistry.registerStackedSpawner(stackedSpawner);
					StackedSpawnerRegistry.loadStackedSpawnerInChunk(stackedSpawner);
					event.setCancelled(true);
				});
			});
		} else {
			final Chunk chunk = blockPlaced.getLocation().getChunk();
			StackedSpawnerRegistry.searchStackedSpawnerInChunk(chunk, spawnerType).ifPresentOrElse(stackedSpawner -> {
				final Location location = stackedSpawner.getBlock().getLocation();
				player.sendMessage(HexUtil.colorify("&cYou are not allowed to place more than 1 spawner stack of the same entity type in the same chunk! &oThe stacked spawner is placed here X: " +
						location.getX() + ", Y: " + location.getY() + ", Z: " + location.getZ()));
				event.setCancelled(true);
			}, () -> {
				int finalItemAmount = !isSneaking ? 1 : itemAmount;

				final StackedSpawner stackedSpawner = new StackedSpawner(blockPlaced, spawnerType, finalItemAmount);
				SpawnerUtil.takeSpawnerFromInventory(player, finalItemAmount);

				Bukkit.getServer().getPluginManager().callEvent(new SpawnerStackEvent(player, stackedSpawner, 1, finalItemAmount));

				StackedSpawnerRegistry.registerStackedSpawner(stackedSpawner);
				StackedSpawnerRegistry.loadStackedSpawnerInChunk(stackedSpawner);
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawnerSpawn(SpawnerSpawnEvent event) {
		final CreatureSpawner spawner = event.getSpawner();
		StackedSpawnerRegistry.isStackedSpawner(spawner.getBlock(),
				stackedSpawner -> {
					event.setCancelled(true);

					final Location location = stackedSpawner.getBlock().getLocation();
					final int stackAmount = stackedSpawner.getStackAmount();
					final EntityType entityType = stackedSpawner.getEntityType();

					final LivingEntity livingEntity = (LivingEntity) location.getWorld().spawnEntity(event.getLocation(), entityType);
					if (livingEntity instanceof Ageable ageable) ageable.setAdult();
					final StackEntity stackEntity = StackMob.getPlugin(StackMob.class).getEntityManager().registerStackedEntity(livingEntity);
					stackEntity.setSize(stackAmount);
				}, unused -> {
				});
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();
		if (event.isCancelled() || block.getType() != Material.SPAWNER) return;

		event.setExpToDrop(0);

		final Player player = event.getPlayer();
		StackedSpawnerRegistry.isStackedSpawner(block,
				stackedSpawner -> {
					event.setCancelled(true);
					if (player.isSneaking()) {
						this.handleSilktouch(player, block, player.getInventory().getItemInMainHand(), stackedSpawner.getStackAmount());
						Bukkit.getServer().getPluginManager().callEvent(new SpawnerBreakEvent(player, stackedSpawner));

						stackedSpawner.deleteHologram();
						block.setType(Material.AIR);

						StackedSpawnerRegistry.unregisterStackedSpawner(stackedSpawner);
						StackedSpawnerRegistry.unregisterStackedSpawnerInChunk(stackedSpawner);
					} else {
						this.handleSilktouch(player, block, player.getInventory().getItemInMainHand(), 1);
						if (stackedSpawner.getStackAmount() <= 1) {
							stackedSpawner.deleteHologram();
							block.setType(Material.AIR);

							StackedSpawnerRegistry.unregisterStackedSpawner(stackedSpawner);
							StackedSpawnerRegistry.unregisterStackedSpawnerInChunk(stackedSpawner);
						} else {
							Bukkit.getServer().getPluginManager().callEvent(new SpawnerUnstackEvent(player, stackedSpawner,
									stackedSpawner.getStackAmount(), stackedSpawner.getStackAmount() - 1));
							stackedSpawner.removeStackAmount(1);
							stackedSpawner.updateHologram();
							ForkJoinPool.commonPool().execute(() -> spawnerDatabase.updateStackedSpawnerAmount(stackedSpawner));
						}
					}
				},
				unused -> {
					this.handleSilktouch(player, block, player.getInventory().getItemInMainHand(), 1);
				});
	}

	private void handleSilktouch(Player player, Block block, ItemStack itemStack, int amount) {
		if (player.getGameMode() == GameMode.SURVIVAL && (!itemStack.containsEnchantment(Enchantment.SILK_TOUCH)
				|| !player.hasPermission("silkspawners.silkdrop.*"))) return;

		CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
		final EntityType spawnedType = creatureSpawner.getSpawnedType();
		final ItemStack spawnerItemStack = SpawnerUtil.createSpawnerItemStack(spawnedType, amount);

		final PlayerInventory inventory = player.getInventory();
		if (inventory.firstEmpty() != -1) inventory.addItem(spawnerItemStack);
		else block.getWorld().dropItemNaturally(block.getLocation(), spawnerItemStack);
	}
}
