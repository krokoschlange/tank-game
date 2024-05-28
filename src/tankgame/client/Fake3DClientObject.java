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
package tankgame.client;

import blackhole.client.game.ClientObject;
import blackhole.client.game.DrawStrategy;
import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public class Fake3DClientObject extends ClientObject {

	public Fake3DClientObject() {
		/*Fake3DDrawUpdateStrategy f3duStrat = new Fake3DDrawUpdateStrategy();
		setDrawStrategy(f3duStrat);
		addUpdateStrategy(f3duStrat);*/
	}

	@Override
	public void setDrawStrategy(DrawStrategy drawStrat) {
		if (drawStrat instanceof Fake3DDrawStrategy) {
			super.setDrawStrategy(drawStrat);
		}
	}
	
	@Override
	public Fake3DDrawStrategy getDrawStrategy() {
		return (Fake3DDrawStrategy) super.getDrawStrategy();
	}

	public void setHeight(double height) {
		getDrawStrategy().setBaseHeight(height);
	}
	
	public double getHeight() {
		return getDrawStrategy().getBaseHeight();
	}

	@Override
	public void step(double dtime) {
	}

	@Override
	public void remove() {
		super.remove();
		getDrawStrategy().remove();
	}

}
