package pl.tomaszjader.codescanner;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button helpButton;
    private Button scanCodeButton;
    private Button confirmButton;
    private EditText passwordField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.helpButton = (Button) findViewById(R.id.helpButton);
        this.helpButton.setOnClickListener(view -> openHelpActivity());
        final Activity activity = this;
        this.scanCodeButton = findViewById(R.id.scanCodeButton);
        this.scanCodeButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(activity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        });
        this.confirmButton = findViewById(R.id.confirmButton);
        this.confirmButton.setOnClickListener(view -> {
            Context context = MainActivity.this;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                return;
            }
            CodeChecker codeChecker = new CodeChecker(passwordField.getText().toString());
            if (!codeChecker.checkCodeValid()) {
                Toast.makeText(context, "Code not valid", Toast.LENGTH_LONG).show();
                return;
            }
            String response;
            response = codeChecker.send();
            if (response != null) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("response", response);
                startActivity(intent);
            } else {
                Toast.makeText(context, "The call was not successful", Toast.LENGTH_LONG).show();
            }
        });
        this.passwordField = findViewById(R.id.password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                passwordField.setText(result.getContents());
                Toast.makeText(this, "Password " + passwordField.getText(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        StringBuilder idDec = new StringBuilder(Long.toString(getDec(id)));
        while (idDec.length() < 10) idDec.insert(0, "0");
        this.passwordField.setText(idDec.toString());
        Toast.makeText(this, "Scanned NFC: " + idDec.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter},
                    new String[][]{new String[]{
                            NfcA.class.getName(),
                            NfcB.class.getName(),
                            NfcF.class.getName(),
                            NfcV.class.getName()
                    }});
        }
    }

    private void openHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffL;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
}

