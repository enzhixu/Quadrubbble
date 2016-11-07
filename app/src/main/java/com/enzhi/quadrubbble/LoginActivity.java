package com.enzhi.quadrubbble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Dribbble.init(this);

        TextView loginButton = (TextView) findViewById(R.id.login_button);

        if(Dribbble.isLoggedIn()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();       //什麼意思
        }else{
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Auth.openAuthActivity(LoginActivity.this);
                }
            });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //如果結果沒問題，就進入 MainActivity
        if(requestCode == Auth.REQ_CODE && resultCode == RESULT_OK){
            final String authCode = data.getStringExtra(AuthActivity.KEY_CODE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String accessToken = Auth.fetchAccessToken(authCode);
                        Dribbble.login(LoginActivity.this, accessToken);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
