package org.jsilib.util;

import org.jsilib.model.Complex;

/**
 *
 * @author Kirk
 */
public class Util {

    public static double getAngle(Complex complex) {
        return Math.atan2(complex.getReal(), complex.getImaginary());
    }

    /**
     * normalizaLog Normaliza a parte da magnitude da imagem para numeros entre
     * 0 e 255
     */
    public static double[][] normalizaLog(Complex complex[][]) {
        //Funciona pq a matriz é quadrada
        int n = complex.length;
        double result[][] = new double[n][n];
        //Pega o maior valor
        double max = 0;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                max = (max < Math.log(1 + complex[x][y].abs())) ? Math.log(1 + complex[x][y].abs()) : max;
            }
        }
        //Normaliza entre 0 e 255
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                result[x][y] = (Math.log(1 + complex[x][y].abs()) / max) * 255;
            }
        }
        return result;
    }

    /**
     * fftShift Faz a mesma coisa que a FFTShift do Matlab (movendo os
     * quadrantes) Para uma matriz de numeros complexos
     */
    public static Complex[][] fftShift(Complex complex[][]) {
        int n = complex.length;
        Complex result[][] = new Complex[n][n];
        //Encontra o meio pra fazer o SHIFT
        int meiox = (int) Math.floor(n / 2);
        int meioy = (int) Math.floor(n / 2);
        int deslocaX, deslocaY;

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                deslocaX = (x < meiox) ? meiox : -meiox;
                deslocaY = (y < meioy) ? meioy : -meioy;
                result[x + deslocaX][y + deslocaY] = complex[x][y];
            }
        }
        return result;
    }

    /**
     * FFTShift Faz a mesma coisa que a FFTShift do Matlab (movendo os
     * quadrantes) Para uma matriz de numeros double
     */
    public static double[][] fftShift(double original[][]) {
        int n = original.length;
        double result[][] = new double[n][n];
        //Encontra o meio pra fazer o SHIFT
        int meiox = (int) Math.floor(n / 2);
        int meioy = (int) Math.floor(n / 2);
        int deslocaX, deslocaY;

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                deslocaX = (x < meiox) ? meiox : -meiox;
                deslocaY = (y < meioy) ? meioy : -meioy;
                result[x + deslocaX][y + deslocaY] = original[x][y];
            }
        }
        return result;
    }

    /**
     * getFilterCoeficient Calcula o coeficiente do filtro retorna 1/somatorio
     * se somatório maior que zero caso contrario retorna 1
     */
    public static double getFilterCoeficient(int[] filtro) {
        //Somatorio dos coeficientes do filtro pra fazer o 1/aux
        double aux = 0;
        for (int i = 0; i < filtro.length; i++) {
            aux += filtro[i];
        }

        aux = (aux != 0) ? 1 / aux : 1;
        return aux;
    }

    public static Complex[] ifftAux(Complex x[]) {
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

    public static Complex[] fftAux(Complex x[]) {
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

    public static Complex[][] linhaColuna(Complex x[][]) {
        int n = x.length;
        Complex y[][] = new Complex[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                y[i][j] = x[j][i];
            }
        }
        return y;
    }

    public static Complex[][] fft(Complex x[][]) {
        int n = x.length;
        Complex entrada[][];
        entrada = linhaColuna(x);
        Complex aux[][] = new Complex[n][];
        Complex aux2[][] = new Complex[n][];

        for (int i = 0; i < n; i++) {
            aux[i] = fftAux(entrada[i]);
        }
        aux = linhaColuna(aux);
        for (int i = 0; i < n; i++) {
            aux2[i] = Util.fftAux(aux[i]);
        }
        aux2 = linhaColuna(aux2);
        return (aux2);
    }

    public static Complex[][] ifft(Complex x[][]) {
        int n = x.length;
        Complex entrada[][];
        entrada = linhaColuna(x);
        Complex aux[][] = new Complex[n][];
        Complex aux2[][] = new Complex[n][];

        for (int i = 0; i < n; i++) {
            aux[i] = ifftAux(entrada[i]);
        }
        aux = linhaColuna(aux);
        for (int i = 0; i < n; i++) {
            aux2[i] = ifftAux(aux[i]);
        }
        aux2 = linhaColuna(aux2);
        return (aux2);
    }
}
