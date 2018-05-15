/*
   Copyright 2018 John Fischer

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package com.phaosoft.android.popularmovies.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.phaosoft.android.popularmovies.R;

public class ImageUtils {
    public static Drawable scaleImage(Context context, int image, int width, int height) {
        // image_unavailable is too big for the size of the thumbnails, therefore, resize it
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                image, null);
        Bitmap bitmap;
        if (drawable != null) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            try {
                drawable = new BitmapDrawable(context.getResources(),
                        Bitmap.createScaledBitmap(bitmap, width, height, true));
            } catch (IllegalArgumentException e) {
                Log.d("scaleImage", "Image not scaled");
                Log.d("scaleImage", e.getMessage());
            }
        }

        return drawable;
    }

}
