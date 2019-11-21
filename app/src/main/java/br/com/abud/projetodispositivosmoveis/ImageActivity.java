package br.com.abud.projetodispositivosmoveis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ImageActivity extends AppCompatActivity {


    private EditText tagEditText;
    private Button textButton;
    private ImageView tagButton;
    private FloatingActionButton cameraButton;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private GridView imagesGridView;

    private CollectionReference collMensagensReference;
    private List<Image> listImageShare;

    private BaseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        textButton = findViewById(R.id.textButtonId);

        imagesGridView = findViewById(R.id.gridImagesId);

        cameraButton = findViewById(R.id.fabCameraId);

        tagEditText = findViewById(R.id.editText);
        tagButton = findViewById(R.id.tagButtonId);

        listImageShare = new ArrayList<>();
        //inicializa o adapter falando que a variável que contem os dados é o array list acima
        adapter = new ImageAdapterGridView(this, listImageShare);

        //informa ao GridView o adapter que irá controlar sempre que umitem deve ser mostrado no Grid
        imagesGridView.setAdapter(adapter);

        //se clicado, esse botão leva a tela de compartilhameto de texto
        textButton.setOnClickListener((view) -> {

            Intent intent = new Intent(this,  MainActivity.class);
            startActivity(intent);

        });

        //Botão tirar foto
        cameraButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);//Informando o código da Câmera como a ação da intent para saber diferenciar o retorno no Action Result
        });

        //Botao tag
        tagButton.setOnClickListener((v) ->{
            //Verifica se alguma tag foi preenchida ou cancela a ação
            if(tagEditText.getText().length() == 0){
                Toast.makeText(this, "Digite uma tag", Toast.LENGTH_SHORT);
                return;
            }


            tagEditText.clearFocus();
            //Esconde o teclado
            esconderTeclado(v);

            //Cria no Firebase uma coleção com o nome "_tag informada"
            collMensagensReference = FirebaseFirestore.getInstance().collection(String.format(
                    Locale.getDefault(),
                    "_%s",
                    tagEditText.getText().toString() // Pega a Tag
            ));

            //Cria um Listener para a referencia da coleção criada anteriormente, para sempre que houver um item  novo na coleção receber esse aviso
            collMensagensReference.addSnapshotListener((result, e) -> {
                listImageShare.clear();

                //Verifica os Jsons que da coleção
                for(DocumentSnapshot doc: result.getDocuments())
                    listImageShare.add(new Image(doc.get("imagePath").toString()));//Para cada Json adiciona no List o caminho informado

                adapter.notifyDataSetChanged();//Avisa o adapter que houve alteração no Array List para que ele possa avisar o GridView para ser recarregado
            });
        });

    }

    //Esconde teclado
    private void esconderTeclado (View v){
        InputMethodManager ims = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ims.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //Retorno da câmera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Apenas entra nesse if se tiver algum retorno(resultCode == RESULT_OK) e se esse retorno for da camera ou da galeria(requestCode == REQ_CODE_CAMERA || requestCode == REQ_CODE_GALERIA)
        if(resultCode == RESULT_OK && (requestCode == REQUEST_IMAGE_CAPTURE)) {
            Bitmap picture = null;//Variável que recebera a imagem da foto tirada ou escolhida na galeria

            //Pega a imagem e converte para bitmap
            if(requestCode == REQUEST_IMAGE_CAPTURE)
                picture = (Bitmap) data.getExtras().get("data");

            //Verifica se a imagem existe
            if(picture != null) {
                //Pega o ID do aparelho para gerar um nome único para a imagem que será compartilhada evitando nomes duplicados
                String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                Date date = new Date();

                //junta o ID do aparelho com a data e hora atual substituindo : e espaço por -, e colocando .jpg no final
                String nomeArquivo = (android_id + DataHelper.format(date).replace("/", "-") + ".jpg").replace(" ", "-").replace(":", "-");

                //Cria uma referencia a uma pasta no FireStorage
                StorageReference pictureStorageReference = FirebaseStorage.getInstance()
                        .getReference(
                                String.format(
                                        Locale.getDefault(),
                                        "dontpad/%s/%s",//a pasta na raiz sempre é dontpad
                                        tagEditText.getText().toString(),//Tag informada
                                        nomeArquivo//Imagem compartilhada
                                )
                        );

                //Converte a imagem Bitmap em JPEG
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();

                //Faz o upload da Imagem
                pictureStorageReference.putBytes(bytes)
                        //Adiciona um Listener pno upload para adicionar o Json novo no FireBase com o caminho da imagem
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                //Cria o novo objeto que será convertido para Json para subir no FireBase
                                Image imageShare = new Image(String.format(
                                        Locale.getDefault(),
                                        "dontpad/%s/%s",
                                        tagEditText.getText().toString(),
                                        nomeArquivo
                                ));

                                //Adiciona novo Json ao FireBase
                                collMensagensReference.add(imageShare);

                                //Isso foi feito para evitar q o texto fosse atualizado antes do upload da imagem
                            }
                        });
            }
        }
    }

    //Controle do GridView
    public class ImageAdapterGridView extends BaseAdapter {
        //Guarda uma refência do Context para poder usar o inflator de Views e afins
        private Context mContext;
        //Guarda uma cópia da lista de caminhos de imagens compartilhadas
        private List<Image> listImageShare;
        //Toda imagem que for baixada é guardada nesse HashMap para não ficar baixando novamente a mesma imagem
        private HashMap<String, byte[]> buffer;

        public ImageAdapterGridView(Context c, List<Image> listImageShare) {
            this.mContext = c;
            this.listImageShare = listImageShare;
            buffer = new HashMap<>();
        }
        //Esse getCount informa o Grid quantaos itens existem para serem exibidos
        public int getCount() {
            return listImageShare.size();
        }

        //Não está usando
        public Object getItem(int position) {
            return null;
        }

        //Não está usando
        public long getItemId(int position) {
            return 0;
        }

        //Item que retorna a view que aparecerá no Grid
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;// Convert View virá preenchido caso haja Views para serem recicladas
            ImageView imageItem;// Image View que mostrará a imagem compartihada

            //Caso não haja view para ser reciclada, infla uma nova View
            if (v == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.gridview, parent, false);// Infla a View GridItem
                v.setTag(R.id.imageId, v.findViewById(R.id.imageId));//Adiciona o ImageView a essa view GridItem
            }

            imageItem = (ImageView) v.getTag(R.id.imageId);//Busca o ImageView que contem no GridItem

            Image image = listImageShare.get(position);// Busca o item exato no List do caminho que está querendo ser exibido

            imageItem.setImageResource(R.drawable.ic_launcher_background);//Seta uma imagem padrão no Grid apenas para mostrar que o item ainda será carregado

            //Se a imagem já foi baixada e está no buffer, apenas converte ela de array de bytes para Bitmap e mostra no ImageView do GridItem
            if(buffer.containsKey(image.getImagePath())){
                Bitmap figura = BitmapFactory.decodeByteArray(buffer.get(image.getImagePath()), 0, buffer.get(image.getImagePath()).length);
                imageItem.setImageBitmap(figura);
            }
            else{
                //Caso não tenha sido baixada, guarda o caminho da imagem
                String imagem = image.getImagePath();
                //cria uma referência para a imagem que está no FireStorage
                StorageReference pictureStorageReference = FirebaseStorage.getInstance()
                        .getReference(imagem);//<-- variável imagem está fazendo essa referência

                //Faz o download da imagem, se não me engano 1024 * 1024 * 1024 é = a 1GB de limite no download
                pictureStorageReference.getBytes(1024 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        //Quando o Download é realizado, adiciona no buffer sendo que a chave é o próprio caminho da imagem
                        buffer.put(image.getImagePath(), bytes);
                        //Converte de bytes para bitmap para poder mostrar no ImageView do GridItem
                        Bitmap figura = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageItem.setImageBitmap(figura);

                        //Adiciona um Listener para se a imagem do Grid For Clicada abrir ela na activity DisplayImage
                        imageItem.setOnClickListener(vImage ->{
                            //CAptura a imagem que está sendo exibida no ImageView do Grid
                            Drawable imagem = ((ImageView) vImage).getDrawable();
                            Bitmap bitmap = ((BitmapDrawable)imagem).getBitmap();

                            //Converte em array de bytes
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] b = baos.toByteArray();

                            //Chama a DisplayImage passando a imagem convertida em bytes para ser exibida
                            Intent intent = new Intent(mContext, FullImageActivity.class);
                            intent.putExtra("figura", b);
                            mContext.startActivity(intent);

                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }

            return v;
        }
    }



}
