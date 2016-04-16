package stellarapi.api.mc;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarapi.api.CelestialLightSources;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.StellarAPIReference;
import stellarapi.config.IConfigHandler;

public class SleepWakeManager implements IConfigHandler {
	
	private boolean enabled;
	//true for first, false for last
	private boolean mode;
	private List<WakeHandler> wakeHandlers = Lists.newArrayList();
	
	/**
	 * Registers wake handler.
	 * @param handler the wake handler to register
	 * */
	public void register(String name, IWakeHandler handler) {
		wakeHandlers.add(0, new WakeHandler(name, handler));
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configuration for Waking System.");
		config.setCategoryLanguageKey(category, "config.category.server.wake");
		config.setCategoryRequiresWorldRestart(category, true);
		
		Property allEnabled = config.get(category, "Custom_Wake_Enabled", true);
		allEnabled.comment = "Enable/Disable wake system provided by Stellar Sky";
		allEnabled.setRequiresWorldRestart(true);
		allEnabled.setLanguageKey("config.property.server.wakeenable");
		
		Property mode = config.get(category, "Wake_Mode", "latest")
				.setValidValues(new String[]{"earliest", "latest"});
		mode.comment = "You can choose earliest or latest available wake time"
				+ "among these wake properties";
		mode.setRequiresWorldRestart(true);
		mode.setLanguageKey("config.property.server.wakemode");
		
		/*for() {
			
		}*/
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.enabled = config.getCategory(category).get("Custom_Wake_Enabled").getBoolean();
		this.mode = config.getCategory(category).get("Wake_Mode").getString().equals("first");
	}

	@Override
	public void saveToConfig(Configuration config, String category) { }
	
	/**
	 * Calculates time to wake.
	 * @param world the world to wake up
	 * @param defaultWakeTime the default wake time
	 * */
	public long getWakeTime(World world, long defaultWakeTime) {
		
		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(world);
		CelestialLightSources lightSources = StellarAPIReference.getLightSources(world);
		
		if(coordinate != null && lightSources != null)
		{
			long wakeTime;
			boolean accepted = false;

			if(this.mode)
			{
				wakeTime=Integer.MAX_VALUE;
				for(WakeHandler handler : wakeHandlers) {
					if(handler.handler.accept(world, lightSources, coordinate))
					{
						wakeTime = Math.min(wakeTime, handler.handler.getWakeTime(world, lightSources, coordinate, world.getWorldTime()));
						accepted = true;
					}
				}
			} else {
				wakeTime=Integer.MIN_VALUE;
				for(WakeHandler handler : wakeHandlers) {
					if(handler.handler.accept(world, lightSources, coordinate)) {
						wakeTime = Math.max(wakeTime, handler.handler.getWakeTime(world, lightSources, coordinate, world.getWorldTime()));
						accepted = true;
					}
				}
			}
			
			if(accepted)
				return wakeTime;
		}
		
		return defaultWakeTime;
	}
	
	/**
	 * Checks if sleep is possible or not.
	 * @param world the world to wake up
	 * @param defaultWakeTime the default wake time
	 * @return sleep possibility status, should be one of
	 * {@code EntityPlayer.EnumStatus.OK} or
	 * {@code EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW} or
	 * {@code EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE}
	 * */
	public EntityPlayer.EnumStatus getSleepPossibility(World world, EntityPlayer.EnumStatus defaultStatus) {
		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(world);
		CelestialLightSources lightSources = StellarAPIReference.getLightSources(world);
		
		if(coordinate != null && lightSources != null)
		{
			EntityPlayer.EnumStatus status = EntityPlayer.EnumStatus.OK;
			boolean accepted = false;

			for(WakeHandler handler : this.wakeHandlers) {
				if(status == EntityPlayer.EnumStatus.OK && handler.handler.accept(world, lightSources, coordinate)) {
					status = handler.handler.getSleepPossibility(world, lightSources, coordinate, world.getWorldTime());
					accepted = true;
				}
			}
			
			if(accepted)
				return status;
		}
		
		return defaultStatus;
	}
	
	private class WakeHandler {
		public WakeHandler(String name, IWakeHandler handler) {
			this.name = name;
			this.handler = handler;
		}
		protected String name;
		protected IWakeHandler handler;
	}
}
