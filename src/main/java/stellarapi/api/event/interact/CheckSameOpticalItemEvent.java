package stellarapi.api.event.interact;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fired when it needs to check if certain item is same optical instrument with
 * the other item.
 * <p>
 * Note that this event can be called for any item or <code>null</code>.
 * <p>
 * Cancel this event to force the two item not to be same.
 */
@Cancelable
public class CheckSameOpticalItemEvent extends LivingEvent {

	private final ItemStack item1, item2;
	private boolean isSame = false;

	public CheckSameOpticalItemEvent(EntityLivingBase entity, ItemStack item1, ItemStack item2) {
		super(entity);
		this.item1 = item1;
		this.item2 = item2;
	}

	public ItemStack getFirstItem() {
		return this.item1;
	}

	public ItemStack getSecondItem() {
		return this.item2;
	}

	public void markAsSame() {
		this.isSame = true;
	}

	public boolean isSame() {
		return this.isSame;
	}

}
