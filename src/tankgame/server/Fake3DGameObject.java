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
package tankgame.server;

import blackhole.common.ObjectStrategy;
import blackhole.server.game.ServerObject;
import tankgame.client.DefaultFake3DClientObject;

/**
 *
 * @author fabian
 */
public class Fake3DGameObject extends ServerObject {

	public Fake3DGameObject() {
		setClientObjectClass(DefaultFake3DClientObject.class);

		Fake3DVisualStrategy fake3DStrategy = new Fake3DVisualStrategy();
		addUpdateStrategy(fake3DStrategy);
		setVisualStrategy(fake3DStrategy);
	}

	@Override
	public void setVisualStrategy(ObjectStrategy vstrat) {
		if (vstrat instanceof Fake3DVisualStrategy) {
			super.setVisualStrategy(vstrat);
		}
	}

	@Override
	public Fake3DVisualStrategy getVisualStrategy() {
		return (Fake3DVisualStrategy) super.getVisualStrategy();
	}
	
	public void setHeight(double height) {
		getVisualStrategy().setBaseHeight(height);
	}
	
	public double getHeight() {
		return getVisualStrategy().getBaseHeight();
	}

	@Override
	public void step(double dtime) {

	}

	@Override
	public void init() {

	}
}
