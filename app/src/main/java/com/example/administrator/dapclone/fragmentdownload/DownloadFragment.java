package com.example.administrator.dapclone.fragmentdownload;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.dapclone.MyApplication;
import com.example.administrator.dapclone.R;

import javax.inject.Inject;

import static com.example.administrator.dapclone.fragmentdownload.IDownloadFragment.*;

/**
 * Created by Administrator on 03/21/2017.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener, RequiredView {

	private static final String TAG = DownloadFragment.class.getSimpleName();
	private EditText editText;
	private Button button;
	private TextView errorTextView;
	@Inject
	ProvidedPresenter providedPresenter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.download_fragment, container, false);
		editText = (EditText) view.findViewById(R.id.edit_text_download);
		button = (Button) view.findViewById(R.id.button_download);
		errorTextView = (TextView) view.findViewById(R.id.error_text_view);
		button.setOnClickListener(this);
		editText.setText("http://www.intrawallpaper.com/static/images/the-dark-angel-hd-wallpaper-hd-1080p_2_WvRULzk.jpg");
		((MyApplication) getActivity().getApplication()).getNetComponent().inject(this);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		providedPresenter.setView(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_download:
				errorTextView.setVisibility(View.GONE);
				providedPresenter.download(editText.getText().toString().trim());
				editText.setText("http://s72.stream.nixcdn.com/d0950d4a64ca0986958fd316301666eb/58de04be/PreNCT12/MillionReasons-LadyGaga-4707870.mp4?t=1490946859464");
				break;
			default:
				break;
		}
	}

	@Override
	public void errorDownload(String message) {
		errorTextView.setText(message);
		errorTextView.setVisibility(View.VISIBLE);
	}

	@Override
	public Context getFragmentContext() {
		return getContext();
	}
}
