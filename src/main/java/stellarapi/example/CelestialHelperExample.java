package stellarapi.example;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.wavecolor.EnumRGBA;
import stellarapi.api.wavecolor.Wavelength;

/**
 * Helper for WorldProvider. <p>
 * Note that this can be only used when <p>
 * <ul>
 *  <li> There is one Sun and one Moon.
 *  <li>{@link ICelestialObject#getHorizontalPeriod()} is not null for Sun and Moon.
 *  <li>{@link ICelestialObject#getPhasePeriod()} is not null for Moon.
 * </ul>
 * */
public class CelestialHelperExample {
	
	private final float relativeMultiplierSun;
	private final float relativeMultiplierMoon;
	
	private final ICelestialObject sun;
	private final ICelestialObject moon;
	
	private final ICelestialCoordinate coordinate;
	
	private final ISkyEffect sky;
	
	public CelestialHelperExample(float relativeMultiplierSun, float relativeMultiplierMoon,
			ICelestialObject sun, ICelestialObject moon,
			ICelestialCoordinate coordinate, ISkyEffect sky) {
		this.relativeMultiplierSun = relativeMultiplierSun;
		this.relativeMultiplierMoon = relativeMultiplierMoon;
		this.sun = sun;
		this.moon = moon;
		this.coordinate = coordinate;
		this.sky = sky;
	}
	
	/**
	 * Calculates current celestial angle. <p>
	 * Basically for WorldProvider. <p>
	 * @param worldTime current world time
	 * @param partialTicks the partial tick
	 * */
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return (float) sun.getHorizontalPeriod().getOffset(worldTime, partialTicks) + 0.25f;
	}
	
	/**
	 * Gets current sun height rate.
	 * @param partialTicks the partial tick
	 * @return <code>sin(Height_Angle_Of_Sun)</code>
	 * */
	public float getSunHeightFactor(float partialTicks) {
		return (float) Spmath.sind(sun.getCurrentHorizontalPos().y);
	}
	
	/**
	 * Gets current sunlight factor.
	 * @param color the color to get sunlight factor
	 * @param partialTicks the partial tick
	 * */
	public float getSunlightFactor(EnumRGBA color, float partialTicks) {
		return 2.0f*this.getSunHeightFactor(partialTicks)+0.5f
				* (float)sun.getCurrentBrightness(Wavelength.colorWaveMap.get(color)) * this.relativeMultiplierSun;
	}
	
	/**
	 * Gets current sky transmission factor.
	 * @param partialTicks the partial tick
	 * */
	public float getSkyTransmissionFactor(float partialTicks) {
		return 1.0f - sky.getAbsorptionFactor(partialTicks);
	}

	
	/**
	 * Gets current sunrise sunset factor(brightness of sunrise/sunset color).
	 * @param partialTicks the partial tick
	 * */
	public float calculateSunriseSunsetFactor(EnumRGBA color, float partialTicks) {
		return (float) (sun.getCurrentBrightness(Wavelength.colorWaveMap.get(color)))
				* this.relativeMultiplierSun;
	}
	
	/**
	 * Gets dispersion factor. (brightness of sky itself, not ground light level) <p>
	 * @param partialTicks the partial tick
	 * */
	public float getDispersionFactor(EnumRGBA color, float partialTicks) {
		return sky.getDispersionFactor(Wavelength.colorWaveMap.get(color), partialTicks);
	}
	
	/**
	 * Gets light pollution factor.
	 * @param partialTicks the partial tick
	 * */
	public float getLightPollutionFactor(EnumRGBA color, float partialTicks) {
		return sky.getLightPollutionFactor(Wavelength.colorWaveMap.get(color), partialTicks);
	}
	
	/**
	 * Gets current moon phase time.
	 * @param worldTime the current World Time. If it isn't, this method will give undefined result.
	 * */
	public int getCurrentMoonPhase(long worldTime) {
		return (int) Math.floor(moon.getPhasePeriod().getOffset(worldTime, 0.0f) * 8);
	}
	
	/**
	 * Gets current moon phase factor. <p>
	 * */
	public float getCurrentMoonPhaseFactor() {
		return (float) moon.getCurrentPhase() * this.relativeMultiplierMoon;
	}

}
