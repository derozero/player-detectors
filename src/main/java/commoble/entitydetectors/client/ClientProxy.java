package commoble.entitydetectors.client;

import java.util.Optional;

import commoble.entitydetectors.EntityDetectors;
import commoble.entitydetectors.registrables.ImprintedSlimeballItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientProxy
{
	public static void subscribeClientEvents(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(ClientProxy::onRegisterEntityRenderers);
		modBus.addListener(ClientProxy::onRegisterItemColors);
	}
	
	private static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(EntityDetectors.MOB_DETECTOR_BET.get(), MobDetectorBlockEntityRenderer::new);
	}
	
	private static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
	{
		event.register(ClientProxy::getImprintedSlimeballColor, EntityDetectors.IMPRINTED_SLIME_BALL.get());
	}
	
	private static int getImprintedSlimeballColor(ItemStack stack, int tintIndex)
	{
		return ImprintedSlimeballItem.getEntityType(stack)
			.flatMap(type -> Optional.ofNullable(ForgeSpawnEggItem.fromEntityType(type)))
			.map(egg -> egg.getColor(tintIndex))
			.orElse(0xFFFFFFFF); // no tint
	}
}
