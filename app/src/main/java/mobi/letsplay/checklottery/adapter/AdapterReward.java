package mobi.letsplay.checklottery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import mobi.letsplay.checklottery.R;


public class AdapterReward extends RecyclerView.Adapter<AdapterReward.VersionViewHolder> {
    ArrayList<String> number;

    public AdapterReward(ArrayList<String> number) {
        this.number = number;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_controller, viewGroup, false);

        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
        versionViewHolder.txtNumber.setText(number.get(i));
    }

    @Override
    public int getItemCount() {
        return number == null ? 0 : number.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumber;
        public VersionViewHolder(View itemView) {
            super(itemView);
            txtNumber = itemView.findViewById(R.id.txtNumber);
        }
    }

}
