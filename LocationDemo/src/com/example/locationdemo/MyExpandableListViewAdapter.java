package com.example.locationdemo;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * @author optimus158 Adapter Class for setting views to the Expandable Listview
 */
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

	private List<String> list;
	private HashMap<String, List<String>> map;
	private Context context;

	public MyExpandableListViewAdapter(Context context, List<String> list,
			HashMap<String, List<String>> map) {
		this.context = context;
		this.list = list;
		this.map = map;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return (String) map.get(list.get(groupPosition)).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String temp = (String) getChild(groupPosition, childPosition);
		LayoutInflater inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflator.inflate(R.layout.layout_child, null);

		TextView child = (TextView) convertView
				.findViewById(R.id.textViewChild);
		child.setText(temp);
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return map.get(list.get(groupPosition)).size();
	}

	public Object getGroup(int groupPosition) {
		return (String) list.get(groupPosition);
	}

	public int getGroupCount() {
		return list.size();
	}

	public long getGroupId(int groupPosition) {
		return 0;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String temp = (String) getGroup(groupPosition);
		LayoutInflater inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflator.inflate(R.layout.layout_parent, null);

		TextView parent_tv = (TextView) convertView
				.findViewById(R.id.textViewParent);
		parent_tv.setText(temp);
		return convertView;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
