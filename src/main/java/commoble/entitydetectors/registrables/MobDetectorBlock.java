package commoble.entitydetectors.registrables;

import commoble.entitydetectors.EntityDetectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MobDetectorBlock extends EntityDetectorBlock
{
	public MobDetectorBlock(Properties properties)
	{
		super(properties);
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
