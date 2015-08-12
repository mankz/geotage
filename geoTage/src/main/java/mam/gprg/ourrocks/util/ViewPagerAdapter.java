package mam.gprg.ourrocks.util;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	List<Fragment> fragments;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<Fragment>();
	}

	public void addFragment(Fragment f) {
		fragments.add(f);
		notifyDataSetChanged();
	}

	public void removeFragment(Fragment f) {
		fragments.remove(f);
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		if (fragments == null)
			return 0;
		return fragments.size();
	}

}
