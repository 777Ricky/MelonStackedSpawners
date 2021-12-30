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

package gg.blocky.melonstackedspawners.module.chunk;

import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;

import java.util.Objects;

public record ChunkPair(Chunk chunk, EntityType entityType) {

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ChunkPair chunkPair = (ChunkPair) o;
		return Objects.equals(chunk.getX(), chunkPair.chunk.getX())
				&& Objects.equals(chunk.getZ(), chunkPair.chunk.getZ())
				&& Objects.equals(chunk.getWorld().getUID(), chunkPair.chunk.getWorld().getUID())
				&& entityType == chunkPair.entityType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID(), entityType);
	}
}
