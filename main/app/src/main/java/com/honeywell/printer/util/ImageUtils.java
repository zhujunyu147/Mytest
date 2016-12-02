package com.honeywell.printer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by zhujunyu on 16/9/21.
 */
public class ImageUtils {
    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];

        //获取位图中的像素值存入pixels
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        //0xFF 右移24位
        int alpha = 0xFF << 24;
        Log.e("**********0****", "" + System.currentTimeMillis());
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                //分离三原色
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                //转化成灰度像素
                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Log.e("**********1****", "" + System.currentTimeMillis());
        //新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 500, 500);
//        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        return resizeBmp;
    }

    public static Bitmap convertToBlackWhite3(Bitmap switchBitmap) {
        int width = switchBitmap.getWidth();
        int height = switchBitmap.getHeight();
        Log.e("图片的长宽", "宽：" + width + "高:" + height);
        Log.e("**********0****", "" + System.currentTimeMillis());

        int red;
        int green;
        int blue;
        int alpha;
        int avg;
        int[] pixels = new int[width * height];
        switchBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        switchBitmap.recycle();
        Log.e("**********1****", "" + System.currentTimeMillis());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int grey = pixels[height * i + j];
                //获得三原色
                red = Color.red(grey);
                green = Color.green(grey);
                blue = Color.blue(grey);
                alpha = Color.alpha(grey);
                avg = (red + green + blue) / 3;// RGB average


                if (avg > 126) {
                    pixels[height * i + j] = Color.argb(alpha, 255, 255, 255);
                } else {
                    pixels[height * i + j] = Color.argb(alpha, 0, 0, 0);
                }
            }
        }
