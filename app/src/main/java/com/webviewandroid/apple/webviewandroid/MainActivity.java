package com.webviewandroid.apple.webviewandroid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.HTML2PDF;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import com.android.dx.stock.ProxyBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {//implements HTML2PDF.HTML2PDFListener {

//    @Override
//    public void onConversionFailed() {
//
//    }
//
//    @Override
//    public void onConversionFinished(String pdfOutput) {
//        //Log.ERROR("PDF FILE",pdfOutput);
//    }

    private WebView wv;
private File cacheFolder;
    PrintDocumentAdapter  printAdapter;
    private PageRange[] ranges;
    private String TAG ="MainActivity.class";
    ParcelFileDescriptor descriptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        wv = (WebView) findViewById(R.id.web_view);
        wv.loadUrl("file:///android_asset/report.html");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //printOrCreatePdfFromWebview(wv,"report");
                //printOrCreatePdf();
                //printDocument(wv);
                HTML2PDF Html = new HTML2PDF(MainActivity.this);
//                Html.fromUrl("file:///android_asset/report.html");
                Html.fromHTMLDocument("",Doc);
                //Html.doHtml2Pdf();;
               // HTML2PDF.fromWebView(wv,MainActivity.this);
            }
        });
    }

    public PrintJob printOrCreatePdfFromWebview(WebView webview, String jobName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Context c = webview.getContext();
            PrintDocumentAdapter printAdapter;
            PrintManager printManager = (PrintManager) c.getSystemService(Context.PRINT_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                printAdapter = webview.createPrintDocumentAdapter(jobName);
            } else {
                printAdapter = webview.createPrintDocumentAdapter();
            }
            if (printManager != null) {
                return printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            }
        } else {
            Log.e(getClass().getName(), "ERROR: Method called on too low Android API version");
        }
        return null;
    }

    public void printDocument(View viewjjj)
    {
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +
                " Document";

        printManager.print(jobName, new MyPrintDocumentAdapter(MainActivity.this),
                null);
    }


    public void printOrCreatePdf(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/report.pdf");


        if (file.exists()) {
            file.delete();
        }
        try
        {
            printAdapter = wv.createPrintDocumentAdapter();
            file.createNewFile();
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
            PrintAttributes attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("id", PRINT_SERVICE, 300, 300))
                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                    .setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0))
                    .build();
            ranges = new PageRange[]{new PageRange(1, 5)};
            cacheFolder =  new File(getFilesDir() +"/etemp/");
if(!cacheFolder.exists()){
    cacheFolder.mkdir();

}

            printAdapter.onStart();

            printAdapter.onLayout(attributes,attributes,new CancellationSignal(),getLayoutResultCallback(new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

                    if (method.getName().equals("onLayoutFinished")) {
                        onLayoutSuccess();
                    } else {
                        Log.e(TAG, "Layout failed");
                        //pdfCallback.onPdfFailed();
                    }
                    return null;
                }
            }, cacheFolder), new Bundle());





        }
        catch (Exception e){
e.printStackTrace();
        }

    }


    private void onLayoutSuccess() throws IOException {
        PrintDocumentAdapter.WriteResultCallback callback = getWriteResultCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if (method.getName().equals("onWriteFinished")) {
                    //pdfCallback.onPdfCreated();
                    System.out.print("Create");
                } else {
                    System.out.print("Failed");
                    //pdfCallback.onPdfFailed();
                }
                return null;
            }
        }, cacheFolder);
        printAdapter.onWrite(ranges, descriptor, new CancellationSignal(), callback);
    }
    public static PrintDocumentAdapter.LayoutResultCallback getLayoutResultCallback(InvocationHandler invocationHandler,
                                                                                    File dexCacheDir) throws IOException {
        return ProxyBuilder.forClass(PrintDocumentAdapter.LayoutResultCallback.class)
                .dexCache(dexCacheDir)
                .handler(invocationHandler)
                .build();
    }

    public static PrintDocumentAdapter.WriteResultCallback getWriteResultCallback(InvocationHandler invocationHandler,
                                                                                  File dexCacheDir) throws IOException {
        return ProxyBuilder.forClass(PrintDocumentAdapter.WriteResultCallback.class)
                .dexCache(dexCacheDir)
                .handler(invocationHandler)
                .build();
    }


    public boolean IsPermissionAllowed(){
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                //File write logic here
                return true;
            }else{
                return false;
            }
        }
       return  false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public class MyPrintDocumentAdapter extends PrintDocumentAdapter
    {
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 4;

        MyPrintDocumentAdapter(Context context){
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight =
                    newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            pageWidth =
                    newAttributes.getMediaSize().getWidthMils()/1000 * 72;

            if (cancellationSignal.isCanceled() ) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }

        @Override
        public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {

            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i))
                {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();


                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i);
                    myPdfDocument.finishPage(page);
                }
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

        private void drawPage(PdfDocument.Page page,
                              int pagenumber) {
            Canvas canvas = page.getCanvas();

            pagenumber++; // Make sure page numbers start at 1

            int titleBaseLine = 72;
            int leftMargin = 54;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            canvas.drawText(
                    "Test Print Document Page " + pagenumber,
                    leftMargin,
                    titleBaseLine,
                    paint);

            paint.setTextSize(14);
            canvas.drawText("This is some test content to verify that custom document printing works", leftMargin, titleBaseLine + 35, paint);

            if (pagenumber % 2 == 0)
                paint.setColor(Color.RED);
            else
                paint.setColor(Color.GREEN);

            PdfDocument.PageInfo pageInfo = page.getInfo();


            canvas.drawCircle(pageInfo.getPageWidth()/2,
                    pageInfo.getPageHeight()/2,
                    150,
                    paint);
        }





        private boolean pageInRange(PageRange[] pageRanges, int page)
        {
            for (int i = 0; i<pageRanges.length; i++)
            {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }


    }


