package cz.davidsabata.at.postareg.immandbeta120803;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player;

public class PlayerListAdapter extends BaseAdapter {

	private final List<Player> data;
	private LayoutInflater inflater = null;

	public PlayerListAdapter(LayoutInflater inflater, List<Player> data) {
		this.data = data;
		this.inflater = inflater;
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
			vi = inflater.inflate(R.layout.item_playerlist, null);

		ImageView icon = (ImageView) vi.findViewById(R.id.icon);
		TextView nickname = (TextView) vi.findViewById(R.id.nickname);

		Player p = data.get(position);

		nickname.setText(p.nickname);
		icon.setImageResource(p.getRoleIcon());

		return vi;
	}
}
