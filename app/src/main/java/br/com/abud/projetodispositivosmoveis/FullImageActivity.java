package br.com.abud.projetodispositivosmoveis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        int imageid = getIntent().getIntExtra("imageId", R.drawable.ic_launcher_background);
        fullImage.setImageResource(imageid);

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
