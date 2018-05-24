package mobi.letsplay.checklottery.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mobi.letsplay.checklottery.R;
import mobi.letsplay.checklottery.model.ItemRowModel;
import mobi.letsplay.checklottery.model.StaticModel;

public class MultiFeedAdapter extends RecyclerView.Adapter {
    String TAG = "StatFragment";
    public static final int TYPE_TIME = 0;
    public static final int TYPE_FEED = 1;

    ArrayList<ItemRowModel> rowModels;
    Context context;
    int sizeRow = 0;

    public MultiFeedAdapter(Context context, ArrayList<ItemRowModel> rowModels, int size) {
        this.rowModels = rowModels;
        this.context = context;
        this.sizeRow = size;
    }

    @Override
    public int getItemViewType(int position) {
        if (rowModels.get(position).getType() == 0) {
            return TYPE_TIME;
        }else{
            return TYPE_FEED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_TIME) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new ViewHolderHeader(itemView);
        } else {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
            return new ViewHolderContent(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderHeader) {
            setViewHeader(((ViewHolderHeader) holder),position);
        } else if (holder instanceof ViewHolderContent) {
            setViewContent(((ViewHolderContent) holder),position);
        }
    }

    @Override
    public int getItemCount() {
        return sizeRow;
    }

    class ViewHolderHeader extends RecyclerView.ViewHolder {
        TextView txtHeader;
        public ViewHolderHeader(View itemView) {
            super(itemView);
            txtHeader = itemView.findViewById(R.id.txtHeader);
        }
    }

    private void setViewHeader(ViewHolderHeader holder, int position) {
        int status = rowModels.get(position).getStatus();
        String title = "";
        if (status == 1) {
            title = "เลขหน้า 3 ตัว";
        } else if (status == 2) {
            title = "เลขท้าย 3 ตัว";
        } else if (status  == 3) {
            title = "เลขท้าย 2 ตัว";
        }
        holder.txtHeader.setText(title);
    }

    class ViewHolderContent extends RecyclerView.ViewHolder {
        TextView[] txtNumber;
        TextView[] txtCount;
        LinearLayout[] back;

        public ViewHolderContent(View itemView) {
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

    private void setViewContent(ViewHolderContent holder, int position) {

        for (int i = 0; i < rowModels.get(position).getModel().size(); i++) {
            holder.txtNumber[i].setText(rowModels.get(position).getModel().get(i).getNumber());
            holder.txtCount[i].setText(rowModels.get(position).getModel().get(i).getCount() + " ครั้ง");

            if (rowModels.get(position).getModel().get(i).getCount() > 1) {
                holder.txtNumber[i].setTextColor(context.getResources().getColor(R.color.white));
                holder.txtCount[i].setTextColor(context.getResources().getColor(R.color.white));
            }

            switch (rowModels.get(position).getModel().get(i).getCount()) {
                case 0:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count1));
                    break;
                case 1:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count1));
                    break;
                case 2:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count2));
                    break;
                case 3:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count3));
                    break;
                case 4:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count4));
                    break;
                case 5:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count5));
                    break;
                case 6:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count6));
                    break;
                case 7:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count7));
                    break;
                case 8:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.count8));
                    break;
                default:
                    holder.back[i].setBackgroundColor(context.getResources().getColor(R.color.red));
                    break;
            }
        }
    }

}
