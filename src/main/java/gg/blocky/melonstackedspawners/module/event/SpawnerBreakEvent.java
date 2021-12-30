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

package gg.blocky.melonstackedspawners.module.event;

import gg.blocky.melonstackedspawners.module.spawner.StackedSpawner;
import lombok.Getter;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class SpawnerBreakEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final StackedSpawner stackedSpawner;

	public SpawnerBreakEvent(Player player, StackedSpawner stackedSpawner) {
		this.player = player;
		this.stackedSpawner = stackedSpawner;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
