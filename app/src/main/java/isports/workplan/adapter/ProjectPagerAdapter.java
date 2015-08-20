package isports.workplan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import isports.workplan.bean.Project;
import isports.workplan.fragment.ProjectUserListFragment;
import isports.workplan.fragment.TaskListFragment;

/**
 * Created by Duan on 7月16日.
 */
public class ProjectPagerAdapter extends FragmentStatePagerAdapter {
    private TaskListFragment taskListFragment;
    private ProjectUserListFragment projectUserListFragment;

    public ProjectPagerAdapter(FragmentManager fm, Project project) {
        super(fm);
        taskListFragment = TaskListFragment.newInstance(TaskListFragment.PROJECTTASK, project.getId());
        projectUserListFragment = ProjectUserListFragment.newInstance(project);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return taskListFragment;
            case 1:
                return projectUserListFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "任务";
            case 1:
                return "成员";
        }
        return null;
    }
}
