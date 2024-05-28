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

import blackhole.utils.Vector;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import tankgame.common.Particle;
import tankgame.common.Position3D;

/**
 *
 * @author fabian
 */
public class ClientParticleSpawner extends Fake3DClientObject {

	private CopyOnWriteArrayList<Particle> particles;

	private double timeSinceLastSpawn;
	private Random random;

	private Function<Double, Double> particleAlphaFunction;
	
	private double spawnRate;
	
	private double lifetime;
	
	private Position3D positionSpread;
	private Position3D initialVelocity;
	private Position3D velocitySpread;
	private Position3D acceleration;
	private Position3D accelerationSpread;
	
	private double dragFactor;
	private double dragSpread;
	
	private double rotationSpread;
	private double particleAngularVelocity;
	private double particleAngularVelocitySpread;
	private double particleAngularAcceleration;
	private double particleAngularAccelerationSpread;
	
	private Vector particleSize;
	private Vector sizeSpread;
	private Vector sizeVelocity;
	private Vector sizeVelocitySpread;
	private Vector sizeAcceleration;
	private Vector sizeAccelerationSpread;

	public ClientParticleSpawner() {
		random = new Random();
		particles = new CopyOnWriteArrayList<>();
		timeSinceLastSpawn = 0;
		
		spawnRate = 0;
		
		particleAlphaFunction = (lifetime) -> {return lifetime > 1 ? 1 : Math.max(0, lifetime);};
		
		lifetime = 1;
		positionSpread = new Position3D(new Vector(), 0);
		initialVelocity = new Position3D(new Vector(), 0);
		velocitySpread = new Position3D(new Vector(), 0);
		acceleration = new Position3D(new Vector(), 0);
		accelerationSpread = new Position3D(new Vector(), 0);
		
		dragFactor = 1;
		dragSpread = 0;
		
		rotationSpread = 0;
		particleAngularVelocity = 0;
		particleAngularVelocitySpread = 0;
		particleAngularAcceleration = 0;
		particleAngularAccelerationSpread = 0;
		
		particleSize = new Vector(1, 1);
		sizeSpread = new Vector();
		sizeVelocity = new Vector();
		sizeVelocitySpread = new Vector();
		sizeAcceleration = new Vector();
		sizeAccelerationSpread = new Vector();
		
		ParticleDrawStrategy strat = new ParticleDrawStrategy();
		strat.setStep(0.5);
		setDrawStrategy(strat);
		addUpdateStrategy(strat);
	}
	
	public CopyOnWriteArrayList<Particle> getParticles() {
		return particles;
	}

	public void setSpawnRate(double rate) {
		spawnRate = rate;
	}

	public double getSpawnRate() {
		return spawnRate;
	}
	
	public void setParticleAlphaFunction(Function<Double, Double> pAF) {
		particleAlphaFunction = pAF;
	}

	public Function<Double, Double> getParticleAlphaFunction() {
		return particleAlphaFunction;
	}

	public void setPositionSpread(Position3D spread) {
		positionSpread = spread;
	}

	public Position3D getPositionSpread() {
		return positionSpread;
	}
	
	public void setInitialVelocity(Position3D vel) {
		initialVelocity = vel;
	}

	public Position3D getInitialVelocity() {
		return initialVelocity;
	}

	public void setVelocitySpread(Position3D spread) {
		velocitySpread = spread;
	}

	public Position3D getVelocitySpread() {
		return velocitySpread;
	}

	public void setAcceleration(Position3D acc) {
		acceleration = acc;
	}

	public Position3D getAcceleration() {
		return acceleration;
	}

	public void setAccelerationSpread(Position3D spread) {
		accelerationSpread = spread;
	}

	public Position3D getAccelerationSpread() {
		return accelerationSpread;
	}

	public void setDragFactor(double drag) {
		dragFactor = drag;
	}

	public double getDragFactor() {
		return dragFactor;
	}

	public void setDragSpread(double spread) {
		dragSpread = spread;
	}

	public double getDragSpread() {
		return dragSpread;
	}
	
	public void setRotationSpread(double spread) {
		rotationSpread = spread;
	}

	public double getRotationSpread() {
		return rotationSpread;
	}

	public void setParticleAngularVelocity(double pAV) {
		particleAngularVelocity = pAV;
	}

	public double getParticleAngularVelocity() {
		return particleAngularVelocity;
	}

	public void setParticleAngularVelocitySpread(double pAVS) {
		particleAngularVelocitySpread = pAVS;
	}

