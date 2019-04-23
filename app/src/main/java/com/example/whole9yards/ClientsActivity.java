package com.example.whole9yards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClientsActivity extends AppCompatActivity {
    Toast t;
    ScrollView scroll;
    LinearLayout list;
    DatabaseReference dbClients;
    ArrayList<Client> localClients;
    final int IMAGE_CODE = 123;
    Button remove;
    boolean selected;
    int selectedIndex;
    String selectedId;
    HashMap<String, String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        dbClients = FirebaseDatabase.getInstance().getReference("dbClients");
        localClients = new ArrayList<Client>();
        createPopup();
        scroll = findViewById(R.id.scroll);
        list = findViewById(R.id.listInScroll);
        remove = findViewById(R.id.remove);
        selected = false;
        selectedIndex = -1;
        ids = new HashMap<String, String>();
        selectedId = "";

    }

    @Override
    protected void onStart() {
        super.onStart();
        dbClients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idArg = snapshot.child("clientId").getValue().toString();
                    String nameArg = snapshot.child("clientName").getValue().toString();
                    String numArg = snapshot.child("clientNumber").getValue().toString();
                    String addressArg = snapshot.child("clientAddress").getValue().toString();
                    Client c = new Client(idArg, nameArg, numArg, addressArg);
                    localClients.add(c);
                    ids.put(nameArg, idArg);
                    //for(DataSnapshot field : snapshot.getChildren()) {
                    //    Log.v("mytag", field.getValue().toString());
                    //}
                }
                for (Client c : localClients) {
                    addCliToList(c);
                }
                dbClients.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //Helper function to prevent toasts from waiting for old toasts
    //to finish before displaying.
    public void showToast(String input) {
        if (t != null) {
            t.cancel();
        }
        t = Toast.makeText(this, input, Toast.LENGTH_SHORT);
        t.show();
    }

    //This function ensures that the String input is a 10 digit phone number.
    //It will also extract digits out of numbers with other characters.
    //For example, numbers in the format (###) ###-####, ###-###-####,
    //and even #a##b####x#-## will be converted into ##########.
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

    public void createClient(String name, String num, String address) {
        String id = dbClients.push().getKey();
        Client c = new Client(id, name, num, address);
        dbClients.child(id).setValue(c);
        addCliToList(c);
        showToast("Client successfully added!");
        ids.put(name, id);
    }

    public void addCliToList(final Client c) {
        final CardView cv = new CardView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(20, 10, 20, 10);
        cv.setLayoutParams(lp);
        cv.setRadius(15);

        LinearLayout l = new LinearLayout(getApplicationContext());
        l.setLayoutParams(lp);
        l.setOrientation(LinearLayout.VERTICAL);
       final TextView t1 = new TextView(getApplicationContext());
        TextView t2 = new TextView(getApplicationContext());
        TextView t3 = new TextView(getApplicationContext());
        t1.setText(c.clientName);
        t2.setText(c.clientNumber);
        t3.setText(c.clientAddress);
        l.addView(t1);
        l.addView(t2);
        l.addView(t3);
        cv.addView(l);
        list.addView(cv);
        cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!selected || list.getChildAt(selectedIndex) == cv){
                    if (cv.getCardBackgroundColor().getDefaultColor() == Color.RED) {
                        cv.setCardBackgroundColor(Color.WHITE);
                        remove.setVisibility(View.GONE);
                        selected = false;
                    } else {
                        selectedIndex = list.indexOfChild(cv);
                        selectedId = ids.get(t1.getText().toString());
                        if(selectedId == null){
                            selectedId= ids.get(c.clientName);
                        }
                        cv.setCardBackgroundColor(Color.RED);
                        remove.setVisibility(View.VISIBLE);
                        selected = true;
                    }
                    return false;
                }
                return false;
            }
        });
    }

    public void deleteClient(View v) {
        if (selected) {
            list.removeViewAt(selectedIndex);
            dbClients.child(selectedId).removeValue();
            selected = false;
            remove.setVisibility(View.GONE);
        }
    }

    //This function creates the dialog box that allows the user to enter
    //information about the client.
    public void createPopup() {
        Button showPopup = (Button) findViewById(R.id.popup);
        showPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClientsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.clientpopup, null);
                final EditText name = (EditText) mView.findViewById(R.id.name);
                final EditText number = (EditText) mView.findViewById(R.id.phoneNum);
                final EditText address = (EditText) mView.findViewById(R.id.address);


                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
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
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ensures that all required fields have been filled out
                        if (name.getText().toString().equals("") || number.getText().toString().equals("")
                                || address.getText().toString().equals("")) {
                            showToast("Please fill out all of the fields");
                        } else if (!verify(number.getText().toString())) {
                            showToast("Please enter a valid phone number with the area code");
                        } else {
                            createClient(name.getText().toString(), number.getText().toString(), address.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}
