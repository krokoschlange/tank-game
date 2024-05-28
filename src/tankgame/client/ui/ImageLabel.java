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
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.client.graphicsEngine.GameDrawable;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.awt.Font;
import static java.awt.SystemColor.text;
import java.awt.geom.Rectangle2D;
import static javafx.scene.text.Font.font;

/**
 *
 * @author fabian
 */
public class ImageLabel extends Widget {

	private GameDrawable drawable;

	public enum DrawMode {
		STRETCH,
		RESIZE_MIN,
		RESIZE_MAX
	}

	private DrawMode drawMode;

	public ImageLabel(UI ui) {
		super(ui);
		drawable = null;
		drawMode = DrawMode.STRETCH;
	}

	public void setDrawable(GameDrawable d) {
		drawable = d;
	}

	public GameDrawable getDrawable() {
		return drawable;
	}

	public void setDrawMode(DrawMode dm) {
		drawMode = dm;
	}

	public DrawMode getDrawMode() {
		return drawMode;
	}

	@Override
	public void draw() {
		if (drawable != null) {
			AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();
			Vector fbSize = drawer.getDrawbufferSize();

			Vector p1 = null;
			Vector p3 = null;
			if (drawMode == DrawMode.STRETCH) {
				p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
				p3 = Vector.add(p1, getScreenSize());
			} else {
				double imgRatio = 1. * drawable.getHeight() / drawable.getWidth();
				Vector screenSize = getScreenSize();
				double sizeRatio = screenSize.y() / screenSize.x();

				if (drawMode == DrawMode.RESIZE_MIN) {
					if (imgRatio < sizeRatio) {
						double height = screenSize.x() * imgRatio;

						p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
						p1.set(p1.x(), p1.y() + (screenSize.y() - height) / 2);

						p3 = new Vector(p1.x() + screenSize.x(), p1.y() + height);
					} else {
						double width = screenSize.y() / imgRatio;

						p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
						p1.set(p1.x() + (screenSize.x() - width) / 2, p1.y());

						p3 = new Vector(p1.x() + width, p1.y() + screenSize.y());
					}
				} else if (drawMode == DrawMode.RESIZE_MAX) {
					if (imgRatio < sizeRatio) {
						double width = screenSize.y() / imgRatio;

						p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
						p1.set(p1.x() + (screenSize.x() - width) / 2, p1.y());

						p3 = new Vector(p1.x() + width, p1.y() + screenSize.y());
					} else {
						double height = screenSize.x() * imgRatio;

						p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
						p1.set(p1.x(), p1.y() + (screenSize.y() - height) / 2);

						p3 = new Vector(p1.x() + screenSize.x(), p1.y() + height);
					}
				}
			}
			if (p1 != null && p3 != null) {
				drawer.drawTexture(drawable, 1, p1.x(), p1.y(), p3.x(), p3.y());
			}
		}
	}
}
