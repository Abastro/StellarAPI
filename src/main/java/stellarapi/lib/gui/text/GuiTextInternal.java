package stellarapi.lib.gui.text;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import stellarapi.lib.gui.GuiPositionHierarchy;
import stellarapi.lib.gui.IFontHelper;
import stellarapi.lib.gui.IGuiElementType;
import stellarapi.lib.gui.IGuiPosition;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.RectangleBound;

/**
 * Internal for text field, which resembles one from GuiTextField.
 */
public class GuiTextInternal implements IGuiElementType<ITextInternalController> {

	private IGuiPosition position;
	private ITextInternalController controller;

	private String text = "";
	private int cursorPosition, selectionEnd;
	private int cursorCounter;
	private boolean focused, dragging;
	private RectangleBound tempBound = new RectangleBound(0, 0, 0, 0);
	private RectangleBound tempClipBound = new RectangleBound(0, 0, 0, 0);

	@Override
	public void initialize(GuiPositionHierarchy positions, ITextInternalController controller) {
		this.position = positions.getPosition();
		this.controller = controller;
		this.focused = !controller.canLoseFocus();
		this.dragging = false;
	}

	@Override
	public void updateElement() {
		this.cursorCounter++;
		if (controller.notifyText(this.text, this.cursorPosition, this.selectionEnd, this.focused))
			this.focused = false;

		if (!this.focused || !controller.canModify()) {
			String updated = controller.updateText(this.text);
			if (!text.equals(updated)) {
				int max = controller.maxStringLength();
				if (updated.length() > max)
					this.text = updated.substring(0, max);
				else
					this.text = updated;
				this.setCursorPositionEnd();
			}
		}
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		if (controller.canLoseFocus())
			this.setFocused(position.getClipBound().isInBound(mouseX, mouseY));

		if (this.focused && eventButton == 0) {
			float xPos = mouseX - position.getElementBound().getLeftX();

			String trimmed = controller.getFontHelper().trimStringToWidth(this.text, xPos);
			this.setCursorPosition(trimmed.length());

			this.dragging = true;
		}
	}

	@Override
	public void mouseClickMove(float mouseX, float mouseY, int eventButton, long timeSinceLastClick) {
		if (this.dragging) {
			float xPos = mouseX - position.getElementBound().getLeftX();

			String trimmed = controller.getFontHelper().trimStringToWidth(this.text, xPos);
			this.setSelectionPos(trimmed.length());
		}
	}

