package mobi.letsplay.checklottery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobi.letsplay.checklottery.fragment.HistoryFragment;
import mobi.letsplay.checklottery.fragment.HomeFragment;
import mobi.letsplay.checklottery.fragment.OtherFragment;
import mobi.letsplay.checklottery.fragment.StatFragment;
import mobi.letsplay.checklottery.helper.AppStatus;
import mobi.letsplay.checklottery.helper.BaseActivity;
import mobi.letsplay.checklottery.helper.PrefUtils;
import mobi.letsplay.checklottery.model.UserModel;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

import com.facebook.ads.*;

import org.json.JSONObject;

public class HomeActivity extends BaseActivity {
    String TAG = "HomeActivity";
    ViewPager viewPager;
    TabLayout tabLayout;
    LinearLayout bottomBar;
    LinearLayout bottomBarStat;
    FrameLayout iconBar;
    public FloatingActionButton btnCheckNumber;
    public CardView btnNumSort;
    public CardView btnFront3, btnLast3,btnLast2;
    private AdView adView;
    private CallbackManager mCallbackManagerFB;

    private FirebaseAuth mAuth;

    String lottery = "357 130 980 527 273 654 787 131 720 064 318 870 007 388 106 947 624 799 495 373 616 836 626 303 961 831 165 425 180 971 726 611 172 647 345 679 115 302 061 386 835 584 226 489 626 878 121 218 008 396 949 573 766 973 057 020 918 324 560 450 464 128 066 807 511 663 890 304 596 366 513 873 682 040 976 824 885 692 194 280 583 493 335 334 966 366 538 983 734 552 169 609 804 251 111 775 975 382 238 403 228 008 546 390 877 855 530 426 699 312 625 999 532 031 264 246 140 250 238 181 241 028 106 757 457 134 175 918 435 209 260 403";

    Toolbar toolbar;
    TextView txtTitle;
    public ImageView imgRefrash;
    public ImageView imgShare;


