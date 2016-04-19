package stellarapi.api.event;

import net.minecraft.entity.Entity;
import stellarapi.api.optics.IViewScope;

/**
 * Fired to reset the scope.
 * */
public class UpdateScopeEvent extends PerEntityEvent {

	private IViewScope scope;
	
	/** Additional parameters, like items which is changed or started using */
	private Object[] params;
	
	public UpdateScopeEvent(Entity entity, IViewScope defScope, Object... additionalParams) {
		super(entity);
		this.scope = defScope;
		this.params = additionalParams;
	}
	
	public IViewScope getScope() {
		return this.scope;
	}
	
	public void setScope(IViewScope scope) {
		this.scope = scope;
	}
	
	public Object[] getAdditionalParams() {
		return this.params;
	}

}
