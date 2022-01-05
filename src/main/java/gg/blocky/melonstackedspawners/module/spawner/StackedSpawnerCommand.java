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
import gg.blocky.melonstackedspawners.SpawnerPlugin;
import gg.blocky.melonstackedspawners.util.HexUtil;
import gg.blocky.melonstackedspawners.util.SpawnerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

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

	@Subcommand("save|savespawners|saveall")
	@CommandPermission("melonstackedspawners.admin")
	public void onSave(CommandSender sender, boolean sync) {
		final Runnable runnable = () -> SpawnerPlugin.getInstance().getSpawnerDatabase().updateAllStackAmounts();

		if (sync) runnable.run();
		else ForkJoinPool.commonPool().execute(runnable);

		sender.sendMessage(HexUtil.colorify("<g:#FB8801:#FDE696>MelonStackedSpawners: All spawners have been saved to the database!"));
	}
}