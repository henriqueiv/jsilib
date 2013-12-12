package org.jsilib.gui;

import org.jsilib.model.Image;

/**
 *
 * @author Kirk
 */
public class Visual {
    
    public static void show(Image imagem){
        show(imagem, "");
    }
    
    public static void show(Image imagem, String nome){
        imagem.saveToFile("temp.jpg");
        ImageFrame tela = new ImageFrame();
        tela.setData("temp.jpg", nome);
        tela.show();
    }
}
