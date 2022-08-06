package commoble.entitydetectors.registrables;

import commoble.entitydetectors.EntityDetectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MobDetectorBlock extends EntityDetectorBlock
{
	public MobDetectorBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		ItemStack stack = player.getItemInHand(hand);
		if (MobDetectorItemHandler.isItemValidFilter(stack) && world.getBlockEntity(pos) instanceof MobDetectorBlockEntity mobDetector)
		{
			if (!world.isClientSide)
			{
				mobDetector.onRightClickWithSlime(player, stack, world, pos);
			}
			return InteractionResult.SUCCESS;
		}
		return super.use(state, world, pos, player, hand, hit);
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (state.getBlock() != newState.getBlock() && world.getBlockEntity(pos) instanceof MobDetectorBlockEntity mobDetector)
		{
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), mobDetector.getSlimeStack());
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return MobDetectorBlockEntity.create(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return !level.isClientSide && type == EntityDetectors.MOB_DETECTOR_BET.get()
			? (BlockEntityTicker<T>)MobDetectorBlockEntity.TICKER
			: null;
	}
}
