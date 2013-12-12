package org.jsilib.util;

import org.jsilib.execao.ImgLibError;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author Kirk
 */
public class ImagePersistence {

    public static BufferedImage loadImage(BufferedImage image, String nome) throws ImgLibError {
        try {
            image = ImageIO.read(new File(nome));
            return (image);
        } catch (Exception e) {
            throw new ImgLibError("Erro ao Carregar Imagem");
        }
    }

    public static void saveToFile(BufferedImage img, String nome) throws ImgLibError {
        if (nome.length() < 4) {
            nome += ".jpg";
        } else {
            if (!nome.substring(nome.length() - 4, nome.length()).equals(".jpg")) {
                nome += ".jpg";
            }
        }
        try {
            ImageIO.write(img, "jpg", new File(nome));
        } catch (Exception e) {
            throw new ImgLibError("Erro ao Salvar Imagem");
        }
    }

}
