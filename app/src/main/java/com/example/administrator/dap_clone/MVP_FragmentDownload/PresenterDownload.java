package com.example.administrator.dap_clone.MVP_FragmentDownload;

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
		UrlValidate.isValid(url);
	}
}
