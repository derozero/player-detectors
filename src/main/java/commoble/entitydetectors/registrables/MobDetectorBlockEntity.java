package commoble.entitydetectors.registrables;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import commoble.entitydetectors.EntityDetectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class MobDetectorBlockEntity extends EntityDetectorBlockEntity<Mob>
{
	public static final String FILTER_KEY = "filter";
	
	public static final BlockEntityTicker<MobDetectorBlockEntity> TICKER = (level,pos,state,be) -> be.tick(level, pos, state);

	private ItemStack slimeStack = ItemStack.EMPTY;
	
	private final IItemHandler itemHandler = new MobDetectorItemHandler(this);
	private final LazyOptional<IItemHandler> capHolder = LazyOptional.of(() -> this.itemHandler);
	
	public static MobDetectorBlockEntity create(BlockPos pos, BlockState state)
	{
		return new MobDetectorBlockEntity(EntityDetectors.MOB_DETECTOR_BET.get(), pos, state, Mob.class);
	}

	public MobDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Class<? extends Mob> mobClass)
	{
		super(type, pos, state, mobClass);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		this.capHolder.invalidate();
	}

	public ItemStack getSlimeStack()
	{
		return this.slimeStack;
	}
	
	public void setSlimeStack(ItemStack slimeStack)
	{
		this.slimeStack = slimeStack;
		this.setChanged();
		this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
	}
	
	public boolean isEntityDetectable(Mob ent)
	{
		return ImprintedSlimeballItem.getEntityType(this.slimeStack).map(type -> type == ent.getType()).orElse(true);
	}
	
	public Optional<EntityType<?>> getFilteredEntityType()
	{
		return ImprintedSlimeballItem.getEntityType(this.slimeStack);
	}
	
	public void onRightClickWithSlime(Player player, ItemStack stack, Level level, BlockPos pos)
	{
		this.dropSlime(level, pos);
		
		ItemStack remainder = this.itemHandler.insertItem(0, stack, false);
		
		stack.setCount(remainder.getCount());
	}
	
	private void dropSlime(Level level, BlockPos pos)
	{
		if (!level.isClientSide)
		{
			ItemStack extractedStack = this.itemHandler.extractItem(0, 1, false);
			if (!extractedStack.isEmpty())
			{
				level.playSound(null, pos, SoundEvents.SLIME_SQUISH_SMALL, SoundSource.BLOCKS,
					0.1F + level.random.nextFloat()*0.3F,
					level.random.nextFloat()*1.5F + 1F);

				double d0 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
				double d1 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
				double d2 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
				ItemEntity itementity = new ItemEntity(level, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, extractedStack.copy());
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return this.capHolder.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void load(CompoundTag compound)
	{
		super.load(compound);
		this.readData(compound);
	}
	
	public void readData(CompoundTag compound)
	{
		if (compound.contains(FILTER_KEY))
		{
			this.slimeStack = ItemStack.of(compound.getCompound(FILTER_KEY));
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		CompoundTag itemNBT = this.slimeStack.save(new CompoundTag());
		nbt.put(FILTER_KEY, itemNBT);
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		return this.saveWithoutMetadata();
	}

	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
	{
		this.readData(pkt.getTag());
	}
}
