package com.example.administrator.dap_clone.MVP_FragmentDownload;

import com.example.administrator.dap_clone.Exception.NetworkException;
import com.example.administrator.dap_clone.Utils.UrlValidate;

import retrofit2.http.Url;

/**
 * Created by Administrator on 03/21/2017.
 */

public class PresenterDownload implements MVP_FragmentDownload.ProvidedPresenter, MVP_FragmentDownload.RequiredPresenter {

	private MVP_FragmentDownload.RequiredView requiredView;
	private MVP_FragmentDownload.ProvidedModel providedModel;


	@Override
	public void download(String url) {
		try {
			UrlValidate.isValid(url);
		} catch (NetworkException e) {
			requiredView.invalidUrl(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setView(MVP_FragmentDownload.RequiredView view) {
		requiredView = view;
	}

	@Override
	public void setModel(MVP_FragmentDownload.ProvidedModel model) {
		providedModel = model;
	}
}
