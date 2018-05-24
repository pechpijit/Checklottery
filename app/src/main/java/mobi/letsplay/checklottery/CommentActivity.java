package mobi.letsplay.checklottery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mobi.letsplay.checklottery.helper.BaseActivity;

public class CommentActivity extends BaseActivity {

    private DatabaseReference myRefDate;
    private DatabaseReference myRefReward;

    NiceSpinner niceSpinner;
    String[] monthArr = {"มกราคม", "กุมภาพันธ์","มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
    String[] sortKey;
    Button btn_check_lottery, btn_check_lottery_qr;
    String date;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_lottery);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        niceSpinner = findViewById(R.id.nice_date);
        btn_check_lottery = findViewById(R.id.btn_check_lottery);
        btn_check_lottery_qr = findViewById(R.id.btn_check_lottery_qr);

        getDate();
    }

    private void getDate() {

        myRefDate = FirebaseDatabase.getInstance().getReference("LotteryApp").child("Lottery");
        myRefDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> keyDate = new ArrayList<String>();
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    keyDate.add(childDataSnapshot.getKey());
                }

                sortKey = new String[keyDate.size()];
                Arrays.sort(keyDate.toArray(sortKey), Collections.reverseOrder());

                String[] keyText = new String[sortKey.length];
                for (int i = 0; i < sortKey.length; i++) {
                    String year = sortKey[i].substring(0, 4);
                    String month = sortKey[i].substring(4, 6);
                    String day = sortKey[i].substring(6, 8);
                    keyText[i] = day + " " + monthArr[Integer.parseInt(month)] + " " + year;
                }

                List<String> dataset = new LinkedList<>(Arrays.asList(keyText));
                niceSpinner.attachDataSource(dataset);

                date = sortKey[0];

                setListenerView();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setListenerView() {

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date = sortKey[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_check_lottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReward();
            }
        });

        btn_check_lottery_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                startActivityForResult(new Intent(CommentActivity.this, Camera_QRscan.class),1);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                checkPermission();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("กรุณาเปิดสิทธิ์การใช้งานกล้อง เพื่อแสกน QR Code")
                .setRationaleConfirmText("ตกลง")
                .setDeniedMessage("กรุณาเปิดสิทธิ์ที่ [Setting] > [Permission]")
                .setGotoSettingButtonText("ตกลง")
                .setPermissions(Manifest.permission.CAMERA)
                .check();
    }

    private void getReward() {
        myRefDate = FirebaseDatabase.getInstance().getReference("LotteryApp").child("Lottery").child(date);
        myRefReward.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkNumber(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkNumber(DataSnapshot dataSnapshot) {
        String reward1 = dataSnapshot.child("01reward1").child("0").getValue(String.class);
        String rewardFront3_1 = dataSnapshot.child("03rewardFront3").child("0").getValue(String.class);
        String rewardFront3_2 = dataSnapshot.child("03rewardFront3").child("1").getValue(String.class);
        String rewardLast3_1 = dataSnapshot.child("04rewardLast3").child("0").getValue(String.class);
        String rewardLast3_2 = dataSnapshot.child("04rewardLast3").child("1").getValue(String.class);
        String rewardLast2 = dataSnapshot.child("02rewardLast2").child("0").getValue(String.class);
        String reward1Close_1 = dataSnapshot.child("05reward1Close").child("0").getValue(String.class);
        String reward1Close_2 = dataSnapshot.child("05reward1Close").child("1").getValue(String.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra(Camera_QRscan.EXTRA_DATA);
                Toast.makeText(this, ""+result, Toast.LENGTH_SHORT).show();
            }
        }
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
