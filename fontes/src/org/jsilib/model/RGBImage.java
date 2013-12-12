package org.jsilib.model;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *
 * @author Kirk
 */
public class RGBImage extends Image {

    public RGBImage(String nome) {
        super(nome);
    }

    public RGBImage(int w, int h) {
        super(w, h, Image.COLOR);
    }

    public BWImage convertToBW() {
        BWImage pb = new BWImage(getWidth(), getHeight());
        WritableRaster wr = pb.getRaster();

        Raster r = getRaster();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                wr.setSample(x, y, 0,
                        (r.getSample(x, y, 0) * 0.299
                        + r.getSample(x, y, 1) * 0.587
                        + r.getSample(x, y, 2) * 0.114));
            }
        }
        return pb;
    }

    public BImage convertToBinary() {
        return (convertToBinary(128));
    }

    public BImage convertToBinary(int limiar) {
        BImage bin = convertToBW().convertToBinary(limiar);
        return (bin);
    }

    public RGBImage getRImage() {
        return getSubImage(0);
    }

    public RGBImage getGImage() {
        return getSubImage(1);
    }

    public RGBImage getBImage() {
        return getSubImage(2);
    }

    private RGBImage getSubImage(int band) {
        RGBImage aux = new RGBImage(getWidth(), getHeight());
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                aux.getRaster().setSample(x, y, band, this.getRaster().getSample(x, y, band));
            }
        }
        return aux;
    }

    public int[][] histogram() {
        int res[][] = new int[3][256];
        Raster r = getRaster();
        for (int indice = 0; indice < 3; indice++) {
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    res[indice][r.getSample(x, y, 0)]++;
                }
            }
        }
        setHistogram(res);
        return res;
    }

    public BImage getImgHistogramR() {
        return super.getImgHistogram(0);
    }

    public BImage getImgHistogramG() {
        return super.getImgHistogram(1);
    }

    public BImage getImgHistogramB() {
        return super.getImgHistogram(2);
    }
}
