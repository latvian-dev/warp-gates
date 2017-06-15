package com.latmod.warp_pads;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.util.LMUtils;

import java.io.File;

public class WarpPadsConfig // WarpPads
{
	private static final PropertyDouble LEVELS_PER_BLOCK = new PropertyDouble(0.001D, 0D, 5D);
	private static final PropertyInt LEVELS_FOR_CROSSDIM = new PropertyInt(30, 0, 200);

	public static void init(IFTBLibRegistry reg)
	{
		reg.addConfigFileProvider(WarpPads.MOD_ID, () -> new File(LMUtils.folderConfig, "WarpPads.json"));

		reg.addConfig(WarpPads.MOD_ID, "general.levels_per_block", LEVELS_PER_BLOCK);
		reg.addConfig(WarpPads.MOD_ID, "general.levels_for_crossdim", LEVELS_FOR_CROSSDIM);
	}

	public static int getEnergyRequired(double distance)
	{
		if (distance < 0D)
		{
			return LEVELS_FOR_CROSSDIM.getInt();
		}

		return (int) (distance * LEVELS_PER_BLOCK.getDouble());
	}
}