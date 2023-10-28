package com.ozkanseyyarer.migle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher; // TextWatcher ekledik
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddBookActivity extends AppCompatActivity {
    private EditText editTextKitapIsmi, editTextKitapYazari, editTextKitapOzeti;
    private ImageView imgKitapResim;
    private String kitapIsmi, kitapYazari, kitapOzeti;
    private Button btnKaydet;
    private Bitmap secilenResim, kucultulenResim, enbastakiResim;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

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
        registerLauncher();

        // TextWatcher ekledik
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // EditText alanları veya resim değiştikçe çağrılır
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        // EditText alanlarına TextWatcher'ları ekliyoruz
        editTextKitapIsmi.addTextChangedListener(textWatcher);
        editTextKitapYazari.addTextChangedListener(textWatcher);
        editTextKitapOzeti.addTextChangedListener(textWatcher);

        imgKitapResim.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

                // Kullanıcının izin verip vermediğini kontrol ediyoruz.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    // Kullanıcı izin vermediyse bu kısma girecek ve kullanıcıdan tekrar izin isteyecek.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {

                        Snackbar.make(view, "Galeriye gitmek için izin lazım", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                            }
                        }).show();
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                } else {
                    // Eğer kullanıcı daha önce izin verdiyse bu bloğa girecektir.
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }

            }else{


                // Kullanıcının izin verip vermediğini kontrol ediyoruz.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Kullanıcı izin vermediyse bu kısma girecek ve kullanıcıdan tekrar izin isteyecek.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        Snackbar.make(view, "Galeriye gitmek için izin lazım", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                } else {
                    // Eğer kullanıcı daha önce izin verdiyse bu bloğa girecektir.
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }

            }
        });

        btnKaydet.setOnClickListener(view -> {
            kitapIsmi = editTextKitapIsmi.getText().toString();
            kitapYazari = editTextKitapYazari.getText().toString();
            kitapOzeti = editTextKitapOzeti.getText().toString();

            if (!TextUtils.isEmpty(kitapIsmi) && !TextUtils.isEmpty(kitapYazari) && !TextUtils.isEmpty(kitapOzeti) && secilenResim != null) {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                kucultulenResim = resimiKucult(secilenResim);
                kucultulenResim.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
                byte[] kayitEdilecekResim = outputStream.toByteArray();

                try {
                    SQLiteDatabase database = this.openOrCreateDatabase("Migle", MODE_PRIVATE, null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id INTEGER PRIMARY KEY, kitapAdi VARCHAR,kitapResim BLOB," +
                            "kitapYazari VARCHAR, kitapOzeti VARCHAR)");

                    String sqlSorgusu = "INSERT INTO kitaplar(kitapAdi,kitapYazari,kitapOzeti, kitapResim) VALUES(?,?,?,?) ";
                    SQLiteStatement statement = database.compileStatement(sqlSorgusu);
                    statement.bindString(1, kitapIsmi);
                    statement.bindString(2, kitapYazari);
                    statement.bindString(3, kitapOzeti);
                    statement.bindBlob(4, kayitEdilecekResim);
                    statement.execute();
                    nesneleriTemizle();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Kayıt başarıyla eklendi!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Lütfen boş yer bırakmayınız!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    //kullanıcı galeriden bir şeyler seçmişse buraya girecektir
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                        //geriye bir veri döndüyse buraya girecektir
                        Uri imageData = intentFromResult.getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source resimSource = ImageDecoder.createSource(AddBookActivity.this.getContentResolver(), imageData);
                                secilenResim = ImageDecoder.decodeBitmap(resimSource);
                                imgKitapResim.setImageBitmap(secilenResim);
                            } else {
                                secilenResim = MediaStore.Images.Media.getBitmap(AddBookActivity.this.getContentResolver(), imageData);
                                imgKitapResim.setImageBitmap(secilenResim);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                //eğer result true ise izin verildi demektir
                if (result) {
                    //izin verildi
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    //izin verilmedi
                    Toast.makeText(AddBookActivity.this, "Galeriye gitmek için izin gerekli", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void nesneleriTemizle() {
        editTextKitapIsmi.setText("");
        editTextKitapOzeti.setText("");
        editTextKitapYazari.setText("");
        enbastakiResim = BitmapFactory.decodeResource(this.getResources(), R.drawable.add_image);
        imgKitapResim.setImageBitmap(enbastakiResim);
        btnKaydet.setEnabled(false);
    }

    private Bitmap resimiKucult(Bitmap resim) {
        return Bitmap.createScaledBitmap(resim, 240, 300, true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, KitapActivity.class);
        finish();
        startActivity(intent);
    }

    // TextWatcher ile düğme durumunu güncelleyen metod
    private void updateSaveButtonState() {
        kitapIsmi = editTextKitapIsmi.getText().toString();
        kitapYazari = editTextKitapYazari.getText().toString();
        kitapOzeti = editTextKitapOzeti.getText().toString();

        boolean isFieldsNotEmpty = !TextUtils.isEmpty(kitapIsmi) && !TextUtils.isEmpty(kitapYazari) && !TextUtils.isEmpty(kitapOzeti);
        boolean isImageSelected = secilenResim != null;

        btnKaydet.setEnabled(isFieldsNotEmpty && isImageSelected);
    }
}
