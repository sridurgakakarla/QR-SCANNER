package net.smallacademy.qrapp;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
//package com.ccc.gsheetitem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


import java.io.*;


public class Scanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView resultData;
    String scannedData,scannedData1,allcontents[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scannView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this,scannView);
        resultData = findViewById(R.id.resultsOfQr);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData.setText(result.getText());
                        if(result.getText() != null)
                            scannedData1=result.getText();
                        scannedData=separate(scannedData1);

                        //scannsedData=""+resultData;
                        addItemToSheet();
                      //  addItemToSheet();



                    }
                });

            }
        });


        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();

    }

    public void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(Scanner.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();




            }
        }).check();
    }

    public static String separate(String QRcode){
        System.out.println("I am here");
        System.out.println(QRcode);
        String refcode = QRcode.substring(0,6);
        System.out.println(refcode);
        //expiry=qrcode.substr(qrcode.length-9,8)
        String expiry = QRcode.substring(33,41);
        String expirydate=expiry.substring(0,4)+"/"+expiry.substring(4,6)+"/"+expiry.substring(6,8);
        System.out.println(expiry);


        String	patchid=QRcode.substring(20,25);
// 	 patchid= QRcode.length()-22;
        System.out.println(patchid);
        String manufacturing=QRcode.substring(25,33);
        System.out.println(manufacturing);
        String manufacturingdate = manufacturing.substring(0,4)+"/"+manufacturing.substring(4,6)+"/"+manufacturing.substring(6,8);
        System.out.println(manufacturingdate);
        String totstring = patchid+","+refcode+","+manufacturingdate+","+expirydate;
        System.out.println(refcode+","+patchid+","+manufacturingdate+","+expirydate);
        saveRecord(refcode,patchid,manufacturingdate,expirydate,"./Read.csv");

    return totstring;
    }


    ////////////////save record function starts/////////////////////////////////////////////////////////
    public static void saveRecord(String refcode,String patchid, String manu,String exp,String filepath) {
        try {
            FileWriter fw = new FileWriter(filepath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(patchid + "," + refcode + "," + manu + "," + exp);
            System.out.println("hi");

            pw.flush();
            pw.close();
            //JoptionPane.showMessageDialog(null,"file saved");
        } catch (Exception e) {
            e.printStackTrace();
            //JoptionPane.showMessageDialog(null,"file not saved");
        }

    }







    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
      //  final String name = editTextItemName.getText().toString().trim();
       // final String brand = editTextBrand.getText().toString().trim();




        StringRequest stringRequest = new StringRequest(Request.Method.POST, "your google script url",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(Scanner.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                   System.out.println(scannedData);
                 allcontents = scannedData.split(",");

                System.out.println("@@@@@@@@@@@@@@"+allcontents[2]);

                //here we pass params

                parmas.put("sdata",scannedData1);
                parmas.put("patchid",allcontents[0]);
               parmas.put("refcode",allcontents[1]);
               parmas.put("manufacturing",allcontents[2]);
               parmas.put("expiry",allcontents[3]);

              System.out.println(allcontents[0]+"@@@@"+allcontents[1]+"@@@@@@"+allcontents[2]+"@@"+allcontents[3]);
             // System.out.println(scannedData);
             //  parmas.put("sdata",scannedData);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }


    //addItemToSheet();
    //@Override
//    public void onClick(View v) {
//
//        if(v==buttonAddItem){
//            addItemToSheet();
//
//            //Define what to do when button is clicked
//        }
//    }
}
