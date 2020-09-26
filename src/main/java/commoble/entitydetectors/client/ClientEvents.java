package commoble.entitydetectors.client;

import commoble.entitydetectors.blocks.BlockRegistrar;
import commoble.entitydetectors.blocks.TileEntityRegistrar;
import commoble.entitydetectors.items.ItemRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents
{
	public static void subscribeClientEvents(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(ClientEvents::onClientSetup);
	}
	
	public static void onClientSetup(FMLClientSetupEvent event)
	{
		RenderTypeLookup.setRenderLayer(BlockRegistrar.FAKE_SLIME, RenderType.translucent());
		ClientRegistry.bindTileEntityRenderer(TileEntityRegistrar.MOB_DETECTOR, MobDetectorTileEntityRenderer::new);
		Minecraft.getInstance().getItemColors().register(new ImprintedSlimeballItemColor(), ItemRegistrar.IMPRINTED_SLIME_BALL);
	}
}
