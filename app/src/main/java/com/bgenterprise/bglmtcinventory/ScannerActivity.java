package com.bgenterprise.bglmtcinventory;

/**
 * The scanner class that handles the scanning of all QR codes for various activities.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.text.TextUtils.isDigitsOnly;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scanner;
    SharedPreferences QRPrefs;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanner = new ZXingScannerView(this);
        setContentView(scanner);
        QRPrefs = getSharedPreferences("QRPreferences", MODE_PRIVATE);
        session = new SessionManager(ScannerActivity.this);
        setTitle(QRPrefs.getString("scanner_title", ""));
    }

    @Override
    public void onResume(){
        super.onResume();
        scanner.setResultHandler(this);
        setTitle(QRPrefs.getString("scanner_title", ""));
        scanner.startCamera();
    }

    @Override
    public void onPause(){
        super.onPause();
        scanner.stopCamera();
    }

    @Override
    public void handleResult(final Result result){
        String[] cleanedData = result.getText().split("\\*");   //gets the scanned data without the * after
        String[] data = cleanedData[0].split(",");

        switch (QRPrefs.getString("required", "")){
            case "LMD_Receipt":
                try {

                    if ((data[1].charAt(0) != 'R') || (data[1].charAt(16) != '9')) {
                        Toast.makeText(getApplicationContext(), "Please scan an LMD's details to proceed", Toast.LENGTH_LONG).show();
                        finish();
                    }

                        Log.d("LMD_Receipt", data[0]+" "+data[1]+" "+data[2]);
                        session.CREATE_LMD_SESSION(data[0], data[1], data[2]);
                        startActivity(new Intent(ScannerActivity.this, NewReceipt.class));

                } catch (ArrayIndexOutOfBoundsException exception) {
                    Toast.makeText(getApplicationContext(), "Please scan the appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                } catch (StringIndexOutOfBoundsException e) {
                    Toast.makeText(getApplicationContext(), "Please scan an appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            case "Receipt":
                try {
                    Log.d("Receipt", data[0]);
                    session.CREATE_RECEIPT_SESSION(data[0]);
                    startActivity(new Intent(ScannerActivity.this, NewReceipt.class));

                } catch (ArrayIndexOutOfBoundsException exception) {
                    Toast.makeText(getApplicationContext(), "Please scan the appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                } catch (StringIndexOutOfBoundsException e) {
                    Toast.makeText(getApplicationContext(), "Please scan an appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            case "TeamMember_Receipt":
                try {

                    if (data[1].charAt(0) != 'T') {
                        Toast.makeText(getApplicationContext(), "Please scan a BG Team Member's QR code to proceed", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    Log.d("FOD_Receipt", data[0]);
                    session.CREATE_TEAM_MEMBER_SESSION(data[0], data[1]);
                    startActivity(new Intent(ScannerActivity.this, NewReceipt.class));

                } catch (ArrayIndexOutOfBoundsException exception) {
                    Toast.makeText(getApplicationContext(), "Please scan the appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                } catch (StringIndexOutOfBoundsException e) {
                    Toast.makeText(getApplicationContext(), "Please scan an appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            case "LMD_Count":
                try {

                    if ((data[1].charAt(0) != 'R') || (data[1].charAt(16) != '9')) {
                        Toast.makeText(getApplicationContext(), "Please scan an LMD's details to proceed", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    Log.d("LMD_Count", data[0]+" "+data[1]+" "+data[2]);
                    session.CREATE_LMD_SESSION(data[0], data[1], data[2]);
                    startActivity(new Intent(ScannerActivity.this, StockCount.class));

                } catch (ArrayIndexOutOfBoundsException exception) {
                    Toast.makeText(getApplicationContext(), "Please scan the appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                } catch (StringIndexOutOfBoundsException e) {
                    Toast.makeText(getApplicationContext(), "Please scan an appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            case "Product_Count":
                try{
                    //check if the product id is a number of length 9
                    if ((data[1].length() != 9) || (!isDigitsOnly(data[1]))) {
                        Toast.makeText(getApplicationContext(), "Please scan a BG product QR code to proceed", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    Log.d("Product_Count", data[0]+" "+data[1]);
                    session.CREATE_PRODUCT_SESSION(data[0], data[1]);
                    startActivity(new Intent(ScannerActivity.this, StockCount.class));

                }catch (ArrayIndexOutOfBoundsException exception) {
                    Toast.makeText(getApplicationContext(), "Please scan the appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                } catch (StringIndexOutOfBoundsException e) {
                    Toast.makeText(getApplicationContext(), "Please scan an appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;


            case "Teller_Receipt":
                try{
                    Log.d("Receipt", data[0]);

                    TellerDBHandler tellerDBHandler = new TellerDBHandler(this);
                    final SessionManager session = new SessionManager(this);
                    HashMap<String, String> allDetails = session.getAllDetails();

                    if(tellerDBHandler.checkDuplicateTellerReceipt(data[0], allDetails.get(SessionManager.KEY_TELLER_ID))){
                        //A matching receipt id was found.
                        new AlertDialog.Builder(this)
                                .setTitle("Duplicate Receipt ID")
                                .setMessage("This Receipt ID already exists in the database.")
                                .setPositiveButton("OK, Scan Another...", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        session.CLEAR_TELLER_RECEIPT_DETAILS();
                                        finish();

                                    }
                                }).show();
                    }else{
                        session.CREATE_RECEIPT_SESSION(data[0]);
                        startActivity(new Intent(ScannerActivity.this, AddTellerReceipt.class));
                    }

                }catch (ArrayIndexOutOfBoundsException exception) {
                    Toast.makeText(getApplicationContext(), "Please scan the appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                } catch (StringIndexOutOfBoundsException e) {
                    Toast.makeText(getApplicationContext(), "Please scan an appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            case "LMD_Receivable":
                try {

                    if ((data[1].charAt(0) != 'R') || (data[1].charAt(16) != '9')) {
                        Toast.makeText(getApplicationContext(), "Please scan an LMD's details to proceed", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    Log.d("LMD_Receipt", data[0]+" "+data[1]+" "+data[2]);
                    session.CREATE_LMD_SESSION(data[0], data[1], data[2]);
                    startActivity(new Intent(ScannerActivity.this, View_Receivable.class));

                } catch (ArrayIndexOutOfBoundsException exception) {
                    Toast.makeText(getApplicationContext(), "Please scan the appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                } catch (StringIndexOutOfBoundsException e) {
                    Toast.makeText(getApplicationContext(), "Please scan an appropriate LMD QR code to proceed", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            default:
                finish();
                break;
        }

        QRPrefs.edit().putString("scanner_title", "").commit();
    }
}
