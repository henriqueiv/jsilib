package org.jsilib.morfologia;

import org.jsilib.model.BImage;

/**
 *
 * @author Kirk
 */
public class Morfologia {

    private static BImage getCrossFilter() {
        BImage filtro = new BImage(3, 3);
        filtro.getRaster().setSample(0, 0, 0, 0);
        filtro.getRaster().setSample(0, 1, 0, 1);
        filtro.getRaster().setSample(0, 2, 0, 0);
        filtro.getRaster().setSample(1, 0, 0, 1);
        filtro.getRaster().setSample(1, 1, 0, 1);
        filtro.getRaster().setSample(1, 2, 0, 1);
        filtro.getRaster().setSample(2, 0, 0, 0);
        filtro.getRaster().setSample(2, 1, 0, 1);
        filtro.getRaster().setSample(2, 2, 0, 0);
        return filtro;
    }

    public static BImage contornoPorSubtracao(BImage original) {
        BImage filtro = getCrossFilter();

        BImage erosao = erosion(original, filtro);
        BImage resultado = new BImage(original.getWidth(), original.getHeight());
        for (int i = 0; i < resultado.getWidth(); i++) {
            for (int j = 0; j < resultado.getHeight(); j++) {
                resultado.getRaster().setSample(i, j, 0, (original.getRaster().getSample(i, j, 0) - erosao.getRaster().getSample(i, j, 0)));
            }
        }
        return resultado;
    }

    public static BImage contornoPorDilatacao(BImage original) {
        BImage filtro = getCrossFilter();

        BImage dilatacao = dilation(original, filtro);
        BImage resultado = new BImage(original.getWidth(), original.getHeight());
        for (int i = 0; i < resultado.getWidth(); i++) {
            for (int j = 0; j < resultado.getHeight(); j++) {
                resultado.getRaster().setSample(i, j, 0, (dilatacao.getRaster().getSample(i, j, 0) - original.getRaster().getSample(i, j, 0)));
            }
        }
        return resultado;
    }

    public static BImage complement(BImage imagem) {
        BImage resultado = new BImage(imagem.getWidth(), imagem.getHeight());
        for (int i = 0; i < imagem.getWidth(); i++) {
            for (int j = 0; j < imagem.getHeight(); j++) {
                //XOR
                resultado.getRaster().setSample(i, j, 0, imagem.getRaster().getSample(i, j, 0) ^ 1);
            }
        }
        return resultado;
    }

    public static BImage dilation(BImage imagem, BImage filtro) {
        BImage resultado = new BImage(imagem.getWidth(), imagem.getHeight());

        //Nao pode comecar no pix 0. por causa do tamanho do filtro
        int deslocamentoX = (int) (filtro.getWidth() - 1) / 2;
        int deslocamentoY = (int) (filtro.getHeight() - 1) / 2;

        for (int i = 0 + deslocamentoX; i < (imagem.getWidth() - deslocamentoX); i++) {
            for (int j = 0 + deslocamentoY; j < (imagem.getHeight() - deslocamentoY); j++) {
                int conta = 0;

                for (int x = 0; x < filtro.getWidth(); x++) {
                    for (int y = 0; y < filtro.getHeight(); y++) {
                        if ((filtro.getRaster().getSample(x, y, 0) == 1) && (imagem.getRaster().getSample(i + (x - deslocamentoX), j + (y - deslocamentoY), 0) == 1)) {
                            conta++;
                        }
                    }
                }

                if (conta != 0) {
                    resultado.getRaster().setSample(i, j, 0, 1);
                    conta = 0;
                }
            }
        }
        return (resultado);
    }

    public static BImage erosion(BImage imagem, BImage filtro) {
        BImage resultado = new BImage(imagem.getWidth(), imagem.getHeight());
        int quantidade = 0;
        for (int i = 0; i < filtro.getWidth(); i++) {
            for (int j = 0; j < filtro.getHeight(); j++) {
                if (filtro.getRaster().getSample(i, j, 0) == 1) {
                    quantidade++;
                }
            }
        }

        //Nao pode comecar no pix 0. por causa do tamanho do filtro
        int deslocamentoX = (int) (filtro.getWidth() - 1) / 2;
        int deslocamentoY = (int) (filtro.getHeight() - 1) / 2;

        for (int i = 0 + deslocamentoX; i < (imagem.getWidth() - deslocamentoX); i++) {
            for (int j = 0 + deslocamentoY; j < (imagem.getHeight() - deslocamentoY); j++) {
                int conta = 0;

                for (int x = 0; x < filtro.getWidth(); x++) {
                    for (int y = 0; y < filtro.getHeight(); y++) {
                        //System.out.println("I: "+ i + " J: "+ j);
                        if ((filtro.getRaster().getSample(x, y, 0) == 1) && (imagem.getRaster().getSample(i + (x - deslocamentoX), j + (y - deslocamentoY), 0) == 1)) {
                            conta++;
                        }
                    }
                }

                if (conta == quantidade) {
                    resultado.getRaster().setSample(i, j, 0, 1);
                    conta = 0;
                }
            }
        }
        return (resultado);
    }

    public static BImage opening(BImage imagem, BImage filtro) {
        BImage aux = erosion(imagem, filtro);
        BImage resultado = dilation(aux, filtro);
        return (resultado);
    }

    public static BImage closing(BImage imagem, BImage filtro) {
        BImage aux = dilation(imagem, filtro);
        BImage resultado = erosion(aux, filtro);
        return (resultado);
    }
}
