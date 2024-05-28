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

import blackhole.client.game.input.InputEvent;
import blackhole.client.game.ClientManager;
import blackhole.client.game.ClientObject;
import blackhole.client.game.DrawStrategy;
import blackhole.client.graphicsEngine.AbstractDrawer;
import blackhole.common.GameObject;
import blackhole.utils.Vector;

/**
 *
 * @author fabian
 */
public class Widget {

	private Vector position;
	private Vector size;

	private Theme theme;

	private final UI root;

	private Group parent;

	private boolean mouseTouching;
	private UIEvent enterEvent;
	private UIEvent exitEvent;
	
	public Widget(UI ui) {
		root = ui;
		//need to check if root is null, used by UI class
		if (root != null) {
			theme = ui.getDefaultTheme();
		}
		position = new Vector();
		size = new Vector(0.1, 0.1);
	}

	public UI getRoot() {
		return root;
	}

	public Vector getSize() {
		return size;
	}

	public void setSize(Vector s) {
		size = s;
	}

	public Vector getPosition() {
		/*if (parent != null) {
			return Vector.add(parent.getPosition(), position);
		}*/
		return position;
	}

	public Vector getScreenPos() {
		if (parent != null) {
			return Vector.multiply(getPosition(), getRoot().getScreenSize()).add(getParent().getScreenPos());
		}
		return Vector.multiply(getPosition(), getRoot().getScreenSize()).add(getRoot().getScreenPos());
	}

	public Vector getScreenSize() {
		return Vector.multiply(getSize(), getRoot().getScreenSize());
	}

	public void setPosition(Vector pos) {
		position = pos;
	}

	public void setParent(Group group) {
		parent = group;
	}

	public Group getParent() {
		return parent;
	}

	public void setTheme(Theme t) {
		theme = t;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setEnterEvent(UIEvent event) {
		enterEvent = event;
	}

	public void setExitEvent(UIEvent event) {
		exitEvent = event;
	}

	public boolean isTouching(Vector pos) {
		Vector wpos = getScreenPos();
		Vector wsize = getScreenSize();
		return (wpos.x() <= pos.x() && pos.x() <= wpos.x() + wsize.x())
				&& (wpos.y() <= pos.y() && pos.y() <= wpos.y() + wsize.y());
	}

	public boolean handle(InputEvent event) {
		if (event.isType(InputEvent.BUTTON) && event.button == 1 && event.active) {
			getRoot().requestFocus(this);
		}
		if (event.isType(InputEvent.MOUSE)) {
			boolean newPosTouching = isTouching(event.mousePos);
			if (enterEvent != null && newPosTouching && !mouseTouching) {
				enterEvent.process();
			} else if (exitEvent != null && !newPosTouching && mouseTouching) {
				exitEvent.process();
			}
			mouseTouching = newPosTouching;
		}
		return false;
	}

	public void draw() {
		AbstractDrawer drawer = ClientManager.getInstance().getGraphicsBackend().getDrawer();
		Vector fbSize = drawer.getDrawbufferSize();

		Vector p1 = Vector.subtract(getScreenPos(), Vector.divide(fbSize, 2));
		Vector p3 = Vector.add(p1, getScreenSize());
		Vector p2 = new Vector(p1.x(), p3.y());
		Vector p4 = new Vector(p3.x(), p1.y());

		float[] color = getTheme().getBackground().getComponents(null);
		drawer.drawPolygon(color[0], color[1], color[2], color[3], true, p1, p2, p3, p4);
	}
}
