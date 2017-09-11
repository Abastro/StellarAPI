package stellarapi.api.coordinates;

import java.util.Map;

import net.minecraft.util.ResourceLocation;

public class CCoordInstance {

	private ResourceLocation name;
	private CCoordInstance parent;

	private CCoordinates origin = null;
	private ICoordElement[] elements = null;

	public CCoordInstance(ResourceLocation originKey, CCoordinates origin, CCoordInstance parent) {
		this(originKey, parent);
		this.origin = origin;
		this.elements = origin.getDefaultElements();
		this.parent = parent;
	}

	public CCoordInstance(ResourceLocation name, CCoordInstance parent) {
		this.name = name;
		this.parent = parent;
	}

	/** Don't use this instance. */
	@Deprecated
	public static final CCoordInstance PLACEHOLDER = new CCoordInstance(new ResourceLocation(""), null);

	/** Only to instantiate the placeholders, mainly for something to put on parents. */
	public static CCoordInstance of(ResourceLocation originKey, CCoordinates origin, Map<ResourceLocation, CCoordInstance> instances) {
		if(origin.getDefaultParentID() == null)
			return new CCoordInstance(originKey, origin, null);
		else if(instances.containsKey(origin.getDefaultParentID()))
			return new CCoordInstance(originKey, origin, instances.get(origin.getDefaultParentID()));
		else return new CCoordInstance(originKey, origin, PLACEHOLDER);
	}


	public ResourceLocation getID() {
		return this.name;
	}


	public CCoordinates getOrigin() {
		return this.origin;
	}


	public boolean isRoot() {
		return this.parent == null;
	}

	public CCoordInstance getParent() {
		return this.parent;
	}

	/** Internal method */
	@Deprecated
	public void setParent(CCoordInstance newParent) {
		this.parent = newParent;
	}

	
	public boolean hasCoordElements() {
		return this.elements != null;
	}

	public ICoordElement[] getCoordElements() {
		return this.elements;
	}

	public ICoordElement getCoordElement(int index) {
		return this.elements[index];
	}

	public int numElements() {
		return elements.length;
	}

	public CCoordInstance setCoordElements(ICoordElement[] newElements) {
		this.elements = newElements;
		return this;
	}

	public CCoordInstance setCoordElement(int index, ICoordElement element) {
		this.elements[index] = element;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CCoordInstance) {
			CCoordInstance other = (CCoordInstance) obj;
			return name.equals(other.name);
		} else if(obj instanceof ResourceLocation) {
			return name.equals(obj);
		} else return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
