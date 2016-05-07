package stellarapi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.optics.FilterHelper;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarapi.api.optics.NakedFilter;
import stellarapi.feature.gui.overlay.OverlayHandler;

public class StellarAPIClientForgeEventHook {
	
	private static final Field lightMapField = ReflectionHelper.findField(EntityRenderer.class,
			ObfuscationReflectionHelper.remapFieldNames(EntityRenderer.class.getName(), "lightmapTexture", "field_78513_d"));
	
	private static final Field lightMapUpdatedField = ReflectionHelper.findField(EntityRenderer.class,
			ObfuscationReflectionHelper.remapFieldNames(EntityRenderer.class.getName(), "lightmapUpdateNeeded", "field_78536_aa"));
	
	static {
		try {
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(lightMapField, lightMapField.getModifiers() & ~ Modifier.FINAL);
		} catch(Exception exc) {
			Throwables.propagate(exc);
		}
	}
	
	private OverlayHandler overlay;
	
	public StellarAPIClientForgeEventHook(OverlayHandler overlay) {
		this.overlay = overlay;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUpdateFOV(FOVUpdateEvent event) {
		IViewScope scope = StellarAPIReference.getScope(event.entity);
		if(scope.forceChange())
			event.newfov = event.fov / (float)scope.getMP();
		else event.newfov /= (float)scope.getMP();
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDecideFogColor(EntityViewRenderEvent.FogColors event) {
		IViewScope scope = StellarAPIReference.getScope(event.entity);
		IOpticalFilter filter = StellarAPIReference.getFilter(event.entity);
		
		double multiplier = scope.getLGP() / (scope.getMP() * scope.getMP());
		
		double[] value = FilterHelper.getFilteredRGBBounded(filter, new double[] {
				event.red * multiplier, event.green * multiplier, event.blue * multiplier});
		event.red = (float) value[0];
		event.green = (float) value[1];
		event.blue = (float) value[2];
		
		if(multiplier != 1.0 || !(filter instanceof NakedFilter)) {			
			DynamicTexture texture;
			try {
				texture = (DynamicTexture) lightMapField.get(event.renderer);

				for(int i = 0; i < 255; i++)
				{
					int data = texture.getTextureData()[i];
					int red = data & 0x000000ff;
					int green = ((data & 0x0000ff00) >> 8);
					int blue = ((data & 0x00ff0000) >> 16);
					
					double[] modified = FilterHelper.getFilteredRGBBounded(filter, new double[] {
							red / 255.0 * multiplier, green / 255.0 * multiplier, blue / 255.0 * multiplier});

					red = Math.min(0xff, (int)(modified[0]*0xff));
					green = Math.min(0xff, (int)(modified[1]*0xff));
					blue = Math.min(0xff, (int)(modified[2]*0xff));

					texture.getTextureData()[i] = 0xff << 24 | red << 16 | green << 8 | blue;
				}

				texture.updateDynamicTexture();

				lightMapUpdatedField.set(event.renderer, true);

			} catch (Exception exc) {
				Throwables.propagate(exc);
			}
		}
	}
	
	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.type == RenderGameOverlayEvent.ElementType.ALL)
			overlay.renderGameOverlay(event.resolution, event.mouseX, event.mouseY, event.partialTicks);
	}
}
