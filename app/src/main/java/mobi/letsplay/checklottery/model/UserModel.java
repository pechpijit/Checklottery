package mobi.letsplay.checklottery.model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class UserModel {
    String email;
    ArrayList<CheckLotteryModel> history;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<CheckLotteryModel> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<CheckLotteryModel> history) {
        this.history = history;
    }
}
