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
import isports.workplan.bean.Task;
import isports.workplan.bean.TaskList;

/**
 * Created by Duan on 7月15日.
 */
public class TaskInfo {
    private static final String TABLENAME = "Task";

    /***
     * 发送单个项目通知
     *
     * @param info
     * @param e
     */
    private static void postInfo(AVObject info, AVException e) {
        if (e == null) {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(converToTask(info));
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(converToTask(info));
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
            LinkedList<Task> tasks = new LinkedList<>();
            for (AVObject ob : list) {
                tasks.add(converToTask(ob));
            }
            TaskList taskList = new TaskList();
            taskList.setTasks(tasks);
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(taskList);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(taskList);
        } else {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
        }
    }

    private static Task converToTask(AVObject object) {
        Task task = new Task();
        task.setId(object.getObjectId());
        task.setProjectId(object.getString("projectId"));
        task.setCreateaccount(object.getString("createaccount"));
        task.setCreatedate(object.getLong("createdate"));
        task.setCreatename(object.getString("createname"));
        task.setName(object.getString("name"));
        task.setProjectName(object.getString("projectName"));
        task.setDescribed(object.getString("described"));
        task.setState(object.getInt("state"));
        task.setExecutoraccount(object.getString("executoraccount"));
        task.setExecutorname(object.getString("executorname"));
        return task;
    }

    /***
     * 新建一个项目
     */
    public static void createTask(String name, String projectId, String projectName) {
        final AVObject object = new AVObject(TABLENAME);
        object.put("name", name);
        object.put("projectId", projectId);
        object.put("projectName", projectName);
        object.put("createdate", new Date().getTime());
        AVUser user = AVUser.getCurrentUser();
        object.put("createaccount", user.getUsername());
        object.put("createname", user.getString("name"));
        object.put("described", "");
        object.put("executoraccount", user.getUsername());
        object.put("executorname", user.getString("name"));
        //1、进行中 2、待确认 3、已完成 4、搁置
        object.put("state", 1);
        SaveCallback callback = new SaveCallback() {
            @Override
            public void done(AVException e) {
                postInfo(object, e);
                if (e == null) {
                    ProjectInfo.addTasknum(object.getString("projectId"));
                }
            }
        };
        saveTask(object, callback);
    }

    /***
     * 保存项目状态
     */
    private static void saveTask(AVObject avObject, SaveCallback callback) {
        //更新后获取最新值
        avObject.setFetchWhenSave(true);
        avObject.saveInBackground(callback);
    }

    /***
     * 得到某一项目的全部任务列表,按照创建时间排序
     */
    public static void getAllTaskByProjectId(String projectId) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLENAME);
        query.orderByDescending("createdate");
        query.whereEqualTo("projectId", projectId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                postInfoList(list, e);
            }
        });
    }


    /***
     * 得到某人的全部任务列表,按照创建时间排序
     */
    public static void getAllTaskByExecutorAccount(String account) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLENAME);
        query.orderByDescending("createdate");
        query.whereEqualTo("executoraccount", account);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                postInfoList(list, e);
            }
        });
    }

    /***
     * 修改任务的状态，先查询对象，然后进行属性修改，随后保存,保存完成后发送广播通知
     *
     * @param state 1、进行中 2、关闭 3、搁置
     */
    public static void changeState(String id, final int state) {
        GetCallback getCallback = new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
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
                    saveTask(avObject, saveCallback);
                } else {
                    Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                    Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                }
            }
        };
        getTask(id, getCallback);
    }

    /***
     * 修改任务的执行者，先查询对象，然后进行属性修改，随后保存,保存完成后发送广播通知
     */
    public static void changeExecutor(String id, final String executoraccount, final String executorname, final String described) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLENAME);
        GetCallback getCallback = new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    avObject.put("executorname", executorname);
                    avObject.put("executoraccount", executoraccount);
                    avObject.put("described", described);
                    SaveCallback saveCallback = new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                                Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                            }
                        }
                    };
                    saveTask(avObject, saveCallback);
                } else {
                    Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                    Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                }
            }
        };
        getTask(id, getCallback);
    }

    /***
     * 根据id查询任务
     */
    private static void getTask(String id, GetCallback callback) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLENAME);
        query.getInBackground(id, callback);
    }


}
