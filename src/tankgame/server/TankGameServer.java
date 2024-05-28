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

import blackhole.server.game.GameManager;
import blackhole.server.network.ServerEndpoint;

/**
 *
 * @author fabian
 */
public class TankGameServer {
	private static TankGameServer singleton;
	
	private GameManager manager;
	private ServerEndpoint network;
	private GOHandler goHandler;
	
	private TankGameServer() {
		
	}
	
	public static TankGameServer getInstance() {
		if (singleton == null) {
			singleton = new TankGameServer();
		}
		return singleton;
	}
	
	public void start(int port) {
		String basePath = System.getProperty("user.dir");
		
		network = ServerEndpoint.getInstance();
		manager = GameManager.getInstance();
		
		manager.loadGame("/res/config/game.conf", basePath);
		
		goHandler = new GOHandler();
		manager.setObjectHandler(goHandler);
		manager.gameLoop();
		
		network.bind(port);
		network.start();
	}
	
	public void kill() {
		network.stop();
		manager.killGame();
	}
	
	public boolean isRunning() {
		return manager.isRunning();
	}
}
