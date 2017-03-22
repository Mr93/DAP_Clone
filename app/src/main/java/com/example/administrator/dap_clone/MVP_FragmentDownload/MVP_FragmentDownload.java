package com.example.administrator.dap_clone.MVP_FragmentDownload;

/**
 * Created by Administrator on 03/21/2017.
 */

public interface MVP_FragmentDownload {
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
