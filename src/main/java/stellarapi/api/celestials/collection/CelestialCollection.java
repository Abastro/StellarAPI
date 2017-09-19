package stellarapi.api.celestials.collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Celestial Collection containing the data of celestial objects.
 * Serializable version should be distinct for each worldset.
 * */
public abstract class CelestialCollection<P> implements INBTSerializable<NBTTagCompound> {

	private long updatePeriod; // -1L for infinity, 0L for every render ticks
	private boolean isUpdatePeriodVariable;
	private EnumMaxIntensity maxIntensity;

	/** Maximum intensity level. */
	public static enum EnumMaxIntensity {
		STELLAR, PLANETARY, LOCALSTAR
	}

	/** Gets the adaption on world */
	public abstract ICollectionAdaption<P> adaption(World world, boolean forVanilla);

	public void setupPartial() { }
	public void setupComplete() { }

	/**
	 * Handles vanilla case. returns false if this collection is not capable of that.
	 * */
	public boolean handleVanilla() {
		return false;
	}

}