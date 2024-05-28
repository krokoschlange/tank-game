/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client.ui;

import blackhole.client.game.ClientManager;
import blackhole.client.game.input.InputEvent;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.awt.Font;

/**
 *
 * @author fabian
 */
public class Entry extends Widget {

	private GraphicsBackend backend;

	private TextElement text;

	public Entry(UI ui) {
		super(ui);
		backend = ClientManager.getInstance().getGraphicsBackend();

		text = new TextElement(this);
	}

	public void setFont(Font f) {
		text.setFont(f);
	}

	public Font getFont() {
		return text.getFont();
	}

	public void setText(String t) {
		text.setText(t);
	}

	public String getText() {
		return text.getText();
	}

	public TextElement getTextElement() {
		return text;
	}

	@Override
	public boolean handle(InputEvent event) {
		boolean ret = super.handle(event);
		if (event.isType(InputEvent.TYPED) && getRoot().hasFocus(this)) {
			String txt = text.getText();
			int cursorPos = txt.length();
			if (text.getCursorPos() != null) {
				cursorPos = text.getCursorPos();
			}
			StringBuilder sb = new StringBuilder(txt);
			int newCursorPos;
			switch (event.character) {
				case 8:
					if (cursorPos > 0) {
						sb.delete(cursorPos - 1, cursorPos);
					}
					newCursorPos = cursorPos - 1;
					break;
				case 10:
				case 12:
				case 13:
					newCursorPos = cursorPos;
					break;
				default:
					sb.insert(cursorPos, event.character);
					newCursorPos = cursorPos + 1;
					break;
			}

			text.setText(sb.toString());

			if (text.getCursorPos() != null) {
				text.setCursorPos(newCursorPos);
			}
			ret = true;
		} else if (event.isType(InputEvent.KEY) && getRoot().hasFocus(this) && event.active) {
			if (event.key == 37) {
				text.setCursorPos(text.getCursorPos() - 1);
			}
			if (event.key == 38) {
				Vector cPos = text.posFromTextPos(text.getCursorPos());
				cPos.set(cPos.x(), cPos.y() - text.getLineHeight());
				text.setCursorPos(text.textPosFromPos(cPos));
			}
			if (event.key == 39) {
				text.setCursorPos(text.getCursorPos() + 1);
			}
			if (event.key == 40) {
				Vector cPos = text.posFromTextPos(text.getCursorPos());
				cPos.set(cPos.x(), cPos.y() + text.getLineHeight());
				text.setCursorPos(text.textPosFromPos(cPos));
			}
		} else if (event.isType(InputEvent.BUTTON)) {
			if (isTouching(event.mousePos) && event.active) {
				Vector clickPos = Vector.subtract(event.mousePos, getScreenPos()).subtract(text.getScrollPos());
				text.setCursorPos(text.textPosFromPos(clickPos));
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void draw() {
		AbstractDrawer drawer = backend.getDrawer();
		Vector fbSize = drawer.getDrawbufferSize();

		Vector p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
		Vector p3 = Vector.add(p1, getScreenSize());
		Vector p2 = new Vector(p1.x(), p3.y());
		Vector p4 = new Vector(p3.x(), p1.y());

		float[] bg = getTheme().getBackground().getComponents(null);

		drawer.drawPolygon(bg[0], bg[1], bg[2], bg[3], true, p1, p2, p3, p4);

		float[] txt = getTheme().getContrast().getComponents(null);

		text.draw();
	}

}
