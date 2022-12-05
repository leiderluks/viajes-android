package com.example.agencia_viajes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    EditText jetCodigoV, jetNombreU;
    RadioButton jrbprimeraC, jrbEjecutiva, jrbEconomica;
    Boolean respuesta;
    CheckBox jcbActivo;
    String Codigo, Nombre, vuelos, activo, Ident_Doc;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        jetCodigoV = findViewById(R.id.etCodigoV);
        jetNombreU = findViewById(R.id.etNombreU);
        jrbprimeraC = findViewById(R.id.rbPrimeraC);
        jrbEjecutiva = findViewById(R.id.rbEjecutiva);
        jrbEconomica = findViewById(R.id.rbEconomica);
        jcbActivo = findViewById(R.id.cbActivo);
    }

    public void Adicionar(View view) {
        Codigo = jetCodigoV.getText().toString();
        Nombre = jetNombreU.getText().toString();

        if (Codigo.isEmpty() || Nombre.isEmpty()) {
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetCodigoV.requestFocus();
        } else {
            if (jrbprimeraC.isChecked())
                vuelos = "Primera Clase";
            else if (jrbEjecutiva.isChecked())
                vuelos = "Clase Ejecutiva";

            else
                vuelos = "Economica";

            Map<String, Object> pelicula = new HashMap<>();
            pelicula.put("Codigo", Codigo);
            pelicula.put("Nombre", Nombre);
            pelicula.put("vuelos", vuelos);
            pelicula.put("activo", "si");

// Add a new document with a generated ID
            db.collection("Peliculas")
                    .add(pelicula)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(MainActivity.this, "vuelo registrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //  Log.w(TAG, "Error adding document", e);
                            Toast.makeText(MainActivity.this, "Error al adicionar", Toast.LENGTH_SHORT).show();
                        }
                    });


        }

    }

    public void Consultar(View view) {
        Buscar_Pelicula();

    }

    public void Buscar_Pelicula() {
        respuesta = false;
        Codigo = jetCodigoV.getText().toString();
        if (Codigo.isEmpty()) {
            Toast.makeText(this, "El codigo es necesario", Toast.LENGTH_SHORT).show();
            jetCodigoV.requestFocus();
        } else {
            db.collection("Peliculas")
                    .whereEqualTo("Codigo",Codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    respuesta=true;
                                    if (document.getString("Stock").equals("no")){
                                        Toast.makeText(MainActivity.this, "El documento existe pero no está activo", Toast.LENGTH_SHORT).show();
                                    }
                                    else{

                                    }
                                    Ident_Doc=document.getId();
                                    jetNombreU.setText(document.getString("Nombre"));
                                    jetCodigoV.setText(document.getString("Codigo"));
                                    if (document.getString("Genero").equals("Accion"))
                                        jrbprimeraC.setChecked(true);
                                    else
                                    if (document.getString("Genero").equals("Fantasia"))
                                        jrbprimeraC.setChecked(true);
                                    else
                                        jrbEjecutiva.setChecked(true);
                                    if (document.getString("Stock").equals("si"))
                                        jcbActivo.setChecked(true);
                                    else
                                        jcbActivo.setChecked(false);
                                    //  Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                // Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

        }
    }



    public void Anular (View view){
        Codigo=jetCodigoV.getText().toString();
        Nombre=jetNombreU.getText().toString();

        if (Codigo.isEmpty()) {
            Toast.makeText(this, "El codigo es requerido", Toast.LENGTH_SHORT).show();
            jetCodigoV.requestFocus();

        }
        else{
            if (respuesta==true){
                if (jrbprimeraC.isChecked())
                    vuelos="Accion";
                else if(jrbEjecutiva.isChecked())
                    vuelos="Fantasia";
                else
                    vuelos="Suspenso";

                Map<String, Object> pelicula = new HashMap<>();
                pelicula.put("Codigo", Codigo);
                pelicula.put("Nombre", Nombre);
                pelicula.put("Genero", vuelos);
                pelicula.put("Stock", "no");

                db.collection("Peliculas").document(Ident_Doc)
                        .set(pelicula)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Pelicula anulada", Toast.LENGTH_SHORT).show();
                                // Limpiar();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error al anular", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            else{
                Toast.makeText(this, "Debe primero consultar", Toast.LENGTH_SHORT).show();
                jetCodigoV.requestFocus();
            }
        }

    }



    public void Limpiar(View view){
        jetCodigoV.setText("");
        jetNombreU.setText("");
        jrbprimeraC.setChecked(true);
        jcbActivo.setChecked(false);
        jetCodigoV.requestFocus();
        respuesta=false;
    }
}
