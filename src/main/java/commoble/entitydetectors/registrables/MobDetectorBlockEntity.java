package commoble.entitydetectors.registrables;

import java.util.Optional;

import javax.annotation.Nullable;

import commoble.entitydetectors.EntityDetectors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MobDetectorBlockEntity extends EntityDetectorBlockEntity<Mob>
{
	public static final String FILTER_KEY = "filter";
	
	public static final BlockEntityTicker<MobDetectorBlockEntity> TICKER = (level,pos,state,be) -> be.tick(level, pos, state);
	
	public static MobDetectorBlockEntity create(BlockPos pos, BlockState state)
	{
		return new MobDetectorBlockEntity(EntityDetectors.MOB_DETECTOR_BET.get(), pos, state, Mob.class);
	}

	public MobDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Class<? extends Mob> mobClass)
	{
		super(type, pos, state, mobClass);
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
	public boolean isEntityDetectable(Mob entity) {
		// TODO Auto-generated method stub
		return false;
	}

	public Optional<EntityType<?>> getFilteredEntityType() {
		// TODO Auto-generated method stub
		return null;
	}
}
