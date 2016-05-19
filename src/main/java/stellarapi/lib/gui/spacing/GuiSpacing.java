package stellarapi.lib.gui.spacing;

import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.GuiPositionHierarchy;
import stellarapi.lib.gui.IGuiElementType;
import stellarapi.lib.gui.IGuiPosition;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.RectangleBound;

public class GuiSpacing implements IGuiElementType<ISpacingController> {

	private IGuiPosition position;
	private ISpacingController controller;
	private GuiElement subElement;
	
	public GuiSpacing(GuiElement subElement) {
		this.subElement = subElement;
	}
	
	@Override
	public void initialize(GuiPositionHierarchy positions, ISpacingController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		subElement.initialize(positions.addChild(new SpacedPosition()));
	}

	@Override
	public void updateElement() {
		subElement.getType().updateElement();
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		subElement.getType().mouseClicked(mouseX, mouseY, eventButton);
	}
	

	@Override
	public void mouseClickMove(float mouseX, float mouseY, int eventButton, long timeSinceLastClick) {
		subElement.getType().mouseClickMove(mouseX, mouseY, eventButton, timeSinceLastClick);
	}

	@Override
	public void mouseReleased(float mouseX, float mouseY, int eventButton) {
		subElement.getType().mouseReleased(mouseX, mouseY, eventButton);
	}

	@Override
	public void keyTyped(char eventChar, int eventKey) {
		subElement.getType().keyTyped(eventChar, eventKey);
	}

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		subElement.getType().checkMousePosition(mouseX, mouseY);
	}
	
	@Override
	public void render(IRenderer renderer) {
		IRectangleBound clipBound = position.getClipBound();
		if(clipBound.isEmpty())
			return;
		
		renderer.startRender();
		String model = controller.setupSpacingRenderer(renderer);
		if(model != null)
			renderer.render(model, position.getElementBound(), clipBound);
		renderer.endRender();
		subElement.getType().render(renderer);
	}

	public class SpacedPosition implements IGuiPosition {
		private RectangleBound element, clip;

		@Override
		public IRectangleBound getElementBound() {
			return this.element;
		}

		@Override
		public IRectangleBound getClipBound() {
			return this.clip;
		}

		@Override
		public IRectangleBound getAdditionalBound(String boundName) {
			return position.getAdditionalBound(boundName);
		}

		@Override
		public void initializeBounds() {
			this.element = new RectangleBound(position.getElementBound());
			this.clip = new RectangleBound(position.getClipBound());
			element.extend(-controller.getSpacingLeft(), -controller.getSpacingUp(),
					-controller.getSpacingRight(), -controller.getSpacingDown());
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateBounds() {
			element.set(position.getElementBound());
			clip.set(position.getClipBound());
			element.extend(-controller.getSpacingLeft(), -controller.getSpacingUp(),
					-controller.getSpacingRight(), -controller.getSpacingDown());
			clip.setAsIntersection(this.element);
		}

		@Override
		public void updateAnimation(float partialTicks) {
			this.updateBounds();
		}

	}

}
