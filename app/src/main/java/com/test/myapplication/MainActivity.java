package com.test.myapplication;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String DATA_DIR = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        List<ContentsData> _contentsData = null;

        AssetManager as = getResources().getAssets();

        // Contents data
//        String contentsFileName = "Barcode_CONTENTS.plist";
        String contentsFileName = "Barcode128_CONTENTS.plist";
        LWPrintContentsXmlParser xmlParser = new LWPrintContentsXmlParser();
        InputStream in = null;
        try {
            in = as.open(DATA_DIR + "/" + contentsFileName);
            _contentsData = xmlParser.parse(in, "UTF-8");
            Log.i("Test", "test");
        } catch (Exception e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
                in = null;
            }
        }

        String CODE_128_KEY = "Code-CODE128";

        List<ContentsData> _contentsData2 = new ArrayList<>();
        ContentsData contentsData = new ContentsData();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(CODE_128_KEY, "001-L");
        contentsData.setElementMap(hashMap);

        InputStream _formDataInputStream = null;

        if (_formDataInputStream != null) {
            try {
                _formDataInputStream.close();
            } catch (IOException e) {

            }
            _formDataInputStream = null;
        }


        try {
            AssetManager asM = getResources().getAssets();
            _formDataInputStream = as.open(DATA_DIR + "/" + "Barcode.plist");
            Log.i("Test", "test");
        } catch (IOException e) {
            Log.i("Test", "test");
        }

        ImageView ivBarcode = (ImageView) findViewById(R.id.iv_barcode);

        String barcode_data = "00000000";

        // barcode image
        Bitmap bitmap = null;

        try {
            bitmap = BitmapUtils.encodeAsBitmapWithText(makeEAN8("71"), BarcodeFormat.EAN_8, 150, 80, "0012-21312");
            ivBarcode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String makeEAN8(String s) {
        s = addLeadingZero(s, 7);
        int checkSum = checkSum(s);
        s += checkSum;
        return s;
    }

    public static String addLeadingZero(String s, int length) {
        while (s.length() < length) {
            s = "0" + s;
        }
        return s;
    }

    public static int checkSum(String code){
        int sum1 = code.charAt(1)+ code.charAt(3) + code.charAt(5) - (3*48);
        int sum2 = 3 * (code.charAt(0) + code.charAt(2) + code.charAt(4) + code.charAt(6) - 4*48);
        int checksum_value = sum1 + sum2;

        int checksum_digit = 10 - (checksum_value % 10);
        if (checksum_digit == 10)
            checksum_digit = 0;
        return checksum_digit;
    }

    public static Bitmap rotate (Bitmap bitmap, int rotationAngle) throws IOException {
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format,
                          int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        Paint textPaint = new Paint();
        textPaint.setARGB(255, 0, 0, 0);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize((int)(0.2*height));
        textPaint.setTypeface(Typeface.MONOSPACE);

        Bitmap bitmapWithText = Bitmap.createBitmap(width, height + (int)(0.25*height),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithText);
        canvas.drawColor(0xffffffff);
        canvas.drawBitmap(bitmap, 0,0, null);
        canvas.drawText("0012-L", canvas.getWidth()/2, (int)(height * 1.2)  , textPaint);
        return Bitmap.createScaledBitmap(bitmapWithText, 250, 100, false);
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
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
}
