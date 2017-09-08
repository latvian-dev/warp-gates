package com.latmod.warp_pads.block;

import com.feed_the_beast.ftbl.lib.EnumPrivacyLevel;
import com.feed_the_beast.ftbl.lib.tile.EnumSaveType;
import com.feed_the_beast.ftbl.lib.tile.TileBase;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.latmod.warp_pads.WarpPadsConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class TilePortal extends TileBase implements ITickable
{
	public EnumFacing facing;
	private UUID owner;
	private UUID uuid;
	public boolean active = true;
	private String name = "";
	private EnumPrivacyLevel privacyLevel = EnumPrivacyLevel.TEAM;
	public int tick;

	public TilePortal()
	{
		facing = EnumFacing.DOWN;
	}

	public TilePortal(EnumFacing f)
	{
		facing = f;
	}

	public UUID getUUID()
	{
		if (uuid == null)
		{
			uuid = UUID.randomUUID();
		}

		return uuid;
	}

	@Override
	protected void writeData(NBTTagCompound nbt, EnumSaveType type)
	{
		if (type.save)
		{
			if (owner != null)
			{
				nbt.setString("Owner", StringUtils.fromUUID(owner));
			}

			EnumPrivacyLevel.NAME_MAP.writeToNBT(nbt, "Privacy", type, privacyLevel);
		}

		nbt.setString("Name", name);

		if (type.save || active)
		{
			nbt.setBoolean("Active", active);
		}

		nbt.setString("Facing", facing.getName());
		nbt.setUniqueId("UUID", getUUID());
	}

	@Override
	protected void readData(NBTTagCompound nbt, EnumSaveType type)
	{
		if (type.save)
		{
			owner = nbt.hasKey("Owner") ? StringUtils.fromString(nbt.getString("Owner")) : null;
			privacyLevel = EnumPrivacyLevel.NAME_MAP.readFromNBT(nbt, "Privacy", type);
		}

		name = nbt.getString("Name");
		active = nbt.getBoolean("Active");
		facing = EnumFacing.byName(nbt.getString("Facing"));
		uuid = nbt.getUniqueId("UUID");

		if (uuid != null && uuid.getLeastSignificantBits() == 0L && uuid.getMostSignificantBits() == 0L)
		{
			uuid = null;
		}
	}

	@Override
	public String getName()
	{
		return name.isEmpty() ? "Unnamed" : name;
	}

	@Override
	public boolean hasCustomName()
	{
		return !name.isEmpty();
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TextComponentString(getName());
	}

	public void setName(String n)
	{
		name = n;
		markDirty();
	}

	@Nullable
	public UUID getOwner()
	{
		return owner;
	}

	public void setOwner(UUID id)
	{
		owner = id;
	}

	public boolean isOwner(UUID id)
	{
		return owner == null || owner.equals(id);
	}

	public EnumPrivacyLevel getPrivacyLevel()
	{
		return privacyLevel;
	}

	public void togglePrivacyLevel()
	{
		privacyLevel = EnumPrivacyLevel.VALUES[privacyLevel.ordinal() % EnumPrivacyLevel.VALUES.length];
		markDirty();
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		WarpPadsNet.add(this);
	}

	@Override
	public void invalidate()
	{
		WarpPadsNet.remove(this);
		super.invalidate();
	}

	@Override
	public void update()
	{
		if (!world.isRemote && tick % 20 == 19 && !BlockPortal.canExist(facing, world, pos))
		{
			world.setBlockToAir(pos);
		}

		checkIfDirty();
		tick++;
	}

	public int getEnergyRequired(TilePortal teleporter)
	{
		if (teleporter.world.provider.getDimension() == world.provider.getDimension())
		{
			return WarpPadsConfig.getEnergyRequired(Math.sqrt(teleporter.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D)));
		}

		return WarpPadsConfig.getEnergyRequired(-1D);
	}

	public boolean consumeEnergy(EntityPlayer ep, int levels, boolean simulate)
	{
		if (levels <= 0 || ep.capabilities.isCreativeMode || ep.experienceLevel >= levels)
		{
			if (!simulate && levels > 0)
			{
				ep.addExperienceLevel(-levels);
			}

			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return new AxisAlignedBB(pos.getX() - 2D, pos.getY() - 1D, pos.getZ() - 2D, pos.getX() + 3D, pos.getY() + 50D, pos.getZ() + 3D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 10000D;
	}
}