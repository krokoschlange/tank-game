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

import blackhole.server.game.ServerObject;
import blackhole.utils.Debug;
import java.util.ArrayList;
import tankgame.server.Damageable;

/**
 *
 * @author fabian.baer2
 */
public class Turret extends ServerObject {

    private double rotationSpeed;
    private double targetRot;
	
    public Turret() {
        rotationSpeed = 0.3;
    }

    public void setRotationSpeed(double rS) {
        rotationSpeed = rS;
    }

    public double getRotationSpeed() {
        return rotationSpeed;
    }

    public void setTargetRot(double tR) {
        tR = tR % (2 * Math.PI);
        if (tR > Math.PI) {
            tR -= 2 * Math.PI;
        } else if (tR < -Math.PI) {
            tR += 2 * Math.PI;
        }
        targetRot = tR;
    }

    public double getTargetRot() {
        return targetRot;
    }

    @Override
    public void step(double dtime) {
        double rot = getRotation() % (2 * Math.PI);
        if (rot > Math.PI) {
            rot -= 2 * Math.PI;
        } else if (rot < -Math.PI) {
            rot += 2 * Math.PI;
        }
        double rotS = 0;

        double delta = targetRot - rot;
        if (Math.abs(delta) < rotationSpeed * 1.5 * dtime) {
            rotS = 0;
			setRotation(targetRot);
        } else if (delta > Math.PI
                || (0 > delta && delta > -Math.PI)) {
            rotS = -rotationSpeed;
        } else if (delta < -Math.PI
                || (Math.PI > delta && delta > 0)) {
            rotS = rotationSpeed;
        }
        setAngularVelocity(rotS);
    }

    @Override
    public void init() {
        /*setTexture("/res/g1799.png");
                getTexture().setRotationOffset(Math.PI / 2);
                getTexture().setOffset(new Vector(0, 0.3));*/

        setVisible(true);
        //setScale(1.2, 1.2);

        //setParent(getID());
        //setPosition(0, 1.3);
        //setDrawPosition(10);
    }
}
