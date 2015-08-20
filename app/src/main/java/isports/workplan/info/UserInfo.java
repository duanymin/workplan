package isports.workplan.info;


import com.BaseApplaction;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.UpdatePasswordCallback;

import java.util.List;

import isports.workplan.Application;

/**
 * Created by Duan on 7月10日.
 */
public class UserInfo {
    private static void postInfo(AVUser user, AVException e) {
        if (e == null) {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(user);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(user);
        } else {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
        }
    }

    private static void postInfoList(List<AVUser> users, AVException e) {
        if (e == null) {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(users);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(users);
        } else {
            Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
            Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
        }
    }

    /***
     * 创建一个用户账户,当创建成功后自动登录
     */
    public static void createUser(final String account, final String password, String Email, String name) {
        final AVUser user = new AVUser();
        user.setUsername(account);
        user.setPassword(password);
        user.setEmail(Email);
        user.put("name", name);
        user.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                if (e == null) {
                    Login(account, password);
                } else {
                    Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                    Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                }
            }
        });
    }

    /***
     * 得到全部用户列表
     */
    public static void getAllUser() {
        AVQuery<AVUser> query = AVUser.getQuery();
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                postInfoList(list, e);
            }
        });
    }


    /***
     * 登录
     */
    public static void Login(String account, String password) {
        AVUser.logInInBackground(account, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser user, AVException e) {
                if (e == null) {
                    user.put("installationId", AVInstallation.getCurrentInstallation().getInstallationId());
                    user.saveInBackground();
                }
                postInfo(user, e);
            }
        });
    }

    /***
     * 修改一个用户的信息
     */
    public static void updateUser(final AVUser user) {
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                postInfo(user, e);
            }
        });
    }

    /***
     * 得到当前用户
     *
     * @return 当前用户
     */
    public static AVUser getCurrentUser() {
        AVUser currentUser = AVUser.getCurrentUser();
        return currentUser;
    }

    /***
     * 修改密码
     */
    public static void changePwd(String old_password, String new_password) {
        AVUser user = getCurrentUser();
        if (user != null) {
            user.updatePasswordInBackground(old_password, new_password, new UpdatePasswordCallback() {
                @Override
                public void done(AVException e) {
                    if (e != null) {
                        Application.getBusInstance(BaseApplaction.ACTIVITY).post(e);
                        Application.getBusInstance(BaseApplaction.FRAGMENT).post(e);
                    }
                }
            });
        }
    }

    /***
     * 注销
     */
    public static void logOut() {
        AVUser.logOut();
    }

    /***
     * 通过同帐号比对来判断是否有修改对应项目的权限
     */
    public static boolean hasRight(String account) {
        return getCurrentUser().getUsername().equals("admin") || account.equals(getCurrentUser().getUsername());
    }

    public static void getUserByAccount(String account, FindCallback<AVUser> callback) {
        AVQuery<AVUser> query = AVUser.getQuery();
        query.whereEqualTo("username", account);
        query.findInBackground(callback);
    }

    /***
     * 发送消息给某个用户
     */
    public static void sendMsg(String account, final String msg) {
        //先按照帐号查找用户 如果用户不为空 那么发送消息
        FindCallback<AVUser> callback = new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null && list != null && list.size() > 0) {
                    AVUser user = list.get(0);
                    AVQuery pushQuery = AVInstallation.getQuery();
                    // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
                    pushQuery.whereEqualTo("installationId", user.getString("installationId"));
                    AVPush.sendMessageInBackground(msg, pushQuery);
                }
            }
        };
        getUserByAccount(account, callback);
    }
}
