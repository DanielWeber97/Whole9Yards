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

public class ClientsActivity extends AppCompatActivity  {
    private boolean canExitDialog;
    Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        canExitDialog= false;


        Button showPopup = (Button) findViewById(R.id.popup);
        showPopup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClientsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.clientpopup,null);
                final EditText name = (EditText)mView.findViewById(R.id.name);
                final EditText number = (EditText)mView.findViewById(R.id.phoneNum);
                final EditText address = (EditText)mView.findViewById(R.id.address);

                builder.setPositiveButton("confirm",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(name.getText().toString().equals("") || number.getText().toString().equals("")
                        || address.getText().toString().equals("")){
                            showToast("Please fill out all of the fields");
                        } else if(!verify(number.getText().toString())){
                            showToast("Please enter a valid phone number with the area code");
                        } else{
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

    }

    public void showToast(String input){
        if (t != null){
            t.cancel();
        }
        t = Toast.makeText(this,input, Toast.LENGTH_SHORT);
        t.show();
    }

    public boolean verify(String input) {
        String s = "";
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) <= '9' && input.charAt(i) >= '0') {
                s += input.charAt(i);
            }
        }

        if (!s.matches("[0-9]{10}")) {
             return false;
        } else {
            return true;
        }
    }

}
