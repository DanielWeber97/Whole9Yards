package com.example.whole9yards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ClientsActivity extends AppCompatActivity implements ClientPopup.PopupInterface {
    private boolean canExitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        canExitDialog= false;
    }


    public void addClient(View v) {
     //   ClientPopup popup = new ClientPopup();
     //   popup.show(getSupportFragmentManager(),"client popup");
        showDialog();
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.clientpopup);
        // Button b = getView().findViewById(R.id.okButton);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Log.v("mytag", "canPrint: ");

    }



    @Override
    public String[] applyTexts(String name, String number, String address) {
            Log.v("myTag", name + number+address);
            return new String[]{name,number,address};
    }

    @Override
    public void canExit(boolean b){
        canExitDialog = b;
        Log.v("mytag", "in canExit, in ClientActivity. canExitDialog = " + b);
    }
}
