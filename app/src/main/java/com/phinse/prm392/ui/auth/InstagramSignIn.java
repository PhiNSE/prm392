package com.phinse.prm392.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.phinse.prm392.R;

public class InstagramSignIn extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_sign_in);

        findViewById(R.id.llLoginWithFB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstagramSignIn.this, FacebookSignInActivity.class);
                startActivity(intent);
            }
        });
    }
}