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

package gg.blocky.melonstackedspawners.util;

import gg.blocky.melonstackedspawners.SpawnerPlugin;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public final class SpawnerUtil {

	public static String getAppropriateEntityName(EntityType entitytype) {
		return WordUtils.capitalizeFully(entitytype.name().replaceAll("[^A-Za-z0-9]", " "));
	}

	public static EntityType getSpawnerTypeFromItemStack(ItemStack is) {
		if (is == null || is.getType() != Material.SPAWNER) return null;
		if (is.getType() != Material.SPAWNER) return null;

		if (is.hasItemMeta() && is.getItemMeta() instanceof BlockStateMeta bsm) {
			if (bsm.hasBlockState() && bsm.getBlockState() instanceof CreatureSpawner bs) {
				return bs.getSpawnedType();
			}
		}

		return null;
	}

	public static void takeSpawnerFromInventory(Player player, int amount) {
		final ItemStack clonedItemStack = player.getInventory().getItemInMainHand().clone();

		final int newItemAmount = clonedItemStack.getAmount() - amount;
		if (newItemAmount >= 1) {
			clonedItemStack.setAmount(newItemAmount);
			player.getInventory().setItemInMainHand(clonedItemStack);
		} else player.getInventory().setItemInMainHand(null);

		player.updateInventory();
	}

	@SuppressWarnings("ConstantConditions")
	public static ItemStack createSpawnerItemStack(EntityType entityType, int itemAmount) {
//		final ItemStack itemStack = new ItemStack(Material.SPAWNER, itemAmount, entityType.getTypeId());
//		final ItemMeta itemMeta = itemStack.getItemMeta();
//
//		itemMeta.setDisplayName(HexUtil.colorify("&a◆ &c{type} Spawner &a◆"
//				.replace("{type}", SpawnerUtil.getAppropriateEntityName(entityType))


//		));

		final ItemStack spawnerItemStack = SpawnerPlugin.getInstance().getSilkUtil().newSpawnerItem(entityType.name(),
				HexUtil.colorify("&d◆ &f{type} Spawner &d◆".replace("{type}", SpawnerUtil.getAppropriateEntityName(entityType))), itemAmount, false);

//		final PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
//		persistentDataContainer.set(new NamespacedKey(SpawnerPlugin.getInstance(), "spawner_entity_type"),
//				PersistentDataType.STRING, entityType.name());

//		BlockState blockState = ((BlockStateMeta) itemMeta).getBlockState();
//		CreatureSpawner creatureSpawner = (CreatureSpawner) blockState;
//
//		creatureSpawner.setSpawnedType(entityType);
//		((BlockStateMeta) itemMeta).setBlockState(blockState);
//
//		itemStack.setItemMeta(itemMeta);
		return spawnerItemStack;
	}
}
