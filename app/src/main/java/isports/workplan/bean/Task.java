package isports.workplan.bean;

import android.text.TextUtils;

/**
 * Created by Duan on 7月15日.
 */
public class Task {
    private String id;
    private String projectId;
    private String projectName;
    private String name;
    private String described;
    private String createaccount;
    private String createname;
    private long createdate;
    //1、进行中 2、待确认 3、已完成 4、搁置
    private int state;
    private String executoraccount;
    private String executorname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribed() {
        if (TextUtils.isEmpty(described)) {
            return "";
        } else {
            return described;
        }
    }

    public void setDescribed(String described) {
        this.described = described;
    }


    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public long getCreatedate() {
        return createdate;
    }

    public void setCreatedate(long createdate) {
        this.createdate = createdate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCreateaccount() {
        return createaccount;
    }

    public void setCreateaccount(String createaccount) {
        this.createaccount = createaccount;
    }

    public String getExecutoraccount() {
        return executoraccount;
    }

    public void setExecutoraccount(String executoraccount) {
        this.executoraccount = executoraccount;
    }

    public String getExecutorname() {
        return executorname;
    }

    public void setExecutorname(String executorname) {
        this.executorname = executorname;
    }

}
