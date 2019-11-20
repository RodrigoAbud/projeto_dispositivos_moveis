package br.com.abud.projetodispositivosmoveis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FullImageActivity extends AppCompatActivity {

    private Button textButton;
    private Button imageButton;
    private ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        fullImage = findViewById(R.id.fullImageId);
        //Aqui está pegando a imagem que foi transformada em umarray de Byte para ser passada da Activity de compartilhamento de imagem para essa
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        //Aqui está pegando o array de byte e convertendo em Bitmap para poder exibir ela mp ImageView
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        fullImage.setImageBitmap(image);

        //Botao voltar para o texto
        textButton = findViewById(R.id.textButtonId);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textScreen = new Intent(getBaseContext(),MainActivity.class);
                startActivity(textScreen);
            }
        });

        //Botao voltar para imagem
        imageButton = findViewById(R.id.imageButtonId);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageScreen = new Intent(getBaseContext(),ImageActivity.class);
                startActivity(imageScreen);
            }
        });
    }
}
