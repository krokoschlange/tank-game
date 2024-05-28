/* 
 * The MIT License
 *
 * Copyright 2020 fabian.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tankgame.client.ui;

import blackhole.client.game.ClientManager;
import blackhole.client.game.input.InputEvent;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author fabian
 */
public class Button extends Widget {

	private boolean state;

	/*private Font font;
	private Font useFont;
	private String text;
	private String useText;*/
	
	private TextElement text;
	
	private GraphicsBackend backend;

	private UIEvent pressEvent;
	private UIEvent releaseEvent;

	public Button(UI ui) {
		super(ui);
		backend = ClientManager.getInstance().getGraphicsBackend();
		/*font = new Font("Helvetica", 0, 20);
		text = "";
		useFont = font;
		useText = text;*/
		text = new TextElement(this);
		text.setCenterText(true);
		text.setRelativeFontSize(0.8);
	}

	public void setFont(Font f) {
		/*font = f;
		useFont = font;
		useText = text;*/
		text.setFont(f);
	}

	public Font getFont() {
		//return font;
		return text.getFont();
	}

	public void setText(String t) {
		/*text = t;
		useText = t;
		useFont = font;*/
		text.setText(t);
	}

	public String getText() {
		//return text;
		return text.getText();
	}
	
	public TextElement getTextElement() {
		return text;
	}

	public void setPressEvent(UIEvent e) {
		pressEvent = e;
	}

	public void setReleaseEvent(UIEvent e) {
		releaseEvent = e;
	}

	@Override
	public boolean handle(InputEvent event) {
		super.handle(event);
		if (event.isType(InputEvent.BUTTON) && event.button == 1) {
			if (isTouching(event.mousePos) && event.active) {
				state = true;
				if (pressEvent != null) {
					pressEvent.process();
				}
				return true;
			} else if(state && isTouching(event.mousePos)) {
				if (releaseEvent != null) {
					releaseEvent.process();
				}
				state = false;
				return true;
			} else {
				state = false;
			}
		}
		return false;

	}

	@Override
	public void draw() {
		AbstractDrawer drawer = backend.getDrawer();
		Vector fbSize = drawer.getDrawbufferSize();

		Vector p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
		Vector p3 = Vector.add(p1, getScreenSize());
		Vector p2 = new Vector(p1.x(), p3.y());
		Vector p4 = new Vector(p3.x(), p1.y());

		float[] bg;
		if (state) {
			bg = getTheme().getHighlight().getComponents(null);
		} else {
			bg = getTheme().getBackground().getComponents(null);
		}
		drawer.drawPolygon(bg[0], bg[1], bg[2], bg[3], true, p1, p2, p3, p4);

		float[] txt = getTheme().getContrast().getComponents(null);

		text.draw();
		/*Rectangle2D layout = drawer.getTextExtents(useText, useFont);

		int fontSize = useFont.getSize();
		while (getScreenSize().y() < layout.getHeight() + layout.getY() && fontSize >= 0) {
			fontSize--;
			useFont = new Font(font.getName(), font.getStyle(), fontSize);
			layout = drawer.getTextExtents(text, useFont);
		}

		if (fontSize > 0) {
			while (getScreenSize().x() < layout.getWidth() && useText.length() >= 3) {
				useText = useText.substring(0, useText.length() - 3) + "..";
				layout = drawer.getTextExtents(useText, useFont);
			}

			if (getScreenSize().x() > layout.getWidth()) {
				Vector textSize = new Vector(layout.getWidth(), layout.getHeight());

				Vector offset = Vector.subtract(getScreenSize(),
						textSize).divide(2);
				offset.set(offset.x(), -offset.y());

				Vector textPos = Vector.add(p2, offset);

				textPos.set(textPos.x(), textPos.y() - layout.getY());
				drawer.drawString(useText, textPos, useFont, txt[0], txt[1], txt[2], txt[3], true);
			}
		}*/
	}

	/*@Override
	public void setSize(Vector s) {
		super.setSize(s);
		useFont = font;
		useText = text;
	}*/

}
