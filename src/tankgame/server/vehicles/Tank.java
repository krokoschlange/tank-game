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
package tankgame.server.vehicles;

import blackhole.server.physicsEngine.core.Force;
import blackhole.server.physicsEngine.core.PhysicsStrategy;
import blackhole.utils.Debug;
import blackhole.utils.Vector;
import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public abstract class Tank extends Vehicle {

    //characteristics
    protected double sidewaysTrackKineticFriction;
    protected double rollingFriction;

    protected double wheelRadius;

    protected Engine engine;
    protected Transmission transmission;

    protected double brakeForce;
    protected double brakeRadius;
    protected double brakeFriction;

    protected Vector leftForcePoint;
    protected Vector rightForcePoint;

    protected ArrayList<Vector> frictionPoints;
    protected ArrayList<Vector> oldFrictionPoints;

    //state
    protected double leftBrake;
    protected double rightBrake;

    public Tank() {
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public Engine getEngine() {
        return engine;
    }

    @Override
    public void step(double dtime) {
		super.step(dtime);
        steeringInput(dtime);

        double rightSpeed = (getVelocity().dot(new Vector(0, 1).rotate(getRotation())) + rightForcePoint.x() * getAngularVelocity()) / wheelRadius;
        double leftSpeed = (getVelocity().dot(new Vector(0, 1).rotate(getRotation())) + leftForcePoint.x() * getAngularVelocity()) / wheelRadius;

        double[] torques = transmission.calculateTorques(dtime, new double[]{leftSpeed, rightSpeed});

		PhysicsStrategy pStrat = getPhysicsStrategy();
		
        pStrat.applyForce(new Force(new Vector(0, 1).rotate(getRotation()).multiply(torques[0] / wheelRadius), Vector.rotate(leftForcePoint, getRotation())));
        pStrat.applyForce(new Force(new Vector(0, 1).rotate(getRotation()).multiply(torques[1] / wheelRadius), Vector.rotate(rightForcePoint, getRotation())));

        applyFriction(dtime);

        double leftBrakeForce = -Math.signum(leftSpeed) * leftBrake * brakeForce * brakeFriction * brakeRadius / wheelRadius;
        double rightBrakeForce = -Math.signum(rightSpeed) * rightBrake * brakeForce * brakeFriction * brakeRadius / wheelRadius;
        pStrat.applyForce(new Force(new Vector(0, 1).rotate(getRotation()).multiply(leftBrakeForce), Vector.rotate(leftForcePoint, getRotation())));
        pStrat.applyForce(new Force(new Vector(0, 1).rotate(getRotation()).multiply(rightBrakeForce), Vector.rotate(rightForcePoint, getRotation())));
    }

    public void steeringInput(double dtime) {
    }

    public void applyFriction(double dtime) {
        ArrayList<Vector> newFrictionPoints = new ArrayList<>();
        for (int i = 0; i < frictionPoints.size(); i++) {
            newFrictionPoints.add(Vector.add(getPosition(), Vector.rotate(frictionPoints.get(i), getRotation())));
        }

		PhysicsStrategy pStrat = getPhysicsStrategy();
		
        for (int i = 0; i < newFrictionPoints.size(); i++) {
            Vector delta = Vector.subtract(newFrictionPoints.get(i), oldFrictionPoints.get(i));

            double forwardVel = delta.dot(new Vector(0, 1).rotate(getRotation())) / dtime;
            double fricForward = pStrat.getMass() * -9.81 * rollingFriction;
            if (Math.abs(fricForward) > pStrat.getMass() * Math.abs(forwardVel) / dtime) {
                fricForward = -pStrat.getMass() * Math.abs(forwardVel) / dtime;
            }
            fricForward *= Math.signum(forwardVel) / newFrictionPoints.size();
            pStrat.applyForce(new Force(new Vector(0, fricForward).rotate(getRotation()), Vector.rotate(frictionPoints.get(i), getRotation())));

            double sidewaysVel = delta.dot(new Vector(1, 0).rotate(getRotation())) / dtime;
            double fricSideways = pStrat.getMass() * -9.81 * sidewaysTrackKineticFriction;
            if (Math.abs(fricSideways) > pStrat.getMass() * Math.abs(sidewaysVel) / dtime) {
                fricSideways = -pStrat.getMass() * Math.abs(sidewaysVel) / dtime;
            }
            fricSideways *= Math.signum(sidewaysVel) / newFrictionPoints.size();
            pStrat.applyForce(new Force(new Vector(fricSideways, 0).rotate(getRotation()), Vector.rotate(frictionPoints.get(i), getRotation())));
        }
        oldFrictionPoints = newFrictionPoints;
    }
}
