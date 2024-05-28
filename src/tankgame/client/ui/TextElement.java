/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client.ui;

import blackhole.client.game.ClientManager;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.AbstractFramebuffer;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public class TextElement {

	private Widget widget;

	private AbstractFramebuffer buffer;

	private String text;

	private Vector scrollPos;

	private Integer cursorPos;

	private double relativeFontSize;

	private Font font;

	private boolean centerText;

	private double lineHeight;

	public TextElement(Widget w) {
		widget = w;
		scrollPos = new Vector();
		cursorPos = null;
		relativeFontSize = -1;
		font = new Font("Helvetica", 0, 10);
		text = " ";
	}

	public void setFont(Font f) {
		font = f;
	}

	public Font getFont() {
		return font;
	}

	public void setText(String t) {
		text = t;
	}

	public String getText() {
		return text;
	}

	public void setScrollPos(Vector pos) {
		scrollPos = pos;
	}

	public Vector getScrollPos() {
		return scrollPos;
	}

	public void setCursorPos(Integer pos) {
		if (pos < 0) {
			pos = 0;
		} else if (pos > text.length()) {
			pos = text.length();
		}

		cursorPos = pos;

		if (cursorPos != null && buffer != null) {
			Vector screenPos = posFromTextPos(cursorPos);
			Vector screenPos2 = posFromTextPos(cursorPos - 1);

			if (screenPos.x() > buffer.getWidth() - scrollPos.x()) {
				scrollPos.set(-screenPos.x() + buffer.getWidth(), scrollPos.y());
			} else if (screenPos2.x() < -scrollPos.x()) {

				scrollPos.set(-screenPos2.x(), scrollPos.y());
			}
			if (screenPos.y() + lineHeight > buffer.getHeight() - scrollPos.y()) {
				scrollPos.set(scrollPos.x(), -screenPos.y() + buffer.getHeight() - lineHeight);
			} else if (screenPos.y() < -scrollPos.y()) {
				scrollPos.set(scrollPos.x(), -screenPos.y());
			}
		}
	}

	public Integer getCursorPos() {
		return cursorPos;
	}

	public void setRelativeFontSize(double size) {
		relativeFontSize = size;
	}

	public double getRelativeFontSize() {
		return relativeFontSize;
	}

	public void setCenterText(boolean state) {
		centerText = state;
	}

	public boolean isCenterText() {
		return centerText;
	}
	
	public double getLineHeight() {
		return lineHeight;
	}

	public void draw() {
		Font f = getDrawFont();
		Vector widgetSize = widget.getScreenSize();

		if (f.getSize() <= 0) {
			return;
		}

		AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();

		AbstractFramebuffer fb = drawer.getDrawBuffer();

		if (buffer == null) {
			buffer = drawer.createFramebuffer((int) widgetSize.x(),
					(int) widgetSize.y());
		} else if (buffer.getWidth() != (int) widgetSize.x()
				|| buffer.getHeight() != (int) widgetSize.y()) {
			drawer.destroyFramebuffer(buffer);
			buffer = drawer.createFramebuffer((int) widgetSize.x(),
					(int) widgetSize.y());
		}

		lineHeight = drawer.getLineHeight(f);

		Vector textPos = new Vector(scrollPos);
		textPos.set(textPos.x() - buffer.getWidth() / 2,
				textPos.y() + lineHeight - drawer.getDescent(f) - buffer.getHeight() / 2);

		if (centerText) {
			Rectangle2D bounds = drawer.getTextExtents(text, f);
			double dx = (buffer.getWidth() - bounds.getWidth()) / 2;
			double dy = (buffer.getHeight() - bounds.getHeight()) / 2;
			textPos.add(new Vector(dx, dy));
		}

		String[] lines = text.split("\\r?\\n|\\r");

		drawer.setDrawBuffer(buffer);

		drawer.clear(0, 0, 0, 0);
		for (int i = 0; i < lines.length; i++) {
			drawer.drawString(lines[i], textPos, f, 0, 0, 0, 1, true);
			textPos.set(textPos.x(), textPos.y() + lineHeight);
		}

		if (cursorPos != null && widget.getRoot().hasFocus(widget)) {
			Vector cPos = posFromTextPos(cursorPos);//.add(new Vector(buffer.getWidth(), buffer.getHeight()).divide(2));
			cPos.set(cPos.x() - buffer.getWidth() / 2, cPos.y() - buffer.getHeight() / 2);
			cPos.add(scrollPos);
			double cWidth = 2;//drawer.getLineHeight(f) / 10;
			Vector[] cursor = {
				new Vector(cPos.x() - cWidth / 2, cPos.y()),
				new Vector(cPos.x() + cWidth / 2, cPos.y()),
				new Vector(cPos.x() + cWidth / 2, cPos.y() + lineHeight),
				new Vector(cPos.x() - cWidth / 2, cPos.y() + lineHeight)
			};
			drawer.drawPolygon(0, 0, 0, 1, true, cursor);
		}

		drawer.setDrawBuffer(fb);

		Vector fbSize = drawer.getDrawbufferSize().divide(2);
		Vector p1 = widget.getScreenPos().subtract(fbSize);
		Vector p2 = Vector.add(widget.getScreenPos(), widgetSize).subtract(fbSize);

		drawer.drawTexture(buffer.toTexture(), 1, p1.x(), p1.y(), p2.x(), p2.y());
	}

	public Font getDrawFont() {
		Font f;
		Vector widgetSize = widget.getScreenSize();
		if (relativeFontSize > 0) {
			f = new Font(font.getName(), font.getStyle(),
					(int) (widgetSize.y() * relativeFontSize));
		} else {
			f = font;
		}
		return f;
	}

	public int textPosFromPos(Vector pos) {
		AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();

		Font f = getDrawFont();

		int line = (int) (pos.y() / lineHeight);

		int textPos = 0;
		for (int i = 0; i < line && textPos < text.length(); i++) {
			while (text.charAt(textPos) != '\n' && textPos < text.length() - 1) {
				textPos++;
			}
			textPos++;
		}

		if (textPos >= text.length()) {
			return text.length();
		}

		double xTextPos = 0;
		double lastCharWidth = 0;
		while (xTextPos < pos.x() && textPos < text.length() && text.charAt(textPos) != '\n') {
			lastCharWidth = drawer.getCharacterWidth(text.charAt(textPos), f);
			xTextPos += lastCharWidth;
			textPos++;
		}
		if (xTextPos >= pos.x() && xTextPos - pos.x() > lastCharWidth / 2) {
			textPos--;
		}
		return textPos;
	}

	public Vector posFromTextPos(int textPos) {
		AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();

		Font f = getDrawFont();

		if (textPos > text.length()) {
			textPos = text.length();
		}

		Vector pos = new Vector();

		for (int i = 0; i < textPos; i++) {
			if (text.charAt(i) != '\n') {
				pos.set(pos.x() + drawer.getCharacterWidth(text.charAt(i), f), pos.y());
			} else {
				pos.set(0, pos.y() + drawer.getLineHeight(f));
			}
		}
		return pos;
	}
}
