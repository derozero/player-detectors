package com.github.commoble.entitydetectors.blocks;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.commoble.entitydetectors.items.ImprintedSlimeballItem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class MobDetectorTileEntity extends TileEntity
{
	public static final String FILTER_KEY = "filter";

	public ItemStack slimeStack = ItemStack.EMPTY;
	public final IItemHandler itemHandler;

	public MobDetectorTileEntity()
	{
		super(TileEntityRegistrar.MOB_DETECTOR);
		this.itemHandler = new MobDetectorItemHandler(this);
	}
	
	public boolean isEntityDetectable(Entity ent)
	{
		return ImprintedSlimeballItem.getEntityType(this.slimeStack).map(type -> type == ent.getType()).orElse(true);
	}
	
	public Optional<EntityType<?>> getFilteredEntityType()
	{
		return ImprintedSlimeballItem.getEntityType(this.slimeStack);
	}
	
	public ActionResultType onRightClickWithSlime(PlayerEntity player, ItemStack stack, World world, BlockPos pos)
	{
		this.dropSlime(world, pos);
		
		ItemStack remainder = this.itemHandler.insertItem(0, stack, false);
		
		stack.setCount(remainder.getCount());
		
		this.markDirty();
		world.notifyBlockUpdate(pos, this.getBlockState(), this.getBlockState(), 2);
		
		return ActionResultType.SUCCESS;
	}
	
	private void dropSlime(World worldIn, BlockPos pos)
	{
		if (!worldIn.isRemote)
		{
			ItemStack extractedStack = this.itemHandler.extractItem(0, 1, false);
			if (!extractedStack.isEmpty())
			{
				worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS,
					0.1F + this.world.rand.nextFloat()*0.3F,
					this.world.rand.nextFloat()*1.5F + 1F);
				float f = 0.7F;
				double d0 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
				double d1 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
				double d2 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
				ItemEntity itementity = new ItemEntity(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, extractedStack.copy());
				itementity.setDefaultPickupDelay();
				worldIn.addEntity(itementity);
			}
		}
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return LazyOptional.of(() -> this.itemHandler).cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void read(CompoundNBT compound)
	{
		super.read(compound);
		if (compound.contains(FILTER_KEY))
		{
			this.slimeStack = ItemStack.read(compound.getCompound(FILTER_KEY));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound)
	{
		CompoundNBT nbt = super.write(compound);
		CompoundNBT itemNBT = this.slimeStack.write(new CompoundNBT());
		nbt.put(FILTER_KEY, itemNBT);
		return nbt;
	}

	/**
	 * Get an NBT compound to sync to the client with SPacketChunkData, used for
	 * initial loading of the chunk or when many blocks change at once. This
	 * compound comes back to you clientside in {@link handleUpdateTag}
	 */
	@Override
	public CompoundNBT getUpdateTag()
	{
		return this.write(new CompoundNBT());
	}

	/**
	 * Retrieves packet to send to the client whenever this Tile Entity is resynced
	 * via World.notifyBlockUpdate. For modded TE's, this packet comes back to you
	 * clientside in {@link #onDataPacket}
	 */
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(this.getPos(), 1, this.write(new CompoundNBT()));
	}

	/**
	 * Called when you receive a TileEntityData packet for the location this
	 * TileEntity is currently in. On the client, the NetworkManager will always be
	 * the remote server. On the server, it will be whomever is responsible for
	 * sending the packet.
	 *
	 * @param net
	 *            The NetworkManager the packet originated from
	 * @param pkt
	 *            The data packet
	 */
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		this.read(pkt.getNbtCompound());
	}
}
