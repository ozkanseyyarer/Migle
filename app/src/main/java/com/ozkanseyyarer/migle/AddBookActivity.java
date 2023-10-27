package com.ozkanseyyarer.migle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddBookActivity extends AppCompatActivity {
    private EditText editTextKitapIsmi, editTextKitapYazari, editTextKitapOzeti;
    private ImageView imgKitapResim;
    private String kitapIsmi, kitapYazari, kitapOzeti;
    private Button btnKaydet;

    private  int izinAlmaKodu = 0, izinVerildiKodu=1;
    private Bitmap secilenResim,kucultulenResim, enbastakiResim;

    private void init() {

        btnKaydet = findViewById(R.id.kitapKaydet);

        editTextKitapIsmi = findViewById(R.id.activity_add_book_editTextKitapIsmi);
        editTextKitapYazari = findViewById(R.id.activity_add_book_editTextKitapYazari);
        editTextKitapOzeti = findViewById(R.id.activity_add_book_editTextKitapOzeti);
        imgKitapResim = findViewById(R.id.activity_add_book_imageViewKitapResmi);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        init();

        imgKitapResim.setOnClickListener(view -> {

            //kullanıcının izin verip vermediğini kontrol ediyoruz
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
                //kullanıcı izin vermediyse bu kısma girecek ve kullanıcıdan tekrar izin isteyecek
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},izinAlmaKodu);
            }else {
                //eğer kullanıcı daha önce izin verdiyse bu bloğa girecektir
                Intent resmiAl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(resmiAl,izinVerildiKodu);
            }
        });


        btnKaydet.setOnClickListener(view -> {

            kitapIsmi = editTextKitapIsmi.getText().toString();
            kitapYazari = editTextKitapYazari.getText().toString();
            kitapOzeti = editTextKitapOzeti.getText().toString();

            if (!TextUtils.isEmpty(kitapIsmi) || !TextUtils.isEmpty(kitapIsmi) || !TextUtils.isEmpty(kitapIsmi)) {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                kucultulenResim = resimiKucult(secilenResim);
                secilenResim.compress(Bitmap.CompressFormat.PNG,75,outputStream);
                byte [] kayitEdilecekResim = outputStream.toByteArray();


                try {
                    SQLiteDatabase database = this.openOrCreateDatabase("Migle",MODE_PRIVATE,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id INTEGER PRIMARY KEY, kitapAdi VARCHAR,kitapResim BLOB," +
                            "kitapYazari VARCHAR, kitapOzeti VARCHAR)");

                    String sqlSorgusu = "INSERT INTO kitaplar(kitapAdi,kitapYazari,kitapOzeti, kitapResim) VALUES(?,?,?,?) ";
                    SQLiteStatement statement = database.compileStatement(sqlSorgusu);
                    statement.bindString(1,kitapIsmi);
                    statement.bindString(2,kitapYazari);
                    statement.bindString(3,kitapOzeti);
                    statement.bindBlob(4,kayitEdilecekResim);
                    statement.execute();
                    nesneleriTemizle();

                }catch (Exception e){
                    e.printStackTrace();
                }
                Toast.makeText(this, "Kayıt başarıyla eklendi!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Lütfen boş yer bırakmayınız!", Toast.LENGTH_LONG).show();

            }

        });

    }




    private void nesneleriTemizle(){
        editTextKitapIsmi.setText("");
        editTextKitapOzeti.setText("");
        editTextKitapYazari.setText("");
        enbastakiResim = BitmapFactory.decodeResource(this.getResources(),R.drawable.add_image);
        imgKitapResim.setImageBitmap(enbastakiResim);
        btnKaydet.setEnabled(false);
    }


    private Bitmap resimiKucult(Bitmap resim){
        return Bitmap.createScaledBitmap(resim,120,150,true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == izinAlmaKodu){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //eğer kullanıcı daha önce izin verdiyse bu bloğa girecektir
                Intent resmiAl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(resmiAl,izinVerildiKodu);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == izinVerildiKodu){
            if (resultCode == RESULT_OK && data != null){
                Uri resimUri = data.getData();// resimUriyi aldıktan sonra bizim bunu bitmap haline çevirmemiz gerek

                try {
                    if (Build.VERSION.SDK_INT>=28){

                        ImageDecoder.Source resimSource = ImageDecoder.createSource(this.getContentResolver(),resimUri);
                        secilenResim = ImageDecoder.decodeBitmap(resimSource);
                        imgKitapResim.setImageBitmap(secilenResim);
                    }else {
                        secilenResim = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resimUri);
                        imgKitapResim.setImageBitmap(secilenResim);
                    }

                    btnKaydet.setEnabled(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, KitapActivity.class);
        finish();
        startActivity(intent);
    }
}