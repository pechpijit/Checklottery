package mobi.letsplay.checklottery;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;

import mobi.letsplay.checklottery.helper.AppStatus;
import mobi.letsplay.checklottery.helper.BaseActivity;

import static java.security.AccessController.getContext;

public class SortNumberActivity extends BaseActivity implements OnPageChangeListener
        , OnLoadCompleteListener, OnDrawListener {

    private PDFView pdfView;
    String TAG = "SortNumberActivity";
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_number);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pdfView = findViewById(R.id.pdfView);

        String data = getIntent().getExtras().getString("data");
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            showProgressDialog("กำลังโหลดข้อมูล...");
            getPdf(data);
        } else {
            checkError(7);
        }
    }

    private void getPdf(final String data) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("LotteryApp/" + data.trim() + ".pdf");
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess " + uri);
                displayFromFile1(uri.toString(), data + ".pdf");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure " + exception);
                Toast.makeText(SortNumberActivity.this, "ไม่สามารถโหลดข้อมูลเรียงเบอร์ได้", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
    }

    private void displayFromFile1(String fileUrl, String fileName) {
        pdfView.enableDoubletap(true);
        pdfView.enableSwipe(true);
        pdfView.fileFromLocalStorage(this, this, this, fileUrl, fileName);
        hideProgressDialog();

        if (!pdfView.isShown()) {
            hideProgressDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }
}
