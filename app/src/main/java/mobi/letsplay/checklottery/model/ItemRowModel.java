package mobi.letsplay.checklottery.model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
@IgnoreExtraProperties
public class ItemRowModel {
    int status;
    int type;
    ArrayList<StaticModel> model;

    public ItemRowModel() {
    }

    public ItemRowModel(int status,int type) {
        this.status = status;
        this.model = null;
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<StaticModel> getModel() {
        return model;
    }

    public void setModel(ArrayList<StaticModel> model) {
        this.model = model;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
