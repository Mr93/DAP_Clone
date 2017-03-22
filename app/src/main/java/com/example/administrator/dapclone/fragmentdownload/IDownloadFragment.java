package com.example.administrator.dapclone.fragmentdownload;

/**
 * Created by Administrator on 03/21/2017.
 */

public interface IDownloadFragment {
	interface RequiredView {
		void invalidUrl(String message);
	}

	interface ProvidedPresenter {
		void download(String url);

		void setView(RequiredView view);

		void setModel(ProvidedModel model);
	}

	interface RequiredPresenter {

	}

	interface ProvidedModel {

	}
}
