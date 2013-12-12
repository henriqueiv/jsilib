package org.jsilib.execao;

/**
 *
 * @author Kirk
 */
public class ImgLibError extends Exception {

    public ImgLibError() {
        super("Erro Desconhecido");
    }

    public ImgLibError(String s) {
        super(s);
    }

    public ImgLibError(Throwable t) {
        super(t);
    }
}
