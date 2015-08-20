package isports.workplan.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.activity.BaseActivity;
import com.anno.ActionBarSet;
import com.avos.avoscloud.AVException;
import com.fragment.BaseFragment;
import com.squareup.otto.Subscribe;

import isports.workplan.R;
import isports.workplan.fragment.MainFragment;
import isports.workplan.fragment.ProjectListFragment;
import isports.workplan.fragment.TaskListFragment;
import isports.workplan.fragment.UserListFragment;
import isports.workplan.info.UserInfo;

@ActionBarSet(title = "首页")
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        if (UserInfo.getCurrentUser() == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
        } else {
            String useraccount = UserInfo.getCurrentUser().getUsername();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TaskListFragment.newInstance(TaskListFragment.USERTASK, useraccount)).commit();
            toolbar.setTitle("我的任务");
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawerContentDescRes, R.string.closeDrawerContentDescRes);
        drawerLayout.setDrawerListener(drawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        menuSetting();
    }

    private void menuSetting() {
        if (UserInfo.getCurrentUser() == null) {
            navigationView.getMenu().findItem(R.id.work).setVisible(false);
            navigationView.getMenu().findItem(R.id.manage).setVisible(false);
            navigationView.getMenu().findItem(R.id.account_1).setTitle("登录");
        } else {
            navigationView.getMenu().findItem(R.id.work).setVisible(true);
            if (UserInfo.hasRight("admin")) {
                navigationView.getMenu().findItem(R.id.manage).setVisible(true);
            } else {
                navigationView.getMenu().findItem(R.id.manage).setVisible(false);
            }
            navigationView.getMenu().findItem(R.id.account_1).setTitle("帐号信息");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.account_1:
                if (UserInfo.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                } else {
                    changeContainer(new MainFragment(), "项目管理");
                }
                break;
            case R.id.manage_1:
                changeContainer(new ProjectListFragment(), "项目管理");
                break;
            case R.id.manage_2:
                changeContainer(new UserListFragment(), "成员管理");
                break;
            case R.id.work_1:
                String useraccount = UserInfo.getCurrentUser().getUsername();
                changeContainer(TaskListFragment.newInstance(TaskListFragment.USERTASK, useraccount), "我的任务");
                break;
        }
        return false;
    }

    private void changeContainer(BaseFragment fragment, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        drawerLayout.closeDrawers();
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onError(AVException e) {
        String message = e.getMessage();
        View parent = findViewById(R.id.ll_main);
        Snackbar.make(parent, message, Snackbar.LENGTH_LONG)
                .show();
    }
}
