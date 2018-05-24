package mobi.letsplay.checklottery.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.RealmResults;
import mobi.letsplay.checklottery.R;
import mobi.letsplay.checklottery.model.CheckLotteryModel;


public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.VersionViewHolder> {
    RealmResults<CheckLotteryModel> results;
    OnItemClickListener clickListener;

    public AdapterHistory(RealmResults<CheckLotteryModel> results) {
        this.results = results;
    }

    String[] monthArr = {"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_history, viewGroup, false);

        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
        versionViewHolder.txtNumber.setText(results.get(i).getLottery());
        versionViewHolder.txtType.setText(results.get(i).getStatus() == 0 ? "ไม่ถูกรางวัล" : "ถูกรางวัล");
        if (results.get(i).getStatus() == 1) {
            versionViewHolder.txtType.setTextColor(Color.parseColor("#8BC34A"));
        }

        String[] dmy = results.get(i).getDateTime().split("-");
        StringBuilder builder = new StringBuilder();
        builder.append("วันที่");
        builder.append(" ");
        builder.append(dmy[0]);
        builder.append(" ");
        builder.append(monthArr[Integer.parseInt(dmy[1]) - 1]);
        builder.append(" ");
        builder.append(Integer.parseInt(dmy[2].substring(0, 4)) + 543);
        builder.append(" ");
        builder.append("เวลา");
        builder.append(" ");
        builder.append(dmy[2].substring(5));
        builder.append(" ");
        builder.append("น.");
        versionViewHolder.txtDateTime.setText(builder.toString());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtNumber;
        TextView txtType;
        TextView txtDateTime;
        CardView iView;

        public VersionViewHolder(View itemView) {
            super(itemView);

            txtNumber = itemView.findViewById(R.id.txtNumber);
            txtType = itemView.findViewById(R.id.txtType);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            iView = itemView.findViewById(R.id.itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
