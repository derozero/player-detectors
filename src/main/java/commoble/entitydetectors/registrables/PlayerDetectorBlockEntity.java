package commoble.entitydetectors.registrables;

import java.util.ArrayList;
import java.util.List;

import commoble.entitydetectors.EntityDetectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PlayerDetectorBlockEntity extends EntityDetectorBlockEntity<Player>
{
	private List<String> playerUsernames = new ArrayList<>();
	
	public void addUsername(String username) {
	    if (!playerUsernames.contains(username)) {
	        playerUsernames.add(username);
	        setChanged();
	    }
	}

	public List<String> getPlayerUsernames() {
	    return playerUsernames;
	}
	
	
	public static final BlockEntityTicker<PlayerDetectorBlockEntity> TICKER = (level, pos, state, be) -> be.tick(level, pos, state);
		
	public static PlayerDetectorBlockEntity create(BlockPos pos, BlockState state)
	{
		return new PlayerDetectorBlockEntity(EntityDetectors.PLAYER_DETECTOR_BET.get(), pos, state, Player.class);
	}

	public PlayerDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Class<? extends Player> entityClass)
	{
		super(type, pos, state, entityClass);
	}

	@Override
	public boolean isEntityDetectable(Player entity)
	{
		String playerName = entity.getDisplayName().getString();
		addUsername(playerName);
		return true;
	}
}
