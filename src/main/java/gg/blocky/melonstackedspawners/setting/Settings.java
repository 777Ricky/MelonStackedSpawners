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

package gg.blocky.melonstackedspawners.setting;

import gg.blocky.melonstackedspawners.util.Config;

import java.io.File;

public class Settings extends Config {

	@Ignore
	public static final Settings IMP = new Settings();

	@Final
	public String VERSION = "1.0";

//	@Create
//	public TOOLS TOOLS;

	public void reload(File file) {
		if (this.load(file)) {
			this.save(file);
		} else {
			this.save(file);
			this.load(file);
		}
	}

	public static class MOB_NAMES {

	}

}
