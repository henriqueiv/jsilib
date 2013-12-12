package org.jsilib.model;

import java.awt.image.Raster;

/**
 *
 * @author Kirk Binary Image
 */
public class BImage extends Image {

    public BImage(String nome) {
        super(nome);
    }

    public BImage(int w, int h) {
        super(w, h, Image.BINARY);
    }

    public int[][] histogram() {
        int res[][] = new int[1][2];
        Raster r = getRaster();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                res[0][r.getSample(x, y, 0)]++;
            }
        }
        setHistogram(res);
        return res;
    }

    public BImage getImgHistogram() {
        return super.getImgHistogram(0);
    }
}
