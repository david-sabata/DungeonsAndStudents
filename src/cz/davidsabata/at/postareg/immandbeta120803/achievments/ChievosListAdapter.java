package cz.davidsabata.at.postareg.immandbeta120803.achievments;

import java.util.List;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cz.davidsabata.at.postareg.immandbeta120803.R;

public class ChievosListAdapter extends BaseAdapter {

	private final List<Achievment> data;
	private LayoutInflater inflater = null;

	private final int colorGreen;
	private final int colorRed;

	public ChievosListAdapter(Resources res, LayoutInflater inflater, List<Achievment> data) {
		this.data = data;
		this.inflater = inflater;
		colorGreen = res.getColor(R.color.green);
		colorRed = res.getColor(R.color.red);
	}



	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}



	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.item_chievolist, null);

		ImageView icon = (ImageView) vi.findViewById(R.id.icon);
		TextView title = (TextView) vi.findViewById(R.id.title);
		TextView text = (TextView) vi.findViewById(R.id.text);

		Achievment a = data.get(position);

		if (a.titleResId == -1)
			title.setText("??? ? ?? ???? ??");
		else
			title.setText(a.titleResId);

		icon.setImageResource(a.imageResId);

		if (a.isDone) {
			text.setText(R.string.achievment_done);
			text.setTextColor(colorGreen);
		} else {
			text.setText(R.string.achievment_not_done);
			text.setTextColor(colorRed);
		}

		return vi;
	}
}
