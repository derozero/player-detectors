package com.github.commoble.entitydetectors.blocks;

import java.util.Random;
import java.util.function.Predicate;

import com.github.commoble.entitydetectors.EntityDetectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class EntityDetectorBlock extends Block
{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_3;

	public static final AxisAlignedBB CUBE_BOX = VoxelShapes.fullCube().getBoundingBox();
	
	public final Class<? extends Entity> entityClass;

	public EntityDetectorBlock(Class<? extends Entity> entityClass, Properties properties)
	{
		super(properties);
		this.entityClass = entityClass;
		this.setDefaultState(this.getDefaultState().with(POWERED, false).with(LEVEL, 0));
	}
	
	public abstract <T extends Entity> Predicate<? extends T> getEntityFilter(IWorld world, BlockPos pos);

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(POWERED, LEVEL);
	}

	/**
	 * How many world ticks before ticking
	 */
	@Override
	public int tickRate(IWorldReader worldIn)
	{
		return EntityDetectors.config.refreshRate.get();
	}

	/**
	 * Amount of light emitted
	 * 
	 * @deprecated prefer calling {@link BlockState#getLightValue()}
	 */
	@Deprecated
	@Override
	public int getLightValue(BlockState state)
	{
		return state.get(POWERED) ? super.getLightValue(state) : 0;
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change
	 * based on its state.
	 * 
	 * @deprecated call via {@link BlockState#canProvidePower()} whenever possible.
	 *             Implementing/overriding is fine.
	 */
	@Deprecated
	@Override
	public boolean canProvidePower(BlockState state)
	{
		return true;
	}

	/**
	 * @deprecated call via
	 *             {@link BlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	@Deprecated
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		return blockState.getWeakPower(blockAccess, pos, side);
	}

	/**
	 * @deprecated call via
	 *             {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	@Deprecated
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		return blockState.get(POWERED) ? 15 : 0;
	}

	// onBlockActivated
	@Override
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if (state.has(LEVEL))
		{
			int oldLevel = state.get(LEVEL);
			int newLevel = (oldLevel + 1) % 4; // cycle forward
			world.setBlockState(pos, state.with(LEVEL, newLevel));
			this.onUpdate(state, world, pos);
			float volume = 0.25F + world.rand.nextFloat() * 0.1F;
			float pitch = 0.5F + world.rand.nextFloat() * 0.1F;
			world.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, volume, pitch);

			return ActionResultType.SUCCESS;
		}
		else
		{
			return super.func_225533_a_(state, world, pos, player, hand, hit);
		}
	}

	// onRandomTick
	@Override
	public void func_225534_a_(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		this.onUpdate(state, world, pos);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		if (state.getBlock() != oldState.getBlock())
		{
			this.onUpdate(state, worldIn, pos);
		}
	}

	// public BlockState getStateForPlacement(BlockItemUseContext context)
	// {
	// return this.getDefaultState().with(FACING,
	// context.getNearestLookingDirection().getOpposite().getOpposite());
	// }

	public void onUpdate(BlockState state, IWorld world, BlockPos pos)
	{
		BlockState actualState = world.getBlockState(pos);
		if (!world.isRemote() && actualState.has(LEVEL) && actualState.has(POWERED))
		{
			int level = actualState.get(LEVEL);
			double radius = 2D * Math.pow(2D, level);
			double radiusSquared = radius * radius;
			AxisAlignedBB aabb = CUBE_BOX.offset(pos).grow(radius);
			Vec3d center = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			boolean areEntitiesNear = world.getEntitiesWithinAABB(this.entityClass, aabb, this.getEntityFilter(world, pos))
				.stream().anyMatch(entity -> entity.getDistanceSq(center) < radiusSquared);
			if (areEntitiesNear != actualState.get(POWERED))
			{
				world.setBlockState(pos, actualState.with(POWERED, areEntitiesNear), 3);
			}
			world.getPendingBlockTicks().scheduleTick(pos, this, EntityDetectors.config.refreshRate.get());
		}
	}

}
