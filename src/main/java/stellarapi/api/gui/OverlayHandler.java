package stellarapi.api.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import stellarapi.api.lib.config.ConfigManager;

public class OverlayHandler {
	
	private OverlayContainer container;
	private Minecraft mc;
	
	public OverlayHandler(ConfigManager guiConfig) {
		this.container = new OverlayContainer(guiConfig);
	}
	
	public void initialize(Minecraft mc) {
		this.mc = mc;
		container.initialize(mc);
	}
	
	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.type == RenderGameOverlayEvent.ElementType.ALL) {
			container.setResolution(event.resolution);
			container.render(event.mouseX, event.mouseY, event.partialTicks);
		}
	}
	
	public void updateOverlay() {
		container.updateOverlay();
	}
	
	public void openGui(Minecraft mc, KeyBinding focusGuiKey) {
		mc.displayGuiScreen(new GuiScreenOverlay(this.container, focusGuiKey));
	}
}
