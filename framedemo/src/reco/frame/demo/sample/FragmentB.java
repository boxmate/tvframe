package reco.frame.demo.sample;

import java.util.ArrayList;
import java.util.List;

import reco.frame.demo.R;
import reco.frame.demo.adapter.TvGridAdapter;
import reco.frame.demo.entity.AppInfo;
import reco.frame.tv.view.TvGridView;
import reco.frame.tv.view.TvGridView.OnItemClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentB extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.frag_b, container, false);
		return parent;
	}
	

	
}
