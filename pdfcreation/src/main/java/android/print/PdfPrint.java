package android.print;

import android.os.Build;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;

public class PdfPrint
{
    public interface PdfPrintListener {
        void onWriteFinished(String output);
        void onError();
    }

        private final PrintAttributes printAttributes;

        private PdfPrintListener mListener;

        public void setPdfPrintListener(PdfPrintListener listener) {
        mListener = listener;
    }

        public PdfPrint(PrintAttributes printAttributes) {
        this.printAttributes = printAttributes;
    }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void print(final PrintDocumentAdapter printAdapter, final File path, final String fileName) {
        printAdapter.onLayout(null, printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                Log.e("Finish", "onLayoutFinished" );
                ParcelFileDescriptor fileDescriptor = getOutputFile(path, fileName);
                if (null == fileDescriptor) {
                    if (mListener != null) {
                        mListener.onError();
                    }
                    return;
                }
                printAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, fileDescriptor, new CancellationSignal(), new PrintDocumentAdapter.WriteResultCallback() {
                    @Override
                    public void onWriteFinished(PageRange[] pages) {
                        Log.e("Finish", "onWriteFinished" );
                        super.onWriteFinished(pages);
                        if (mListener != null) {
                            mListener.onWriteFinished((new File(path, fileName)).getAbsolutePath());
                        }
                    }
                });
            }
        }, null);
    }

        private ParcelFileDescriptor getOutputFile(File path, String fileName) {
        boolean success = true;
        if (!path.exists()) {
            success = path.mkdirs();
        }
        if (success) {
            File file = new File(path, fileName);
            try {
                success = file.createNewFile();
                if (success) {
                    return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
                }
            } catch (Exception e) {

            }
        }
        return null;
    }
}
