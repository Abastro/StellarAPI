package stellarapi.feature.gui.overlay;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import stellarapi.api.gui.overlay.EnumOverlayMode;
import stellarapi.api.gui.overlay.IOverlayElement;
import stellarapi.api.gui.overlay.IRawOverlayElement;
import stellarapi.api.gui.pos.ElementPos;

@SuppressWarnings("rawtypes")
public class OverlayContainer {

	private int width;
	private int height;

	private EnumOverlayMode currentMode = EnumOverlayMode.OVERLAY;
	private ImmutableList<OverlayElementDelegate> currentlyDisplayedList;
	private boolean pauseGame;

	public void resetDisplayList(Iterable<OverlayElementDelegate> iterable) {
		this.currentlyDisplayedList = ImmutableList.copyOf(iterable);
	}

	ImmutableList<? extends IRawOverlayElement> getCurrentDisplayedList() {
		return this.currentlyDisplayedList;
	}

	public void setResolution(ScaledResolution resolution) {
		this.width = resolution.getScaledWidth();
		this.height = resolution.getScaledHeight();
	}

	public void switchMode(EnumOverlayMode mode) {
		this.currentMode = mode;
		if (!mode.displayed())
			this.pauseGame = false;

		for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList)
			delegate.getElement().switchMode(mode);
	}

	public void updateOverlay() {
		if(this.currentlyDisplayedList == null) // Safety check for change of order (postinit & first client tick)
			return;

		for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList) {
			delegate.getElement().updateOverlay();
			if (delegate.getHandler() != null)
				delegate.getHandler().updateHandler();
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int eventButton) {
		boolean changedUniversal = false;

		for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList) {
			ElementPos pos = delegate.getPosition();
			IOverlayElement<?> element = delegate.getElement();
			int width = element.getWidth();
			int height = element.getHeight();
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= element.animationOffsetX(0.0f);
			scaledMouseY -= element.animationOffsetY(0.0f);

			if (element.mouseClicked(scaledMouseX, scaledMouseY, eventButton))
				delegate.notifyChange();

			if (delegate.getHandler() != null)
				changedUniversal = delegate.getHandler().mouseClicked(mouseX, mouseY, eventButton) || changedUniversal;
		}

		if (changedUniversal)
			for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList)
				delegate.notifyChange();
	}

	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		boolean changedUniversal = false;

		for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList) {
			ElementPos pos = delegate.getPosition();
			IOverlayElement<?> element = delegate.getElement();
			int width = element.getWidth();
			int height = element.getHeight();
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= element.animationOffsetX(0.0f);
			scaledMouseY -= element.animationOffsetY(0.0f);

			if (element.mouseClickMove(scaledMouseX, scaledMouseY, clickedMouseButton, timeSinceLastClick))
				delegate.notifyChange();

			if (delegate.getHandler() != null)
				changedUniversal = delegate.getHandler().mouseClickMove(mouseX, mouseY, clickedMouseButton,
						timeSinceLastClick) || changedUniversal;
		}

		if (changedUniversal)
			for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList)
				delegate.notifyChange();
	}

	public void mouseReleased(int mouseX, int mouseY, int eventButton) {
		boolean changedUniversal = false;

		for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList) {
			ElementPos pos = delegate.getPosition();
			IOverlayElement<?> element = delegate.getElement();
			int width = element.getWidth();
			int height = element.getHeight();
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= element.animationOffsetX(0.0f);
			scaledMouseY -= element.animationOffsetY(0.0f);

			if (element.mouseReleased(scaledMouseX, scaledMouseY, eventButton))
				delegate.notifyChange();

			if (delegate.getHandler() != null)
				changedUniversal = delegate.getHandler().mouseReleased(mouseX, mouseY, eventButton) || changedUniversal;
		}

		if (changedUniversal)
			for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList)
				delegate.notifyChange();
	}

	public void keyTyped(char eventChar, int eventKey) {
		boolean changedUniversal = false;

		for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList) {
			if (delegate.getElement().keyTyped(eventChar, eventKey))
				delegate.notifyChange();

			if (delegate.getHandler() != null)
				changedUniversal = delegate.getHandler().keyTyped(eventChar, eventKey) || changedUniversal;
		}

		if (changedUniversal)
			for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList)
				delegate.notifyChange();
	}

	public void render(int mouseX, int mouseY, float partialTicks) {
		for (OverlayElementDelegate<?, ?> delegate : this.currentlyDisplayedList) {
			ElementPos pos = delegate.getPosition();
			IOverlayElement<?> element = delegate.getElement();
			int width = element.getWidth();
			int height = element.getHeight();
			float animationOffsetX = element.animationOffsetX(partialTicks);
			float animationOffsetY = element.animationOffsetY(partialTicks);

			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= animationOffsetX;
			scaledMouseY -= animationOffsetY;

			GlStateManager.pushMatrix();
			GlStateManager.translate((pos.getHorizontalPos().getOffset(this.width, width) + animationOffsetX),
					(pos.getVerticalPos().getOffset(this.height, height) + animationOffsetY), 0.0f);

			element.render(scaledMouseX, scaledMouseY, partialTicks);
			GlStateManager.popMatrix();

			if (delegate.getHandler() != null)
				delegate.getHandler().render(mouseX, mouseY, partialTicks);
		}

		// Refreshes color for next elements to be rendered correctly.
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public boolean isGamePaused() {
		return this.pauseGame;
	}

	public void setGamePaused(boolean pause) {
		if (currentMode.displayed())
			this.pauseGame = pause;
	}
}
