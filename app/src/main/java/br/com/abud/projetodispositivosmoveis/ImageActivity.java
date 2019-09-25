package br.com.abud.projetodispositivosmoveis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {

    private Button textButton;
    private ImageView cameraButton;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    GridView gridView;
    int images[] = {R.drawable.image1,R.drawable.image2,R.drawable.image3,
                    R.drawable.image1,R.drawable.image2,R.drawable.image3,
                    R.drawable.image1,R.drawable.image2,R.drawable.image3};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        gridView = findViewById(R.id.gridImagesId);
        ViewCompat.setNestedScrollingEnabled(gridView,true);
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(),images);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent fullImage = new Intent(getApplicationContext(),FullImageActivity.class);
                fullImage.putExtra("imageId", images[position]);
                startActivity(fullImage);
            }
        });
        //Botao camera
        cameraButton = findViewById(R.id.cameraButtonId);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (camera.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        //Botao ir para tela texto
        textButton = findViewById(R.id.textButtonId);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textScreen = new Intent(getBaseContext(),MainActivity.class);
                startActivity(textScreen);
            }
        });
    }



}
