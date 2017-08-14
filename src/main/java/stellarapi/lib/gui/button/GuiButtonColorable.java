package stellarapi.lib.gui.button;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class GuiButtonColorable extends GuiButton {

	public float red = 1.0f;
	public float green = 1.0f;
	public float blue = 1.0f;
	public float alpha = 1.0f;

	public GuiButtonColorable(int id, int xPos, int yPos, int width, int height, String displayString) {
		super(id, xPos, yPos, width, height, displayString);
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
		if (this.visible) {
			FontRenderer fontrenderer = p_146112_1_.fontRenderer;
			p_146112_1_.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(red, green, blue, alpha);
			this.hovered = p_146112_2_ >= this.x && p_146112_3_ >= this.y
					&& p_146112_2_ < this.x + this.width && p_146112_3_ < this.y + this.height;
			int k = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2,
					46 + k * 20, this.width / 2, this.height);
			this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
			int l = 14737632;

			if (packedFGColour != 0) {
				l = packedFGColour;
			} else if (!this.enabled) {
				l = 10526880;
			} else if (this.hovered) {
				l = 16777120;
			}

			int alpha = 0xff;
			int red = ((l >> 16) & 0xff);
			int green = ((l >> 8) & 0xff);
			int blue = l & 0xff;
			alpha *= MathHelper.clamp(this.alpha, 0.0f, 1.0f);
			red *= this.red;
			green *= this.green;
			blue *= this.blue;

			l = ((alpha << 24) | (red << 16) | (green << 8) | blue);
			if (alpha > 3)
				this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2,
						this.y + (this.height - 8) / 2, l);
		}
	}

}
