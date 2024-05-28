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
import blackhole.client.game.input.InputEventListener;
import blackhole.client.game.input.InputHandler;
import blackhole.utils.Vector;
import java.awt.Color;

/**
 *
 * @author fabian
 */
public class UI extends Group {

	private Theme uiDefaultTheme;

	private Widget focus;
	
	private int drawPosition;
	
	private boolean eatAllEvents;
	
	public UI() {
		super(null);
		uiDefaultTheme = new Theme(Color.darkGray, Color.gray, Color.cyan,
				Color.black, Color.black);
		focus = null;
		drawPosition = 0;
		eatAllEvents = false;

		setTheme(uiDefaultTheme);
	}

	@Override
	public UI getRoot() {
		return this;
	}

	public Theme getDefaultTheme() {
		return uiDefaultTheme;
	}

	@Override
	public Vector getPosition() {
		return new Vector();
	}

	@Override
	public Vector getScreenPos() {
		return super.getPosition();
	}

	@Override
	public Vector getSize() {
		return new Vector(1, 1);
	}

	@Override
	public Vector getScreenSize() {
		return super.getSize();
	}

	public void requestFocus(Widget w) {
		focus = w;
	}

	public Widget getFocus() {
		return focus;
	}

	public boolean hasFocus(Widget w) {
		return w == focus;
	}

	public void setDrawPosition(int dp) {
		drawPosition = dp;
	}

	public int getDrawPosition() {
		return drawPosition;
	}

	public void setEatAllEvents(boolean eat) {
		eatAllEvents = eat;
	}

	public boolean isEatingAllEvents() {
		return eatAllEvents;
	}

	@Override
	public boolean handle(InputEvent event) {
		boolean ret = super.handle(event);
		if (eatAllEvents) {
			return true;
		}
		return ret;
	}
	
	public void step(double dtime) {
		
	}
}
