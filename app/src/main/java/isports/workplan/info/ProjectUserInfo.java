package isports.workplan.info;

import com.BaseApplaction;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.LinkedList;
import java.util.List;

import isports.workplan.Application;
import isports.workplan.bean.ProjectUser;
import isports.workplan.bean.ProjectUserList;

/**
 * Created by Duan on 7月16日.
 */
public class ProjectUserInfo {
    private static final String TABLENAME = "ProjectUser";

    /***
     * 发送单个项目成员通知
     *
     * @param info
     * @param e
     */
    private static void postInfo(AVObject info, AVException e) {
        if (e == null) {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(converToProjectUser(info));
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(converToProjectUser(info));
        } else {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
        }
    }

    /***
     * 发送项目成员列表
     */
    private static void postInfoList(List<AVObject> list, AVException e) {
        if (e == null) {
            LinkedList<ProjectUser> projectsUsers = new LinkedList<>();
            for (AVObject ob : list) {
                projectsUsers.add(converToProjectUser(ob));
            }
            ProjectUserList projectUserList = new ProjectUserList();
            projectUserList.setProjectUsers(projectsUsers);
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(projectUserList);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(projectUserList);
        } else {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
        }
    }

    private static ProjectUser converToProjectUser(AVObject object) {
        ProjectUser projectUser = new ProjectUser();
        projectUser.setId(object.getObjectId());
        projectUser.setProjectId(object.getString("projectId"));
        projectUser.setUseraccount(object.getString("useraccount"));
        projectUser.setUsername(object.getString("username"));
        projectUser.setUseremail(object.getString("useremail"));
        return projectUser;
    }

    /***
     * 新建一个项目成员
     */
    public static void createProjectUser(final String projectId, final AVUser user) {
        //先判断该用户是否存在
        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLENAME);
        query.whereEqualTo("useraccount", user.getUsername());
        query.whereEqualTo("projectId", projectId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list != null && list.size() > 0) {
                    AVException exception = new AVException(101, "该成员已存在");
                    Application.getBusInstance(BaseApplaction.ACTIVITY).post(exception);
                    Application.getBusInstance(BaseApplaction.FRAGMENT).post(exception);
                } else {
                    //如果用户不存在，添加一个新的用户
                    final AVObject object = new AVObject(TABLENAME);
                    object.put("projectId", projectId);
                    object.put("useraccount", user.getUsername());
                    object.put("username", user.getString("name"));
                    object.put("useremail", user.getEmail());
                    SaveCallback callback = new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            //创建数据成功后，更新项目的成员列表数
                            postInfo(object, e);
                            if (e == null) {
                                ProjectInfo.addMembernum(projectId);
                            }
                        }
                    };
                    saveProjectUser(object, callback);
                }
            }
        });
    }

    /***
     * 保存项目状态
     */
    private static void saveProjectUser(AVObject avObject, SaveCallback callback) {
        //更新后获取最新值
        avObject.setFetchWhenSave(true);
        avObject.saveInBackground(callback);
    }


    /***
     * 得到某一项目的全部成员列表
     */
    public static void getAllUserByProjectId(String projectId) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLENAME);
        query.whereEqualTo("projectId", projectId);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                postInfoList(list, e);
            }
        });
    }
}
