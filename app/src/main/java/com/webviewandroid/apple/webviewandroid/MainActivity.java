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
import android.print.HTMLTOPDFListener;
import android.print.PageRange;
import android.print.PdfPrint;
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


public class MainActivity extends AppCompatActivity implements HTMLTOPDFListener {

    @Override
    public void onConversionFailed() {

    }

    @Override
    public void onConversionFinished(String pdfOutput) {
        //Log.ERROR("PDF FILE",pdfOutput);
    }

    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        wv = findViewById(R.id.web_view);
        wv.loadUrl("file:///android_asset/report.html");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HTML2PDF Html = new HTML2PDF(MainActivity.this);
                Html.fromHTMLDocument("", Doc);
                Html.setHTML2PDFListener(MainActivity.this);
            }
        });
    }

    String Doc = "<!DOCTYPE html>\n" +
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
