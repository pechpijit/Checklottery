package mobi.letsplay.checklottery.model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;


public class NumberArrModel {

    ArrayList<String> list = new ArrayList<>();

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }
}
