package org.jsilib.model;

import org.jsilib.execao.ImgLibError;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.jsilib.util.ImagePersistence;
import org.jsilib.util.Util;

/**
 *
 * @author Kirk
 */
public class BWImage extends Image {

    public BWImage(String nome) {
        super(nome);
    }

    public BWImage(int w, int h) {
        super(w, h, Image.BW);
    }

    public BImage convertToBinary() {
        return (convertToBinary(128));
    }

    public BImage convertToBinary(int limiar) {
        BImage bin = new BImage(getWidth(), getHeight());
        WritableRaster wr = bin.getRaster();

        Raster r = getRaster();
        int pix = 0;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                pix = r.getSample(x, y, 0);
                wr.setSample(x, y, 0, (pix >= limiar) ? 255 : 0);
            }
        }
        return (bin);
    }

    public void calculaDFT() throws ImgLibError {
        if (getHeight() != getWidth()) {
            throw new ImgLibError("A imagem Deve ser quadrada");
        }
        int n = getHeight();
        Raster r = getRaster();

        //Instancia matriz
        setSpectro(new Complex[n][n]);
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                Complex sum = new Complex(0, 0);
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        //Cria um complexo com o valor do pixel
                        Complex signal = new Complex(r.getSample(x, y, 0));

                        double cosTerm = Math.cos(2 * Math.PI * (u * x + v * y) / (double) n);
                        double sinTerm = -Math.sin(2 * Math.PI * (u * x + v * y) / (double) n);

                        Complex prod = signal.multiply(new Complex(cosTerm, sinTerm));
                        sum = sum.add(prod);
                    }
                }
                spectro[u][v] = sum.multiply(1.0 / (n * n));
            }
        }
    }

    public void calculaDFTInversa() throws ImgLibError {
        BWImage tempImg = new BWImage(getWidth(), getHeight());
        int n = getHeight();
        Raster r = getRaster();
        WritableRaster wr = tempImg.getRaster();

        //Instancia matriz
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                Complex sum = new Complex(0, 0);
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        //Cria um complexo com o valor do pixel
                        Complex signal = spectro[x][y];

                        double cosTerm = Math.cos(2 * Math.PI * (u * x + v * y) / (double) n);
                        double sinTerm = Math.sin(2 * Math.PI * (u * x + v * y) / (double) n);

                        Complex prod = signal.multiply(new Complex(cosTerm, sinTerm));
                        sum = sum.add(prod);
                    }
                }

                Complex term = sum;
                wr.setSample(u, v, 0, term.abs());
            }
        }
        tempImg.saveToFile("imagem_recuperada.jpg");
    }

    public void saveTransformToFile(String nome) {
        BufferedImage temp = new BufferedImage(getWidth(), getHeight(), Image.BW);
        BufferedImage temp2 = new BufferedImage(getWidth(), getHeight(), Image.BW);
        WritableRaster wr = temp.getRaster();
        WritableRaster wr2 = temp2.getRaster();

        //Normaliza entre 0 e 255 o faz o shift
        double mag[][] = Util.fftShift(Util.normalizaLog(spectro));
        //Faz o shift no array bidimensional Complexo
        Complex ang[][] = Util.fftShift(spectro);

        for (int x = 0; x < getHeight(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                wr.setSample(x, y, 0, mag[x][y]);
                wr2.setSample(x, y, 0, Util.getAngle(ang[x][y]));
            }
        }

        //Remove o .jpg pra gravar com os nomes corretos
        if (nome.substring(nome.length() - 4, nome.length()).equals(".jpg")) {
            nome = nome.substring(0, nome.length() - 4);
        }
        try {
            ImagePersistence.saveToFile(temp, nome + "_mag.jpg");
            ImagePersistence.saveToFile(temp2, nome + "_phase.jpg");
        } catch (ImgLibError e) {
            e.printStackTrace();
        }
    }

    public BWImage aplicaFiltro(int[] filtro) {
        BWImage imagemFiltrada = new BWImage(getWidth(), getHeight());
        Raster r = getRaster();
        WritableRaster wr = imagemFiltrada.getRaster();

        //Somatorio dos coeficientes do filtro pra fazer o 1/aux
        double aux = Util.getFilterCoeficient(filtro);
        //System.out.println(aux);
        //Preenche as bordas
        for (int x = 0; x < getHeight(); x++) {
            wr.setSample(x, 0, 0, r.getSample(x, 0, 0));
            wr.setSample(x, getHeight() - 1, 0, r.getSample(x, getHeight() - 1, 0));
            wr.setSample(0, x, 0, r.getSample(0, x, 0));
            wr.setSample(getHeight() - 1, x, 0, r.getSample(getHeight() - 1, x, 0));
        }

        //Deixa 1 pixel de margem em cada canto da imagem
        for (int x = 1; x < getWidth() - 1; x++) {
            for (int y = 1; y < getHeight() - 1; y++) {
                double pix = 0;
                pix += r.getSample(x - 1, y - 1, 0) * filtro[0];
                pix += r.getSample(x, y - 1, 0) * filtro[1];
                pix += r.getSample(x + 1, y - 1, 0) * filtro[2];
                pix += r.getSample(x - 1, y, 0) * filtro[3];
                pix += r.getSample(x, y, 0) * filtro[4];
                pix += r.getSample(x + 1, y, 0) * filtro[5];
                pix += r.getSample(x - 1, y + 1, 0) * filtro[6];
                pix += r.getSample(x, y + 1, 0) * filtro[7];
                pix += r.getSample(x + 1, y + 1, 0) * filtro[8];
                pix *= aux;

                //wr.setSample(x, y, 0, pix);
                double aa = 0;
                if (pix > 255) {
                    aa = 255;
                } else if (pix < 0) {
                    aa = pix * -1;//ABS
                }
                wr.setSample(x, y, 0, aa);
                //System.out.println(aa);
            }
        }
        return imagemFiltrada;
    }

    public BWImage getMagnitude() {
        BWImage aux = new BWImage(getWidth(), getHeight());

        //Normaliza entre 0 e 255 o faz o shift
        double mag[][] = Util.fftShift(Util.normalizaLog(spectro));
        //Faz o shift no array bidimensional Complexo
        Complex ang[][] = Util.fftShift(spectro);

        for (int x = 0; x < getHeight(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                aux.getRaster().setSample(x, y, 0, mag[x][y]);
            }
        }
        return aux;
    }

    public BWImage getPhase() {
        BWImage aux = new BWImage(getWidth(), getHeight());

        //Normaliza entre 0 e 255 o faz o shift
        double mag[][] = Util.fftShift(Util.normalizaLog(spectro));
        //Faz o shift no array bidimensional Complexo
        Complex ang[][] = Util.fftShift(spectro);

        for (int x = 0; x < getHeight(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                aux.getRaster().setSample(x, y, 0, Util.getAngle(ang[x][y]));
            }
        }
        return aux;
    }

    public static BWImage imageSum(BWImage img, BWImage img2) throws ImgLibError {
        if ((img.getWidth() != img2.getWidth()) || (img.getHeight() != img.getHeight())) {
            throw new ImgLibError("Para efetuar a SOMA das Imagens o Tamanho deve ser o mesmo");
        }
        Raster rAux = img.getRaster();
        Raster rAux2 = img2.getRaster();

        BWImage aux = new BWImage(img.getWidth(), img.getHeight());
        WritableRaster wr = aux.getRaster();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int valor = rAux.getSample(x, y, 0) + rAux2.getSample(x, y, 0);
                if (valor < 0) {
                    valor *= -1;
                }
                if (valor > 255) {
                    valor = 255;
                }
                wr.setSample(x, y, 0, valor);
            }
        }
        return aux;
    }

    public void bright(int valor) {
        WritableRaster r = getRaster();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                int pix = r.getSample(x, y, 0) + valor;
                if (pix > 255) {
                    pix = 255;
                }
                if (pix < 0) {
                    pix = 0;
                }
                r.setSample(x, y, 0, pix);
            }
        }
    }

    public int[][] histogram() {
        int res[][] = new int[1][256];
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
