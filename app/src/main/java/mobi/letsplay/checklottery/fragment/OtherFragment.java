package mobi.letsplay.checklottery.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import mobi.letsplay.checklottery.HomeActivity;
import mobi.letsplay.checklottery.R;
import mobi.letsplay.checklottery.SplashActivity;
import mobi.letsplay.checklottery.helper.IabHelper;
import mobi.letsplay.checklottery.helper.IabResult;
import mobi.letsplay.checklottery.helper.Inventory;
import mobi.letsplay.checklottery.helper.PrefUtils;
import mobi.letsplay.checklottery.helper.Purchase;
import mobi.letsplay.checklottery.helper.SkuDetails;
import mobi.letsplay.checklottery.model.UserModel;

public class OtherFragment extends Fragment {
    String TAG = "OtherFragment";
    Button btn_game,btn_comment,btn_share, btn_star;
    TextView appVersion;
    private IabHelper mHelper;
    private final String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj/YVW8pKJhPqskMFFbkCGJIwsJ1DRKoCIOrkvDL+XTL7BEp7fT9BYL9eDEVCaXHQIiSWcqzMTAnhAgI1uYjVjuC0F+wVoWyoAv7MV+C7Fs12rorBeNzKTjdBr9SYCY9TZ+Brehaug2G8xNrY08oguHcdmI9v5mLbhnSLyzZYMWtrl4tjAcfyXWppQIMdr7BHh/OisG7qgeLKRT5FoHix3kHaf11DxOmhNKw+jSSjZgWbT5jv+T7Y/0KYV4sREWKYXYRP6nZsnEdqnOqxiXietJkUd+pVyBnQ/3luMoS9aiAWOo5IFJ1yf2df8Z0/GdnHKQz79iX0Ll6PaHCyntKiPQIDAQAB";
    private boolean isSetup;
    private final String productID = "purchase_id";
    private Purchase purchaseOwned;
    private ImageView adsRemove;
    LoginButton loginButton;
    private CallbackManager mCallbackManagerFB;
    private FirebaseAuth mAuth;

