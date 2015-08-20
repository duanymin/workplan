package isports.workplan.activity;

import android.os.Bundle;

import com.activity.BaseActivity;
import com.anno.ActionBarSet;

import isports.workplan.R;
import isports.workplan.fragment.UserListFragment;

/**
 * Created by Duan on 7月16日.
 */
@ActionBarSet(title = "选择项目成员", homeAsUpEnabled = true)
public class UserListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);
    }

    @Override
    protected void initView() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserListFragment()).commit();
    }
}
