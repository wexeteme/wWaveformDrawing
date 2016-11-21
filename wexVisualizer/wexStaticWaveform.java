
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class wexStaticWaveform {

	private Context context;

	private LinearLayout drawingView;

	private drawingImage mVisualizerView;

	private String tag = "wexStaticWaveform TAG - ";

    public static enum TYPE_OF_DRAWING {

        BYTES("BYTES"),
        NUMBERS("NUMBERS"),
        MP3("MP3"),
        WAV("WAV");

        private String value = "";

        TYPE_OF_DRAWING(String value) {
            this.value = value;
        }
    }

	// init wexStaticWaveform
	public wexStaticWaveform(Context context, LinearLayout drawingView) {
		this.context = context;
		this.drawingView = drawingView;
	}
    // init wexStaticWaveform

	// get waveform from file PAHT & FILE
	public void exeWaveform(TYPE_OF_DRAWING TYPE, byte[] bytes, double[] numbers, final String track_uri, File track_file, String bg_color, String color, float stroke, int drawingDensity, int channels) {

		mVisualizerView = new drawingImage(context, bg_color, color, stroke, drawingDensity, channels);

        switch (TYPE){
            case BYTES:
                class getTrackWaveform_BYTES   extends AsyncTask<Void, Void, Bitmap> {

                    byte[] bytes = null;

                    public getTrackWaveform_BYTES(byte[] bytes){
                        this.bytes = bytes;
                    }

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        // TODO Auto-generated method stub

                        // predpostavka deka pesnata e so dva kanala!
                        Bitmap rez = mVisualizerView.myDraw_BYTES(drawingView, bytes);

                        return rez;
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);

                        if (result != null) {
                            Drawable d = new BitmapDrawable(context.getResources(), result);
                            drawingView.setBackground(d);
                        }
                    }

                }
                new getTrackWaveform_BYTES(bytes ).execute(null, null, null);
                break;
            case NUMBERS:
                class getTrackWaveform_NUMBERS extends AsyncTask<Void, Void, Bitmap> {

                    double[] data = null;

                    public getTrackWaveform_NUMBERS(double[] data){
                        this.data = data;
                    }

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        // TODO Auto-generated method stub

                        Bitmap rez = mVisualizerView.myDraw_NUMBERS(drawingView, data);

                        return rez;
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);

                        if (result != null) {
                            Drawable d = new BitmapDrawable(context.getResources(), result);
                            drawingView.setBackground(d);
                        } else {

                        }
                    }
                }
                new getTrackWaveform_NUMBERS(numbers).execute(null, null, null);
                break;
            case MP3:
                class getTrackWaveform_MP3     extends AsyncTask<Void, Void, Bitmap> {

                    String myPath = null; File myFile = null;

                    public getTrackWaveform_MP3(String myPath, File myFile){
                        this.myPath = myPath;
                        this.myFile = myFile;
                    }

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        // TODO Auto-generated method stub

                        if(myFile == null){
                            myFile = new File(myPath);
                        }

                        int size = (int) myFile.length();

                        byte[] bytes = new byte[size];

                        try {
                            FileInputStream f = new FileInputStream(myFile);
                            f.read(bytes);
                            f.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        // kanalite se predadeni preku KONSTRUKTOROT
                        Bitmap rez = mVisualizerView.myDraw_MP3(drawingView, bytes);

                        return rez;
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);

                        if (result != null) {
                            Drawable d = new BitmapDrawable(context.getResources(), result);
                            drawingView.setBackground(d);
                        }
                    }
                }
                new getTrackWaveform_MP3(track_uri, track_file).execute(null, null, null);
                break;
            case WAV:
                class getTrackWaveform_WAV     extends AsyncTask<Void, Void, Bitmap> {

                    String myPath = null; File myFile = null;

                    public getTrackWaveform_WAV(String myPath, File myFile){
                        this.myPath = myPath;
                        this.myFile = myFile;
                    }

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        // TODO Auto-generated method stub

                        if(myFile == null){
                            myFile = new File(myPath);
                        }

                        int size = (int) myFile.length();

                        byte[] bytes = new byte[size];

                        try {
                            FileInputStream f = new FileInputStream(myFile);
                            f.read(bytes);
                            f.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        // kanalite se predadeni preku KONSTRUKTOROT
                        Bitmap rez = mVisualizerView.myDraw_WAV(drawingView, bytes);

                        return rez;
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);

                        if (result != null) {
                            Drawable d = new BitmapDrawable(context.getResources(), result);
                            drawingView.setBackground(d);
                        }
                    }
                }
                new getTrackWaveform_WAV(track_uri, track_file).execute(null, null, null);
                break;
        }

	}

}
