package isports.workplan;

import com.BaseApplaction;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;

import isports.workplan.activity.MainActivity;

/**
 * Created by Duan on 7月10日.
 */
public class Application extends BaseApplaction {

    @Override
    public void onCreate() {
        super.onCreate();
        String APPID = "j68cras53qu1ln9ym9x78h9j3yfggeykz2scjx9cpb0l47j2";
        String APPKEY = "ayo41r1s36vyebsio7sbq8lvkfbtzuns9k5fy0emotv2p4w4";
        AVOSCloud.initialize(this, APPID, APPKEY);
        initPush();
    }

    //推送
    private void initPush() {
        PushService.setDefaultPushCallback(this, MainActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground();
    }
}
