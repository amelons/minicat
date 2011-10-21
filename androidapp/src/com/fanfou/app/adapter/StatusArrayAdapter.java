package com.fanfou.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.R;
import com.fanfou.app.api.Status;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.StringHelper;

public class StatusArrayAdapter extends BaseArrayAdapter<Status> {

	private static final String TAG = StatusArrayAdapter.class.getSimpleName();

	void log(String message) {
		Log.e(TAG, message);
	}

	private List<Status> mStatus;

	public StatusArrayAdapter(Context context, List<Status> ss) {
		super(context, ss);
		if (ss == null) {
			mStatus = new ArrayList<Status>();
		} else {
			mStatus = ss;
		}
	}

	@Override
	public int getCount() {
		return mStatus.size();
	}

	@Override
	public Status getItem(int position) {
		return mStatus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void setTextStyle(ViewHolder holder) {
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.metaText.setTextSize(fontSize - 4);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(getLayoutId(), null);
			holder = new ViewHolder(convertView);
			setTextStyle(holder);
			setHeadImage(holder.headIcon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Status s = mStatus.get(position);

		mLoader.set(s.userProfileImageUrl, holder.headIcon,
				R.drawable.default_head);

		if (StringHelper.isEmpty(s.inReplyToStatusId)) {
			holder.replyIcon.setVisibility(View.GONE);
		} else {
			holder.replyIcon.setVisibility(View.VISIBLE);
		}

		if (StringHelper.isEmpty(s.photoLargeUrl)) {
			holder.photoIcon.setVisibility(View.GONE);
		} else {
			holder.photoIcon.setVisibility(View.VISIBLE);
		}

		holder.nameText.setText(s.userScreenName);
		holder.contentText.setText(s.simpleText);
		holder.metaText.setText(DateTimeHelper.getInterval(s.createdAt) + " 来自"
				+ s.source);

		return convertView;
	}

	static class ViewHolder {
		ImageView headIcon = null;
		ImageView replyIcon = null;
		ImageView photoIcon = null;
		TextView nameText = null;
		TextView metaText = null;
		TextView contentText = null;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base
					.findViewById(R.id.item_status_head);
			this.replyIcon = (ImageView) base
					.findViewById(R.id.item_status_icon_reply);
			this.photoIcon = (ImageView) base
					.findViewById(R.id.item_status_icon_photo);
			this.contentText = (TextView) base
					.findViewById(R.id.item_status_text);
			this.metaText = (TextView) base.findViewById(R.id.item_status_meta);
			this.nameText = (TextView) base.findViewById(R.id.item_status_user);

		}
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_status;
	}

	public void updateDataAndUI(List<Status> ss) {
		mStatus = ss;
		notifyDataSetChanged();
	}

}