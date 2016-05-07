package stellarapi.api.gui.content.simple;

import stellarapi.api.gui.content.GuiPositionHierarchy;
import stellarapi.api.gui.content.IGuiElementType;
import stellarapi.api.gui.content.IRenderer;

public class GuiEmptyElement implements IGuiElementType<ISimpleController> {
	
	@Override
	public void initialize(GuiPositionHierarchy positions, ISimpleController controller) { }

	@Override
	public void updateElement() { }

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) { }

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) { }

	@Override
	public void keyTyped(char eventChar, int eventKey) { }

	@Override
	public void checkMousePosition(float mouseX, float mouseY) { }
	
	@Override
	public void render(IRenderer renderer) { }

}
