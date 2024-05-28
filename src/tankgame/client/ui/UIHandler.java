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
import blackhole.client.game.ClientObjectHandler;
import blackhole.client.game.input.InputEvent;
import blackhole.client.game.input.InputEventListener;
import blackhole.client.game.input.InputHandler;
import blackhole.client.graphicsEngine.AbstractGameWindow;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author fabian.baer2
 */
public class UIHandler extends ClientObjectHandler implements InputEventListener {

	private ArrayList<UI> uis;

	public UIHandler() {
		uis = new ArrayList<>();
		setDrawPosition(10);
		
		InputHandler.getInstance().addListener(this);
	}

	public ArrayList<UI> getUIs() {
		return uis;
	}

	public void addUI(UI ui) {
		if (ui == null) {
			return;
		}
		AbstractGameWindow win = ClientManager.getInstance().getGraphicsBackend().getWindow();
		ui.setSize(new Vector(win.getWidth(), win.getHeight()));
		uis.add(ui);
	}

	public void removeUI(UI ui) {
		uis.remove(ui);
	}

	@Override
	public void handleEvent(InputEvent event) {
		boolean eaten = false;
		
		UI[] sorted = new UI[uis.size()];
		uis.toArray(sorted);

		Arrays.sort(sorted, new Comparator<UI>() {
			@Override
			public int compare(UI t, UI t1) {
				return t1.getDrawPosition() - t.getDrawPosition();
			}
		});
		for (int i = 0; i < sorted.length && !eaten; i++) {
			eaten = sorted[i].handle(event);
		}
	}

	@Override
	public void init() {
		setScale(1);
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public void draw() {
		UI[] sorted = new UI[uis.size()];
		uis.toArray(sorted);

		Arrays.sort(sorted, new Comparator<UI>() {
			@Override
			public int compare(UI t, UI t1) {
				return t.getDrawPosition() - t1.getDrawPosition();
			}
		});
		for (int i = 0; i < sorted.length; i++) {
			sorted[i].draw();
		}
	}

	@Override
	public void step(double dtime) {
		for (int i = 0; i < uis.size(); i++) {
			uis.get(i).step(dtime);
		}
	}

	@Override
	public void windowResized(int newW, int newH) {
		//getPanel().size.set(newW, newH);
		for (int i = 0; i < uis.size(); i++) {
			uis.get(i).setSize(new Vector(newW, newH));
		}
	}

	@Override
	public void clear() {
		super.clear();
		uis.clear();
	}
}
