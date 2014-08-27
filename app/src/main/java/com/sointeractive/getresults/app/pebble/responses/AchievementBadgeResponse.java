package com.sointeractive.getresults.app.pebble.responses;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.sointeractive.android.kit.util.PebbleDictionary;
import com.sointeractive.getresults.app.pebble.responses.utils.DictionaryBuilder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class AchievementBadgeResponse implements ResponseItem {
    private static final String TAG = AchievementBadgeResponse.class.getSimpleName();

    private static final int RESPONSE_ID = 6;
    private static final int BITS_IN_BYTE = 8;

    private static final int PACKAGE_BYTE_SIZE = 86;

    private static final int IMAGE_WIDTH = 64;
    private static final int IMAGE_HEIGHT = 64;

    private final int id;
    private final int offset;
    private final byte[] bytes;

    public AchievementBadgeResponse(final int id, final int offset, final byte[] bytes) {
        this.id = id;
        this.offset = offset;
        this.bytes = bytes;
    }

    public static Collection<ResponseItem> getResponse(final int id, final Bitmap badge) {
        final Collection<ResponseItem> responses = new LinkedList<ResponseItem>();

        final Queue<Byte> bytesQueue = getBytesQueue(badge);
        final Queue<byte[]> badgeParts = partitionBadge(bytesQueue);

        int offset = 0;
        while (!badgeParts.isEmpty()) {
            final byte[] badgePart = badgeParts.poll();
            final ResponseItem item = new AchievementBadgeResponse(id, offset, badgePart);
            responses.add(item);
            offset += badgePart.length;
        }
        return responses;
    }

    private static Queue<byte[]> partitionBadge(final Queue<Byte> bytesList) {
        final Queue<byte[]> bytePackages = new LinkedList<byte[]>();

        final int imageBytesSize = bytesList.size();
        final int fullPackagesNumber = imageBytesSize / PACKAGE_BYTE_SIZE;
        for (int i = 0; i < fullPackagesNumber; i++) {
            final byte[] singleFullPackage = getPackage(bytesList, PACKAGE_BYTE_SIZE);
            bytePackages.add(singleFullPackage);
        }

        final int fullPackagesSize = fullPackagesNumber * PACKAGE_BYTE_SIZE;
        final int lastPackageSize = imageBytesSize - fullPackagesSize;
        final byte[] lastPackage = getPackage(bytesList, lastPackageSize);
        bytePackages.add(lastPackage);

        return bytePackages;
    }

    private static byte[] getPackage(final Queue<Byte> bytesList, final int size) {
        final byte[] singleFullPackage = new byte[size];
        for (int j = 0; j < size; j++) {
            singleFullPackage[j] = bytesList.poll();
        }
        return singleFullPackage;
    }

    public static Queue<Byte> getBytesQueue(final Bitmap badge) {
        final Queue<Byte> bytesQueue = new LinkedList<Byte>();
        final String bitmapString = getBitmapString(badge);
        final Queue<String> byteStrings = partitionStringToBytes(bitmapString);
        for (final String byteString : byteStrings) {
            final String reversedString = new StringBuilder(byteString).reverse().toString();
            final byte byteValue = (byte) Integer.parseInt(reversedString, 2);
            bytesQueue.add(byteValue);
        }
        return bytesQueue;
    }

    public static String getBitmapString(final Bitmap badge) {
        final StringBuilder bitmapStringBuilder = new StringBuilder();
        final int[] bitmapPixels = getBitmapPixels(badge);
        final int averageBrightness = getAverageBrightness(bitmapPixels);
        Log.v(TAG, "Average brightness: " + averageBrightness);
        for (final int pixel : bitmapPixels) {
            if (isWhite(pixel, averageBrightness)) {
                bitmapStringBuilder.append("1");
            } else {
                bitmapStringBuilder.append("0");
            }
        }
        return bitmapStringBuilder.toString();
    }

    private static int getAverageBrightness(final int[] bitmapPixels) {
        int brightnessSum = 0;
        for (final int pixel : bitmapPixels) {
            brightnessSum += Color.red(pixel);
        }
        return brightnessSum / bitmapPixels.length;
    }

    public static boolean isWhite(final int bitmapByte, final int threshold) {
        // Assuming that pixel is gray, so red == green == blue
        return Color.red(bitmapByte) > threshold;
    }

    public static int[] getBitmapPixels(final Bitmap badge) {
        final Bitmap grayIcon = createGrayscale(badge);
        final Bitmap scaledIcon = Bitmap.createScaledBitmap(grayIcon, IMAGE_WIDTH, IMAGE_HEIGHT, false);

        final int pixelsNumber = scaledIcon.getWidth() * scaledIcon.getHeight();
        final int[] intArray = new int[pixelsNumber];
        scaledIcon.getPixels(intArray, 0, scaledIcon.getWidth(), 0, 0, scaledIcon.getWidth(), scaledIcon.getHeight());
        return intArray;
    }

    private static Bitmap createGrayscale(final Bitmap sourceBitmap) {
        final Bitmap outputBitmap = getBlankOutputBitmap(sourceBitmap);
        final Canvas canvas = new Canvas(outputBitmap);
        final ColorMatrix colorMatrix = getColorMatrix();
        final Paint paint = getPaint(colorMatrix);
        canvas.drawBitmap(sourceBitmap, 0, 0, paint);
        return outputBitmap;
    }

    private static ColorMatrix getColorMatrix() {
        final ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        return colorMatrix;
    }

    private static Paint getPaint(final ColorMatrix colorMatrix) {
        final Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        return paint;
    }

    private static Bitmap getBlankOutputBitmap(final Bitmap sourceBitmap) {
        final int width = sourceBitmap.getWidth();
        final int height = sourceBitmap.getHeight();
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    private static Queue<String> partitionStringToBytes(final String text) {
        final Queue<String> descriptionParts = new LinkedList<String>();
        for (int start = 0; start < text.length(); start += BITS_IN_BYTE) {
            descriptionParts.add(text.substring(start, Math.min(text.length(), start + BITS_IN_BYTE)));
        }
        return descriptionParts;
    }

    @Override
    public PebbleDictionary getData() {
        return new DictionaryBuilder(RESPONSE_ID)
                .addInt(id)
                .addInt(offset)
                .addBytes(bytes)
                .build();

    }
}