//        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Bitmap newBitmap = createBitmap(width, height, Bitmap.Config.RGB_565);
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBitmap, 600, 600);
        newBitmap.recycle();
        Log.e("**********2****", "" + System.currentTimeMillis());
        return resizeBmp;
    }

    public static Bitmap convertToBlackWhite2(Bitmap switchBitmap) {
        int width = switchBitmap.getWidth();
        int height = switchBitmap.getHeight();
        Log.e("**********0****", "" + System.currentTimeMillis());
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(switchBitmap, new Matrix(), new Paint());
        int current_color;
        int red;
        int green;
        int blue;
        int alpha;
        int avg = 0;
        Log.e("**********1****", "" + System.currentTimeMillis());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Log.e("---------------", "width:" + width + "height:" + height);
                //获得每一个位点的颜色
                current_color = switchBitmap.getPixel(i, j);
                //获得三原色
                red = Color.red(current_color);
                green = Color.green(current_color);
                blue = Color.blue(current_color);

                alpha = Color.alpha(current_color);
                avg = (red + green + blue) / 3;// RGB average
                if (avg >= 210) {  //亮度：avg>=126
                    //设置颜色
                    newBitmap.setPixel(i, j, Color.argb(alpha, 255, 255, 255));// white
                } else if (avg < 210 && avg >= 80) {  //avg<126 && avg>=115
                    newBitmap.setPixel(i, j, Color.argb(alpha, 108, 108, 108));//grey
//                    newBitmap.setPixel(i, j, Color.argb(alpha, 0, 0, 0));// black
                } else {
                    newBitmap.setPixel(i, j, Color.argb(alpha, 0, 0, 0));// black
                }
            }
        }
        Log.e("**********2****", "" + System.currentTimeMillis());
        return newBitmap;
    }


    public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            Log.e("内存溢出", "创建图片内存溢出");
            while(bitmap == null) {
                System.gc();
                System.runFinalization();
                bitmap = createBitmap(width, height, config);
            }
        }
        return  bitmap;
    }

    public static byte[] image2byte(String path) {
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }


    public static byte[] getByteFromBitmap(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] array = buffer.array();
        return array;
    }


    /**
     * 将Bitmap存为 .bmp格式图片
     *
     * @param bitmap
     */
    public static String saveBmp(Context context, Bitmap bitmap) {
        String fileName = null;
        if (bitmap == null) {
            return fileName;
        }
        // 位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        Log.e("数据图像的大小", "" + bufferSize);
        Log.e("**********2****", "" + System.currentTimeMillis());
        try {
            // 存储文件名
            String filehead = String.valueOf(System.currentTimeMillis());
            String path = FileUtil.setFolderPath(context, "PRINTER_IMG");
            fileName = path + filehead + ".bmp";
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileos = new FileOutputStream(fileName);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头
            writeDword(fileos, biSize);
            writeLong(fileos, biWidth);
            writeLong(fileos, biHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            Log.e("**********3****", "" + System.currentTimeMillis());

            for (int i = 0; i < nBmpHeight; i++) {
                for (int j = 0; j < nBmpWidth; j++) {

                }
            }


            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol) {
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                }
            }
            fileos.write(bmpData);
            fileos.flush();
            fileos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("**********4****", "" + System.currentTimeMillis());
        return fileName;
    }

    protected static void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    protected static void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    protected static void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    private static final int BMP_WIDTH_OF_TIMES = 4;
    private static final int BYTE_PER_PIXEL = 3;

    public static String save(Bitmap orgBitmap, Context context) throws IOException {
        long start = System.currentTimeMillis();
        if (orgBitmap == null) {
            return null;
        }

        boolean isSaveSuccess = true;
        //image size
        int width = orgBitmap.getWidth();
        int height = orgBitmap.getHeight();
        byte[] dummyBytesPerRow = null;
        boolean hasDummy = false;
        int rowWidthInBytes = BYTE_PER_PIXEL * width;
        if (rowWidthInBytes % BMP_WIDTH_OF_TIMES > 0) {
            hasDummy = true;
            dummyBytesPerRow = new byte[(BMP_WIDTH_OF_TIMES - (rowWidthInBytes % BMP_WIDTH_OF_TIMES))];
            for (int i = 0; i < dummyBytesPerRow.length; i++) {
                dummyBytesPerRow[i] = (byte) 0xFF;
            }
        }

        int[] pixels = new int[width * height];
        int imageSize = (rowWidthInBytes + (hasDummy ? dummyBytesPerRow.length : 0)) * height;
        int imageDataOffset = 0x36;
        int fileSize = imageSize + imageDataOffset;
        orgBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        ByteBuffer buffer = ByteBuffer.allocate(fileSize);
        buffer.put((byte) 0x42);
        buffer.put((byte) 0x4D);
        buffer.put(writeInt(fileSize));
        buffer.put(writeShort((short) 0));
        buffer.put(writeShort((short) 0));
        buffer.put(writeInt(imageDataOffset));
        buffer.put(writeInt(0x28));
        buffer.put(writeInt(width + (hasDummy ? (dummyBytesPerRow.length == 3 ? 1 : 0) : 0)));
        buffer.put(writeInt(height));
        buffer.put(writeShort((short) 1));

        //bit count
        buffer.put(writeShort((short) 24));

        //bit compression
        buffer.put(writeInt(0));

        //image data size
        buffer.put(writeInt(imageSize));
        //horizontal resolution in pixels per meter
        buffer.put(writeInt(0));

        //vertical resolution in pixels per meter (unreliable)
        buffer.put(writeInt(0));

        buffer.put(writeInt(0));

        buffer.put(writeInt(0));

        /** BITMAP INFO HEADER Write End */

        int row = height;
        int col = width;
        int startPosition = (row - 1) * col;
        int endPosition = row * col;
        while (row > 0) {
            for (int i = startPosition; i < endPosition; i++) {
                buffer.put((byte) (pixels[i] & 0x000000FF));
                buffer.put((byte) ((pixels[i] & 0x0000FF00) >> 8));
                buffer.put((byte) ((pixels[i] & 0x00FF0000) >> 16));
            }
            if (hasDummy) {
                buffer.put(dummyBytesPerRow);
            }
            row--;
            endPosition = startPosition;
            startPosition = startPosition - col;
        }
        // 存储文件名
        String filehead = String.valueOf(System.currentTimeMillis());
        String path = FileUtil.setFolderPath(context, "PRINTER_IMG");
        String fileName = path + File.separator + filehead + ".bmp";
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }


        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(buffer.array());
        fos.close();
        Log.e("AndroidBmpUtil", System.currentTimeMillis() - start + " ms");
        return fileName;
    }

    /**
     * Write integer to little-endian
     *
     * @param value
     * @return
     * @throws IOException
     */
    private static byte[] writeInt(int value) throws IOException {
        byte[] b = new byte[4];

        b[0] = (byte) (value & 0x000000FF);
        b[1] = (byte) ((value & 0x0000FF00) >> 8);
        b[2] = (byte) ((value & 0x00FF0000) >> 16);
        b[3] = (byte) ((value & 0xFF000000) >> 24);

        return b;
    }

    /**
     * Write short to little-endian byte array
     *
     * @param value
     * @return
     * @throws IOException
     */
    private static byte[] writeShort(short value) throws IOException {
        byte[] b = new byte[2];

        b[0] = (byte) (value & 0x00FF);
        b[1] = (byte) ((value & 0xFF00) >> 8);

        return b;
    }

}
