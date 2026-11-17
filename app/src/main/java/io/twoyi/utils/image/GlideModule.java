/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package io.twoyi.utils.image;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

/** Loads installed app icons directly, without Glide annotation processing. */
public final class GlideModule {
    private GlideModule() { }
    public static void loadApplicationIcon(Context context, ApplicationInfo applicationInfo, ImageView view) {
        Glide.with(context).load(applicationInfo.loadIcon(context.getPackageManager())).into(view);
    }
}
