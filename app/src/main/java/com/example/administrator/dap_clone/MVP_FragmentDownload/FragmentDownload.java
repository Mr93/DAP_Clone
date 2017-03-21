package com.example.administrator.dap_clone.MVP_FragmentDownload;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.dap_clone.R;

/**
 * Created by Administrator on 03/21/2017.
 */

public class FragmentDownload extends Fragment implements View.OnClickListener, MVP_FragmentDownload.RequiredView {

	private EditText editText;
	private Button button;
	private TextView errorTextView;
	private MVP_FragmentDownload.ProvidedPresenter providedPresenter;

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
		providedPresenter = new PresenterDownload();
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_download:
				providedPresenter.download(editText.getText().toString().trim());
				break;
			default:
				break;
		}
	}
}
