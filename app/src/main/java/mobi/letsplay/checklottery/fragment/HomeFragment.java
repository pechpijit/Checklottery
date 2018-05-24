package mobi.letsplay.checklottery.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.angmarch.views.NiceSpinner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

import mobi.letsplay.checklottery.BuildConfig;
import mobi.letsplay.checklottery.HomeActivity;
import mobi.letsplay.checklottery.R;
import mobi.letsplay.checklottery.SortNumberActivity;
import mobi.letsplay.checklottery.adapter.AdapterReward;
import mobi.letsplay.checklottery.helper.AppStatus;
import mobi.letsplay.checklottery.model.ReWardModel;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class HomeFragment extends Fragment {

    String TAG = "HomeFragment";
    Uri imageUri = null;
    private DatabaseReference myRefDate;
    private DatabaseReference myRefReward;
    private TextView txtReward1,txtRewardFront3_1,txtRewardFront3_2,txtRewardLast3_1,txtRewardLast3_2,txtRewardLast2,txtReward1Close_1,txtReward1Close_2;
    private TextView txtMoneyReward1,txtMoneyReward2,txtMoneyReward3,txtMoneyReward4,txtMoneyReward5,txtMoneyRewardFront3,txtMoneyRewardLast3,txtMoneyRewardLast2,txtMoneyReward1Close;
    NiceSpinner niceSpinner;
    String[] monthArr = {"มกราคม", "กุมภาพันธ์","มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
    String[] sortKey;
    private RecyclerView recycleview_reward2,recycleview_reward3,recycleview_reward4, recycleview_reward5;

    public HomeFragment() {
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);

        setTextMoney(view);

        setUpRecycleView(view);

        ((HomeActivity)getActivity()).imgRefrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    ((HomeActivity)getActivity()).showProgressDialog("กำลังโหลดข้อมูล");
                    getDate();
                } else {
                    ((HomeActivity)getActivity()).checkError(7);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            ((HomeActivity)getActivity()).showProgressDialog("กำลังโหลดข้อมูล");
            getDate();
        } else {
            ((HomeActivity)getActivity()).checkError(7);
        }
    }

    private void initView(View view) {
        niceSpinner = view.findViewById(R.id.nice_date);
        txtReward1 = view.findViewById(R.id.txtReward1);
        txtRewardFront3_1 = view.findViewById(R.id.txtRewardFront3_1);
        txtRewardFront3_2 = view.findViewById(R.id.txtRewardFront3_2);
        txtRewardLast3_1 = view.findViewById(R.id.txtRewardLast3_1);
        txtRewardLast3_2 = view.findViewById(R.id.txtRewardLast3_2);
        txtRewardLast2 = view.findViewById(R.id.txtRewardLast2);
        txtReward1Close_1 = view.findViewById(R.id.txtReward1Close_1);
        txtReward1Close_2 = view.findViewById(R.id.txtReward1Close_2);
    }

    private void setUpRecycleView(View view) {
        recycleview_reward2 = view.findViewById(R.id.recycleview_reward2);
        recycleview_reward3 = view.findViewById(R.id.recycleview_reward3);
        recycleview_reward4 = view.findViewById(R.id.recycleview_reward4);
        recycleview_reward5 = view.findViewById(R.id.recycleview_reward5);

        recycleview_reward2.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recycleview_reward2.setHasFixedSize(true);
        setLayMange(recycleview_reward2);

        recycleview_reward3.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recycleview_reward3.setHasFixedSize(true);
        setLayMange(recycleview_reward3);

        recycleview_reward4.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recycleview_reward4.setHasFixedSize(true);
        setLayMange(recycleview_reward4);

        recycleview_reward5.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recycleview_reward5.setHasFixedSize(true);
        setLayMange(recycleview_reward5);
    }

    private void setTextMoney(View view) {
        DecimalFormat df = new DecimalFormat("###,###,###.##");
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        txtMoneyReward1 = view.findViewById(R.id.money_reward1);
        txtMoneyReward2 = view.findViewById(R.id.money_reward2);
        txtMoneyReward3 = view.findViewById(R.id.money_reward3);
        txtMoneyReward4 = view.findViewById(R.id.money_reward4);
        txtMoneyReward5 = view.findViewById(R.id.money_reward5);
        txtMoneyRewardFront3 = view.findViewById(R.id.money_rewardFront3);
        txtMoneyRewardLast3 = view.findViewById(R.id.money_rewardLast3);
        txtMoneyRewardLast2 = view.findViewById(R.id.money_rewardLast2);
        txtMoneyReward1Close = view.findViewById(R.id.money_reward1Close);

        txtMoneyReward1.setText("รางวัลละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_reward1)))+" บาท");
        txtMoneyReward2.setText("5 รางวัลๆละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_reward2)))+" บาท");
        txtMoneyReward3.setText("10 รางวัลๆละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_reward3)))+" บาท");
        txtMoneyReward4.setText("50 รางวัลๆละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_reward4)))+" บาท");
        txtMoneyReward5.setText("100 รางวัลๆละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_reward5)))+" บาท");

        txtMoneyRewardFront3.setText("2 รางวัลๆละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_rewardFront3_1)))+" บาท");
        txtMoneyRewardLast3.setText("2 รางวัลๆละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_rewardLast3_1)))+" บาท");
        txtMoneyRewardLast2.setText("1 รางวัลๆละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_rewardLast2)))+" บาท");
        txtMoneyReward1Close.setText("2 รางวัลๆละ "+df.format(new BigDecimal(getActivity().getResources().getInteger(R.integer.money_reward1Close_1)))+" บาท");

    }

    private void getReward(final String date) {
        myRefReward = FirebaseDatabase.getInstance().getReference("LotteryApp").child("Lottery").child(date);
        myRefReward.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ReWardModel model = dataSnapshot.getValue(ReWardModel.class);
                txtReward1.setText(model.getReward1().get(0));
                txtRewardFront3_1.setText(model.getRewardFront3().get(0));
                txtRewardFront3_2.setText(model.getRewardFront3().get(1));
                txtRewardLast3_1.setText(model.getRewardLast3().get(0));
                txtRewardLast3_2.setText(model.getRewardLast3().get(1));
                txtRewardLast2.setText(model.getRewardLast2().get(0));
                txtReward1Close_1.setText(model.getReward1Close().get(0));
                txtReward1Close_2.setText(model.getReward1Close().get(1));

                Collections.sort(model.getReward2());
                Collections.sort(model.getReward3());
                Collections.sort(model.getReward4());
                Collections.sort(model.getReward5());

                recycleview_reward2.setAdapter(new AdapterReward(model.getReward2()));
                recycleview_reward3.setAdapter(new AdapterReward(model.getReward3()));
                recycleview_reward4.setAdapter(new AdapterReward(model.getReward4()));
                recycleview_reward5.setAdapter(new AdapterReward(model.getReward5()));

                ((HomeActivity)getActivity()).hideProgressDialog();

                enableListener();
                setSharedImage(model,date);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ((HomeActivity)getActivity()).hideProgressDialog();
                Log.w(TAG, "loadPost:onCancelled getReward", databaseError.toException());
                ((HomeActivity)getActivity()).checkError(databaseError.getCode());
            }
        });

    }

    private void setLayMange(RecyclerView recycleview_reward2) {
        recycleview_reward2.setLayoutManager(new GridLayoutManager(getContext(), 4,GridLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }

    private void setSharedImage(ReWardModel model, final String date) {
        try {
            imageUri = getOutputMediaFileUri(getActivity(), writeTextOnDrawable(model,date).getBitmap());
            ((HomeActivity)getActivity()).imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    startActivity(Intent.createChooser(intent, "Share picture with..."));
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        ((HomeActivity)getActivity()).btnNumSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    checkPermission(date);
                } else {
                    ((HomeActivity)getActivity()).checkError(7);
                }
            }
        });
    }

    private void enableListener() {
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    ((HomeActivity)getActivity()).showProgressDialog("กำลังโหลดข้อมูล");
                    getReward(sortKey[position]);
                } else {
                    ((HomeActivity)getActivity()).checkError(7);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

                getReward(sortKey[0]);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ((HomeActivity)getActivity()).hideProgressDialog();
                Log.w(TAG, "loadPost:onCancelled getDate", databaseError.toException());
                ((HomeActivity)getActivity()).checkError(databaseError.getCode());
            }
        });
    }

    private BitmapDrawable writeTextOnDrawable(ReWardModel model, String date) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inMutable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.lottery_share, options);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.green));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(getResources().getColor(R.color.colorPrimary));
        paint1.setTextAlign(Paint.Align.CENTER);

        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                paint1.setTextSize(convertToPixels(getActivity(), 30));
                paint.setTextSize(convertToPixels(getActivity(), 36));
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                paint1.setTextSize(convertToPixels(getActivity(), 30));
                paint.setTextSize(convertToPixels(getActivity(), 36));
                break;
            case DisplayMetrics.DENSITY_HIGH:
                paint1.setTextSize(convertToPixels(getActivity(), 30));
                paint.setTextSize(convertToPixels(getActivity(), 36));
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                paint1.setTextSize(convertToPixels(getActivity(), 30));
                paint.setTextSize(convertToPixels(getActivity(), 36));
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                paint1.setTextSize(convertToPixels(getActivity(), 18));
                paint.setTextSize(convertToPixels(getActivity(), 22));
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                paint1.setTextSize(convertToPixels(getApplicationContext(), 14));
                paint.setTextSize(convertToPixels(getApplicationContext(), 18));
                break;
            default:
                paint1.setTextSize(convertToPixels(getApplicationContext(), 18));
                paint.setTextSize(convertToPixels(getApplicationContext(), 22));
                break;
        }

        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String dmy = "\t\t\tงวดวันที่ "+day + " " + monthArr[Integer.parseInt(month)-1] + " " + String.valueOf(Integer.parseInt(year)+543);

        String reWard = dmy;
        String reWard1 = model.getReward1().get(0);
        String reWard2 = "\t\t\t\t"+model.getRewardLast2().get(0);
        String reWard3 = model.getRewardFront3().get(0)+"\t"+model.getRewardFront3().get(1);
        String reWard4 = model.getRewardLast3().get(0)+"\t"+model.getRewardLast3().get(1);

        Rect textRect = new Rect();
        paint1.getTextBounds(reWard, 0, reWard.length(), textRect);
        paint.getTextBounds(reWard1, 0, reWard1.length(), textRect);
        paint.getTextBounds(reWard2, 0, reWard2.length(), textRect);
        paint.getTextBounds(reWard3, 0, reWard3.length(), textRect);
        paint.getTextBounds(reWard4, 0, reWard4.length(), textRect);

        Canvas canvas = new Canvas(bm);

        canvas.drawText(reWard, (bm.getWidth() / 2) - 50, bm.getHeight() / 6, paint1);
        canvas.drawText(reWard1, (float) ((bm.getWidth() / 4.70)), (float) (bm.getHeight() / 2.4), paint);
        canvas.drawText(reWard2, (float) ((bm.getWidth() / 1.38)), (float) (bm.getHeight() / 2.4), paint);
        canvas.drawText(reWard3, (float) ((bm.getWidth() / 4.40)), (float) (bm.getHeight() / 1.34), paint);
        canvas.drawText(reWard4, (float) ((bm.getWidth() / 1.46)), (float) (bm.getHeight() / 1.34), paint);

        return new BitmapDrawable(getResources(), bm);
    }

    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f);
    }

    private Uri getOutputMediaFileUri(Context mContext, Bitmap bitmap) throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            return FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(mContext, bitmap));
        } else {
            return Uri.fromFile(getOutputMediaFile(mContext, bitmap));
        }
    }

    private File getOutputMediaFile(Context mContext, Bitmap bitmap) throws IOException {

        File mediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "LotteryApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "Lottery_" + timeStamp;
        mediaStorageDir = File.createTempFile(imageFileName, ".png", mediaStorageDir);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(mediaStorageDir));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.flush();
        os.close();
        return mediaStorageDir;
    }

    private void checkPermission(final String date) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                startActivity(new Intent(getActivity(), SortNumberActivity.class).putExtra("data",date));
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                checkPermission(date);
            }
        };

        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("จำเป็นต้องเปิดสิทธิ์การใช้งานพื้นที่เก็บข้อมูล เพื่อเข้าสู่หน้าแสดผลเรียงเบอร์")
                .setRationaleConfirmText("ตกลง")
                .setDeniedMessage("กรุณาเปิดสิทธิ์ที่ [Setting] > [การอนุญาต] > [พื้นที่เก็บข้อมูล] > [เปิด]")
                .setGotoSettingButtonText("ตกลง")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

}