    private int[] tabIcons = {
            R.drawable.baseline_list_alt_white_48,
            R.drawable.baseline_history_white_48,
            R.drawable.baseline_star_rate_white_48,
            R.drawable.baseline_more_horiz_white_48
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_tab_animation);
//        setFront3();
//                AdSettings.addTestDevice("nSJfQlWIoVXArTU6aycvNhGY5IA=");
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.tabanim_toolbar);
        setSupportActionBar(toolbar);

        PrefUtils utils = new PrefUtils(this);
        if (!utils.getPurchase()) {
            setAds();
        }


        bottomBar = findViewById(R.id.bottomBar);
        bottomBarStat = findViewById(R.id.bottomBarStat);
        btnFront3 = findViewById(R.id.btnFront3);
        btnLast3 = findViewById(R.id.btnLast3);
        btnLast2 = findViewById(R.id.btnLast2);
        iconBar = findViewById(R.id.iconBar);
        btnCheckNumber = findViewById(R.id.btnCheckNumber);
        btnNumSort = findViewById(R.id.btnNumSort);

        imgRefrash = findViewById(R.id.imgRefrash);
        imgShare = findViewById(R.id.imgShare);
        txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setText(getString(R.string.tab_name_1));

        btnCheckNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    startActivity(new Intent(HomeActivity.this, CheckLotteryActivity.class));
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                } else {
                    checkError(7);
                }
            }
        });

        viewPager = findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        setTabListener();
    }

    private void setAds() {
        // Instantiate an AdView view
        adView = new AdView(this, "818785894958013_923105731192695", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(HomeActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error : " + adError.getErrorCode());
                Log.d(TAG, "Error : " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        adView.loadAd();
    }

    private void setTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    btnCheckNumber.show();
                    visibleTab1();
                } else {
                    btnCheckNumber.hide();
                    inVisibleTab1();
                }

                if (tab.getPosition() == 2) {
                    visibleTab3();
                } else {
                    inVisibleTab3();
                }

                switch (tab.getPosition()) {
                    case 0:
                        setAnimTitleBar(R.string.tab_name_1);
                        break;
                    case 1:
                        setAnimTitleBar(R.string.tab_name_2);
                        break;
                    case 2:
                        setAnimTitleBar(R.string.tab_name_3);
                        break;
                    case 3:
                        setAnimTitleBar(R.string.tab_name_4);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setAnimTitleBar(final int name) {
        txtTitle.animate()
                .translationY(txtTitle.getHeight())
                .setDuration(100)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        txtTitle.setText(getString(name));
                        txtTitle.animate()
                                .translationY(0)
                                .setDuration(100)
                                .alpha(1.0f);
                    }
                });
    }

    private void visibleTab1() {
        bottomBar.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(300);
        iconBar.animate()
                .translationX(0)
                .alpha(1.0f)
                .setDuration(300);

        btnCheckNumber.setEnabled(true);
        btnNumSort.setEnabled(true);
    }

    private void inVisibleTab1() {
        bottomBar.animate()
                .translationY(bottomBar.getHeight())
                .setDuration(300)
                .alpha(0.0f);

        iconBar.animate()
                .translationX(imgShare.getWidth())
                .setDuration(300)
                .alpha(0.0f);

        btnCheckNumber.setEnabled(false);
        btnNumSort.setEnabled(false);
    }

    private void visibleTab3() {
        bottomBarStat.setVisibility(View.VISIBLE);
        bottomBarStat.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(300);

        btnFront3.setEnabled(true);
        btnLast3.setEnabled(true);
        btnLast2.setEnabled(true);
    }

    private void inVisibleTab3() {
        bottomBarStat.animate()
                .translationY(bottomBarStat.getHeight())
                .setDuration(300)
                .alpha(0.0f);

        btnFront3.setEnabled(false);
        btnLast3.setEnabled(false);
        btnLast2.setEnabled(false);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment());
        adapter.addFrag(new HistoryFragment());
        adapter.addFrag(new StatFragment());
        adapter.addFrag(new OtherFragment());
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    private void setFront3() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            UserModel userProfile = new UserModel();
            userProfile.setEmail(user.getEmail());
            database.child("Users").child(user.getUid()).setValue(userProfile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(HomeActivity.this, "add User success", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this, "onFailure "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

//
//        String[] arrFront3 = lottery.split(" ");
//
//        ArrayList<String> list = new ArrayList<>();
//
//        for (int i = 0; i < arrFront3.length; i++) {
//            for (int j = 0; j < arrFront3.length; j++) {
//                if (arrFront3[i].equals(arrFront3[j])) {
//                    list.add("1");
//                }
//            }
//            database.child(arrFront3[i]).setValue(list);
//            list.clear();
//        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    public void setFacebookLogin(final LoginButton loginButton) {
//        loginButton.setVisibility(View.VISIBLE);
        loginButton.setReadPermissions("email", "public_profile");
        mCallbackManagerFB = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManagerFB, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                final LoginResult result = loginResult;
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v(TAG, response.toString());
                                String email = object.optString("email");
                                handleFacebookAccessToken(result.getAccessToken(), email,loginButton);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "last_name,first_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token, final String email, final LoginButton loginButton) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog("กำลังเข้าสู่ระบบ");
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "handleFacebookAccessToken:success");
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            UserModel userProfile = new UserModel();
                            userProfile.setEmail(email);
                            database.child("Users").child(mAuth.getUid()).setValue(userProfile);
                            onSocialLoginSuccess(loginButton);
                        } else if (task.getException() instanceof FirebaseNetworkException) {
                            Log.d(TAG, "handleFacebookAccessToken:FirebaseNetworkException");
                            checkError(7);
                        } else {
                            Log.w(TAG, "handleFacebookAccessToken:failure", task.getException());
                            checkError(8);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void onSocialLoginSuccess(LoginButton loginButton) {
        hideProgressDialog();
        loginButton.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "เข้าสู่ระบบสำเร็จแล้ว", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (mCallbackManagerFB != null) {
//                mCallbackManagerFB.onActivityResult(requestCode, resultCode, data);
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }


}