    public OtherFragment() {
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        FacebookSdk.sdkInitialize(getActivity());
        AppEventsLogger.activateApp(getActivity());
        mAuth = FirebaseAuth.getInstance();

        mHelper = new IabHelper(getContext(), base64PublicKey);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    isSetup = !result.isFailure();
                } else {
                    isSetup = result.isSuccess();
                }
            }
        });


         loginButton = view.findViewById(R.id.login_facebook);
        btn_game = view.findViewById(R.id.btn_game);
        adsRemove = view.findViewById(R.id.adsRemove);
        btn_comment = view.findViewById(R.id.btn_comment);
        btn_share = view.findViewById(R.id.btn_share);
        btn_star = view.findViewById(R.id.btn_star);
        appVersion = view.findViewById(R.id.app_version);


        String app_version = ((HomeActivity) getActivity()).getAppversion(getActivity());
        appVersion.setText("เวอร์ชั่น "+app_version);


        setOnClickButton();

        PrefUtils utils = new PrefUtils(getActivity());
        if (utils.getPurchase()) {
            adsRemove.setImageDrawable(getResources().getDrawable(R.drawable.ic_premiuma));
        } else {
            adsRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchPurchaseFlows();
                }
            });
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            ((HomeActivity)getActivity()).setFacebookLogin(loginButton);
        }

        return view;
    }

    private void setOnClickButton() {
        btn_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=PLAY+Ltd."));
                startActivity(intent);
            }
        });

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"youremail@yahoo.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "ให้คำแนะนำแอปพลิเคชัน ตรวจหวย");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int applicationNameId = getContext().getApplicationInfo().labelRes;
                final String appPackageName = getContext().getPackageName();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(applicationNameId));
                String text = "Install this cool application: ";
                String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
                i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
                startActivity(Intent.createChooser(i, "Share link:"));
            }
        });

        btn_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=mobi.letsplay.checklottery"));
                startActivity(intent);
            }
        });
    }

    private void queryInventoryAsyncs() {
        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {

                Log.d(TAG, "	- inv.hasPurchase()   = " + inv.hasPurchase(productID));
                Log.d(TAG, "	- inv.getPurchase()   = " + inv.getPurchase(productID));
                Log.d(TAG, "	- inv.hasDetails()    = " + inv.hasDetails(productID));
                Log.d(TAG, "	- inv.getSkuDetails() = " + inv.getSkuDetails(productID));

                if (!inv.hasPurchase(productID)) {
                    PrefUtils prefUtils = new PrefUtils(getActivity());
                    prefUtils.setPurchase(false);
                } else {
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        database.child("Users").child(user.getUid()).child("purchase").setValue("1");
                    }
                    PrefUtils prefUtils = new PrefUtils(getActivity());
                    prefUtils.setPurchase(true);
                    adsRemove.setImageDrawable(getResources().getDrawable(R.drawable.ic_premiuma));
                    purchaseOwned = inv.getPurchase(productID);

                    Log.d(TAG, "	- inv.getPurchase() ...");
                    Log.d(TAG, "		.getDeveloperPayload() = " + purchaseOwned.getDeveloperPayload());
                    Log.d(TAG, "		.getItemType()         = " + purchaseOwned.getItemType());
                    Log.d(TAG, "		.getOrderId()          = " + purchaseOwned.getOrderId());
                    Log.d(TAG, "		.getOriginalJson()     = " + purchaseOwned.getOriginalJson());
                    Log.d(TAG, "		.getPackageName()      = " + purchaseOwned.getPackageName());
                    Log.d(TAG, "		.getPurchaseState()    = " + String.valueOf(purchaseOwned.getPurchaseState()));
                    Log.d(TAG, "		.getPurchaseTime()     = " + String.valueOf(purchaseOwned.getPurchaseTime()));
                    Log.d(TAG, "		.getSignature()        = " + purchaseOwned.getSignature());
                    Log.d(TAG, "		.getSku()              = " + purchaseOwned.getSku());
                    Log.d(TAG, "		.getToken()            = " + purchaseOwned.getToken());

                    if (!inv.hasDetails(productID)) return;
                    SkuDetails skuDetails = inv.getSkuDetails(productID);
                    Log.d(TAG, "	- inv.getSkuDetails() ...");
                    Log.d(TAG, "		.getDescription() = " + skuDetails.getDescription());
                    Log.d(TAG, "		.getPrice()       = " + skuDetails.getPrice());
                    Log.d(TAG, "		.getSku()         = " + skuDetails.getSku());
                    Log.d(TAG, "		.getTitle()       = " + skuDetails.getTitle());
                    Log.d(TAG, "		.getType()        = " + skuDetails.getType());
                }
            }
        });
    }

    private void launchPurchaseFlows() {
        mHelper.launchPurchaseFlow(getActivity(), productID, 10111, new IabHelper.OnIabPurchaseFinishedListener() {

            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                Log.d(TAG, "onIabPurchaseFinished");
                if (result.isFailure()) {
                    return;
                } else if (info.getSku().equals(base64PublicKey)) {
                    Toast.makeText(getContext(), "Premium Version กรุณาเคลียแอพใหม่แล้วเปิดใหม่ค่ะ", Toast.LENGTH_LONG).show();
                    purchaseOwned = info;
                    queryInventoryAsyncs();
                } else {
                    Toast.makeText(getContext(), "Premium Version กรุณาปิดแอพแล้วเปิดใหม่ค่ะ", Toast.LENGTH_LONG).show();
                    purchaseOwned = info;
                    queryInventoryAsyncs();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (isSetup) mHelper.dispose();
        mHelper = null;
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isSetup) {
            boolean blnResult = mHelper.handleActivityResult(requestCode, resultCode, data);
            if (blnResult) return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

