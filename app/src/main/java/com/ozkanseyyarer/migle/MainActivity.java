package com.ozkanseyyarer.migle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        CardView kitapCardView = findViewById(R.id.kitapCardView);
        kitapCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // KitapActivity'ye geçiş yap
                Intent intent = new Intent(MainActivity.this, KitapActivity.class);
                startActivity(intent);
            }
        });
    }
}