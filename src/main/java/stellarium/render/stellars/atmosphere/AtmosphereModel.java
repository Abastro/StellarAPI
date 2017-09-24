package stellarium.render.stellars.atmosphere;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.WaveFilterType;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.ICheckedAtmModel;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarDimensionManager;

@Hierarchy
public class AtmosphereModel implements ICheckedAtmModel {

	private float outerRadius = 820.0f;
	private float innerRadius = 800.0f;
	private float heightOffset = 0.2f, heightIncScale = 1.0f;

	private float height;
	private float skyred, skygreen, skyblue;
	private float skyExtRed, skyExtGreen, skyExtBlue;
	private float skyDispRed, skyDispGreen, skyDispBlue;
	
	private boolean azimuthCheckEnabled;
	private double leastAzimuthRendered;

	public void initializeSettings(ClientSettings settings) {
		settings.putSubConfig(AtmosphereSettings.KEY, new AtmosphereSettings());
	}

	public void dimensionLoad(StellarDimensionManager dimManager) {
		this.azimuthCheckEnabled = dimManager.getSettings().hideObjectsUnderHorizon();
		this.leastAzimuthRendered = -90.0f;

		this.outerRadius = (float) dimManager.getSettings().getOuterRadius();
		this.innerRadius = (float) dimManager.getSettings().getInnerRadius();
		this.heightOffset = (float) dimManager.getSettings().getHeightOffset();
		this.heightIncScale = (float) dimManager.getSettings().getHeightIncScale();
	}

	public void onTick(World world, ViewerInfo update) {
		int i = MathHelper.floor(update.currentPosition.getX());
		int j = MathHelper.floor(update.currentPosition.getY());
		int k = MathHelper.floor(update.currentPosition.getZ());
		int l = ForgeHooksClient.getSkyBlendColour(world, new BlockPos(i,j,k));
		this.skyred = (float)(l >> 16 & 255) / 255.0F;
		this.skygreen = (float)(l >> 8 & 255) / 255.0F;
		this.skyblue = (float)(l & 255) / 255.0F;

		this.skyDispRed = update.sky.getDispersionFactor(WaveFilterType.red, 0.0f);
		this.skyDispGreen = update.sky.getDispersionFactor(WaveFilterType.V, 0.0f);
		this.skyDispBlue = update.sky.getDispersionFactor(WaveFilterType.B, 0.0f);
		
		this.skyExtRed = update.sky.getExtinctionRate(WaveFilterType.red);
		this.skyExtGreen = update.sky.getExtinctionRate(WaveFilterType.V);
		this.skyExtBlue = update.sky.getExtinctionRate(WaveFilterType.B);

		this.height = this.heightOffset + update.getHeight(world) * this.heightIncScale;
		
		if(this.azimuthCheckEnabled)
			this.leastAzimuthRendered = Math.acos(1.0 / (1.0 + this.height / this.innerRadius));
	}

	public double getHeight() {
		return this.height;
	}

	public double getOuterRadius() {
		return this.outerRadius;
	}

	public double getInnerRadius() {
		return this.innerRadius;
	}

	public float getSkyColorRed() {
		return this.skyred;
	}

	public float getSkyColorGreen() {
		return this.skygreen;
	}

	public float getSkyColorBlue() {
		return this.skyblue;
	}
	
	public float getSkyDispRed() {
		return this.skyDispRed;
	}

	public float getSkyDispGreen() {
		return this.skyDispGreen;
	}

	public float getSkyDispBlue() {
		return this.skyDispBlue;
	}
	
	public float getSkyExtRed() {
		return this.skyExtRed;
	}

	public float getSkyExtGreen() {
		return this.skyExtGreen;
	}

	public float getSkyExtBlue() {
		return this.skyExtBlue;
	}

	@Override
	public boolean isRendered(SpCoord pos) {
		return pos.y >= this.leastAzimuthRendered;
	}
}