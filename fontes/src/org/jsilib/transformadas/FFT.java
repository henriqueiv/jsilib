package org.jsilib.transformadas;

import org.jsilib.execao.ImgLibError;
import java.awt.image.WritableRaster;
import org.jsilib.model.BWImage;
import org.jsilib.model.Complex;
import org.jsilib.util.Util;

/**
 *
 * @author Kirk
 */
public class FFT {

    public static final int DIRETA = 1;
    public static final int INVERSA = 2;

    private static Complex[] fftAux(Complex x[]) {
        int n = x.length;
        if (n == 1) {
            return x;
        }
        double wn = 2 * Math.PI / n;
        double w = 1;

        Complex par[] = new Complex[n / 2];
        Complex impar[] = new Complex[n / 2];
        for (int i = 0, j = 0; i < n; i += 2) {
            par[j] = x[i];
            impar[j] = x[i + 1];
            j++;
        }
        Complex y0[] = fftAux(par);
        Complex y1[] = fftAux(impar);

        Complex y[] = new Complex[n];
        for (int k = 0; k < (n / 2); k++) {
            w = wn * k;
            Complex wk = new Complex(Math.cos(w), Math.sin(-w));
            y[k] = y0[k].add(wk.multiply(y1[k]));
            y[k + n / 2] = y0[k].subtract(wk.multiply(y1[k]));
        }
        return y;
    }

    private static Complex[] ifftAux(Complex x[]) {
        int n = x.length;
        Complex[] y = new Complex[n];

        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }

        y = fftAux(y);

        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
            y[i] = y[i].multiply(1.0 / n);
        }

        return y;
    }

    public static Complex[][] fft(BWImage imagem) throws ImgLibError {
        //So funciona para imagens quadradas
        if (imagem.getWidth() != imagem.getHeight()) {
            throw new ImgLibError("Esta implementacao aceita apenas imagens quadradas");
        }
        //Teste binario para potencia de 2
        int v = imagem.getWidth();
        int f = v & (v - 1);
        if (f != 0) {
            throw new ImgLibError("Deve ser potencia de 2");
        }

        int n = imagem.getWidth();
        Complex x[][] = new Complex[n][n];
        WritableRaster wr = imagem.getRaster();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //inverte IJ x JI para mudar linha coluna
                x[i][j] = new Complex(wr.getSample(j, i, 0));
            }
        }

        Complex aux[][] = new Complex[n][];
        Complex aux2[][] = new Complex[n][];

        for (int i = 0; i < n; i++) {
            aux[i] = fftAux(x[i]);
        }
        aux = Util.linhaColuna(aux);
        for (int i = 0; i < n; i++) {
            aux2[i] = fftAux(aux[i]);
        }
        aux2 = Util.linhaColuna(aux2);
        imagem.setSpectro(aux2);
        return (aux2);
    }

    public static BWImage ifft(BWImage imagem) {
        int n = imagem.getWidth();
        Complex x[][] = new Complex[n][n];
        WritableRaster wr = imagem.getRaster();
        x = imagem.getSpectro();

        Complex aux[][] = new Complex[n][];
        Complex aux2[][] = new Complex[n][];

        for (int i = 0; i < n; i++) {
            aux[i] = ifftAux(x[i]);
        }
        aux = Util.linhaColuna(aux);
        for (int i = 0; i < n; i++) {
            aux2[i] = ifftAux(aux[i]);
        }
        aux2 = Util.linhaColuna(aux2);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //inverte IJ x JI para mudar linha coluna
                wr.setSample(i, j, 0, aux2[j][i].abs());
            }
        }
        return (imagem);
    }
}
