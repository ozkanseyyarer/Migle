package com.ozkanseyyarer.migle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class KitapActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private KitapAdapter adapter;
    public static KitapDetayi kitapDetayi;

    private FloatingActionButton ekleButonu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_menu_add_book) {

            Intent addBookIntent = new Intent(KitapActivity.this, AddBookActivity.class);
            finish();
            startActivity(addBookIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitap);
        ekleButonu = findViewById(R.id.activity_kitap_ekleButonu);

        mRecyclerView = findViewById(R.id.main_activiy_recyclerView);
        adapter = new KitapAdapter(Kitap.getData(this), this);
        mRecyclerView.setHasFixedSize(true);


        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new KitapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Kitap kitap) {
                kitapDetayi = new KitapDetayi(kitap.getKitapAdi(), kitap.getKitapYazari(), kitap.getKitapOzeti(), kitap.getKitapResim());

                Intent intent = new Intent(KitapActivity.this, DetayActivity.class);
                startActivity(intent);

            }
        });

        ekleButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addBookIntent = new Intent(KitapActivity.this, AddBookActivity.class);
                finish();
                startActivity(addBookIntent);
            }
        });
    }
}