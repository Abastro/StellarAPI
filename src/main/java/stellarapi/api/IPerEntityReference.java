package stellarapi.api;

import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;

/** Interface of per entity reference to improve independence of api. */
public interface IPerEntityReference {

	public void updateScope(Object... additionalParams);
	public void updateFilter(Object... additionalParams);
	public IViewScope getScope();
	public IOpticalFilter getFilter();

}
