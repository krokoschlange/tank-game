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
package tankgame.client.ui;

import java.awt.Color;

/**
 *
 * @author fabian.baer2
 */
public class Theme {
    private Color background;
    private Color foreground;
    private Color highlight;
    private Color outline;
    private Color contrast;
    
    public Theme(Color bg, Color fg, Color hl, Color ol, Color ct) {
        background = bg;
        foreground = fg;
        highlight = hl;
        outline = ol;
        contrast = ct;
    }
    
    public Color getBackground() {
        return background;
    }
    
    public Color getForeground() {
        return foreground;
    }
    
    public Color getHighlight() {
        return highlight;
    }
    
    public Color getOutline() {
        return outline;
    }
    
    public Color getContrast() {
        return contrast;
    }
}
