package isports.workplan.bean;

import java.io.Serializable;

/**
 * Created by Duan on 7月14日.
 */
public class Project implements Serializable {
    private String id;

    private String name;
    private long createdate;
    private String createaccount;
    private String createname;
    // 1、进行中 2、关闭 3、搁置
    private int state;
    private int membernum;
    private int tasknum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedate() {
        return createdate;
    }

    public void setCreatedate(long createdate) {
        this.createdate = createdate;
    }

    public String getCreateaccount() {
        return createaccount;
    }

    public void setCreateaccount(String createaccount) {
        this.createaccount = createaccount;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMembernum() {
        return membernum;
    }

    public void setMembernum(int membernum) {
        this.membernum = membernum;
    }

    public int getTasknum() {
        return tasknum;
    }

    public void setTasknum(int tasknum) {
        this.tasknum = tasknum;
    }
}
