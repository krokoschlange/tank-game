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
package tankgame.client.vehicles;

import tankgame.client.ClientGun;
import tankgame.client.ClientPlayer;
import tankgame.client.Fake3DClientObject;
import tankgame.client.ui.UI;

/**
 *
 * @author fabian
 */
public class ClientVehicle extends Fake3DClientObject {

	protected ClientPlayer[] seats;
	
	private boolean canUseHandgun;

	public ClientVehicle() {
		seats = new ClientPlayer[1];

		getDefaultUpdateStrategy().addParameter("seats",
				(s) -> {
					Integer[] sentSeatsIDs = (Integer[]) s;
					ClientPlayer[] sentSeats = new ClientPlayer[sentSeatsIDs.length];
					for (int  i = 0; i < sentSeats.length; i++) {
						sentSeats[i] = (ClientPlayer) getHandler().getObjectByID(sentSeatsIDs[i]);
					}
					setSeats(sentSeats);
				},
				() -> {
					Integer[] ret = new Integer[seats.length];
					for (int i = 0; i < seats.length; i++) {
						ret[i] = seats[i] == null ? null : seats[i].getID();
					}
					return ret;
				});
	}

	public void setSeats(ClientPlayer[] s) {
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] != null) {
				seats[i].setVehicle(null);
			}
		}
		seats = s;
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] != null) {
				seats[i].setVehicle(this);
			}
		}
	}

	public ClientPlayer[] getSeats() {
		return seats;
	}
	
	public ClientGun getGun(int num) {
		return null;
	}
	
	public UI getUI() {
		return null;
	}
	
	public void setUseHandgun(boolean state) {
		canUseHandgun = state;
	}
	
	public boolean canUseHandgun() {
		return canUseHandgun;
	}
}
