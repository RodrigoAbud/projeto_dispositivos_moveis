package br.com.abud.projetodispositivosmoveis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private Button imageButton;
    private ImageView tagButton;
    private EditText tagEditText, contentEditText;

    //FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    //private DatabaseReference rootReference = firebaseDatabase.getReference();
    //private DatabaseReference chatReference = rootReference.child("dontPad");

    private DocumentReference docRef;
    private CollectionReference collMensagensReference;

    private boolean tagSeted = false;
    private boolean textEdited =false;
    private Date dataUltimaDigitacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.imageButtonId);
        tagButton = findViewById(R.id.tagButtonId);
        tagEditText = findViewById(R.id.tagEditTextId);
        contentEditText = findViewById(R.id.contentEditTextId);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageScreen = new Intent(getBaseContext(), ImageActivity.class);
                startActivity(imageScreen);
            }
        });

        tagButton.setOnClickListener((v) ->{
            if(tagEditText.getText().length() == 0){
                Toast.makeText(this, "Digite uma tag", Toast.LENGTH_SHORT);
                return;
            }

            if(collMensagensReference == null)
                collMensagensReference = FirebaseFirestore.getInstance().collection("dontPad");

            tagSeted = false;

            //contentEditText.setEnabled(false);
            contentEditText.setText("");
            tagButton.clearFocus();

            docRef =null;
            docRef = collMensagensReference.document(tagEditText.getText().toString());

            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    tagSeted = true;

                    if(documentSnapshot.getData() == null)
                        return;

                    String mensagem = documentSnapshot.getData().get("content").toString();

                    if(!mensagem.equals(contentEditText.getText().toString())) {
                        contentEditText.setEnabled(false);
                        contentEditText.setText(mensagem);
                        contentEditText.setEnabled(true);
                        contentEditText.requestFocus(View.FOCUS_RIGHT);
                    }
                }
            });

            contentEditText.setEnabled(true);
            contentEditText.requestFocus();
        });

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(tagSeted && textEdited && new Date().getTime() - dataUltimaDigitacao.getTime() > 1000) {
                    Map<String, Object> mensagem = new HashMap<>();
                    mensagem.put("content", contentEditText.getText().toString());
                    collMensagensReference.document(tagEditText.getText().toString()).set(mensagem, SetOptions.merge());
                    textEdited = false;
                }

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);

        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(contentEditText.isEnabled()){
                    dataUltimaDigitacao = new Date();
                    textEdited = true;
                }
            }
        });
    }

/*
    public void searchTag(View view) {
        // Create a reference to the dontPad collection
        CollectionReference tagRef = db.collection("dontPad");
        // Create a query against the collection.
        String tag = tagEditText.getText().toString();
        Query query = tagRef.whereEqualTo("tag", tag);
        // Retrieving Results
        db.collection("dontPad")
                .whereEqualTo("tag", tag)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {;
                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                contentEditText.setText(doc.getData().values().toArray()[1].toString());
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                            contentEditText.setText("");
                            //createTag(tag,"");
                            //Toast.makeText(MainActivity.this,"criar tag",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }*/







/*
    private void createTag(String tag, String content){
        Map<String, Object> newTag = new HashMap<>();
        newTag.put("tag", tag);
        newTag.put("content", content);
        db.collection("dontPad").add(newTag).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.getId();//id do objeto
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }*/
}
