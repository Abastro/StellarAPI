package stellarapi.api;

import net.minecraft.world.World;
import stellarapi.api.celestials.CelestialEffectors;
import stellarapi.api.celestials.IEffectorType;
import stellarapi.api.view.ICCoordinates;

/**
 * Helper for getting conventional periods.
 */
public class PeriodHelper {

	/**
	 * Gets common day period for primary light source.
	 * 
	 * @param world
	 *            the world
	 * @return common day period for the world, or <code>null</code> if there is
	 *         nothing like day period
	 */
	public static CelestialPeriod getDayPeriod(World world) {
		CelestialEffectors effectors = SAPIReferences.getEffectors(world, IEffectorType.Light);
		ICCoordinates coords = SAPIReferences.getCoordinates(world);
		return effectors != null && coords != null? effectors.getPrimarySource().getHorizontalPeriod(coords) : null;
	}

	/**
	 * Gets common lunar month period for primary tidal source.
	 * 
	 * @param world
	 *            the world
	 * @return common lunar month period for the world, or <code>null</code> if
	 *         there is nothing like lunar month period
	 */
	public static CelestialPeriod getLunarPeriod(World world) {
		CelestialEffectors effectors = SAPIReferences.getEffectors(world, IEffectorType.Tide);
		return effectors != null ? effectors.getPrimarySource().getAbsolutePeriod() : null;
	}

	/**
	 * Gets common year period for primary light source.
	 * 
	 * @param world
	 *            the world
	 * @return common year period for the world, or <code>null</code> if there
	 *         is nothing like year period
	 */
	public static CelestialPeriod getYearPeriod(World world) {
		CelestialEffectors effectors = SAPIReferences.getEffectors(world, IEffectorType.Light);
		return effectors != null ? effectors.getPrimarySource().getAbsolutePeriod() : null;
	}

}
