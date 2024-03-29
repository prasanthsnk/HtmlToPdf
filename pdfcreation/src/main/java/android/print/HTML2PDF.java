package android.print;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class HTML2PDF {

    private static final boolean sDebug = false;


    public static final String TAG = HTML2PDF.class.getName();

    private WebView mWebView;

    private HTMLTOPDFListener mListener;

    /**
     * The output folder path, default to "Download/HTML2PDF"
     * PDFResultListener
     *
     * @param outputFolder The output file path
     */
    public void setOutputFolder(File outputFolder) {
        this.mOutputFolder = outputFolder;
    }

    /**
     * The output file name, default to website title + current time in milliseconds
     *
     * @param fileName the output file name
     */
    public void setOutputFileName(String fileName) {
        this.mOutputFileName = fileName;
    }

    /**
     * The horizontal dpi, default to 600
     *
     * @param horizontalDpi The horizontal dpi
     */
    public void setHorizontalDpi(int horizontalDpi) {
        this.mHorizontalDpi = horizontalDpi;
    }

    /**
     * The vertical dpi, default to 600
     *
     * @param verticalDpi The vertical dpi
     */
    public void setVerticalDpi(int verticalDpi) {
        this.mVerticalDpi = verticalDpi;
    }

    /**
     * The margin, default to {@link PrintAttributes.Margins#NO_MARGINS}
     *
     * @param margins The margin
     */
    public void setMargins(PrintAttributes.Margins margins) {
        this.mMargins = margins;
    }

    /**
     * The page size, default to {@link PrintAttributes.MediaSize#NA_LETTER}
     *
     * @param mediaSize The page size
     */
    public void setMediaSize(PrintAttributes.MediaSize mediaSize) {
        this.mMediaSize = mediaSize;
    }

    private File mOutputFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private String mOutputFileName = "untitled.pdf";
    private int mHorizontalDpi = 600;
    private int mVerticalDpi = 600;
    private PrintAttributes.Margins mMargins = PrintAttributes.Margins.NO_MARGINS;
    private PrintAttributes.MediaSize mMediaSize = PrintAttributes.MediaSize.NA_LETTER;

    public void setHTML2PDFListener(HTMLTOPDFListener listener) {
        mListener = listener;
    }

    public HTML2PDF(Context context) {
        mWebView = new WebView(context);
    }

    public HTML2PDF(WebView webView) {
        mWebView = webView;
    }

    public void doHtml2Pdf() {
        mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "page finished loading " + url);
                mOutputFileName = mWebView.getTitle() + "_" + System.currentTimeMillis() + ".pdf";
                createWebPrintJob();
            }
        });
    }

    /**
     * Convert from a URL link
     *
     * @param context  the context
     * @param url      the link
     * @param listener the listener
     */
    public static void fromUrl(Context context, String url, HTMLTOPDFListener listener) {
        HTML2PDF html2PDF = new HTML2PDF(context);
        html2PDF.setHTML2PDFListener(listener);
        html2PDF.fromUrl(url);
    }

    /**
     * Convert from a URL link
     *
     * @param context  the context
     * @param url      the link
     * @param folder   the destination folder
     * @param listener the listener
     */
    public static void fromUrl(@NonNull Context context, @NonNull String url, @NonNull File folder, @Nullable HTMLTOPDFListener listener) {
        HTML2PDF html2PDF = new HTML2PDF(context);
        html2PDF.setOutputFolder(folder);
        html2PDF.setHTML2PDFListener(listener);
        html2PDF.fromUrl(url);
    }

    /**
     * Convert from HTML document
     *
     * @param context      the context
     * @param baseUrl      the base URL
     * @param htmlDocument the HTML string
     * @param listener     the listener
     */
    public static void fromHTMLDocument(@NonNull Context context, @Nullable String baseUrl, @NonNull String htmlDocument, @Nullable HTMLTOPDFListener listener) {
        HTML2PDF html2PDF = new HTML2PDF(context);
        html2PDF.setHTML2PDFListener(listener);
        html2PDF.fromHTMLDocument(baseUrl, htmlDocument);
    }

    /**
     * Convert from content in a WebView
     *
     * @param webView  the WebView
     * @param listener the listener
     */
    public static void fromWebView(@NonNull WebView webView, @Nullable HTMLTOPDFListener listener) {
        HTML2PDF html2PDF = new HTML2PDF(webView);
        html2PDF.setHTML2PDFListener(listener);
        html2PDF.doHtml2Pdf();
    }

    /**
     * Convert from a URL link
     *
     * @param url the URL link
     */
    public void fromUrl(String url) {
        Log.e("fromUrl", "fromUrl");
        mWebView.loadUrl(url);
        doHtml2Pdf();
    }

    /**
     * Convert from HTML document
     *
     * @param baseUrl      the base URL
     * @param htmlDocument the HTML string
     */
    public void fromHTMLDocument(String baseUrl, String htmlDocument) {
        mWebView.loadDataWithBaseURL(baseUrl, htmlDocument, "text/HTML", "UTF-8", null);
        doHtml2Pdf();
    }

    private void createWebPrintJob() {
        if (mWebView == null) {
            return;
        }
        String jobName = mWebView.getContext().getString(R.string.app_name) + " Document";
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(mMediaSize)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", mHorizontalDpi, mVerticalDpi))
                .setMinMargins(mMargins).build();
        PdfPrint pdfPrint = new PdfPrint(attributes);
        pdfPrint.setPdfPrintListener(new PdfPrint.PdfPrintListener() {
            @Override
            public void onWriteFinished(String output) {
                Log.i(TAG, "done creating pdf at: " + output);
                if (mListener != null) {
                    mListener.onConversionFinished(output);
                }
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError: " + "onError");
                if (mListener != null) {
                    mListener.onConversionFailed();
                }
            }
        });
        pdfPrint.print(mWebView.createPrintDocumentAdapter(jobName), mOutputFolder, mOutputFileName);
    }

}
