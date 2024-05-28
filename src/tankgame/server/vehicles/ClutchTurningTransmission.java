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
public class ClutchTurningTransmission implements Transmission {

    private Engine engine;
    private double clutchSlipTorque;
    private double[] gears;

    private int gear;
    private double leftClutch;
    private double rightClutch;

    public ClutchTurningTransmission(Engine eng, double cSlipT, double[] gearbox) {
        engine = eng;
        clutchSlipTorque = cSlipT;
        gears = gearbox;

        gear = 0;
        leftClutch = 1;
        rightClutch = 1;
    }

    public void setLeftClutch(double val) {
        leftClutch = Math.max(0, Math.min(val, 1));
    }

    public double getLeftClutch() {
        return leftClutch;
    }

    public void setRightClutch(double val) {
        rightClutch = Math.max(0, Math.min(val, 1));
    }

    public double getRightClutch() {
        return rightClutch;
    }

    public void setGear(int g) {
        gear = Math.max(0, Math.min(g, gears.length - 1));
    }

    public int getGear() {
        return gear;
    }

    @Override
    public double[] calculateTorques(double dtime, double[] speeds) {
        if (engine.getSpeed() < 104) {
            leftClutch = 0;
            rightClutch = 0;
        }
		if (engine.getSpeed() < engine.getMinSpeed()) {
			engine.setRunning(false);
		}

        double leftTorque;
        double rightTorque;

        double leftSpeed = speeds[0];
        double rightSpeed = speeds[1];

        if (Math.abs(rightSpeed - engine.getSpeed() / gears[gear]) < 0.1 && Math.abs(leftSpeed - engine.getSpeed() / gears[gear]) < 0.1) {
            leftTorque = engine.getTorque() * gears[gear] * 0.5;
            rightTorque = engine.getTorque() * gears[gear] * 0.5;

            if (Math.abs(leftTorque + rightTorque) > Math.abs(engine.getTorque() * gears[gear])) {
                double factor = engine.getTorque() * gears[gear] / (leftTorque + rightTorque);
                leftTorque = leftTorque * factor;
                rightTorque = rightTorque * factor;
            }

            engine.setSpeed(rightSpeed * gears[gear]);
        } else if (Math.abs(rightSpeed - engine.getSpeed() / gears[gear]) < 0.1) {
            rightTorque = engine.getTorque() * gears[gear] - clutchSlipTorque * leftClutch;
            leftTorque = clutchSlipTorque * leftClutch * Math.signum(gears[gear]);

            if (Math.abs(leftTorque + rightTorque) > Math.abs(engine.getTorque() * gears[gear])) {
                double factor = engine.getTorque() * gears[gear] / (leftTorque + rightTorque);
                leftTorque = leftTorque * factor;
                rightTorque = rightTorque * factor;
            }

            engine.setSpeed(rightSpeed * gears[gear]);
        } else if (Math.abs(leftSpeed - engine.getSpeed() / gears[gear]) < 0.1) {
            leftTorque = engine.getTorque() * gears[gear] - clutchSlipTorque * leftClutch;
            rightTorque = clutchSlipTorque * leftClutch * Math.signum(gears[gear]);

            if (Math.abs(leftTorque + rightTorque) > Math.abs(engine.getTorque() * gears[gear])) {
                double factor = engine.getTorque() * gears[gear] / (leftTorque + rightTorque);
                leftTorque = leftTorque * factor;
                rightTorque = rightTorque * factor;
            }

            engine.setSpeed(leftSpeed * gears[gear]);
        } else {
            double leftFactor = Math.tanh(engine.getSpeed() / gears[gear] - leftSpeed);
            double rightFactor = Math.tanh(engine.getSpeed() / gears[gear] - rightSpeed);
            leftTorque = clutchSlipTorque * leftClutch * leftFactor;
            rightTorque = clutchSlipTorque * rightClutch * rightFactor;

            engine.setSpeed(engine.getSpeed() + dtime * ((engine.getTorque() - leftTorque / gears[gear] - rightTorque / gears[gear]) / 0.3));
        }
        return new double[]{leftTorque, rightTorque};
    }
}
