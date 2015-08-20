package isports.workplan.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.activity.BaseActivity;
import com.anno.ActionBarSet;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.squareup.otto.Subscribe;
import com.util.DateUtils;
import com.util.InputUtils;

import isports.workplan.R;
import isports.workplan.adapter.ProjectPagerAdapter;
import isports.workplan.bean.Project;
import isports.workplan.bean.ProjectUser;
import isports.workplan.bean.Task;
import isports.workplan.info.ProjectInfo;
import isports.workplan.info.ProjectUserInfo;
import isports.workplan.info.TaskInfo;
import isports.workplan.info.UserInfo;

@ActionBarSet(title = "项目详情", homeAsUpEnabled = true)
public class ProjectActivity extends BaseActivity {
    private Project project;
    private boolean hasChange = false;
    private ProjectPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        project = (Project) getIntent().getSerializableExtra("project");
    }

    @Override
    protected void initView() {
        editHeader();
        editList();
        TaskInfo.getAllTaskByProjectId(project.getId());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (UserInfo.hasRight(project.getCreateaccount())) {
            getMenuInflater().inflate(R.menu.menu_project, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_newtask:
                newTask();
                break;
            case R.id.action_newmember:
                newMember();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            AVUser user = new AVUser();
            user.setObjectId(data.getStringExtra("id"));
            user.setUsername(data.getStringExtra("account"));
            user.setEmail(data.getStringExtra("email"));
            user.put("name", data.getStringExtra("name"));
            ProjectUserInfo.createProjectUser(project.getId(), user);
        }
    }

    private void newMember() {
        Intent intent = new Intent(this, UserListActivity.class);
        startActivityForResult(intent, 101);
    }

    private void editHeader() {
        aq.id(R.id.textView1).text(String.format("项目名称:%s", project.getName()));
        aq.id(R.id.textView2).text(String.format("创建者:%s", project.getCreatename()));
        aq.id(R.id.textView3).text(String.format("项目人数:%d", project.getMembernum()));
        String createDate = DateUtils.parseDate2(project.getCreatedate());
        aq.id(R.id.textView4).text(String.format("创建时间:%s", createDate));
        aq.id(R.id.textView6).text(String.format("任务数:%d", project.getTasknum()));
        String state = "";
        switch (project.getState()) {
            case 1:
                state = "进行中";
                break;
            case 2:
                state = "关闭";
                break;
            case 3:
                state = "搁置";
                break;
        }
        aq.id(R.id.textView5).text(String.format("当前状态:%s", state));
    }

    private void editList() {
        adapter = new ProjectPagerAdapter(getSupportFragmentManager(), project);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    public void changeState(View view) {
        if (UserInfo.hasRight(project.getCreateaccount())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final String[] items = new String[]{"进行中", "关闭", "搁置"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    aq.id(R.id.textView5).text(String.format("当前状态:%s", items[which]));
                    project.setState(which + 1);
                    hasChange = true;
                    ProjectInfo.changeState(project.getId(), project.getState());
                }
            });
            builder.show();
        } else {
            Snackbar.make(findViewById(R.id.ll_parent), "您没有该权限", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Subscribe
    public void onAddMember(ProjectUser projectUser) {
        hasChange = true;
        project.setMembernum(project.getMembernum() + 1);
        aq.id(R.id.textView3).text(String.format("项目人数:%d", project.getMembernum()));
    }

    @Subscribe
    public void onAddTask(Task task) {
        hasChange = true;
        project.setTasknum(project.getTasknum() + 1);
        aq.id(R.id.textView6).text(String.format("任务数:%d", project.getTasknum()));
    }

    @Subscribe
    public void onError(AVException e) {
        String message = e.getMessage();
        View parent = findViewById(R.id.ll_parent);
        Snackbar.make(parent, message, Snackbar.LENGTH_LONG)
                .show();
    }

    private void newTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_newtask, null, true);
        builder.setView(view);
        final TextInputLayout layout = (TextInputLayout) view.findViewById(R.id.til_name);
        layout.setHint("任务名称");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = aq.id(view.findViewById(R.id.et_name)).getText().toString();
                if (check(name)) {
                    TaskInfo.createTask(name, project.getId(), project.getName());
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
        InputUtils.showKeyBoard((EditText) view.findViewById(R.id.et_name));
    }

    private boolean check(String name) {
        if (TextUtils.isEmpty(name)) {
            String message = "请输入任务名称";
            Snackbar.make(findViewById(R.id.ll_parent), message, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }


    @Override
    public void finish() {
        if (hasChange) {
            setResult(RESULT_OK);
        }
        super.finish();
    }
}
