package android.print;

public interface HTMLTOPDFListener {
    void onConversionFinished(String pdfOutput);

    void onConversionFailed();
}