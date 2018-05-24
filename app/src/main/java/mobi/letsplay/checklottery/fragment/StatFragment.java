package mobi.letsplay.checklottery.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mobi.letsplay.checklottery.HomeActivity;
import mobi.letsplay.checklottery.R;
import mobi.letsplay.checklottery.adapter.MultiFeedAdapter;
import mobi.letsplay.checklottery.helper.AppStatus;
import mobi.letsplay.checklottery.model.ItemRowModel;
import mobi.letsplay.checklottery.model.NumberArrModel;
import mobi.letsplay.checklottery.model.StaticModel;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class StatFragment extends Fragment {
    String TAG = "StatFragment";
    private RecyclerView mFeedList;
    private RelativeLayout mSuspensionBar;
    private TextView mSuspensionTv;
    private int mCurrentPosition = 0;
    MultiFeedAdapter adapter;
    private int mSuspensionHeight;
    LinearLayoutManager linearLayoutManager;
    private DatabaseReference myRefStatic;
    ArrayList<ItemRowModel> rowModels;
    int[] positionHeader = new int[3];


    public StatFragment() {
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        mSuspensionBar = view.findViewById(R.id.suspension_bar);
        mSuspensionTv = view.findViewById(R.id.tv_time);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        mFeedList = view.findViewById(R.id.feed_list);
        mFeedList.setLayoutManager(linearLayoutManager);
        mFeedList.setHasFixedSize(true);

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            ((HomeActivity)getActivity()).showProgressDialog("กำลังโหลดข้อมูล");
            getData();
        } else {
            ((HomeActivity)getActivity()).checkError(7);
        }


        return view;
    }

    private void getData() {
        myRefStatic = FirebaseDatabase.getInstance().getReference("LotteryApp").child("Static");
        myRefStatic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rowModels = new ArrayList<>();
                int index = 0;

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    int status = Integer.parseInt(childDataSnapshot.getKey().substring(0, 2));
                    String key = childDataSnapshot.getKey();

                    positionHeader[index] = rowModels.size();
                    index += 1;
                    rowModels.add(new ItemRowModel(status,0));

                    ArrayList<StaticModel> listNumber = new ArrayList<>();

                    for (DataSnapshot snapshot: dataSnapshot.child(key).getChildren()) {
                        int count = 0;
                        for (DataSnapshot item:dataSnapshot.child(key).child(snapshot.getKey()).getChildren()) {
                            count += 1;
                        }

                        StaticModel staticModel = new StaticModel();
                        staticModel.setNumber(snapshot.getKey());
                        staticModel.setCount(count);
                        listNumber.add(staticModel);

                        if (listNumber.size() == 5) {

                            final ArrayList<StaticModel> listNumberTemp = new ArrayList<>(listNumber);
                            listNumber.clear();

                            ItemRowModel model = new ItemRowModel();
                            model.setModel(listNumberTemp);
                            model.setStatus(status);
                            model.setType(1);
                            rowModels.add(model);

                        }
                    }

                    ItemRowModel model = new ItemRowModel();
                    model.setModel(listNumber);
                    model.setStatus(status);
                    model.setType(1);
                    rowModels.add(model);
                }

                initView(rowModels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ((HomeActivity)getActivity()).hideProgressDialog();
                Log.w(TAG, "loadPost:onCancelled getDate", databaseError.toException());
                ((HomeActivity)getActivity()).checkError(databaseError.getCode());
            }
        });
    }

    private void initView(ArrayList<ItemRowModel> rowModels) {
        Log.d(TAG, "rowModels size : "+rowModels.size());
        Log.d(TAG, "rowModels 0 type : "+rowModels.get(0).getType());
        Log.d(TAG, "rowModels 0 status : "+rowModels.get(0).getStatus());

        adapter = new MultiFeedAdapter(getActivity(),rowModels,rowModels.size());
        mFeedList.setAdapter(adapter);

        mFeedList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mSuspensionHeight = mSuspensionBar.getHeight();
            }

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (adapter.getItemViewType(mCurrentPosition + 1) == MultiFeedAdapter.TYPE_TIME) {
                    View view = linearLayoutManager.findViewByPosition(mCurrentPosition + 1);
                    if (view != null) {
                        if (view.getTop() <= mSuspensionHeight) {
                            mSuspensionBar.setY(-(mSuspensionHeight - view.getTop()));
                        } else {
                            mSuspensionBar.setY(0);
                        }
                    }
                }

                if (mCurrentPosition != linearLayoutManager.findFirstVisibleItemPosition()) {
                    mCurrentPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    mSuspensionBar.setY(0);

                    updateSuspensionBar();
                }
            }
        });

        updateSuspensionBar();
        setListenerCardBtn();

        ((HomeActivity)getActivity()).hideProgressDialog();
    }

    private void setListenerCardBtn() {

        ((HomeActivity)getActivity()).btnFront3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((HomeActivity)getActivity()).showProgressDialog2("กำลังโหลดข้อมูล");
                mFeedList.scrollToPosition(positionHeader[0]);
                linearLayoutManager.scrollToPositionWithOffset(positionHeader[0],0);
            }
        });

        ((HomeActivity)getActivity()).btnLast3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((HomeActivity)getActivity()).showProgressDialog2("กำลังโหลดข้อมูล");
                mFeedList.scrollToPosition(positionHeader[1]);
                linearLayoutManager.scrollToPositionWithOffset(positionHeader[1],0);
            }
        });

        ((HomeActivity)getActivity()).btnLast2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((HomeActivity)getActivity()).showProgressDialog2("กำลังโหลดข้อมูล");
                mFeedList.scrollToPosition(positionHeader[2]);
                linearLayoutManager.scrollToPositionWithOffset(positionHeader[2],0);
            }
        });
    }


    private void updateSuspensionBar() {
        mSuspensionTv.setText(getTime(mCurrentPosition));
//        setAnimTextHeader();
    }
    String title = "";
    private String getTime(int position) {
        if (rowModels.get(position).getType() == 0 && rowModels.get(position).getStatus() == 1) {
            title = "เลขหน้า 3 ตัว";
        }else  if (rowModels.get(position).getType() == 0 && rowModels.get(position).getStatus() == 2) {
            title = "เลขท้าย 3 ตัว";
        }else  if (rowModels.get(position).getType() == 0 && rowModels.get(position).getStatus() == 3) {
            title = "เลขท้าย 2 ตัว";
        }
        return title;
    }

    private void setAnimTextHeader() {
        mSuspensionTv.animate()
                .translationY(mSuspensionTv.getHeight())
                .setDuration(300)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mSuspensionTv.setText(getTime(mCurrentPosition));
                        mSuspensionTv.animate()
                                .translationY(0)
                                .setDuration(100)
                                .alpha(1.0f);
                    }
                });

    }

}

