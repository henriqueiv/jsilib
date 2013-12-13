package org.jsilib.model;

import org.jsilib.execao.ImgLibError;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.jsilib.util.ImagePersistence;

/**
 *
 * @author Kirk
 */
public abstract class Image {

    public static final int COLOR = BufferedImage.TYPE_3BYTE_BGR;
    public static final int BW = BufferedImage.TYPE_BYTE_GRAY;
    public static final int BINARY = BufferedImage.TYPE_BYTE_BINARY;
    private BufferedImage image = null;
    protected Complex[][] spectro;
    private String nome;
    private int histogram[][];

    /**
     * Contrutor que recebe um nome de imagem e carrega
     */
    public Image(String nome) {
        try {
            image = ImagePersistence.loadImage(image, nome);
            this.nome = nome;
        } catch (ImgLibError e) {
            e.printStackTrace();
        }
    }

    /**
     * Contrutor que recebe Largura e Altura Cria uma imagem Padrão RGB ou PB
     */
    public Image(int w, int h, int type) {
        image = new BufferedImage(w, h, type);
    }

    /**
     * @return the image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @param image the img to set
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * @return the image width
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * @return the image height
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * @return the image raster
     */
    public WritableRaster getRaster() {
        return image.getRaster();
    }

    /**
     * Grava a imagem em arquivo
     */
    public void saveToFile(String nome) {
        try {
            ImagePersistence.saveToFile(image, nome);
        } catch (ImgLibError e) {
            e.printStackTrace();
        }
    }

    public Complex[][] getSpectro() {
        return spectro;
    }

    /**
     * @param spectro the spectro to set
     */
    public void setSpectro(Complex[][] spectro) {
        this.spectro = spectro;
    }

    public abstract int[][] histogram();

    /**
     * @return the histogram
     */
    public int[][] getHistogram() {
        return histogram;
    }

    /**
     * @param histogram the histogram to set
     */
    public void setHistogram(int[][] histogram) {
        this.histogram = histogram;
    }

    public BImage getImgHistogram(int band) {
        if (getHistogram() == null) {
            histogram();
        }
        int maior = 0;
        int[][] hist = getHistogram();
        for (int x = 0; x < hist.length; x++) {
            for (int y = 0; y < hist[x].length; y++) {
                int pix = hist[x][y];
                if (pix > maior) {
                    maior = pix;
                }
            }
        }
        
        int divide = 10;
        if (this instanceof BImage){
            divide = 100;
        }
        BImage aux = new BImage(256, (int) (maior / divide) + 1);
        WritableRaster wr = aux.getRaster();
        for (int x = 0; x < hist[band].length; x++) {
            System.out.println(x);
            int num = (int) (hist[band][x] / divide);
            for (int y = 0; y < num; y++) {
                wr.setSample(x, ((maior / divide) - y), 0, 1);
            }
        }
        return aux;
    }
}
