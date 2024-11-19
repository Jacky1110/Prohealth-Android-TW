package com.v7idea.template.Tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class LoadQRimage {
    private static final String handle = LoadQRimage.class.getSimpleName();

    public static void imageLoading(Context context, String url, ImageView imageView) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(480, 800) //保存每個緩存圖片的最大寬高
                .threadPriority(Thread.NORM_PRIORITY - 2) //線池中的緩存數
                .denyCacheImageMultipleSizesInMemory() //禁止緩存多張圖片
                .memoryCache(new FIFOLimitedMemoryCache(2 * 1024 * 1024))//缓存策略
//                .memoryCacheSize(50 * 1024 * 1024) //設置內存緩存的大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //缓存文件名的保存方式
//                .diskCacheSize(200 * 1024 * 1024) //緩存大小
                .tasksProcessingOrder(QueueProcessingType.LIFO) //工作序列
                .diskCacheFileCount(200) //緩存的文件數量
                .build();
        if (!ImageLoader.getInstance().isInited()) {//偵測如果imagloader已經init，就不再init
            Log.d(handle, "CheckPoint init");
            ImageLoader.getInstance().init(config);
        }
        ImageLoader.getInstance().displayImage(url, imageView);

    }
    //
//    DisplayImageOptions ImageUsing = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.loading)
//            .showImageForEmptyUri(R.drawable.loading)
//            .showImageOnFail(R.drawable.loading).cacheInMemory(true)//緩存
//            .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
//            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//            .build();
}
