
/*

MIT License

Copyright (c) [2016] [Velimir Avramovski]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package com.wex.ringtonemaker.visual_astatic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class drawingImage {

    private float[] mPoints;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();

    private Context context;

    private String tag = "drawingImages TAG - ";

    private String bg_color = "#dcd0c0";
    private String color = "#f22929";
    private float stroke = 2.0f;
    private int drawingDensity = 2;

    private int channels = 2;

    // constructor
    public drawingImage(Context context, String bg_color, String color, float stroke, int drawingDensity, int channels) {
        this.context = context;

        this.bg_color = bg_color;
        this.color = color;
        this.stroke = stroke;
        this.drawingDensity = drawingDensity;

        // FUCKING CHANNEL IS PASSED HERE
        this.channels = channels;

        mForePaint.setStrokeWidth(stroke);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.parseColor("" + color));
    }

    // drawing bytes
    public Bitmap myDraw_BYTES(LinearLayout l, byte[] bytes) {

        Log.w(tag, "called drawing with bytes ...");

        if (l.getWidth() <= 0 || bytes.length <= 0) {
            Log.w(tag, "Drawing failed, view or bytes failed!");
            return null;
        } else {

            Bitmap result = Bitmap.createBitmap(l.getWidth() - 1, l.getHeight() - 1, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(result);
            c.drawColor(Color.parseColor("" + bg_color));
            // c.drawColor(0, PorterDuff.Mode.CLEAR);

            // nova golemina
            int newSize = (bytes.length);

            // nova niza
            int[] mInts = new int[newSize];

            for (int i = 0; i < (bytes.length); i++) {
                int e = (bytes[i]);
                mInts[i] = e;
            }

            // init za prozorocot vo koj ke crtame
            mRect.set(0, 0, l.getWidth() - 1, l.getHeight() - 1);

            // cekor so koj ke se crta, ne e mozno vo prozorec od 200-300px
            // da se iscrtaat site tocki, pa zatoa ne pravi cekor na
            // diskretizacija
            // primer, 3000 tocki, prozorec so dolzina od 200px -> 3000/200
            // = 15
            // sekoja 15esta tocka ke se zema
            // * drug pogled na ovoj problem, e da se pravi sredna vrednost
            // na site 15
            float _widthStep = (float) (mInts.length) / (mRect.width());

            // vo slucaj, da ima pomalu tocni od dolzinata na prozorecot
            int widthStep = 0;
            if (_widthStep < 1.0) {
                widthStep = 1;
            } else {
                widthStep = (int) _widthStep;
            }

            // 3000 / 15 = 200, kolku tocki ke se crtaat
            int drawSize = (mInts.length) / widthStep;

            // 200 * 8 = 1600, edna tocka - linija, se crta so 4 tocki gore,
            // i 4 dole, zatoa pati 8
            mPoints = new float[(drawSize * 8)];

            // najdi maksimalna amplituda, i kooedinirajse po nea
            int maxAplitude = 0;
            for (int i = 0; i < (mInts.length); i++) {
                if (Math.abs(mInts[i]) > maxAplitude) {
                    maxAplitude = Math.abs(mInts[i]);
                }
            }
            // koeficient na mnozenje na drugite amplitudi
            float k = (float) (maxAplitude) / (((mRect.height()) / 2));

            // najdija srednata tocka od prozorecot, i crtaj gore i dole od
            // nea
            float _middlePoint = (((float) (mRect.height()) / 2));
            int middlePoint = (int) _middlePoint;

            // promenlivi za dvizenje po srednata linija, i crtanje na
            // liniiite
            int xMover = 0;
            int fourMover = 0;

            // koordinatniot sistem e postaven vo gorniot lev agol!
            for (int i = 0; i < (mInts.length - widthStep); i = i + widthStep + (drawingDensity - 1)) {
                // draw positive faced up
                if (mInts[i] > 0) {
                    // point 1, TOP POINT OF UPPER SECTION
                    mPoints[fourMover] = xMover; // x coordinate
                    float preV = ((float) (mInts[i]) / k);
                    // Log.w(tag, "positive = " + preV);
                    mPoints[fourMover + 1] = middlePoint - preV; // y
                    // coordinate

                    // point 2, BOT POINT OF UPPER SECTION
                    mPoints[fourMover + 2] = xMover; // x coordinate
                    // positive value, upper section
                    mPoints[fourMover + 3] = middlePoint; // y coordinate
                }
                // redraw same but faced down, negative
                if (mInts[i] > 0) {
                    // negative value, lower section
                    // point 1, TOP POINT OF LOWER SECTION
                    mPoints[fourMover + 4] = xMover; // x coordinate
                    mPoints[fourMover + 5] = middlePoint; // y coordinate

                    // point 2, BOT POINT OF LOWER SECTION
                    mPoints[fourMover + 6] = xMover; // x coordinate
                    float preV = ((float) (mInts[i]) / k);
                    // Log.w(tag, "negative = " + preV);
                    mPoints[fourMover + 7] = Math.abs(preV) + middlePoint; // y
                    // coordinate
                }

                // se dvizime po x linijata
                xMover = xMover + drawingDensity;

                // obicen mover, koj popolnue eden vektro na sekoja
                // iteracija, horizontalno popolnuva
                // edna tocka ili vektor od wavefromo-ot
                fourMover = fourMover + 8;

                c.drawLines(mPoints, mForePaint);
            }

            return result;
        }
    }

    // drawing numbers
    public Bitmap myDraw_NUMBERS(LinearLayout l, double[] data) {

        Log.w(tag, "called drawing with numbers ...");

        if (l.getWidth() <= 0 || data.length <= 0) {
            Log.w(tag, "Drawing failed, view or bytes failed!");
            return null;
        } else {

            Bitmap result = Bitmap.createBitmap(l.getWidth() - 1, l.getHeight() - 1, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(result);
            c.drawColor(Color.parseColor("" + bg_color));
            // c.drawColor(0, PorterDuff.Mode.CLEAR);

            // nova golemina
            int newSize = (data.length);

            // nova niza
            double[] mInts = new double[newSize];

            for (int i = 0; i < (data.length); i++) {
                double e = (data[i]);
                mInts[i] = e;
            }

            // init za prozorocot vo koj ke crtame
            mRect.set(0, 0, l.getWidth() - 1, l.getHeight() - 1);

            // cekor so koj ke se crta, ne e mozno vo prozorec od 200-300px
            // da se iscrtaat site tocki, pa zatoa ne pravi cekor na
            // diskretizacija
            // primer, 3000 tocki, prozorec so dolzina od 200px -> 3000/200 = 15
            // sekoja 15esta tocka ke se zema
            // * drug pogled na ovoj problem, e da se pravi sredna vrednost na
            // sekoi 15
            float _widthStep = (float) (mInts.length) / (mRect.width());

            // vo slucaj, da ima pomalu tocni od dolzinata na prozorecot
            int widthStep = 0;
            if (_widthStep < 1.0) {
                widthStep = 1;
            } else {
                widthStep = (int) _widthStep;
            }

            // 3000 / 15 = 200, kolku tocki ke se crtaat
            int drawSize = (mInts.length) / widthStep;

            // 200 * 8 = 1600, edna tocka, eden podatok, na primer brojot 5, se crta so 2 tocki gore, i 2
            // dole, od srednata linija, pa zatoa pati brojot na crtanje e drawSize*4
            mPoints = new float[(drawSize * 4)];

            // najdi maksimalna amplituda, i kooedinirajse po nea
            double maxAplitude = 0;
            for (int i = 0; i < (mInts.length); i++) {
                if (Math.abs(mInts[i]) > maxAplitude) {
                    maxAplitude = Math.abs(mInts[i]);
                }
            }
            // koeficient na mnozenje na drugite amplitudi
            float k = (float) (maxAplitude) / (((mRect.height()) / 2));

            // najdija srednata tocka od prozorecot, i crtaj gore i dole od nea
            float _middlePoint = (((float) (mRect.height()) / 2));
            int middlePoint = (int) _middlePoint;

            // promenlivi za dvizenje po srednata linija, i crtanje na liniiite
            int xMover = 0;
            int fourMover = 0;

            // koordinatniot sistem e postaven vo gorniot lev agol!
            for (int i = 0; i < (mInts.length); i = i + widthStep + (drawingDensity - 1)) {
                // draw positive
                if (mInts[i] > 0) {
                    // point 1, TOP POINT OF UPPER SECTION
                    mPoints[fourMover] = xMover; // x coordinate
                    float preV = ((float) (mInts[i]) / k);
                    // Log.w(tag, "positive = " + preV);
                    mPoints[fourMover + 1] = middlePoint - preV; // y
                    // coordinate
                    // point 2, BOT POINT OF UPPER SECTION
                    mPoints[fourMover + 2] = xMover; // x coordinate
                    // positive value, upper section
                    mPoints[fourMover + 3] = middlePoint; // y coordinate
                }
                // draw negative
                if (mInts[i] < 0) {
                    // point 1, TOP POINT OF LOWER SECTION
                    mPoints[fourMover] = xMover; // x coordinate
                    // Log.w(tag, "positive = " + preV);
                    mPoints[fourMover + 1] = middlePoint; // y
                    // coordinate
                    // point 2, BOT POINT OF LOWER SECTION
                    mPoints[fourMover + 2] = xMover; // x coordinate
                    // negative value, upper section
                    float preV = ((float) (Math.abs(mInts[i])) / k);
                    mPoints[fourMover + 3] = middlePoint + Math.abs(preV); // y
                    // coordinate
                }
                // se dvizime po x linijata
                xMover = xMover + drawingDensity;

                // obicen mover, koj popolnue eden vektro na sekoja iteracija,
                // horizontalno popolnuva
                // edna tocka ili vektor od wavefromo-ot
                fourMover = fourMover + 4;

                c.drawLines(mPoints, mForePaint);
            }

            return result;
        }
    }

    // drawing mp3
    public Bitmap myDraw_MP3(LinearLayout l, byte[] bytes) {

        Log.w(tag, "called drawing with MP3 ...");
        Log.w(tag, "WARNING - this aint real MP3 drawing, drawing only bytes");

        if (l.getWidth() <= 0 || bytes.length <= 0) {
            Log.w(tag, "Drawing failed, view or bytes failed!");
            return null;
        } else {

            Bitmap result = Bitmap.createBitmap(l.getWidth() - 1, l.getHeight() - 1, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(result);
            c.drawColor(Color.parseColor("" + bg_color));
            // c.drawColor(0, PorterDuff.Mode.CLEAR);

            Log.w(tag, "channels = " + channels);

            if (channels == 2) {

                Log.w(tag, "2 channels!");

                // nova golemina, novata niza e pola od segasnata, ke gi spoime
                // dvata kanali
                int newSize = (bytes.length / 2);

                // nova niza
                int[] mInts = new int[newSize];

                // sredna vrednost od dvata kanala
                for (int i = 0; i < (bytes.length / 2); i++) {
                    int e = (bytes[i] + bytes[i + newSize]) / 2;
                    mInts[i] = e;
                }

                // gi pravime site pozitivni vrednosti
                for (int i = 0; i < mInts.length; i++) {
                    if (mInts[i] < 0) {
                        mInts[i] = (-1) * mInts[i];
                    }
                }

                // init za prozorocot vo koj ke crtame
                mRect.set(0, 0, l.getWidth() - 1, l.getHeight() - 1);

                // cekor so koj ke se crta, ne e mozno vo prozorec od 200-300px
                // da se iscrtaat site tocki, pa zatoa ne pravi cekor na
                // diskretizacija
                // primer, 3000 tocki, prozorec so dolzina od 200px -> 3000/200
                // = 15
                // sekoja 15esta tocka ke se zema
                // * drug pogled na ovoj problem, e da se pravi sredna vrednost
                // na site 15
                float _widthStep = (float) (mInts.length) / (mRect.width());

                // vo slucaj, da ima pomalu tocni od dolzinata na prozorecot
                int widthStep = 1;
                if (_widthStep < 1.0) {
                    widthStep = 1;
                } else {
                    widthStep = (int) _widthStep;
                }

                // 3000 / 15 = 200, kolku tocki ke se crtaat
                int drawSize = (mInts.length) / widthStep;

                // 200 * 8 = 1600, eden podatok, se crta so 2 tocki gore
                // i 2 dole, zatoa pati 4
                mPoints = new float[(drawSize * 8)];

                // najdi maksimalna amplituda, i kooedinirajse po nea
                int maxAplitude = 0;
                for (int i = 0; i < (mInts.length); i++) {
                    if (Math.abs(mInts[i]) > maxAplitude) {
                        maxAplitude = Math.abs(mInts[i]);
                    }
                }
                // koeficient na mnozenje na drugite amplitudi
                float k = (float) (maxAplitude) / (((mRect.height()) / 2));

                // najdija srednata tocka od prozorecot, i crtaj gore i dole od
                // nea
                float _middlePoint = (((float) (mRect.height()) / 2));
                int middlePoint = (int) _middlePoint;

                // promenlivi za dvizenje po srednata linija, i crtanje na
                // liniiite
                int xMover = 0;
                int fourMover = 0;

                for (int i = 0; i < (mInts.length - widthStep); i = i + widthStep + (drawingDensity - 1)) {
                    // draw positive faced up
                    if (mInts[i] > 0) {
                        // point 1, TOP POINT OF UPPER SECTION
                        mPoints[fourMover] = xMover; // x coordinate
                        float preV = ((float) (mInts[i]) / k);
                        // Log.w(tag, "positive = " + preV);
                        mPoints[fourMover + 1] = middlePoint - preV; // y
                        // coordinate

                        // point 2, BOT POINT OF UPPER SECTION
                        mPoints[fourMover + 2] = xMover; // x coordinate
                        // positive value, upper section
                        mPoints[fourMover + 3] = middlePoint; // y coordinate
                    }
                    // redraw same but faced down, negative
                    if (mInts[i] > 0) {
                        // negative value, lower section
                        // point 1, TOP POINT OF LOWER SECTION
                        mPoints[fourMover + 4] = xMover; // x coordinate
                        mPoints[fourMover + 5] = middlePoint; // y coordinate

                        // point 2, BOT POINT OF LOWER SECTION
                        mPoints[fourMover + 6] = xMover; // x coordinate
                        float preV = ((float) (mInts[i]) / k);
                        // Log.w(tag, "negative = " + preV);
                        mPoints[fourMover + 7] = Math.abs(preV) + middlePoint; // y
                        // coordinate
                    }

                    // se dvizime po x linijata
                    xMover = xMover + drawingDensity;

                    // brojac za nizata na tocki koi treba da se crtaat
                    // obicen mover, koj popolnue eden vektro na sekoja
                    // iteracija, horizontalno popolnuva
                    // edna tocka ili vektor od wavefromo-ot
                    fourMover = fourMover + 8;

                }

                c.drawLines(mPoints, mForePaint);

            }

            // some bug, check it when u have time!
            if (channels == 1) {

                Log.w(tag, "1 channels!");

                // nova golemina
                int newSize = (bytes.length);

                // nova niza
                int[] mInts = new int[newSize];

                for (int i = 0; i < (bytes.length); i++) {
                    int e = (bytes[i]);
                    mInts[i] = e;
                }

                // init za prozorocot vo koj ke crtame
                mRect.set(0, 0, l.getWidth() - 1, l.getHeight() - 1);

                // cekor so koj ke se crta, ne e mozno vo prozorec od 200-300px
                // da se iscrtaat site tocki, pa zatoa ne pravi cekor na
                // diskretizacija
                // primer, 3000 tocki, prozorec so dolzina od 200px -> 3000/200
                // = 15
                // sekoja 15esta tocka ke se zema
                // * drug pogled na ovoj problem, e da se pravi sredna vrednost
                // na site 15
                float _widthStep = (float) (mInts.length) / (mRect.width());

                // vo slucaj, da ima pomalu tocni od dolzinata na prozorecot
                int widthStep = 0;
                if (_widthStep < 1.0) {
                    widthStep = 1;
                } else {
                    widthStep = (int) _widthStep;
                }

                // 3000 / 15 = 200, kolku tocki ke se crtaat
                int drawSize = (mInts.length) / widthStep;

                // 200 * 8 = 1600, edna tocka - linija, se crta so 4 tocki gore,
                // i 4 dole, zatoa pati 8
                mPoints = new float[(drawSize * 8)];

                // najdi maksimalna amplituda, i kooedinirajse po nea
                int maxAplitude = 0;
                for (int i = 0; i < (mInts.length); i++) {
                    if (Math.abs(mInts[i]) > maxAplitude) {
                        maxAplitude = Math.abs(mInts[i]);
                    }
                }
                // koeficient na mnozenje na drugite amplitudi
                float k = (float) (maxAplitude) / (((mRect.height()) / 2));

                // najdija srednata tocka od prozorecot, i crtaj gore i dole od
                // nea
                float _middlePoint = (((float) (mRect.height()) / 2));
                int middlePoint = (int) _middlePoint;

                // promenlivi za dvizenje po srednata linija, i crtanje na
                // liniiite
                int xMover = 0;
                int fourMover = 0;

                // koordinatniot sistem e postaven vo gorniot lev agol!
                for (int i = 0; i < (mInts.length - widthStep); i = i + widthStep + (drawingDensity - 1)) {
                    // draw positive faced up
                    if (mInts[i] > 0) {
                        // point 1, TOP POINT OF UPPER SECTION
                        mPoints[fourMover] = xMover; // x coordinate
                        float preV = ((float) (mInts[i]) / k);
                        // Log.w(tag, "positive = " + preV);
                        mPoints[fourMover + 1] = middlePoint - preV; // y
                        // coordinate

                        // point 2, BOT POINT OF UPPER SECTION
                        mPoints[fourMover + 2] = xMover; // x coordinate
                        // positive value, upper section
                        mPoints[fourMover + 3] = middlePoint; // y coordinate
                    }
                    // redraw same but faced down, negative
                    if (mInts[i] > 0) {
                        // negative value, lower section
                        // point 1, TOP POINT OF LOWER SECTION
                        mPoints[fourMover + 4] = xMover; // x coordinate
                        mPoints[fourMover + 5] = middlePoint; // y coordinate

                        // point 2, BOT POINT OF LOWER SECTION
                        mPoints[fourMover + 6] = xMover; // x coordinate
                        float preV = ((float) (mInts[i]) / k);
                        // Log.w(tag, "negative = " + preV);
                        mPoints[fourMover + 7] = Math.abs(preV) + middlePoint; // y
                        // coordinate
                    }

                    // se dvizime po x linijata
                    xMover = xMover + drawingDensity;

                    // obicen mover, koj popolnue eden vektro na sekoja
                    // iteracija, horizontalno popolnuva
                    // edna tocka ili vektor od wavefromo-ot
                    fourMover = fourMover + 8;

                }

                c.drawLines(mPoints, mForePaint);
            }

            return result;
        }
    }

    // drawing wav
    public Bitmap myDraw_WAV(LinearLayout l, byte[] bytes) {

        Log.w(tag, "called drawing with WAV ...");

        if (l.getWidth() <= 0 || bytes.length <= 0) {
            Log.w(tag, "Drawing failed, view or bytes failed!");
            return null;
        } else {

            Bitmap result = Bitmap.createBitmap(l.getWidth() - 1, l.getHeight() - 1, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(result);
            c.drawColor(Color.parseColor("" + bg_color));
            // c.drawColor(0, PorterDuff.Mode.CLEAR);

            byte[] newBytes = new byte[bytes.length-44];

            // removing the header of the wav FILE
            for(int i = 0; i < bytes.length-44; i++){
                newBytes[i] = bytes[i+44];
            }

            Log.w(tag, "channels = " + channels);

            if (channels == 2) {

                Log.w(tag, "2 channels!");

                // nova golemina, novata niza e pola od segasnata, ke gi spoime
                // dvata kanali
                int newSize = (newBytes.length / 2);

                // nova niza
                int[] mInts = new int[newSize];

                // sredna vrednost od dvata kanala
                for (int i = 0; i < (newBytes.length / 2); i++) {
                    int e = (newBytes[i] + newBytes[i + newSize]) / 2;
                    mInts[i] = e;
                }

                // gi pravime site pozitivni vrednosti
                for (int i = 0; i < mInts.length; i++) {
                    if (mInts[i] < 0) {
                        mInts[i] = (-1) * mInts[i];
                    }
                }

                // init za prozorocot vo koj ke crtame
                mRect.set(0, 0, l.getWidth() - 1, l.getHeight() - 1);

                // cekor so koj ke se crta, ne e mozno vo prozorec od 200-300px
                // da se iscrtaat site tocki, pa zatoa ne pravi cekor na
                // diskretizacija
                // primer, 3000 tocki, prozorec so dolzina od 200px -> 3000/200
                // = 15
                // sekoja 15esta tocka ke se zema
                // * drug pogled na ovoj problem, e da se pravi sredna vrednost
                // na site 15
                float _widthStep = (float) (mInts.length) / (mRect.width());

                // vo slucaj, da ima pomalu tocni od dolzinata na prozorecot
                int widthStep = 1;
                if (_widthStep < 1.0) {
                    widthStep = 1;
                } else {
                    widthStep = (int) _widthStep;
                }

                // 3000 / 15 = 200, kolku tocki ke se crtaat
                int drawSize = (mInts.length) / widthStep;

                // 200 * 8 = 1600, eden podatok, se crta so 2 tocki gore
                // i 2 dole, zatoa pati 4
                mPoints = new float[(drawSize * 8)];

                // najdi maksimalna amplituda, i kooedinirajse po nea
                int maxAplitude = 0;
                for (int i = 0; i < (mInts.length); i++) {
                    if (Math.abs(mInts[i]) > maxAplitude) {
                        maxAplitude = Math.abs(mInts[i]);
                    }
                }
                // koeficient na mnozenje na drugite amplitudi
                float k = (float) (maxAplitude) / (((mRect.height()) / 2));

                // najdija srednata tocka od prozorecot, i crtaj gore i dole od
                // nea
                float _middlePoint = (((float) (mRect.height()) / 2));
                int middlePoint = (int) _middlePoint;

                // promenlivi za dvizenje po srednata linija, i crtanje na
                // liniiite
                int xMover = 0;
                int fourMover = 0;

                for (int i = 0; i < (mInts.length - widthStep); i = i + widthStep + (drawingDensity - 1)) {
                    // draw positive faced up
                    if (mInts[i] > 0) {
                        // point 1, TOP POINT OF UPPER SECTION
                        mPoints[fourMover] = xMover; // x coordinate
                        float preV = ((float) (mInts[i]) / k);
                        // Log.w(tag, "positive = " + preV);
                        mPoints[fourMover + 1] = middlePoint - preV; // y
                        // coordinate

                        // point 2, BOT POINT OF UPPER SECTION
                        mPoints[fourMover + 2] = xMover; // x coordinate
                        // positive value, upper section
                        mPoints[fourMover + 3] = middlePoint; // y coordinate
                    }
                    // redraw same but faced down, negative
                    if (mInts[i] > 0) {
                        // negative value, lower section
                        // point 1, TOP POINT OF LOWER SECTION
                        mPoints[fourMover + 4] = xMover; // x coordinate
                        mPoints[fourMover + 5] = middlePoint; // y coordinate

                        // point 2, BOT POINT OF LOWER SECTION
                        mPoints[fourMover + 6] = xMover; // x coordinate
                        float preV = ((float) (mInts[i]) / k);
                        // Log.w(tag, "negative = " + preV);
                        mPoints[fourMover + 7] = Math.abs(preV) + middlePoint; // y
                        // coordinate
                    }

                    // se dvizime po x linijata
                    xMover = xMover + drawingDensity;

                    // brojac za nizata na tocki koi treba da se crtaat
                    // obicen mover, koj popolnue eden vektro na sekoja
                    // iteracija, horizontalno popolnuva
                    // edna tocka ili vektor od wavefromo-ot
                    fourMover = fourMover + 8;

                }

                c.drawLines(mPoints, mForePaint);

            }

            // some bug, check it when u have time!
            if (channels == 1) {

                Log.w(tag, "1 channels!");

                // nova golemina
                int newSize = (newBytes.length);

                // nova niza
                int[] mInts = new int[newSize];

                for (int i = 0; i < (newBytes.length); i++) {
                    int e = (newBytes[i]);
                    mInts[i] = e;
                }

                // init za prozorocot vo koj ke crtame
                mRect.set(0, 0, l.getWidth() - 1, l.getHeight() - 1);

                // cekor so koj ke se crta, ne e mozno vo prozorec od 200-300px
                // da se iscrtaat site tocki, pa zatoa ne pravi cekor na
                // diskretizacija
                // primer, 3000 tocki, prozorec so dolzina od 200px -> 3000/200
                // = 15
                // sekoja 15esta tocka ke se zema
                // * drug pogled na ovoj problem, e da se pravi sredna vrednost
                // na site 15
                float _widthStep = (float) (mInts.length) / (mRect.width());

                // vo slucaj, da ima pomalu tocni od dolzinata na prozorecot
                int widthStep = 0;
                if (_widthStep < 1.0) {
                    widthStep = 1;
                } else {
                    widthStep = (int) _widthStep;
                }

                // 3000 / 15 = 200, kolku tocki ke se crtaat
                int drawSize = (mInts.length) / widthStep;

                // 200 * 8 = 1600, edna tocka - linija, se crta so 4 tocki gore,
                // i 4 dole, zatoa pati 8
                mPoints = new float[(drawSize * 8)];

                // najdi maksimalna amplituda, i kooedinirajse po nea
                int maxAplitude = 0;
                for (int i = 0; i < (mInts.length); i++) {
                    if (Math.abs(mInts[i]) > maxAplitude) {
                        maxAplitude = Math.abs(mInts[i]);
                    }
                }
                // koeficient na mnozenje na drugite amplitudi
                float k = (float) (maxAplitude) / (((mRect.height()) / 2));

                // najdija srednata tocka od prozorecot, i crtaj gore i dole od
                // nea
                float _middlePoint = (((float) (mRect.height()) / 2));
                int middlePoint = (int) _middlePoint;

                // promenlivi za dvizenje po srednata linija, i crtanje na
                // liniiite
                int xMover = 0;
                int fourMover = 0;

                // koordinatniot sistem e postaven vo gorniot lev agol!
                for (int i = 0; i < (mInts.length - widthStep); i = i + widthStep + (drawingDensity - 1)) {
                    // draw positive faced up
                    if (mInts[i] > 0) {
                        // point 1, TOP POINT OF UPPER SECTION
                        mPoints[fourMover] = xMover; // x coordinate
                        float preV = ((float) (mInts[i]) / k);
                        // Log.w(tag, "positive = " + preV);
                        mPoints[fourMover + 1] = middlePoint - preV; // y
                        // coordinate

                        // point 2, BOT POINT OF UPPER SECTION
                        mPoints[fourMover + 2] = xMover; // x coordinate
                        // positive value, upper section
                        mPoints[fourMover + 3] = middlePoint; // y coordinate
                    }
                    // redraw same but faced down, negative
                    if (mInts[i] > 0) {
                        // negative value, lower section
                        // point 1, TOP POINT OF LOWER SECTION
                        mPoints[fourMover + 4] = xMover; // x coordinate
                        mPoints[fourMover + 5] = middlePoint; // y coordinate

                        // point 2, BOT POINT OF LOWER SECTION
                        mPoints[fourMover + 6] = xMover; // x coordinate
                        float preV = ((float) (mInts[i]) / k);
                        // Log.w(tag, "negative = " + preV);
                        mPoints[fourMover + 7] = Math.abs(preV) + middlePoint; // y
                        // coordinate
                    }

                    // se dvizime po x linijata
                    xMover = xMover + drawingDensity;

                    // obicen mover, koj popolnue eden vektro na sekoja
                    // iteracija, horizontalno popolnuva
                    // edna tocka ili vektor od wavefromo-ot
                    fourMover = fourMover + 8;

                }

                c.drawLines(mPoints, mForePaint);
            }

            return result;
        }
    }

}