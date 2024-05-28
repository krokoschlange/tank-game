/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.client.visuals;

import blackhole.client.game.ClientManager;
import blackhole.client.game.ClientObject;
import blackhole.client.graphicsEngine.GraphicsBackend;
import blackhole.utils.Vector;
import tankgame.client.ClientParticleSpawner;
import tankgame.common.Position3D;

/**
 *
 * @author fabian
 */
public class FireEffect extends ClientObject {

	protected ClientParticleSpawner yellowFire;
	protected ClientParticleSpawner orangeFire;
	protected ClientParticleSpawner soot;
	
	private double size;
	
	public FireEffect(double fireSize) {
		yellowFire = new ClientParticleSpawner();
		orangeFire = new ClientParticleSpawner();
		soot = new ClientParticleSpawner();
		
		size = fireSize;
	}

	public void setHeight(double height) {
		yellowFire.setHeight(height);
		orangeFire.setHeight(height);
		soot.setHeight(height);
	}

	public double getHeight() {
		return yellowFire.getHeight();
	}

	public void start() {
		yellowFire.setSpawnRate(100);
		yellowFire.setVisible(true);
		orangeFire.setSpawnRate(200);
		orangeFire.setVisible(true);
		soot.setSpawnRate(80);
		soot.setVisible(true);
	}

	public void end() {
		yellowFire.setSpawnRate(0);
		//yellowFire.setVisible(false);
		orangeFire.setSpawnRate(0);
		//orangeFire.setVisible(false);
		soot.setSpawnRate(0);
		//soot.setVisible(false);
	}
	
	public boolean isStillVisible() {
		return !(yellowFire.getParticles().isEmpty()
				&& orangeFire.getParticles().isEmpty()
				&& soot.getParticles().isEmpty());
	}
	
	@Override
	public void step(double dtime) {
	}

	@Override
	public void init() {
		GraphicsBackend backend = ClientManager.getInstance().getGraphicsBackend();

		yellowFire.getDrawStrategy().addTexture(backend.createGameTexture("/res/fire_yellow.png"));

		yellowFire.setParent(this);
		
		yellowFire.setParticleAlphaFunction((lifetime) -> {
			double a = 0;
			if (lifetime > yellowFire.getLifetime() / 2) {
				a = 1.0;
			} else {
				a = lifetime * (2 / yellowFire.getLifetime());
			}
			return a / 3;
		});

		yellowFire.setSpawnRate(0);
		yellowFire.setLifetime(0.04 * size + 0.1);

		double posSpread = 0.15 * size;
		double vVel = 0.5 * size;
		double vel = 0.2 * size + 2;
		
		yellowFire.setPositionSpread(new Position3D(new Vector(posSpread, posSpread), posSpread));
		yellowFire.setInitialVelocity(new Position3D(new Vector(0, 0), vVel));
		yellowFire.setVelocitySpread(new Position3D(new Vector(vel, vel), vVel));
		yellowFire.setAcceleration(new Position3D(new Vector(0, 0), -vVel * 0.45));
		yellowFire.setAccelerationSpread(new Position3D(new Vector(0.1, 0.1), vVel * 0.09));
		
		yellowFire.setDragFactor(0.1);
		yellowFire.setDragSpread(0.05);

		double pSize = 0.05 + 0.005 * size;
		
		yellowFire.setParticleSize(new Vector(pSize, pSize));
		yellowFire.setSizeSpread(new Vector(pSize * 0.6, pSize * 0.6));
		yellowFire.setSizeVelocity(new Vector(pSize, pSize));
		yellowFire.setSizeVelocitySpread(new Vector(pSize, pSize));
		yellowFire.setSizeAcceleration(new Vector(-4 * pSize, -4 * pSize));
		yellowFire.setSizeAccelerationSpread(new Vector(pSize, pSize));

		yellowFire.setHandler(getHandler());
		yellowFire.activate();

		orangeFire.getDrawStrategy().addTexture(backend.createGameTexture("/res/fire_orange.png"));

		orangeFire.setParent(this);

		orangeFire.setParticleAlphaFunction((lifetime) -> {
			double a = 0;
			if (lifetime > orangeFire.getLifetime()) {
				a = 0.0;
			} else if (lifetime > orangeFire.getLifetime() / 2) {
				a = Math.pow((orangeFire.getLifetime() - lifetime) * (2 / orangeFire.getLifetime()), 10);
			} else {
				a = (2 / orangeFire.getLifetime()) * lifetime;
			}
			return a / 3;
		});
		orangeFire.setSpawnRate(0);
		orangeFire.setLifetime(0.08 * size + 0.1);

		orangeFire.setPositionSpread(new Position3D(new Vector(posSpread, posSpread), posSpread));
		orangeFire.setInitialVelocity(new Position3D(new Vector(0, 0), vVel));
		orangeFire.setVelocitySpread(new Position3D(new Vector(vel, vel), vVel));
		orangeFire.setAcceleration(new Position3D(new Vector(0, 0), -vVel * 0.45));
		orangeFire.setAccelerationSpread(new Position3D(new Vector(0.1, 0.1), vVel * 0.09));

		orangeFire.setDragFactor(0.1);
		orangeFire.setDragSpread(0.05);
		
		orangeFire.setParticleSize(new Vector(pSize, pSize));
		orangeFire.setSizeSpread(new Vector(pSize * 0.6, pSize * 0.6));
		orangeFire.setSizeVelocity(new Vector(pSize, pSize));
		orangeFire.setSizeVelocitySpread(new Vector(pSize, pSize));
		orangeFire.setSizeAcceleration(new Vector(-4 * pSize, -4 * pSize));
		orangeFire.setSizeAccelerationSpread(new Vector(pSize, pSize));

		orangeFire.setHandler(getHandler());
		orangeFire.activate();
		
		
		
		
		soot.getDrawStrategy().addTexture(backend.createGameTexture("/res/fire_soot.png"));

		soot.setParent(this);

		soot.setParticleAlphaFunction((lifetime) -> {
			double a = 0;
			if (lifetime > 1.5) {
				a = -Math.pow((4 / soot.getLifetime()) * (lifetime - 0.75 * soot.getLifetime()), 8) + 1;
			} else {
				a = (4 / (3 * soot.getLifetime())) * lifetime;
			}
			return a / 3;
		});
		soot.setSpawnRate(0);
		soot.setLifetime(0.1 * size + 1);

		soot.setPositionSpread(new Position3D(new Vector(posSpread, posSpread), posSpread));
		soot.setInitialVelocity(new Position3D(new Vector(0, 0), vVel));
		soot.setVelocitySpread(new Position3D(new Vector(vel, vel), vVel));
		soot.setAcceleration(new Position3D(new Vector(0, 0), -vVel * 0.16));
		soot.setAccelerationSpread(new Position3D(new Vector(0.1, 0.1), vVel * 0.09));

		soot.setDragFactor(0.1);
		soot.setDragSpread(0.05);
		
		soot.setParticleSize(new Vector(pSize * 0.6, pSize * 0.6));
		soot.setSizeSpread(new Vector(pSize * 0.4, pSize * 0.4));
		soot.setSizeVelocity(new Vector(pSize, pSize));
		soot.setSizeVelocitySpread(new Vector(pSize, pSize));
		soot.setSizeAcceleration(new Vector(-pSize * 0.1, -pSize * 0.1));
		soot.setSizeAccelerationSpread(new Vector(0, 0));

		soot.setHandler(getHandler());
		soot.activate();
	}

	@Override
	public void remove() {
		yellowFire.remove();
		orangeFire.remove();
		soot.remove();
		super.remove();
	}
}
