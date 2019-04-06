package com.example.whole9yards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ClientPopup extends AppCompatDialogFragment {
    private EditText name;
    private EditText number;
    private EditText address;
    private PopupInterface popupInterface;
    private boolean canExit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = getActivity().getLayoutInflater();
        final View v = li.inflate(R.layout.clientpopup, null);
        name = (EditText) v.findViewById(R.id.name);
        address = v.findViewById(R.id.address);
        number = v.findViewById(R.id.phoneNum);
        canExit = false;

        builder.setView(v).setTitle("Enter Client Info").setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String userName = name.getText().toString();
                String userNumber = number.getText().toString();
                verify(userNumber);
                String userAddress = address.getText().toString();
                popupInterface.applyTexts(userName,userNumber,userAddress);
                popupInterface.canExit(canExit);
            }
        });
        return builder.create();

    }

    public void verify(String input) {
        String s = "";
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) <= '9' && input.charAt(i) >= '0') {
                s += input.charAt(i);
            }
        }
        Log.v("mytag", s);

        if (!s.matches("[0-9]{10}")) {
            Toast.makeText(getActivity(), "Please enter a valid number with area code" + s, Toast.LENGTH_SHORT).show();
            canExit = false;
        } else {
            canExit = true;
        }
        Log.v("mytag", "In verify. very = " + canExit);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            popupInterface = (PopupInterface) context;
        } catch (ClassCastException c){
            throw new ClassCastException(context.toString()+"must implement PopupInterface");
        }
    }

    public interface PopupInterface {
        String[] applyTexts(String name, String number, String address);
        void canExit(boolean validNumber);
    }
}
