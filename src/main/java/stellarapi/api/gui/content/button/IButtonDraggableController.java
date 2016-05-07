package stellarapi.api.gui.content.button;

import stellarapi.api.gui.content.IElementController;
import stellarapi.api.gui.content.IRenderer;

public interface IButtonDraggableController extends IElementController {
	
	public boolean canClick(int eventButton);

	public void onDragStart(int eventButton, float dragRatioX, float dragRatioY);
	public void onDragging(float dragRatioX, float dragRatioY);
	public void onDragEnded(int eventButton, float dragRatioX, float dragRatioY);

	public void setupButton(boolean mouseOver, IRenderer renderer);
	public String setupOverlay(boolean mouseOver, IRenderer renderer);
	public String setupMain(boolean mouseOver, IRenderer renderer);

}
