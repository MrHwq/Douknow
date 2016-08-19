package com.hwqgooo.databinding.command.imageview;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hwqgooo.databinding.command.ReplyCommand;

public class ViewBindingAdapter {
    public final static String TAG = "ImageViewBindingAdapter";

//    @BindingAdapter({"uri"})
//    public static void setImageUri(ImageView iv, String uri) {
//        try {
//            Log.d(TAG, "imageview load uri " + uri);
//            if (!TextUtils.isEmpty(uri)) {
//                Glide.with(iv.getContext())
//                        .load(uri)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .fitCenter()
//                        .into(iv);
//            } else {
//                iv.setBackgroundDrawable(null);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @BindingAdapter(value = {"uri",
            "errorImage",
            "onSuccessCommand",
            "onFailureCommand"},
            requireAll = false)
    public static void loadUrlImage(final ImageView imageView, String uri,
                                    @DrawableRes final int errorImage,
                                    final ReplyCommand<Drawable> onSuccessCommand,
                                    final ReplyCommand onFailureCommand) {
        if (TextUtils.isEmpty(uri)) {
            imageView.setImageDrawable(null);
            return;
        }
        try {
            Glide.with(imageView.getContext())
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            imageView.setImageResource(errorImage);
                            if (onFailureCommand != null) {
                                onFailureCommand.execute();
                            }
                            Log.d(TAG, "onLoadFailed: ");
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super
                                GlideDrawable> glideAnimation) {
                            imageView.setImageDrawable(resource);
                            if (onSuccessCommand != null) {
                                onSuccessCommand.execute(resource);
                            }
                        }
                    });
        } catch (IllegalStateException e) {
            Log.d(TAG, "loadUrlImage: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

