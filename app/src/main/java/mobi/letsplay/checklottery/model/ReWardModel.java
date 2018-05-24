package mobi.letsplay.checklottery.model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class ReWardModel {
    private ArrayList<String> reward1;
    private ArrayList<String> reward2;
    private ArrayList<String> reward3;
    private ArrayList<String> reward4;
    private ArrayList<String> reward5;
    private ArrayList<String> rewardFront3;
    private ArrayList<String> rewardLast3;
    private ArrayList<String> rewardLast2;
    private ArrayList<String> reward1Close;

    public ArrayList<String> getReward1() {
        return reward1;
    }

    public void setReward1(ArrayList<String> reward1) {
        this.reward1 = reward1;
    }

    public ArrayList<String> getReward2() {
        return reward2;
    }

    public void setReward2(ArrayList<String> reward2) {
        this.reward2 = reward2;
    }

    public ArrayList<String> getReward3() {
        return reward3;
    }

    public void setReward3(ArrayList<String> reward3) {
        this.reward3 = reward3;
    }

    public ArrayList<String> getReward4() {
        return reward4;
    }

    public void setReward4(ArrayList<String> reward4) {
        this.reward4 = reward4;
    }

    public ArrayList<String> getReward5() {
        return reward5;
    }

    public void setReward5(ArrayList<String> reward5) {
        this.reward5 = reward5;
    }

    public ArrayList<String> getRewardFront3() {
        return rewardFront3;
    }

    public void setRewardFront3(ArrayList<String> rewardFront3) {
        this.rewardFront3 = rewardFront3;
    }

    public ArrayList<String> getRewardLast3() {
        return rewardLast3;
    }

    public void setRewardLast3(ArrayList<String> rewardLast3) {
        this.rewardLast3 = rewardLast3;
    }

    public ArrayList<String> getRewardLast2() {
        return rewardLast2;
    }

    public void setRewardLast2(ArrayList<String> rewardLast2) {
        this.rewardLast2 = rewardLast2;
    }

    public ArrayList<String> getReward1Close() {
        return reward1Close;
    }

    public void setReward1Close(ArrayList<String> reward1Close) {
        this.reward1Close = reward1Close;
    }
}
