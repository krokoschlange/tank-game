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

/**
 *
 * @author fabian
 */
public class Engine {

    private double maxPower;
    private double maxPowerSpeed;
    private double minSpeed;
    private double maxSpeed;

    private boolean running;
    private double throttle;
    private double speed;

    public Engine() {
        maxPower = 300000;
        maxPowerSpeed = 200;
        minSpeed = 50;
        maxSpeed = 400;

        running = false;
        throttle = 0;
        speed = 0;
    }

    public Engine(double pMax, double pMaxSpeed, double minS, double maxS) {
        maxPower = pMax;
        maxPowerSpeed = pMaxSpeed;
        minSpeed = minS;
        maxSpeed = maxS;

        running = false;
        throttle = 0;
        speed = 0;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(double maxP) {
        maxPower = maxP;
    }

    public double getMaxPowerSpeed() {
        return maxPowerSpeed;
    }

    public void setMaxPowerSpeed(double mps) {
        maxPowerSpeed = mps;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxS) {
        maxSpeed = maxS;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(double minS) {
        minSpeed = minS;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double s) {
        speed = s;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean state) {
        running = state;
    }

    public double getThrottle() {
        return throttle;
    }

    public void setThrottle(double t) {
        throttle = t;
    }

    public void start() {
        setSpeed(100);
        setRunning(true);
        setThrottle(0.01);
    }

    public double getTorque() {
        double speedRatio = getSpeed() / maxPowerSpeed;
        double torque = ((-1 / (throttle + 0.1) + 10) / 9) * (maxPower / maxPowerSpeed) * ((0.6526 * speedRatio + 1.6948 * speedRatio * speedRatio - 1.3474 * Math.pow(speedRatio, 3)) / speedRatio);
        torque = Math.max(torque, 0);
        return getSpeed() > minSpeed && isRunning() && getSpeed() < maxSpeed ? torque : 0;
    }
}
