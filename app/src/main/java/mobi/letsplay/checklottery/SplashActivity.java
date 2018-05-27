package mobi.letsplay.checklottery;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.ads.*;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import io.realm.Sort;
import mobi.letsplay.checklottery.helper.BaseActivity;
import mobi.letsplay.checklottery.helper.PrefUtils;
import mobi.letsplay.checklottery.model.CheckLotteryModel;
import mobi.letsplay.checklottery.model.CheckLotteryModelFirebase;
import mobi.letsplay.checklottery.model.UserModel;

public class SplashActivity extends BaseActivity {
    String TAG = "SplashActivity";
    TextView btnAfter;
    LinearLayout progressBar;
    LinearLayout viewLogin;
    private InterstitialAd interstitialAd;
    private CallbackManager mCallbackManagerFB;

    private FirebaseAuth mAuth;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_splash);
//        AdSettings.addTestDevice("nSJfQlWIoVXArTU6aycvNhGY5IA=");
        interstitialAd = new InterstitialAd(this, getString(R.string.YOUR_PLACEMENT_ID));

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progress_bar);
        viewLogin = findViewById(R.id.viewLogin);
        btnAfter = findViewById(R.id.btnAfter);

        printHashKey();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        PrefUtils utils = new PrefUtils(SplashActivity.this);
                        if (!utils.getPurchase()) {
                            facebookAds();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            showViewLogin();
                        }
                    }
                }, 2000);
    }

    private void facebookAds() {
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                progressBar.setVisibility(View.INVISIBLE);
                showViewLogin();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                progressBar.setVisibility(View.INVISIBLE);
                showViewLogin();
                Toast.makeText(SplashActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                interstitialAd.show();
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
        interstitialAd.loadAd();
    }

    private void showViewLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            viewLogin.setVisibility(View.VISIBLE);
            btnAfter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });

            setFacebookLogin();
        }
    }

    private void setFacebookLogin() {
        LoginButton loginButton = findViewById(R.id.login_facebook);
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
                                handleFacebookAccessToken(result.getAccessToken(), email);
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

    private void handleFacebookAccessToken(final AccessToken token, final String email) {
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
                            database.child("Users").child(mAuth.getUid()).child("email").setValue(email);
                            onSocialLoginSuccess();
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

    private void onSocialLoginSuccess() {
        PrefUtils prefUtils = new PrefUtils(this);

        if (prefUtils.getPurchase()) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                database.child("Users").child(user.getUid()).child("purchase").setValue("1");
            }
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("history");
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<CheckLotteryModel> keyDate = new ArrayList<CheckLotteryModel>();
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        String date = childDataSnapshot.child("DateTime").getValue(String.class);
                        String detail = childDataSnapshot.child("Detail").getValue(String.class);
                        String id = childDataSnapshot.child("Id").getValue(String.class);
                        String lottery = childDataSnapshot.child("lottery").getValue(String.class);
                        Long status = childDataSnapshot.child("status").getValue(Long.class);

                        CheckLotteryModel firebase = new CheckLotteryModel();
                        firebase.setDateTime(date);
                        firebase.setDetail(detail);
                        firebase.setId(id);
                        firebase.setLottery(lottery);
                        firebase.setStatus(status.intValue());
                        keyDate.add(firebase);
                    }

                    savehistoryrealm(keyDate);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void savehistoryrealm(final ArrayList<CheckLotteryModel> keyDate) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.delete(CheckLotteryModel.class);
        realm.commitTransaction();

        realm.executeTransactionAsync(
                new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (final CheckLotteryModel s : keyDate) {
                            CheckLotteryModel model = realm.createObject(CheckLotteryModel.class, UUID.randomUUID().toString());
                            model.setLottery(s.getLottery());
                            model.setDetail(s.getDetail());
                            model.setStatus(s.getStatus());
                            model.setDateTime(s.getDateTime());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Update History Success.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManagerFB.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

}
