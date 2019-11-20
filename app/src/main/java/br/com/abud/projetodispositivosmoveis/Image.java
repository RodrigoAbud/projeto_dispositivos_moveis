package br.com.abud.projetodispositivosmoveis;

public class Image {

    private String ImagePath;

    public Image(String ImagePath){
        this.ImagePath = ImagePath;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

}
