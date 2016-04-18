package stellarapi.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.world.World;
import stellarapi.api.StellarAPIReference;

/**
 * Superclass of per-world(dimension) events in Stellar API. <p>
 * If a method utilizes this {@link Event} as its parameter, the method will 
 *  receive every child event of this class. <p>
 *  
 * All child events of this event is fired on {@link StellarAPIReference#getEventBus()}.
 * */
public class PerWorldEvent extends Event {
	
	private final World world;
	
	public PerWorldEvent(World world) {
		this.world = world;
	}
	
	/**
	 * Getter for the world instance.
	 * */
	public World getWorld() {
		return this.world;
	}
}
