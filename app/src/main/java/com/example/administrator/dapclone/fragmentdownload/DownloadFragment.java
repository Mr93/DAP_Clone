package com.example.administrator.dapclone.fragmentdownload;

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
		View view = inflater.inflate(R.layout.fragment_download, container, false);
		editText = (EditText) view.findViewById(R.id.edit_text_download);
		button = (Button) view.findViewById(R.id.button_download);
		errorTextView = (TextView) view.findViewById(R.id.error_text_view);
		button.setOnClickListener(this);
		editText.setText("http://f9.stream.nixcdn.com/8108205ec8f3eccb98778c0cc2eb887e/58d1ffb9/PreNCT13/TuGiayPhutDau-HariWonTranThanh-4816253.mp4?t=1490157875066");
		setupMVP();
		return view;
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
}
