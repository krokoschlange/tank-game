/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client.maps;

import blackhole.client.game.ClientObject;
import blackhole.client.game.ClientObjectHandler;
import java.util.ArrayList;
import tankgame.client.TankGameClient;

/**
 *
 * @author fabian
 */
public class FinlandGround extends ClientObject {
	private ArrayList<MapTile> tiles;
	
	public FinlandGround() {
		tiles = new ArrayList<>();
	}

	@Override
	public void init() {
		ClientObjectHandler chandler = (ClientObjectHandler) getHandler();
		int imgSize = 2048;
		
		if (TankGameClient.getInstance().getQuality() >= 2) {
			imgSize = 1024;
		}
		
		double scale = 0.4 * (2048d / imgSize);
		double offset = imgSize * scale / chandler.getScale() - 0.1;
		String filename = imgSize == 2048 ? "finland/finland2048_" : "finland/finland_low/finland1024_";
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 6; j++) {
				MapTile tile = new MapTile("/res/"+ filename + i + "_" + j + ".png", imgSize);
				tile.setHandler(getHandler());
				tile.setScale(scale, scale);
				tile.setPosition(j * offset, -i * offset);
				tile.setVisible(true);
				tile.activate();
				
				tiles.add(tile);
			}
		}
	}

	@Override
	public void remove() {
		for (int i = 0; i < tiles.size(); i++) {
			tiles.get(i).remove();
		}
		super.remove();
	}
	
	
}
