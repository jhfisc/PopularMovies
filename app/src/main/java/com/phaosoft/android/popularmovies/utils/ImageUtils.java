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

/**
 * Image utilities module
 */

public class ImageUtils {

    /**
     * scale a resource drawable ID to width by height.
     *
     * @param context the context that is being used
     * @param image_id the resource drawable ID that needs to be scaled
     * @param width the width to scale the image
     * @param height the height to scale the image
     * @return the scaled drawable image
     */
    public static Drawable scaleImage(Context context, int image_id, int width, int height) {
        Drawable drawable =
                ResourcesCompat.getDrawable(context.getResources(), image_id, null);

        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
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
