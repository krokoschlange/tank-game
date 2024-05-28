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
package tankgame.common;

import blackhole.utils.Vector;

/**
 *
 * @author fabian
 */
public class Particle {
	private Position3D pos;
	private Position3D velocity;
	private Position3D acceleration;
	private double drag;
	private double rotation;
	private double angularVelocity;
	private double angularAcceleration;
	private Vector size;
	private Vector sizeVelocity;
	private Vector sizeAcceleration;
	private double lifetime;
	
	public Particle(Position3D start, Position3D vel, Position3D acc,
			double drg, double rot, double aVel, double aAcc, Vector s,
			Vector sVel, Vector sAcc, double time) {
		pos = start;
		velocity = vel;
		acceleration = acc;
		drag = drg;
		lifetime = time;
		rotation = rot;
		angularVelocity = aVel;
		angularAcceleration = aAcc;
		size = s;
		sizeVelocity = sVel;
		sizeAcceleration = sAcc;
	}
	
	public Position3D getPosition() {
		return pos;
	}
	
	public Position3D getVelocity() {
		return velocity;
	}
	
	public Position3D getAcceleration() {
		return acceleration;
	}
	
	public double getRotation() {
		return rotation;
	}

	public double getAngularVelocity() {
		return angularVelocity;
	}

	public double getAngularAcceleration() {
		return angularAcceleration;
	}

	public Vector getSize() {
		return size;
	}

	public Vector getSizeVelocity() {
		return sizeVelocity;
	}

	public Vector getSizeAcceleration() {
		return sizeAcceleration;
	}
	
	public double getRemainingLifetime() {
		return lifetime;
	}
	
	public void update(double dtime) {
		velocity.pos.add(Vector.multiply(acceleration.pos, dtime)).multiply(Math.pow(drag, dtime));
		velocity.vpos += acceleration.vpos * dtime;
		velocity.vpos *= Math.pow(drag, dtime);
		pos.pos.add(Vector.multiply(velocity.pos, dtime));
		pos.vpos += velocity.vpos * dtime;
		
		angularVelocity += angularAcceleration * dtime;
		rotation += angularVelocity * dtime;
		
		sizeVelocity.add(Vector.multiply(sizeAcceleration, dtime));
		size.add(Vector.multiply(sizeVelocity, dtime));
		
		lifetime -= dtime;
	}
}
