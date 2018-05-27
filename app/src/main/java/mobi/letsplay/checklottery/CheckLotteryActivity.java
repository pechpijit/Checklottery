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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.angmarch.views.NiceSpinner;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import mobi.letsplay.checklottery.helper.AppStatus;
import mobi.letsplay.checklottery.helper.BaseActivity;
import mobi.letsplay.checklottery.model.CheckLotteryModel;
import mobi.letsplay.checklottery.model.ReWardModel;

public class CheckLotteryActivity extends BaseActivity {

    private DatabaseReference myRefDate;
    private DatabaseReference myRefReward;
    private Realm realm;
    NiceSpinner niceSpinner;
    String[] monthArr = {"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
    String[] sortKey;
    Button btn_check_lottery, btn_check_lottery_qr;
    String date;
    EditText input_number;
    String number;
    CheckLotteryModel model;

    int nextId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_lottery);
        realm = Realm.getDefaultInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        niceSpinner = findViewById(R.id.nice_date);
        btn_check_lottery = findViewById(R.id.btn_check_lottery);
        btn_check_lottery_qr = findViewById(R.id.btn_check_lottery_qr);
        input_number = findViewById(R.id.input_number);

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            getDate();
        } else {
            checkError(7);
        }

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
                    keyText[i] = day + " " + monthArr[Integer.parseInt(month)-1] + " " + String.valueOf(Integer.parseInt(year)+543);
                }

                List<String> dataset = new LinkedList<>(Arrays.asList(keyText));
                niceSpinner.attachDataSource(dataset);

                date = sortKey[0];

                setListenerView();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                checkError(databaseError.getCode());
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
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    if (input_number.getText().toString().trim().length() > 5) {
                        showProgressDialog("กำลังตรวจสอบข้อมูล...");
                        number = input_number.getText().toString().trim();
                        getReward();
                    } else {
                        new SweetAlertDialog(CheckLotteryActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("ขออภัย")
                                .setContentText("กรุณากรอกเลขให้ครบ 6 หลัก")
                                .setConfirmText("ตกลง")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    }
                } else {
                    checkError(7);
                }

            }
        });

        btn_check_lottery_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    checkPermission();
                } else {
                    checkError(7);
                }
            }
        });
    }

    private void checkPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                startActivityForResult(new Intent(CheckLotteryActivity.this, Camera_QRscan.class), 1);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                checkPermission();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("จำเป็นต้องเปิดสิทธิ์การใช้งานกล้องถ่ายรูป เพื่อแสกน QR Code")
                .setRationaleConfirmText("ตกลง")
                .setDeniedMessage("กรุณาเปิดสิทธิ์ที่ [Setting] > > [การอนุญาต] > [กล้องถ่ายรูป] > [เปิด]")
                .setGotoSettingButtonText("ตกลง")
                .setPermissions(Manifest.permission.CAMERA)
                .check();
    }

    private void getReward() {
        myRefReward = FirebaseDatabase.getInstance().getReference("LotteryApp").child("Lottery").child(date);
        myRefReward.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkNumber(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                checkError(databaseError.getCode());
            }
        });
    }

    private void checkNumber(DataSnapshot dataSnapshot) {
        ReWardModel model = dataSnapshot.getValue(ReWardModel.class);

        String reward1 = model.getReward1().get(0);
        ArrayList<String> reward2 = model.getReward2();
        ArrayList<String> reward3 = model.getReward3();
        ArrayList<String> reward4 = model.getReward4();
        ArrayList<String> reward5 = model.getReward5();
        String rewardFront3_1 = model.getRewardFront3().get(0);
        String rewardFront3_2 = model.getRewardFront3().get(1);
        String rewardLast3_1 = model.getRewardLast3().get(0);
        String rewardLast3_2 = model.getRewardLast3().get(1);
        String rewardLast2 = model.getRewardLast2().get(0);
        String reward1Close_1 = model.getReward1Close().get(0);
        String reward1Close_2 = model.getReward1Close().get(1);


        StringBuilder textDetail = new StringBuilder();
        boolean rewardOver = false;
        int money = 0;

        //รางวัลที่1
        if (number.equals(reward1)) {
            textDetail.append("คุณถูกรางวัลที่ 1 : " + reward1);
            money += getResources().getInteger(R.integer.money_reward1);
            rewardOver = true;
        }

        //รางวัลข้างเคียงรางวัลที่ 1
        if (number.equals(reward1Close_1)) {
            textDetail.append("คุณถูกรางวัลข้างเคียงรางวัลที่ 1 : " + reward1Close_1);
            money += getResources().getInteger(R.integer.money_reward1Close_1);
            rewardOver = true;
        }

        //รางวัลข้างเคียงรางวัลที่ 1
        if (number.equals(reward1Close_2)) {
            textDetail.append("คุณถูกรางวัลข้างเคียงรางวัลที่ 1 : " + reward1Close_2);
            money += getResources().getInteger(R.integer.money_reward1Close_2);
            rewardOver = true;
        }

        //รางวัลที่ 2
        for (String rw: reward2) {
            if (number.equals(rw)) {
                if (rewardOver) {
                    textDetail.append("\nและ ");
                }
                textDetail.append("คุณถูกรางวัลที่ 2 : " + rw);
                money += getResources().getInteger(R.integer.money_reward2);
                rewardOver = true;
                break;
            }
        }

        //รางวัลที่ 3
        for (String rw: reward3) {
            if (number.equals(rw)) {
                if (rewardOver) {
                    textDetail.append("\nและ ");
                }
                textDetail.append("คุณถูกรางวัลที่ 3 : " + rw);
                money += getResources().getInteger(R.integer.money_reward3);
                rewardOver = true;
                break;
            }
        }

        //รางวัลที่ 4
        for (String rw: reward4) {
            if (number.equals(rw)) {
                if (rewardOver) {
                    textDetail.append("\nและ ");
                }
                textDetail.append("คุณถูกรางวัลที่ 4 : " + rw);
                money += getResources().getInteger(R.integer.money_reward4);
                rewardOver = true;
                break;
            }
        }

        //รางวัลที่ 5
        for (String rw: reward5) {
            if (number.equals(rw)) {
                if (rewardOver) {
                    textDetail.append("\nและ ");
                }
                textDetail.append("คุณถูกรางวัลที่ 5 : " + rw);
                money += getResources().getInteger(R.integer.money_reward5);
                rewardOver = true;
                break;
            }
        }

        //รางวัลเลขหน้า 3 ตัว
        if (number.substring(0,3).equals(rewardFront3_1)) {
            if (rewardOver) {
                textDetail.append("\nและ ");
            }
            textDetail.append("คุณถูกรางวัลเลขหน้า 3 ตัว : " + rewardFront3_1);
            money += getResources().getInteger(R.integer.money_rewardFront3_1);
            rewardOver = true;
        }

        //รางวัลเลขหน้า 3 ตัว
        if (number.substring(0,3).equals(rewardFront3_2)) {
            if (rewardOver) {
                textDetail.append("\nและ ");
            }
            textDetail.append("คุณถูกรางวัลเลขหน้า 3 ตัว : " + rewardFront3_2);
            money += getResources().getInteger(R.integer.money_rewardFront3_2);
            rewardOver = true;
        }

        //รางวัลเลขท้าย 3 ตัว
        if (number.substring(3,6).equals(rewardLast3_1)) {
            if (rewardOver) {
                textDetail.append("\nและ ");
            }
            textDetail.append("คุณถูกรางวัลเลขท้าย 3 ตัว : " + rewardLast3_1);
            money += getResources().getInteger(R.integer.money_rewardLast3_1);
            rewardOver = true;
        }

        //รางวัลเลขท้าย 3 ตัว
        if (number.substring(3,6).equals(rewardLast3_2)) {
            if (rewardOver) {
                textDetail.append("\nและ ");
            }
            textDetail.append("คุณถูกรางวัลเลขท้าย 3 ตัว : " + rewardLast3_2);
            money += getResources().getInteger(R.integer.money_rewardLast3_2);
            rewardOver = true;
        }

        //รางวัลเลขท้าย 2 ตัว
        if (number.substring(4,6).equals(rewardLast2)) {
            if (rewardOver) {
                textDetail.append("\nและ ");
            }
            textDetail.append("คุณถูกรางวัลเลขท้าย 2 ตัว : " + rewardLast2);
            money += getResources().getInteger(R.integer.money_rewardLast2);
            rewardOver = true;
        }

        DecimalFormat df = new DecimalFormat("###,###,###.##");
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        hideProgressDialog();
        if (rewardOver) {
            textDetail.append("\nรวมมูลค่า " + df.format(new BigDecimal(money)) + " บาท");
            insertHistoryCheckLottery(number,textDetail.toString(),1,currentDateandTime);
            successCheckLottery(textDetail.toString(),money);
        } else {
            insertHistoryCheckLottery(number,textDetail.toString(),0,currentDateandTime);
            failCheckLottery();
        }
        input_number.setText("");
    }

    private void failCheckLottery() {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("เสียใจด้วย")
                .setContentText("หมายเลขสลาก "+number+" ไม่ถูกรางวัล")
                .setConfirmText("ปิด")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void successCheckLottery(String textDetail, int money) {

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("ยินดีด้วย")
                .setContentText(textDetail)
                .setConfirmText("ปิด")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void insertHistoryCheckLottery(final String number, final String textDetail, final int type, final String DateandTime) {
        realm.beginTransaction();
        realm.executeTransactionAsync(
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String id = UUID.randomUUID().toString();
                        CheckLotteryModel model = realm.createObject(CheckLotteryModel.class, id);
                        model.setLottery(number);
                        model.setDetail(textDetail);
                        model.setStatus(type);
                        model.setDateTime(DateandTime);

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("history").child(id);
                            database.child("Id").setValue(id);
                            database.child("lottery").setValue(number);
                            database.child("status").setValue(type);
                            database.child("Detail").setValue(textDetail);
                            database.child("DateTime").setValue(DateandTime);
                        }

                        realm.insertOrUpdate(model);
                    }
                });
        realm.commitTransaction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra(Camera_QRscan.EXTRA_DATA);
                String[] rewardArr = result.split("-");

                if (rewardArr.length > 3) {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        showProgressDialog("กำลังตรวจสอบข้อมูล...");
                        number = rewardArr[3];
                        getReward();
                    } else {
                        checkError(7);
                    }
                } else {
                    new SweetAlertDialog(CheckLotteryActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("ขออภัย")
                            .setContentText("ไม่สามารถตรวจสอบข้อมูลจากคิวอาร์โค้ดได้\nกรุณาลองใหม่อีกครั้ง")
                            .setConfirmText("ปิด")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
