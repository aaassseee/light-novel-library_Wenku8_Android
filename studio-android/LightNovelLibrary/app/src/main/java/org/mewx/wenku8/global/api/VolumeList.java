package org.mewx.wenku8.global.api;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by MewX on 2015/5/13.
 */
public class VolumeList implements Serializable {
    public String volumeName;
    public int vid;
    public ArrayList<ChapterInfo> chapterList;
}