	@Override
	public void mouseReleased(float mouseX, float mouseY, int eventButton) {
		if (this.focused && eventButton == 0) {
			float xPos = mouseX - position.getElementBound().getLeftX();

			String trimmed = controller.getFontHelper().trimStringToWidth(this.text, xPos);
			this.setSelectionPos(trimmed.length());
			this.dragging = false;
		}
	}

	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
	}

	@Override
	public void keyTyped(char eventChar, int eventKey) {
		if (!this.focused)
			return;
		else {
			switch (eventChar) {
			case 1:
				this.setCursorPositionEnd();
				this.setSelectionPos(0);
				break;
			case 3:
				GuiScreen.setClipboardString(this.getSelectedText());
				break;
			case 22:
				if (controller.canModify())
					this.writeText(GuiScreen.getClipboardString());
				break;
			case 24:
				GuiScreen.setClipboardString(this.getSelectedText());
				if (controller.canModify())
					this.writeText("");
				break;
			default:
				switch (eventKey) {
				case 14:
					if (GuiScreen.isCtrlKeyDown()) {
						if (controller.canModify())
							this.deleteWords(-1);
					} else if (controller.canModify())
						this.deleteFromCursor(-1);
					break;
				case 199:
					if (GuiScreen.isShiftKeyDown())
						this.setSelectionPos(0);
					else
						this.setCursorPositionZero();
					break;
				case 203:
					if (GuiScreen.isShiftKeyDown())
						if (GuiScreen.isCtrlKeyDown())
							this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
						else
							this.setSelectionPos(this.getSelectionEnd() - 1);
					else if (GuiScreen.isCtrlKeyDown())
						this.setCursorPosition(this.getNthWordFromCursor(-1));
					else
						this.moveCursorBy(-1);
					break;
				case 205:
					if (GuiScreen.isShiftKeyDown())
						if (GuiScreen.isCtrlKeyDown())
							this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
						else
							this.setSelectionPos(this.getSelectionEnd() + 1);
					else if (GuiScreen.isCtrlKeyDown())
						this.setCursorPosition(this.getNthWordFromCursor(1));
					else
						this.moveCursorBy(1);
					break;
				case 207:
					if (GuiScreen.isShiftKeyDown())
						this.setSelectionPos(this.text.length());
					else
						this.setCursorPositionEnd();
					break;
				case 211:
					if (GuiScreen.isCtrlKeyDown()) {
						if (controller.canModify())
							this.deleteWords(1);
					} else if (controller.canModify())
						this.deleteFromCursor(1);
					break;
				default:
					if (ChatAllowedCharacters.isAllowedCharacter(eventChar))
						if (controller.canModify())
							this.writeText(Character.toString(eventChar));
				}
			}
		}
	}

	@Override
	public void render(IRenderer renderer) {
		IRectangleBound elementBound = position.getElementBound();
		IRectangleBound clipBound = position.getClipBound();

		if (clipBound.isEmpty())
			return;

		renderer.startRender();

		if (this.focused) {
			IFontHelper helper = controller.getFontHelper();

			controller.setupRendererFocused(renderer);

			int first = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
			int second = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;

			if (first == second) {
				tempBound.set(elementBound);
				tempBound.width = helper.getStringWidth(this.text);
				tempClipBound.set(clipBound);
				tempClipBound.setAsIntersection(this.tempBound);
				tempBound.posY += (tempBound.height - helper.getStringHeight()) / 2;
				tempBound.height = helper.getStringHeight();

				if (!tempClipBound.isEmpty()) {
					controller.setupText(this.text, renderer);
					renderer.render(this.text, this.tempBound, clipBound);
				}
			} else {
				if (first > 0) {
					String pre = text.substring(0, first);
					tempBound.set(elementBound);
					tempBound.width = helper.getStringWidth(pre);
					tempBound.posY += (tempBound.height - helper.getStringHeight()) / 2;
					tempBound.height = helper.getStringHeight();

					tempClipBound.set(clipBound);
					tempClipBound.setAsIntersection(this.tempBound);

					if (!tempClipBound.isEmpty()) {
						controller.setupText(this.text, renderer);
						renderer.render(pre, this.tempBound, this.tempClipBound);
					}
				}

				if (second < text.length()) {
					String pre = text.substring(0, second);
					String post = text.substring(second);
					tempBound.set(elementBound);
					tempBound.posX += helper.getStringWidth(pre);
					tempBound.width = helper.getStringWidth(post);
					tempBound.posY += (tempBound.height - helper.getStringHeight()) / 2;
					tempBound.height = helper.getStringHeight();

					tempClipBound.set(clipBound);
					tempClipBound.setAsIntersection(this.tempBound);

					if (!tempClipBound.isEmpty()) {
						controller.setupText(this.text, renderer);
						renderer.render(post, this.tempBound, this.tempClipBound);
					}
				}

				String pre = text.substring(0, first);
				String selection = text.substring(first, second);

				tempBound.set(elementBound);
				tempBound.posX += helper.getStringWidth(pre);
				tempBound.width = helper.getStringWidth(selection);
				tempBound.posY += (tempBound.height - helper.getStringHeight()) / 2;
				tempBound.height = helper.getStringHeight();

				tempClipBound.set(clipBound);
				tempClipBound.setAsIntersection(this.tempBound);

				if (!tempClipBound.isEmpty()) {
					controller.setupHighlightedText(selection, renderer);
					renderer.render(selection, this.tempBound, this.tempClipBound);

					String overlay = controller.setupHighlightedOverlay(selection, renderer);
					if (overlay != null)
						renderer.render(overlay, this.tempBound, this.tempClipBound);
				}
			}

			String pre = text.substring(0, this.cursorPosition);

			float cursorSpacing = controller.getCursorSpacing();
			tempBound.set(elementBound);
			tempBound.posX += helper.getStringWidth(pre) - cursorSpacing;
			tempBound.width = cursorSpacing * 2;
			tempBound.posY += (tempBound.height - helper.getStringHeight()) / 2 - cursorSpacing;
			tempBound.height = helper.getStringHeight() + 2 * cursorSpacing;

			tempClipBound.set(clipBound);
			tempClipBound.setAsIntersection(this.tempBound);

			String cursor = controller.setupRendererCursor(this.cursorCounter, renderer);

			renderer.render(cursor, this.tempBound, this.tempClipBound);
		} else {
			String replacement = controller.setupRendererUnfocused(this.text, renderer);
			renderer.render(replacement, elementBound, clipBound);
		}

		renderer.endRender();
	}

	public void setFocused(boolean focusToUpdate) {
		if (focusToUpdate && !this.focused)
			this.cursorCounter = 0;

		this.focused = focusToUpdate;
	}

	/**
	 * Getter for the focused field
	 */
	public boolean isFocused() {
		return this.focused;
	}

	/**
	 * returns the text between the cursor and selectionEnd
	 */
	public String getSelectedText() {
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.text.substring(i, j);
	}

	/**
	 * replaces selected text, or inserts text at the position on the cursor
	 */
	public void writeText(String input) {
		String s1 = "";
		String s2 = ChatAllowedCharacters.filterAllowedCharacters(input);
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		int k = controller.maxStringLength() - this.text.length() - (i - this.selectionEnd);

		if (this.text.length() > 0) {
			s1 = s1 + this.text.substring(0, i);
		}

		int l;

		if (k < s2.length()) {
			s1 = s1 + s2.substring(0, k);
			l = k;
		} else {
			s1 = s1 + s2;
			l = s2.length();
		}

		if (this.text.length() > 0 && j < this.text.length()) {
			s1 = s1 + this.text.substring(j);
		}

		this.text = s1;
		this.moveCursorBy(i - this.selectionEnd + l);
	}

	/**
	 * Deletes the specified number of words starting at the cursor position.
	 * Negative numbers will delete words left of the cursor.
	 */
	public void deleteWords(int numberOfWords) {
		if (this.text.length() != 0) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				this.deleteFromCursor(this.getNthWordFromCursor(numberOfWords) - this.cursorPosition);
			}
		}
	}

	/**
	 * delete the selected text, otherwise deletes characters from either side
	 * of the cursor. params: delete num
	 */
	public void deleteFromCursor(int fromCursor) {
		if (this.text.length() != 0) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				boolean flag = fromCursor < 0;
				int j = flag ? this.cursorPosition + fromCursor : this.cursorPosition;
				int k = flag ? this.cursorPosition : this.cursorPosition + fromCursor;
				String s = "";

				if (j >= 0) {
					s = this.text.substring(0, j);
				}

				if (k < this.text.length()) {
					s = s + this.text.substring(k);
				}

				this.text = s;

				if (flag) {
					this.moveCursorBy(fromCursor);
				}
			}
		}
	}

	/**
	 * see @getNthNextWordFromPos() params: N, position
	 */
	public int getNthWordFromCursor(int N) {
		return this.getNthWordFromPos(N, this.getCursorPosition());
	}

	/**
	 * gets the position of the nth word. N may be negative, then it looks
	 * backwards. params: N, position
	 */
	public int getNthWordFromPos(int N, int position) {
		return this.getNthWordFromPos(N, this.getCursorPosition(), true);
	}

	public int getNthWordFromPos(int N, int position, boolean flag) {

		int k = position;
		boolean flag1 = N < 0;
		int l = Math.abs(N);

		for (int i1 = 0; i1 < l; ++i1) {
			if (flag1) {
				while (flag && k > 0 && text.charAt(k - 1) == 32)
					--k;

				while (k > 0 && text.charAt(k - 1) != 32)
					--k;
			} else {
				int j1 = this.text.length();
				k = this.text.indexOf(32, k);

				if (k == -1)
					k = j1;
				else
					while (flag && k < j1 && this.text.charAt(k) == 32)
						++k;
			}
		}

		return k;
	}

	/**
	 * Moves the text cursor by a specified number of characters and clears the
	 * selection
	 */
	public void moveCursorBy(int p_146182_1_) {
		this.setCursorPosition(this.selectionEnd + p_146182_1_);
	}

	/**
	 * sets the position of the cursor to the provided index
	 */
	public void setCursorPosition(int p_146190_1_) {
		this.cursorPosition = MathHelper.clamp(p_146190_1_, 0, text.length());
		this.setSelectionPos(this.cursorPosition);
	}

	/**
	 * sets the cursors position to the beginning
	 */
	public void setCursorPositionZero() {
		this.setCursorPosition(0);
	}

	/**
	 * sets the cursors position to after the text
	 */
	public void setCursorPositionEnd() {
		this.setCursorPosition(this.text.length());
	}

	/**
	 * returns the current position of the cursor
	 */
	public int getCursorPosition() {
		return this.cursorPosition;
	}

	/**
	 * the side of the selection that is not the cursor, may be the same as the
	 * cursor
	 */
	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	/**
	 * Sets the position of the selection anchor (i.e. position the selection
	 * was started at)
	 */
	public void setSelectionPos(int selectionPos) {
		selectionPos = MathHelper.clamp(selectionPos, 0, text.length());
		this.selectionEnd = selectionPos;

		controller.notifySelection(this.cursorPosition, this.selectionEnd);
	}

}
