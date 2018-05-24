package mobi.letsplay.checklottery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import mobi.letsplay.checklottery.helper.BaseActivity;


public class Camera_QRscan extends BaseActivity implements ZXingScannerView.ResultHandler,ActivityCompat.OnRequestPermissionsResultCallback {

    private ZXingScannerView mScannerView;
    public static final String EXTRA_DATA = "EXTRA_DATA";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        QrScanner();
    }



    public void QrScanner(){
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(true);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.e("handler", rawResult.getText());

        String string = rawResult.getText();
        final Intent data = new Intent();
        data.putExtra(EXTRA_DATA, string);
        setResult(Activity.RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }
}
