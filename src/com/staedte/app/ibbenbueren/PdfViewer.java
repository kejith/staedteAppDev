package com.staedte.app.ibbenbueren;

import com.staedte.app.ibbenbueren.R;

import android.os.Bundle;

public class PdfViewer extends net.sf.andpdf.pdfviewer.PdfViewerActivity {

	public PdfViewer() {}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	}

	@Override
	public int getPreviousPageImageResource() {
		return R.drawable.left_arrow;
	}

	@Override
	public int getNextPageImageResource() {
		return R.drawable.right_arrow;
	}

	@Override
	public int getPdfPageNumberEditField() {
		return R.id.pagenum_edit;
	}

	@Override
	public int getPdfPageNumberResource() {
		return R.layout.dialog_pagenumber;
	}

	@Override
	public int getPdfPasswordEditField() {
		 return R.id.etPassword;
	}

	@Override
	public int getPdfPasswordExitButton() {
		return R.id.btExit;
	}

	@Override
	public int getPdfPasswordLayoutResource() {
		return R.layout.pdf_file_password;
	}

	@Override
	public int getPdfPasswordOkButton() {
		return R.id.btOK;
	}

	@Override
	public int getZoomInImageResource() {
		return R.drawable.zoom_in;
	}

	@Override
	public int getZoomOutImageResource() {
		return R.drawable.zoom_out;
	}

}
