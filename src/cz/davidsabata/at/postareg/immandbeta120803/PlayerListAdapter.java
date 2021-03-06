package cz.davidsabata.at.postareg.immandbeta120803;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cz.davidsabata.at.postareg.immandbeta120803.network.Message;
import cz.davidsabata.at.postareg.immandbeta120803.network.Message.Type;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player.Role;

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

		final Player p = data.get(position);

		if (GameService.getInstance().getLocalPlayer().equals(p)) {
			nickname.setText(R.string.you);
			nickname.setText(p.nickname + " (" + nickname.getText() + ")");
		} else {
			nickname.setText(p.nickname);
		}


		icon.setImageResource(p.getRoleIcon());
		icon.setTag(p.getRoleIcon());

		if (GameService.getInstance().getLocalPlayer().equals(p)) {
			icon.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					ImageView img = (ImageView) v;
					int resId = Integer.valueOf(v.getTag().toString());
					if (resId == R.drawable.role_agent) {
						img.setImageResource(R.drawable.role_guard);
						img.setTag(R.drawable.role_guard);
						p.role = Role.GUARD;
					} else {
						img.setImageResource(R.drawable.role_agent);
						img.setTag(R.drawable.role_agent);
						p.role = Role.AGENT;
					}
					// distribute update
					Message msg = new Message();
					msg.type = Type.PREPARING;
					msg.playerMac = p.macAddr;
					msg.nickname = p.nickname;
					msg.playerRole = p.role == Role.AGENT ? cz.davidsabata.at.postareg.immandbeta120803.network.Message.Role.AGENT
							: cz.davidsabata.at.postareg.immandbeta120803.network.Message.Role.GUARD;
					GameService.getInstance().reportSelfStatus(msg);
				}
			});
		}

		return vi;
	}
}
