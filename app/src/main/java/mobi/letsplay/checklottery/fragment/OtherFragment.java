package mobi.letsplay.checklottery.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mobi.letsplay.checklottery.HomeActivity;
import mobi.letsplay.checklottery.R;

public class OtherFragment extends Fragment {

    Button btn_game,btn_comment,btn_share, btn_star;
    TextView appVersion;
    private DatabaseReference myRefComment;

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

        myRefComment = FirebaseDatabase.getInstance().getReference().child("LotteryApp").child("Comment").push();

        btn_game = view.findViewById(R.id.btn_game);
        btn_comment = view.findViewById(R.id.btn_comment);
        btn_share = view.findViewById(R.id.btn_share);
        btn_star = view.findViewById(R.id.btn_star);
        appVersion = view.findViewById(R.id.app_version);


        String app_version = ((HomeActivity) getActivity()).getAppversion(getActivity());
        appVersion.setText("เวอร์ชั่น "+app_version);


        setOnClickButton();
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

}

