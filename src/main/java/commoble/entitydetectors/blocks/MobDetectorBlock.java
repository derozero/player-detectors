package commoble.entitydetectors.blocks;

import java.util.function.Predicate;

import commoble.entitydetectors.util.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class MobDetectorBlock extends EntityDetectorBlock
{
	public MobDetectorBlock(Properties properties)
	{
		super(MobEntity.class, properties);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (MobDetectorItemHandler.isItemValidFilter(stack))
		{
			return WorldHelper.getTileEntityAt(MobDetectorTileEntity.class, world, pos).map(te -> te.onRightClickWithSlime(player, stack, world, pos))
				.orElseGet(() -> super.onBlockActivated(state, world, pos, player, hand, hit));
		}
		return super.onBlockActivated(state, world, pos, player, hand, hit);
	}

	@Override
	public <T extends Entity> Predicate<T> getEntityFilter(IWorld world, BlockPos pos)
	{

		return entity -> WorldHelper.getTileEntityAt(MobDetectorTileEntity.class, world, pos).map(te -> te.isEntityDetectable(entity)).orElse(true);
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return TileEntityRegistrar.MOB_DETECTOR.create();
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (state.getBlock() != newState.getBlock())
		{
			WorldHelper.getTileEntityAt(MobDetectorTileEntity.class, world, pos).ifPresent(te ->
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.slimeStack)
			);

			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}
}
