package org.mewx.wenku8.global.api;

import android.util.Log;

import org.mewx.wenku8.global.GlobalConfig;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MewX on 2015/4/21.
 */
public class Wenku8Parser {

    public static List<Integer> parseNovelItemList(String str, int page) {
        List<Integer> list = new ArrayList<Integer>();

        // <?xml version="1.0" encoding="utf-8"?>
        // <result>
        // <page num='166'/>
        // <item aid='1143'/>
        // <item aid='1034'/>
        // <item aid='1213'/>
        // <item aid='1'/>
        // <item aid='1011'/>
        // <item aid='1192'/>
        // <item aid='433'/>
        // <item aid='47'/>
        // <item aid='7'/>
        // <item aid='374'/>
        // </result>

        // The returning list of this xml is: (total page, aids)
        // { 166, 1143, 1034, 1213, 1, 1011, 1192, 433, 47, 7, 374 }

        final char SEPERATOR = '\''; // seperator

        // get total page
        int beg = 0, temp;
        beg = str.indexOf(SEPERATOR);
        temp = str.indexOf(SEPERATOR, beg + 1);
        if (beg == -1 || temp == -1) return null; // this is an exception
        list.add(Integer.parseInt(str.substring(beg + 1, temp)));
        if (GlobalConfig.inDebugMode())
            Log.v("MewX", "Add novel page number: " + list.get(list.size() - 1));
        beg = temp + 1; // prepare for loop

        // init array
        while (true) {
            beg = str.indexOf(SEPERATOR, beg);
            temp = str.indexOf(SEPERATOR, beg + 1);
            if (beg == -1 || temp == -1) break;

            list.add(Integer.parseInt(str.substring(beg + 1, temp)));
            if (GlobalConfig.inDebugMode())
                Log.v("MewX", "Add novel aid: " + list.get(list.size() - 1));

            beg = temp + 1; // prepare for next round
        }

        return list;
    }


    static public NovelItemMeta parsetNovelFullMeta(String xml) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            NovelItemMeta nfi = null;
            xmlPullParser.setInput(new StringReader(xml));
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:

                        if ("metadata".equals(xmlPullParser.getName())) {
                            nfi = new NovelItemMeta();
                        } else if ("data".equals(xmlPullParser.getName())) {
                            if ("Title".equals(xmlPullParser.getAttributeValue(0))) {
                                nfi.aid = new Integer(
                                        xmlPullParser.getAttributeValue(1));
                                nfi.title = xmlPullParser.nextText();
                            } else if ("Author".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.author = xmlPullParser.getAttributeValue(1);
                            } else if ("DayHitsCount".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.dayHitsCount = new Integer(xmlPullParser.getAttributeValue(1));
                            } else if ("TotalHitsCount".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.totalHitsCount = new Integer(xmlPullParser.getAttributeValue(1));
                            } else if ("PushCount".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.pushCount = new Integer(xmlPullParser.getAttributeValue(1));
                            } else if ("FavCount".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.favCount = new Integer(xmlPullParser.getAttributeValue(1));
                            } else if ("PressId".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.pressId = xmlPullParser.getAttributeValue(1);
                            } else if ("BookStatus".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.bookStatus = xmlPullParser.getAttributeValue(1);
                            } else if ("BookLength".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.bookLength = new Integer(xmlPullParser.getAttributeValue(1));
                            } else if ("LastUpdate".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.lastUpdate = xmlPullParser.getAttributeValue(1);
                            } else if ("LatestSection".equals(xmlPullParser
                                    .getAttributeValue(0))) {
                                nfi.latestSectionCid = new Integer(
                                        xmlPullParser.getAttributeValue(1));
                                nfi.latestSectionName=xmlPullParser.nextText();
                            }
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
            return nfi;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    static public ArrayList<VolumeList> getVolumeList(String xml) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            ArrayList<VolumeList> l = null;
            VolumeList vl = null;
            ChapterInfo ci = null;
            xmlPullParser.setInput(new StringReader(xml));
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        l = new ArrayList<VolumeList>();
                        break;

                    case XmlPullParser.START_TAG:

                        if ("volume".equals(xmlPullParser.getName())) {
                            vl = new VolumeList();
                            vl.chapterList = new ArrayList<ChapterInfo>();
                            vl.vid = new Integer(xmlPullParser.getAttributeValue(0));

                            // Here the returned text has some format error
                            // And I will handle them then
                            Log.v("MewX-XML", "+ " + vl.vid + "; ");
                        } else if ("chapter".equals(xmlPullParser.getName())) {
                            ci = new ChapterInfo();
                            ci.cid = new Integer(xmlPullParser.getAttributeValue(0));
                            ci.chapterName = xmlPullParser.nextText();
                            Log.v("MewX-XML", ci.cid + "; " + ci.chapterName);
                            vl.chapterList.add(ci);
                            ci = null;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("volume".equals(xmlPullParser.getName())) {
                            l.add(vl);
                            vl = null;
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }

            /** Handle the rest problem */
            // Problem like this:
            // <volume vid="41748"><![CDATA[��һ�� ����ڲԿ�֮ҹ]]>
            // <chapter cid="41749"><![CDATA[����]]></chapter>
            int currentIndex = 0;
            for (int i = 0; i < l.size(); i++) {
                currentIndex = xml.indexOf("volume", currentIndex);
                if (currentIndex != -1) {
                    currentIndex = xml.indexOf("CDATA[", currentIndex);
                    if (xml.indexOf("volume", currentIndex) != -1) {
                        int beg = currentIndex + 6;
                        int end = xml.indexOf("]]", currentIndex);

                        if (end != -1) {
                            l.get(i).volumeName = xml.substring(beg, end);
                            Log.v("MewX-XML", "+ " + l.get(i).volumeName + "; ");
                            currentIndex = end + 1;
                        } else
                            break;

                    } else
                        break;
                } else
                    break;
            }

            return l;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
