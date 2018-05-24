package mobi.letsplay.checklottery.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class CheckLotteryModel extends RealmObject{
    @PrimaryKey
    private int Id;

    @Required
    private String lottery;

    @Required
    private String Detail;

    private int typ;

    @Index
    private String DateTime;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getLottery() {
        return lottery;
    }

    public void setLottery(String lottery) {
        this.lottery = lottery;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public int getStatus() {
        return typ;
    }

    public void setStatus(int status) {
        this.typ = status;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dataTime) {
        DateTime = dataTime;
    }
}
