package stellarapi;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import stellarapi.api.SAPIReferences;
import stellarapi.api.SAPIRegistries;
import stellarapi.api.atmosphere.Atmosphere;
import stellarapi.api.atmosphere.IAtmHolder;
import stellarapi.api.atmosphere.ILocalAtmosphere;
import stellarapi.api.celestials.CelestialType;
import stellarapi.api.celestials.ICelestialScene;
import stellarapi.api.celestials.ICelestialSystem;
import stellarapi.api.coordinates.ICoordSystem;
import stellarapi.api.coordinates.ILocalCoordinates;
import stellarapi.internal.atmosphere.CGenericAtmosphere;
import stellarapi.internal.celestial.CelestialScene;
import stellarapi.internal.celestial.CelestialSystem;
import stellarapi.internal.coordinates.CCoordSystem;
import stellarapi.internal.coordinates.CLocalCoordinates;
import stellarapi.internal.reference.CWorldReference;
import worldsets.api.WAPIReference;
import worldsets.api.worldset.WorldSet;

public class SAPICapsHook {

	private static final ResourceLocation CAPS =
			new ResourceLocation(SAPIReferences.modid, "capabilities");

	public void registerCapabilities() {
		// TODO default implementations & some save/load. they are not right for now
		CapabilityManager.INSTANCE.register(ILocalCoordinates.class,
				new IStorage<ILocalCoordinates>() {
					@Override
					public NBTBase writeNBT(Capability<ILocalCoordinates> capability, ILocalCoordinates instance, EnumFacing side) {
						return null;
					}
					@Override
					public void readNBT(Capability<ILocalCoordinates> capability, ILocalCoordinates instance, EnumFacing side, NBTBase nbt) {
					}
		}, CLocalCoordinates.class);

		CapabilityManager.INSTANCE.register(ICoordSystem.class,
				new IStorage<ICoordSystem>() {
			@Override
			public NBTBase writeNBT(Capability<ICoordSystem> capability, ICoordSystem instance, EnumFacing side) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setString("providerID", instance.getProviderID().toString());
				compound.setTag("handler", instance.getHandler().serializeNBT());

				return compound;
			}
			@Override
			public void readNBT(Capability<ICoordSystem> capability, ICoordSystem instance, EnumFacing side, NBTBase nbt) {
				if(nbt instanceof NBTTagCompound) {
					NBTTagCompound compound = (NBTTagCompound) nbt;
					instance.setProviderID(new ResourceLocation(compound.getString("providerID")));
					instance.getHandler().deserializeNBT(compound.getCompoundTag("handler"));
				}
			}
		}, CCoordSystem.class);
	

		CapabilityManager.INSTANCE.register(ICelestialScene.class,
				new IStorage<ICelestialScene>() {
			@Override
			public NBTBase writeNBT(Capability<ICelestialScene> capability, ICelestialScene instance, EnumFacing side) {
				return null;
			}
			@Override
			public void readNBT(Capability<ICelestialScene> capability, ICelestialScene instance, EnumFacing side, NBTBase nbt) {
			}
		}, CelestialScene.class);

		CapabilityManager.INSTANCE.register(ICelestialSystem.class,
				new IStorage<ICelestialSystem>() {
			@Override
			public NBTBase writeNBT(Capability<ICelestialSystem> capability, ICelestialSystem instance, EnumFacing side) {
				NBTTagCompound comp = new NBTTagCompound();
				for(CelestialType type : SAPIRegistries.getCelestialTypeRegistry()) {
					if(instance.isAbsent(type))
						continue;
					NBTTagCompound subComp = instance.getCollection(type).serializeNBT();
					subComp.setString("theProviderID", instance.getProviderID(type).toString());
					comp.setTag(type.delegate.name().toString(), subComp);
				}
				return comp;
			}
			@Override
			public void readNBT(Capability<ICelestialSystem> capability, ICelestialSystem instance, EnumFacing side, NBTBase nbt) {
				NBTTagCompound comp = (NBTTagCompound) nbt;
				for(CelestialType type : SAPIRegistries.getOrderedTypes()) {
					if(!comp.hasKey(type.delegate.name().toString(), comp.getId())) {
						instance.validateNset(type, null);
						continue;
					}
					NBTTagCompound subComp = comp.getCompoundTag(type.delegate.name().toString());
					instance.validateNset(type, new ResourceLocation(subComp.getString("theProviderID")));
					instance.getCollection(type).deserializeNBT(subComp);
				}
			}
		}, CelestialSystem.class);


		CapabilityManager.INSTANCE.register(IAtmHolder.class,
				new IStorage<IAtmHolder>() {
			@Override
			public NBTBase writeNBT(Capability<IAtmHolder> capability, IAtmHolder instance, EnumFacing side) {
				NBTTagCompound compound = new NBTTagCompound();

				compound.setString("providerID", instance.getProviderID().toString());

				Atmosphere atmosphere = instance.getAtmosphere();
				ILocalAtmosphere local = instance.getLocalAtmosphere();
				if(atmosphere != null)
					compound.setTag("atmosphere", atmosphere.serializeNBT());
				else compound.removeTag("atmosphere");

				compound.setTag("local", local.serializeNBT());
				return compound;
			}
			@Override
			public void readNBT(Capability<IAtmHolder> capability, IAtmHolder instance, EnumFacing side, NBTBase nbt) {
				if(nbt instanceof NBTTagCompound) {
					NBTTagCompound compound = (NBTTagCompound) nbt;

					instance.setProviderID(new ResourceLocation(compound.getString("providerID")));
					instance.evaluateAtmosphere(null);

					if(compound.hasKey("atmosphere")) {
						Atmosphere readAtm = instance.getAtmosphere();
						readAtm.deserializeNBT(compound.getCompoundTag("atmosphere"));
					} else if(!compound.hasNoTags())
						instance.setAtmosphere(null);

					if(instance.getLocalAtmosphere() != null)
						instance.getLocalAtmosphere().deserializeNBT(compound.getCompoundTag("local"));
				}
			}
		}, CGenericAtmosphere.class);
	}


	public void attachWorldCaps(AttachCapabilitiesEvent<World> worldCaps) {
		World world = worldCaps.getObject();
		WorldSet worldSet = WAPIReference.getPrimaryWorldSet(world);
		if(worldSet == null)
			return;

		worldCaps.addCapability(CAPS, new CWorldReference(world, worldSet));
	}
}