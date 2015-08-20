package isports.workplan.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.activity.BaseActivity;
import com.anno.ActionBarSet;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.squareup.otto.Subscribe;

import isports.workplan.R;
import isports.workplan.info.UserInfo;

@ActionBarSet(title = "登录", homeAsUpEnabled = true)
public class LoginActivity extends BaseActivity {
    private boolean isLogin = true;
    private TextInputLayout til_account, til_password, til_email, til_name;
    private String account, password, email, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //设置已登录的帐号密码
        SharedPreferences preferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        account = preferences.getString("account", "");
        password = preferences.getString("password", "");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initView() {
        til_account = (TextInputLayout) findViewById(R.id.til_account);
        til_account.setHint("帐号");
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        til_password.setHint("密码");
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_email.setHint("邮箱");
        til_name = (TextInputLayout) findViewById(R.id.til_name);
        til_name.setHint("姓名");
        til_account.postDelayed(new Runnable() {
            @Override
            public void run() {
                aq.id(R.id.et_account).text(account);
                aq.id(R.id.et_password).text(password);
            }
        }, 300);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change) {
            if (isLogin) {
                showRegistInfo(item);
            } else {
                hideRegistInfo(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRegistInfo(MenuItem item) {
        item.setTitle("登录");
        toolbar.setTitle("注册");
        isLogin = false;
        ValueAnimator showAnim_1 = ObjectAnimator.ofFloat(til_email, "alpha", 0f, 1f);
        ValueAnimator showAnim_2 = ObjectAnimator.ofFloat(til_name, "alpha", 0f, 1f);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(showAnim_1, showAnim_2);
        animSetXY.start();
        aq.id(R.id.btn_submit).text("注册");
    }

    private void hideRegistInfo(MenuItem item) {
        item.setTitle("注册");
        toolbar.setTitle("登录");
        isLogin = true;
        ValueAnimator showAnim_1 = ObjectAnimator.ofFloat(til_email, "alpha", 1f, 0f);
        ValueAnimator showAnim_2 = ObjectAnimator.ofFloat(til_name, "alpha", 1f, 0f);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(showAnim_1, showAnim_2);
        animSetXY.start();
        aq.id(R.id.btn_submit).text("登录");
    }

    private boolean check() {
        account = aq.id(R.id.et_account).getText().toString();
        if (TextUtils.isEmpty(account)) {
            til_account.setErrorEnabled(true);
            til_account.setError("请输入帐号");
            return false;
        } else {
            til_account.setErrorEnabled(false);
        }
        password = aq.id(R.id.et_password).getText().toString();
        if (TextUtils.isEmpty(password)) {
            til_password.setErrorEnabled(true);
            til_password.setError("请输入密码");
            return false;
        } else {
            til_password.setErrorEnabled(false);
        }
        if (!isLogin) {
            email = aq.id(R.id.et_email).getText().toString();
            if (TextUtils.isEmpty(email)) {
                til_email.setErrorEnabled(true);
                til_email.setError("请输入邮箱");
                return false;
            } else {
                til_email.setErrorEnabled(false);
            }
            name = aq.id(R.id.et_name).getText().toString();
            if (TextUtils.isEmpty(name)) {
                til_name.setErrorEnabled(true);
                til_name.setError("请输姓名");
                return false;
            } else {
                til_name.setErrorEnabled(false);
            }
        }
        return true;
    }


    public void submit(View view) {
        if (check()) {
            if (isLogin) {
                UserInfo.Login(account, password);
            } else {
                email = aq.id(R.id.et_email).getText().toString();
                name = aq.id(R.id.et_name).getText().toString();
                UserInfo.createUser(account, password, email, name);
            }
        }
    }

    @Subscribe
    public void getUser(AVUser user) {
        SharedPreferences preferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("account", account);
        editor.putString("password", password);
        editor.apply();
        finish();
    }

    @Subscribe
    public void onError(AVException e) {
        String message = e.getMessage();
        View parent = findViewById(R.id.ll_parent);
        Snackbar.make(parent, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
