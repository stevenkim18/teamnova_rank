package com.teamnova.teamnova_rank;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public void progressOn(){
        BaseApplication.getInstance().progressON(this,null);
    }

    public void progressOn(String message){
        BaseApplication.getInstance().progressON(this,message);
    }

    public void progressOFF(){
        BaseApplication.getInstance().progressOFF();
    }
}
