package mobi.letsplay.checklottery.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import mobi.letsplay.checklottery.R;
import mobi.letsplay.checklottery.adapter.AdapterHistory;
import mobi.letsplay.checklottery.model.CheckLotteryModel;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView notWorkHistory;
    private Realm realm;
    RealmResults<CheckLotteryModel> results;
    private RealmChangeListener realmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object o) {
            invalidateView();
        }
    };

    public HistoryFragment() {
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recycleview_history);
        notWorkHistory = view.findViewById(R.id.notWorkHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);

        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmListener);
        invalidateView();
        return view;
    }

    private void invalidateView() {
        results = realm.where(CheckLotteryModel.class).sort("Id", Sort.DESCENDING).findAll();

        AdapterHistory adapterHistory = new AdapterHistory(results);
        if (!results.isEmpty()) {
            notWorkHistory.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(adapterHistory);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false){
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
        } else {
            notWorkHistory.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null);
        }

        adapterHistory.SetOnItemClickListener(new AdapterHistory.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (results.get(position).getStatus() == 1) {
                    successCheckLottery(results,position);
                } else {
                    failCheckLottery(results,position);
                }
            }
        });
    }

    private void failCheckLottery(final RealmResults<CheckLotteryModel> results, final int position) {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("เสียใจด้วย")
                .setContentText("หมายเลขสลาก "+results.get(position).getLottery()+" ไม่ถูกรางวัล")
                .setConfirmText("ปิด")
                .setCancelText("ลบ")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteRow(results,position);
                        sweetAlertDialog
                                .setTitleText("สำเร็จ")
                                .setContentText("ข้อมูลถูกลบแล้ว")
                                .setConfirmText("ปิด")
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }

    private void successCheckLottery(final RealmResults<CheckLotteryModel> results, final int position) {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("ยินดีด้วย")
                .setContentText(results.get(position).getDetail())
                .setConfirmText("ปิด")
                .setCancelText("ลบ")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteRow(results,position);
                        sweetAlertDialog
                                .setTitleText("สำเร็จ")
                                .setContentText("ข้อมูลถูกลบแล้ว")
                                .setConfirmText("ปิด")
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }

    private void deleteRow(final RealmResults<CheckLotteryModel> results, final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.get(position).deleteFromRealm();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

}

