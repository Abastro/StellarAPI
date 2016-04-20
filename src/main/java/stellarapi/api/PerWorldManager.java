package stellarapi.api;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import stellarapi.api.celestials.CelestialCollectionManager;
import stellarapi.api.celestials.CelestialEffectors;
import stellarapi.api.celestials.ICelestialCollection;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.celestials.IEffectorType;
import stellarapi.api.event.ConstructCelestialsEvent;
import stellarapi.api.event.ResetCoordinateEvent;
import stellarapi.api.event.ResetSkyEffectEvent;
import stellarapi.api.event.SortCelestialsEvent;

/**
 * Per world manager to contain the per-world(dimension) objects.
 * */
public class PerWorldManager extends WorldSavedData {
	
	private static final String ID = "stellarapiperworldmanager";
	
	private World world;
	
	private CelestialCollectionManager collectionManager = null;
	private HashMap<IEffectorType, CelestialEffectors> effectorMap = Maps.newHashMap();
	private ICelestialCoordinate coordinate;
	private ISkyEffect skyEffect;
	
	public static void initiatePerWorldManager(World world) {
		world.perWorldStorage.setData(ID, new PerWorldManager(world));
	}
	
	public static PerWorldManager getPerWorldManager(World world) {
		return (PerWorldManager) world.perWorldStorage.loadData(PerWorldManager.class, ID);
	}
	
	private PerWorldManager(World world) {
		super(ID);
		this.world = world;
	}
	
	
	private static final Ordering<ICelestialCollection> collectionOrdering = Ordering.from(
			new Comparator<ICelestialCollection>() {
				@Override
				public int compare(ICelestialCollection col1, ICelestialCollection col2) {
					return Integer.compare(-col1.searchOrder(), -col2.searchOrder());
				}
			});
		
	public void constructCollections() {
		ConstructCelestialsEvent construct = new ConstructCelestialsEvent(this.world);
		StellarAPIReference.getEventBus().post(construct);
		
		ImmutableSet<IEffectorType> effectorTypes = construct.getEffectorTypes();
		Map<IEffectorType, List<ICelestialObject>> effectors= Maps.newHashMap();
		for(IEffectorType type : effectorTypes)
			effectors.put(type, construct.getEffectors(type));
		
		SortCelestialsEvent sort = new SortCelestialsEvent(this.world,
				collectionOrdering, construct.getCollections(), effectors);
		StellarAPIReference.getEventBus().post(sort);
		
		this.collectionManager = new CelestialCollectionManager(sort.getSortedCollections());
		
		for(IEffectorType type : effectorTypes)
			effectorMap.put(type, new CelestialEffectors(sort.getSortedEffectors(type)));
	}
	
	public void resetCoordinate() {
		ResetCoordinateEvent coord = new ResetCoordinateEvent(this.world);
		StellarAPIReference.getEventBus().post(coord);
		this.coordinate = coord.getCoordinate();
	}
	
	public void resetSkyEffect() {
		ResetSkyEffectEvent sky = new ResetSkyEffectEvent(this.world);
		StellarAPIReference.getEventBus().post(sky);
		this.skyEffect = sky.getSkyEffect();
	}
	
	public CelestialCollectionManager getCollectionManager() {
		return this.collectionManager;
	}
	
	public CelestialEffectors getCelestialEffectors(IEffectorType type) {
		return effectorMap.get(type);
	}
	
	public ImmutableSet<IEffectorType> getEffectorTypeSet() {
		return ImmutableSet.copyOf(effectorMap.keySet());
	}
	
	public ICelestialCoordinate getCoordinate() {
		return this.coordinate;
	}
	
	public ISkyEffect getSkyEffect() {
		return this.skyEffect;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) { }

	@Override
	public void writeToNBT(NBTTagCompound compound) { }

}
