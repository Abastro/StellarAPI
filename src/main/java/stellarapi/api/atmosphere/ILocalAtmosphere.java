package stellarapi.api.atmosphere;

import net.minecraft.util.math.Vec3d;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.Wavelength;

public interface ILocalAtmosphere {
	/** Gets refracted direction of specified direction */
	public SpCoord applyRefraction(Vec3d loc, SpCoord dir, Wavelength wave);

	/** Gets rate of resulted intensity from the extinction */
	public double extincted(Vec3d loc, SpCoord dir, Wavelength wave);

	/** Seeing in radians */
	public double seeing(Vec3d loc, SpCoord dir, Wavelength wave);


	/** Height of the location */
	public double getHeight(Vec3d loc, SpCoord dir);

	/** Converts a direction to the local direction */
	public SpCoord localDirection(Vec3d loc, SpCoord dir);
}