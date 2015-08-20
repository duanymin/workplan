package isports.workplan.info;

import com.BaseApplaction;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import isports.workplan.Application;
import isports.workplan.bean.Project;
import isports.workplan.bean.ProjectList;

/**
 * Created by Duan on 7月13日.
 */
public class ProjectInfo {
    private static final String TABLENAME = "Project";

    /***
     * 发送单个项目通知
     *
     * @param info
     * @param e
     */
    private static void postInfo(AVObject info, AVException e) {
        if (e == null) {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(converToProject(info));
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(converToProject(info));
        } else {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
        }
    }

    /***
     * 发送项目列表
     */
    private static void postInfoList(List<AVObject> list, AVException e) {
        if (e == null) {
            LinkedList<Project> projects = new LinkedList<>();
            for (AVObject ob : list) {
                projects.add(converToProject(ob));
            }
            ProjectList projectList = new ProjectList();
            projectList.setProjects(projects);
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(projectList);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(projectList);
        } else {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
        }
    }

    private static Project converToProject(AVObject object) {
        Project project = new Project();
        project.setId(object.getObjectId());
        project.setCreateaccount(object.getString("createaccount"));
        project.setCreatedate(object.getLong("createdate"));
        project.setCreatename(object.getString("createname"));
        project.setName(object.getString("name"));
        project.setState(object.getInt("state"));
        project.setMembernum(object.getInt("membernum"));
        project.setTasknum(object.getInt("tasknum"));
        return project;
    }


    /***
     * 新建一个项目,随后为该项目添加创建者作为项目成员
     */
    public static void createProject(String name) {
        final AVObject object = new AVObject(TABLENAME);
        object.put("name", name);
        object.put("createdate", new Date().getTime());
        final AVUser user = AVUser.getCurrentUser();
        object.put("createaccount", user.getUsername());
        object.put("createname", user.getString("name"));
        object.put("membernum", 0);
        object.put("tasknum", 0);
        object.put("state", 1);
        SaveCallback callback = new SaveCallback() {
            @Override
            public void done(AVException e) {
                object.put("membernum", 1);
                postInfo(object, e);
                ProjectUserInfo.createProjectUser(object.getObjectId(), user);
            }
        };
        saveProject(object, callback);
    }

    /***
     * 根据id查询项目
     */
    private static void getProject(String id, GetCallback callback) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLENAME);
        query.getInBackground(id, callback);
    }

    /***
     * 保存项目状态
     */
    private static void saveProject(AVObject avObject, SaveCallback callback) {
        //更新后获取最新值
        avObject.setFetchWhenSave(true);
        avObject.saveInBackground(callback);
    }

    /***
     * 得到全部项目列表,按照创建时间排序
     */
    public static void getAllProject() {
        AVQuery<AVObject> query = new AVQuery<>(TABLENAME);
        query.orderByDescending("createdate");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                postInfoList(list, e);
            }
        });
    }

    public static void getProjectById(String id) {
        AVQuery<AVObject> query = new AVQuery<>(TABLENAME);
        query.getInBackground(id, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                postInfo(avObject, e);
            }
        });

    }

    /***
     * 修改项目的状态，先查询对象，然后进行属性修改，随后保存,保存完成后发送广播通知
     *
     * @param state 1、进行中 2、关闭 3、搁置
     */
    public static void changeState(String id, final int state) {
        GetCallback getCallback = new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    if (e == null) {
                        avObject.put("state", state);
                        SaveCallback saveCallback = new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e != null) {
                                    Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                                    Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                                }
                            }
                        };
                        saveProject(avObject, saveCallback);
                    } else {
                        Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                        Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                    }
                }
        };
        getProject(id, getCallback);
    }

    /***
     * 添加项目成员数
     */
    public static void addMembernum(String id) {
        GetCallback getCallback = new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    avObject.put("membernum", avObject.getInt("membernum") + 1);
                    SaveCallback saveCallback = new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                                Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                            }
                        }
                    };
                    saveProject(avObject, saveCallback);
                } else {
                    Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                    Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);

                }
            }
        };
        getProject(id, getCallback);
    }

    /***
     * 添加项目任务数
     */
    public static void addTasknum(String id) {
        GetCallback getCallback = new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    avObject.put("tasknum", avObject.getInt("tasknum") + 1);
                    SaveCallback saveCallback = new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                                Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                            }
                        }
                    };
                    saveProject(avObject, saveCallback);
                } else {
                    Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                    Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);

                }
            }
        };
        getProject(id, getCallback);
    }

}
