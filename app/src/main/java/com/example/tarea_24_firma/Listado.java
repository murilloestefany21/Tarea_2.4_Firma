package com.example.tarea_24_firma;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarea_24_firma.Clase.Signaturess;
import com.example.tarea_24_firma.ConectorBD.SQLiteConexion;
import com.example.tarea_24_firma.transacciones.Transacciones;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class Listado extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private List<Signaturess> mLista = new ArrayList<>();
    private ListView mlistView;
    ListAdapter mAdapter;
    public static Bitmap enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        setTitle("Listado de Firmas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mlistView =(ListView)findViewById(R.id.listview);
        mlistView.setOnItemClickListener(this);

        cargarDatos();
    }

    private void cargarDatos(){

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from "+ Transacciones.tablaimagenes,null);

        while (cursor.moveToNext()){

            int id = cursor.getInt(0);
            String description = cursor.getString(2);


            mLista.add(new Signaturess(String.valueOf(id), description, buscarImagen(id)));
            mAdapter = new ListAdapter(this,R.layout.item_row, mLista);

            mlistView.setAdapter(mAdapter);


        }

        cursor.close();

    }

    public Bitmap buscarImagen(long id){
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        String sql = "SELECT * FROM imagenes where id = "+id;
        Cursor cursor = db.rawQuery(sql, new String[] {});
        Bitmap bitmap = null;
        if(cursor.moveToFirst()){
            byte[] blob = cursor.getBlob(1);
            ByteArrayInputStream bais = new ByteArrayInputStream(blob);
            bitmap = BitmapFactory.decodeStream(bais);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return bitmap;
    }

    public static int resultado=1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultado && requestCode == RESULT_OK) {
            cargarDatos();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getApplicationContext(),VisualizarImagen.class);

        enviar= mLista.get(position).getFirmaDigital();
        startActivity(intent);


    }

    public static Bitmap enviar(){
        return enviar;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