String Doc= "<!DOCTYPE html>\n" +
        "<html>\n" +
        "<head>\n" +
        "    <style>\n" +
        "        table {\n" +
        "            width: 100%;\n" +
        "        }\n" +
        "\n" +
        "        table, th, td {\n" +
        "            border: 1px solid black;\n" +
        "            border-collapse: collapse;\n" +
        "        }\n" +
        "\n" +
        "        th, td {\n" +
        "            padding: 15px;\n" +
        "            text-align: left;\n" +
        "        }\n" +
        "\n" +
        "        table#t01 tr:nth-child(even) {\n" +
        "            background-color: #eee;\n" +
        "        }\n" +
        "\n" +
        "        table#t01 tr:nth-child(odd) {\n" +
        "            background-color: #fff;\n" +
        "        }\n" +
        "\n" +
        "        table#t01 th {\n" +
        "            background-color: black;\n" +
        "            color: white;\n" +
        "        }\n" +
        "    </style>\n" +
        "</head>\n" +
        "<body>\n" +
        "\n" +
        "<h2>Styling Tables</h2>\n" +
        "\n" +
        "<table>\n" +
        "    <tr>\n" +
        "        <th>Firstname</th>\n" +
        "        <th>Lastname</th>\n" +
        "        <th>Age</th>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Jill</td>\n" +
        "        <td>Smith</td>\n" +
        "        <td>50</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Eve</td>\n" +
        "        <td>Jackson</td>\n" +
        "        <td>94</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>John</td>\n" +
        "        <td>Doe</td>\n" +
        "        <td>80</td>\n" +
        "    </tr>\n" +
        "</table>\n" +
        "<br>\n" +
        "\n" +
        "<table id=\"t01\">\n" +
        "    <tr>\n" +
        "        <th>Firstname</th>\n" +
        "        <th>Lastname</th>\n" +
        "        <th>Age</th>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Jill</td>\n" +
        "        <td>Smith</td>\n" +
        "        <td>50</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Eve</td>\n" +
        "        <td>Jackson</td>\n" +
        "        <td>94</td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>John</td>\n" +
        "        <td>Doe</td>\n" +
        "        <td>80</td>\n" +
        "    </tr>\n" +
        "</table>\n" +
        "<table id=\"t01\">\n" +
        "    <tr>\n" +
        "        <th>Firstname</th>\n" +
        "        <th>Lastname</th>\n" +
        "        <th>Age</th>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Eve</td>\n" +
        "        <td>Jackson</td>\n" +
        "        <td>94</td>\n" +
        "    </tr>\n" +
        "</table>\n" +
        "<table id=\"t01\">\n" +
        "    <tr>\n" +
        "        <th>Firstname</th>\n" +
        "        <th>Lastname</th>\n" +
        "        <th>Age</th>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Eve</td>\n" +
        "        <td>Jackson</td>\n" +
        "        <td>94</td>\n" +
        "    </tr>\n" +
        "</table>\n" +
        "<table id=\"t01\">\n" +
        "    <tr>\n" +
        "        <th>Firstname</th>\n" +
        "        <th>Lastname</th>\n" +
        "        <th>Age</th>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Eve</td>\n" +
        "        <td>Jackson</td>\n" +
        "        <td>94</td>\n" +
        "    </tr>\n" +
        "</table>\n" +
        "<table id=\"t01\">\n" +
        "    <tr>\n" +
        "        <th>Firstname</th>\n" +
        "        <th>Lastname</th>\n" +
        "        <th>Age</th>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Eve</td>\n" +
        "        <td>Jackson</td>\n" +
        "        <td>94</td>\n" +
        "    </tr>\n" +
        "</table>\n" +
        "<table id=\"t01\">\n" +
        "    <tr>\n" +
        "        <th>Firstname</th>\n" +
        "        <th>Lastname</th>\n" +
        "        <th>Age</th>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Eve</td>\n" +
        "        <td>Jackson</td>\n" +
        "        <td>94</td>\n" +
        "    </tr>\n" +
        "</table>\n" +
        "<table id=\"t01\">\n" +
        "    <tr>\n" +
        "        <th>Firstname</th>\n" +
        "        <th>Lastname</th>\n" +
        "        <th>Age</th>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "        <td>Eve</td>\n" +
        "        <td>Jackson</td>\n" +
        "        <td>94</td>\n" +
        "    </tr>\n" +
        "</table>\n" +
        "\n" +
        "</body>\n" +
        "</html>\n";




}
