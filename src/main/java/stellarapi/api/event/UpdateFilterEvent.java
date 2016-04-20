package stellarapi.api.event;

import net.minecraft.entity.Entity;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;

/**
 * Fired to reset the scope.
 * */
public class UpdateFilterEvent extends PerEntityEvent {

	private IOpticalFilter filter;
	
	/** Additional parameters, like items which is changed or started using */
	private Object[] params;
	
	public UpdateFilterEvent(Entity entity, IOpticalFilter defFilter, Object... additionalParams) {
		super(entity);
		this.filter = defFilter;
		this.params = additionalParams;
	}
	
	public IOpticalFilter getFilter() {
		return this.filter;
	}
	
	public void setFilter(IOpticalFilter filter) {
		this.filter = filter;
	}
	
	public Object[] getAdditionalParams() {
		return this.params;
	}

}