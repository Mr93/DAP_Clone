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

import com.example.administrator.dapclone.R;

/**
 * Created by Administrator on 03/21/2017.
 */

public class DownloadFragment extends Fragment implements View.OnClickListener, IDownloadFragment.RequiredView {

	private static final String TAG = DownloadFragment.class.getSimpleName();
	private EditText editText;
	private Button button;
	private TextView errorTextView;
	private IDownloadFragment.ProvidedPresenter providedPresenter;

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
		editText.setText("http://f9.stream.nixcdn.com/86ec223eff28caa17d3d98a9a03d35f0/58db37c8/PreNCT13/Cold--4772978.mp4?t=1490763377116");
		setupMVP();
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
	}

	private void setupMVP() {
		DownloadPresenter presenter = new DownloadPresenter(this);
		DownloadModel model = new DownloadModel(presenter);
		presenter.setModel(model);
		providedPresenter = presenter;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_download:
				errorTextView.setVisibility(View.GONE);
				providedPresenter.download(editText.getText().toString().trim());
				editText.setText("http://f9.stream.nixcdn.com/e6b05beebf170c3951c85778d9ea06a7/58da098a/PreNCT13/Giants-TakeThat-4853929.mp4?t=1490684397959");
				break;
			default:
				break;
		}
	}

	@Override
	public void invalidUrl(String message) {
		Log.d(TAG, "invalidUrl: ");
		errorTextView.setText(message);
		errorTextView.setVisibility(View.VISIBLE);
	}

	@Override
	public Context getFragmentContext() {
		return getContext();
	}
}
