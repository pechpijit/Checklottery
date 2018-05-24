package mobi.letsplay.checklottery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mobi.letsplay.checklottery.R;
import mobi.letsplay.checklottery.model.StaticModel;


public class AdapterContentStat extends RecyclerView.Adapter<AdapterContentStat.VersionViewHolder> {
    ArrayList<StaticModel> models;
    Context mContext;
    public AdapterContentStat(Context mContext,ArrayList<StaticModel> model) {
        this.models = model;
        this.mContext = mContext;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_content_stat, viewGroup, false);

        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VersionViewHolder v, final int i) {


    }

    @Override
    public int getItemCount() {
        return models == null ? 0 : models.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView[] txtNumber;
        TextView[] txtCount;
        LinearLayout[] back;

        public VersionViewHolder(View itemView) {
            super(itemView);

            txtNumber = new TextView[5];
            txtCount = new TextView[5];
            back = new LinearLayout[5];

            txtNumber[0] = itemView.findViewById(R.id.txtNumber1);
            txtNumber[1] = itemView.findViewById(R.id.txtNumber2);
            txtNumber[2] = itemView.findViewById(R.id.txtNumber3);
            txtNumber[3] = itemView.findViewById(R.id.txtNumber4);
            txtNumber[4] = itemView.findViewById(R.id.txtNumber5);

            txtCount[0] = itemView.findViewById(R.id.txtCount1);
            txtCount[1] = itemView.findViewById(R.id.txtCount2);
            txtCount[2] = itemView.findViewById(R.id.txtCount3);
            txtCount[3] = itemView.findViewById(R.id.txtCount4);
            txtCount[4] = itemView.findViewById(R.id.txtCount5);

            back[0] = itemView.findViewById(R.id.back1);
            back[1] = itemView.findViewById(R.id.back2);
            back[2] = itemView.findViewById(R.id.back3);
            back[3] = itemView.findViewById(R.id.back4);
            back[4] = itemView.findViewById(R.id.back5);
        }
    }




}
