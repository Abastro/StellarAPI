package stellarapi.api.atmosphere;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import stellarapi.api.lib.math.SpCoord;

public class Atmosphere implements INBTSerializable<NBTTagCompound> {

	public static enum EnumAtmosphereType {
		PLANE, SPHERE;
	}

	public static class BoundaryData {
		private double height;
		private double density;
	}

	private BoundaryData[] data;
	private AtmosphereLayer[] layers;
	// TODO fill in these
	private EnumAtmosphereType type;

	@Override
	public NBTTagCompound serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		
	}
}
