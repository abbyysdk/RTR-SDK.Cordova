// ABBYY ® Mobile Imaging UI Components © 2018 ABBYY Production LLC.
// ABBYY is either a registered trademark or a trademark of ABBYY Software Ltd.

package com.abbyy.mobile.rtr.cordova.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abbyy.mobile.rtr.cordova.utils.ImageSaver;
import com.abbyy.mobile.rtr.cordova.utils.ImageUtils;
import com.abbyy.rtrcordovasample.R;

/**
 * Fullscreen dialog to confirm or discard capture result. If confirmed, the page is saved into a file.
 * It is retained across orientation changes and stores the captured page bitmap
 */
public class CaptureResultDialogFragment extends DialogFragment implements ImageSaver.Callback,
	View.OnClickListener {

	private TextView pageNumberText;
	private int pageCount;

	public static CaptureResultDialogFragment newInstance( int pageCount, Bitmap pageImage )
	{
		CaptureResultDialogFragment dialog = new CaptureResultDialogFragment();
		dialog.setPageImage( pageImage );
		if (pageImage != null) {
			dialog.setPageCount( pageCount + 1 );
		} else {
			dialog.setPageCount( pageCount );
		}
		return dialog;
	}

	public interface ResultListener {
		void onCaptureResultConfirmed( Bitmap result, boolean confirmed, boolean finishCapture );
	}

	// Dialog's result callback
	private ResultListener resultListener;

	private Bitmap pageImage;

	private boolean confirmed = false;
	// Whether to finish capture scenario and return straight to main activity
	private boolean finishCapture = false;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setRetainInstance( true );
	}

	@NonNull
	@Override
	public Dialog onCreateDialog( @Nullable Bundle savedInstanceState )
	{
		Dialog dialog = super.onCreateDialog( savedInstanceState );
		dialog.getWindow().requestFeature( Window.FEATURE_NO_TITLE );
		return dialog;
	}

	@Nullable
	@Override
	public View onCreateView( @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
	{
		super.onCreateView( inflater, container, savedInstanceState );

		View view = inflater.inflate( R.layout.fragment_result, container, false );
		initPagesPreview( view );

		Button done = view.findViewById( R.id.done );
		done.setOnClickListener( this );
		Button addPage = view.findViewById( R.id.addPage );
		addPage.setOnClickListener( this );
		ImageButton closeBtn = view.findViewById( R.id.closeResult );
		closeBtn.setOnClickListener( this );

		pageNumberText = view.findViewById( R.id.pageText );
		// Page number is a sum of saved and additional pages
		pageNumberText.setText( getString( R.string.page, pageCount, pageCount ) );

		return view;
	}

	@Override
	public void onDestroyView()
	{
		if( getDialog() != null && getRetainInstance() ) {
			getDialog().setDismissMessage( null );
		}
		super.onDestroyView();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Dialog dialog = getDialog();
		if( dialog != null ) {
			// Make dialog fullscreen
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setLayout( width, height );
		}
	}

	@Override
	public void onAttach( Context context )
	{
		super.onAttach( context );
		if( context instanceof ResultListener ) {
			resultListener = (ResultListener) context;
		} else {
			throw new ClassCastException( context.toString() + " must implement ResultListener" );
		}
	}

	private void initPagesPreview( View view )
	{
		RecyclerView pagesPreview = view.findViewById( R.id.pagesPreview );
		pagesPreview.setHasFixedSize( true );

		LinearLayoutManager layoutManager = new LinearLayoutManager( getContext() );
		layoutManager.setOrientation( LinearLayoutManager.HORIZONTAL );
		pagesPreview.setLayoutManager( layoutManager );
		PagesPreviewAdapter pagesAdapter = new PagesPreviewAdapter( pageCount, pageImage );
		pagesPreview.setAdapter( pagesAdapter );
		SnapHelper snapHelper = new PagerSnapHelper();
		snapHelper.attachToRecyclerView(pagesPreview);
		pagesPreview.addOnScrollListener( new PagesPreviewAdapter.SnapOnScrollListener( snapHelper, new PagesPreviewAdapter.OnSnapPositionChangeListener() {
			@Override public void onPositionChanged( int position )
			{
				int pageNumber = position + 1;
				pageNumberText.setText( getString( R.string.page, pageNumber, pageCount ) );
			}
		} ) );
		pagesPreview.scrollToPosition( pageCount - 1 );
	}

	private void setPageCount( int pageCount )
	{
		this.pageCount = pageCount;
	}

	private void setPageImage( Bitmap page )
	{
		pageImage = page;
	}

	private void saveTempImageAndFinish( boolean finishCapture )
	{
		this.finishCapture = finishCapture;
		if (pageImage != null) {
			String pageFilePath = ImageUtils.getCaptureSessionPageFile( pageCount, getContext() ).getPath();
			ImageSaver tempImageSaver = new ImageSaver( pageImage, pageFilePath, this );
			tempImageSaver.execute();
		} else {
			confirmed = true;
			dismiss();
		}
	}

	@Override
	public void onImageSaved( @NonNull String filePath )
	{
		confirmed = true;
		dismiss();
	}

	@Override
	public void onError( @NonNull Exception error )
	{
		String errorMessage;
		if( error.getMessage() != null ) {
			errorMessage = error.getMessage();
		} else {
			errorMessage = getString( R.string.unknown_error );
		}
		Toast.makeText( getContext(), errorMessage, Toast.LENGTH_SHORT ).show();
	}

	@Override
	public void onClick( View v )
	{
		switch( v.getId() ) {
			case R.id.addPage:
				saveTempImageAndFinish( false );
				break;
			case R.id.done:
				saveTempImageAndFinish( true );
				break;
			case R.id.closeResult:
				dismiss();
				break;
		}
	}

	@Override
	public void onDismiss( DialogInterface dialog )
	{
		resultListener.onCaptureResultConfirmed( pageImage, confirmed, finishCapture );
		super.onDismiss( dialog );
	}

}