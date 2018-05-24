package mobi.letsplay.checklottery.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import mobi.letsplay.checklottery.R;
import mobi.letsplay.checklottery.SplashActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialog2;

    public void showProgressDialog(String message) {

        if (pDialog == null) {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#03a9f4"));
            pDialog.setContentText("");
            pDialog.setTitleText(message);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            pDialog.setCancelable(true);
                            pDialog.setCanceledOnTouchOutside(true);
                            pDialog.setContentText("หากไม่มีการตอบสนองเป็นเวลานาน\nกรุณาตรวจสอบอินเทอร์เน็ตของท่าน\nและลองใหม่อีกครั้ง");
                            pDialog.setCancelText("ตกลก");
                            pDialog.setCancelClickListener(null);
                        }
                    }, 100000);
        }

        pDialog.show();
    }

    public void showProgressDialog2(String message) {

        if (pDialog2 == null) {
            pDialog2 = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog2.getProgressHelper().setBarColor(Color.parseColor("#03a9f4"));
            pDialog2.setTitleText(message);
            pDialog2.setCancelable(false);
            pDialog2.setCanceledOnTouchOutside(false);
            pDialog2.show();
            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    pDialog2.dismiss();
                }
            }.start();
        }

        pDialog.show();
    }

    public void hideProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public String getAppversion(Activity context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public void checkError(int status) {
        if (status == 7){
            dialogResultError("ไม่สามารถเชื่อมอินเทร์เน็ตได้\nกรุณาตรวจสอบอินเทอร์เน็ต\nและลองใหม่อีกครั้ง");
        } else if (status == 8) {
            dialogResultError("เกิดข้อผิดพลาดบางอย่าง กรุณาลองใหม่อีกครั้งหรือติดต่อผู้พัฒนา");
        } else if (status == -4) {
            dialogResultError("การดำเนินการต้องถูกยกเลิก\nเนื่องจากเครือข่ายตัดการเชื่อมต่อ\nกรุณาตรวจสอบอินเทอร์เน็ต\nและลองใหม่อีกครี้งภายหลัง");
        } else if (status == -8) {
            dialogResultError("มีการเรียกข้อมูลใหม่มากเกินไป\nกรุณาลองใหม่อีกครั้งภายหลัง");
        } else if (status == -24) {
            dialogResultError("ไม่สามารถดำเนินการได้\nเนื่องจากข้อผิดพลาดของเครือข่าย\nกรุณาตรวจสอบอินเทอร์เน็ต\nและลองใหม่อีกครี้งภายหลัง");
        } else if (status == -2) {
            dialogResultError("เซิร์ฟเวอร์ระบุว่า\nการดำเนินการนี้ล้มเหลว\nกรุณาลองใหม่อีกครี้ง");
        } else if (status == -3) {
            dialogResultError("สิทธิ์เข้าถึงหรือทำรายการนี้\nกรุณาลองใหม่อีกครี้งภายหลัง");
        } else if (status == -10) {
            dialogResultError("บริการไม่พร้อมใช้งาน\nกรุณาลองใหม่อีกครี้งภายหลัง");
        } else if (status == -999) {
            dialogResultError("เกิดข้อผิดพลาดที่ไม่ทราบสาเหตุ\nกรุณารายงานข้อผิดพลาดนี้แก่ผู้พัฒนา\nเพื่อทำการตรวจสอบ ขอบคุณค่ะ");
        } else if (status == -25) {
            dialogResultError("การเขียนข้อมูลถูกยกเลิก\nกรุณาลองใหม่อีกครี้งภายหลัง");
        }
    }

    public void dialogResultError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("ขออภัย")
                .setContentText(string)
                .setConfirmText("ตกลง")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
