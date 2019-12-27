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

import java.io.*;


public class Scanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView resultData;

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
                            separate(result.getText());

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

    public static void separate(String QRcode){
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

    }
