package com.phantom.utils;


import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;
import android.util.SparseArray;

import com.phantom.onetapvideodownload.Video.Video;
import com.phantom.onetapvideodownload.Video.YoutubeVideo;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class YoutubeParserProxy {
    private static final String TAG = "YoutubeParserProxy";
    private static final String YOUTUBE_URL_PREFIX = "http://youtube.com/watch?v=";

    public static void startParsing(final Context context, String param, final Invokable<Video, Integer> invokable) {
        YouTubeUriExtractor mYoutubeExtractor = new YouTubeUriExtractor(context) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                    YoutubeVideo video = new YoutubeVideo(videoTitle, videoId);
                    for(Pair p : YoutubeVideo.itagQualityMapping) {
                        YtFile videoFormat = ytFiles.get(Integer.parseInt(p.first.toString()));
                        if (videoFormat == null) {
                            continue;
                        }
                        video.addFormat(videoFormat.getUrl(), Integer.parseInt(p.first.toString()));
                    }

                    try {
                        invokable.invoke(video);
                    } catch (java.lang.Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "URLs are empty");
                }
            }
        };

        Log.v(TAG, YOUTUBE_URL_PREFIX + param);
        mYoutubeExtractor.setParseDashManifest(true);
        mYoutubeExtractor.execute(YOUTUBE_URL_PREFIX + param);
    }
}
