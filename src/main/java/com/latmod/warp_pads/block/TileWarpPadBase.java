package com.latmod.warp_pads.block;

import com.feed_the_beast.ftbl.lib.block.EnumHorizontalOffset;
import com.feed_the_beast.ftbl.lib.tile.TileBase;
import com.latmod.warp_pads.item.WarpPadsItems;

/**
 * Created by LatvianModder on 20.02.2017.
 */
public abstract class TileWarpPadBase extends TileBase
{
	public boolean checkUpdates = true;

	public abstract EnumHorizontalOffset getPart();

	public void onNeighborChange()
	{
		if (checkUpdates && !WarpPadsItems.WARP_PAD.canExist(world, pos))
		{
			world.setBlockToAir(pos);
		}
	}
}