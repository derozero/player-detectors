package commoble.entitydetectors.blocks;

import java.util.function.Predicate;

import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class PlayerDetectorBlock extends EntityDetectorBlock
{

	public PlayerDetectorBlock(Properties properties)
	{
		super(PlayerEntity.class, properties);
	}
	
//	public boolean isEntityPlayer(Entity ent)
//	{
//		return ent.getType() == EntityType.PLAYER;
//	}

	@Override
	public <T extends Entity> Predicate<T> getEntityFilter(IWorld world, BlockPos pos)
	{
		// return this::isEntityPlayer;
		return Predicates.alwaysTrue();	// we already filter by the PlayerEntity class
	}

}