	public double getParticleAngularVelocitySpread() {
		return particleAngularVelocitySpread;
	}

	public void setParticleAngularAcceleration(double pAA) {
		particleAngularAcceleration = pAA;
	}

	public double getParticleAngularAcceleration() {
		return particleAngularAcceleration;
	}

	public void setParticleAngularAccelerationSpread(double pAAS) {
		particleAngularAccelerationSpread = pAAS;
	}

	public double getParticleAngularAccelerationSpread() {
		return particleAngularAccelerationSpread;
	}

	public void setParticleSize(Vector size) {
		particleSize = size;
	}

	public Vector getParticleSize() {
		return particleSize;
	}

	public void setSizeSpread(Vector spread) {
		sizeSpread = spread;
	}

	public Vector getSizeSpread() {
		return sizeSpread;
	}

	public void setSizeVelocity(Vector vel) {
		sizeVelocity = vel;
	}

	public Vector getSizeVelocity() {
		return sizeVelocity;
	}

	public void setSizeVelocitySpread(Vector spread) {
		sizeVelocitySpread = spread;
	}

	public Vector getSizeVelocitySpread() {
		return sizeVelocitySpread;
	}

	public void setSizeAcceleration(Vector acc) {
		sizeAcceleration = acc;
	}

	public Vector getSizeAcceleration() {
		return sizeAcceleration;
	}

	public void setSizeAccelerationSpread(Vector spread) {
		sizeAccelerationSpread = spread;
	}

	public Vector getSizeAccelerationSpread() {
		return sizeAccelerationSpread;
	}

	public void setLifetime(double time) {
		lifetime = time;
	}

	public double getLifetime() {
		return lifetime;
	}
	
	private double getNewSpreadFactor() {
		return random.nextDouble() * 2 - 1;
	}

	@Override
	public void onDraw(double dtime) {
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update(dtime);
			if (particles.get(i).getRemainingLifetime() < 0) {
				particles.remove(i);
				i--;
			}
		}
		int newParticles = (int) (spawnRate * dtime);
		timeSinceLastSpawn += dtime;
		if (timeSinceLastSpawn > 1 / spawnRate && newParticles == 0) {
			newParticles = 1;
		}

		double height = getHeight();
		Vector rotVel = Vector.rotate(initialVelocity.pos, getRealRotation());
		Vector rotAcc = Vector.rotate(acceleration.pos, getRealRotation());
		
		for (int i = 0; i < newParticles; i++) {
			Position3D pos = new Position3D(new Vector(getRealPosition().x() + positionSpread.pos.x() * getNewSpreadFactor(),
					getRealPosition().y() + positionSpread.pos.y() * getNewSpreadFactor()),
					height + positionSpread.vpos * getNewSpreadFactor());
			
			Position3D vel = new Position3D(new Vector(rotVel.x() + velocitySpread.pos.x() * getNewSpreadFactor(),
					rotVel.y() + velocitySpread.pos.y() * getNewSpreadFactor()),
					initialVelocity.vpos + velocitySpread.vpos * getNewSpreadFactor());
			Position3D acc = new Position3D(new Vector(rotAcc.x() + accelerationSpread.pos.x() * getNewSpreadFactor(),
					rotAcc.y() + accelerationSpread.pos.y() * getNewSpreadFactor()),
					acceleration.vpos + accelerationSpread.vpos * getNewSpreadFactor());
			
			double drg = dragFactor + getNewSpreadFactor() * dragSpread;
			
			double rot = getRealRotation() + rotationSpread * getNewSpreadFactor();
			double aVel = particleAngularVelocity + particleAngularVelocitySpread * getNewSpreadFactor();
			double aAcc = particleAngularAcceleration + particleAngularAccelerationSpread * getNewSpreadFactor();
			
			Vector size = Vector.add(particleSize, Vector.multiply(sizeSpread, getNewSpreadFactor()));
			Vector sVel = Vector.add(sizeVelocity, Vector.multiply(sizeVelocitySpread, getNewSpreadFactor()));
			Vector sAcc = Vector.add(sizeAcceleration, Vector.multiply(sizeAccelerationSpread, getNewSpreadFactor()));
			
			Particle p = new Particle(pos, vel, acc, drg, rot, aVel, aAcc, size, sVel, sAcc, lifetime);
			particles.add(p);
		}
		if (newParticles > 0) {
			timeSinceLastSpawn = 0;
		}
	}
}
