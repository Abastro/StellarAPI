package stellarapi.lib.gui;

/**
 * Main container GUI for elements.
 * */
public class GuiContent {
	private IRenderer renderer;
	private GuiElement element;
	private IGuiPosition position;
	private GuiPositionHierarchy positions;
	
	public GuiContent(IRenderer renderer, GuiElement element, IGuiPosition position) {
		this.renderer = renderer;
		this.element = element;
		this.position = position;
	}
	
	/**
	 * Initialize(Or re-initialize) the elements.
	 * */
	public void initialize() {
		this.positions = new GuiPositionHierarchy(this.position);
		
		element.initialize(this.positions);
		positions.initializeBounds();
	}
	
	/**Update every tick*/
	public void updateTick() {
		positions.updateBounds();
		element.getType().updateElement();
	}
	
	/**On mouse clicked*/
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		element.getType().mouseClicked(mouseX, mouseY, eventButton);
	}

	/**On mouse moved or up*/
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		element.getType().mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	/**On key typed*/
	public void keyTyped(char eventChar, int eventKey) {
		element.getType().keyTyped(eventChar, eventKey);
	}

	/**On render*/
	public void render(float mouseX, float mouseY, float partialTicks) {
		element.getType().checkMousePosition(mouseX, mouseY);
		positions.updateAnimation(partialTicks);
		
		renderer.preRender(partialTicks);
		element.getType().render(this.renderer);
		renderer.postRender(partialTicks);
	}

}
