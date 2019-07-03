package com.ixuea.courses.mymusic.parser;

import com.ixuea.courses.mymusic.parser.domain.KSCLyric;
import com.ixuea.courses.mymusic.parser.domain.Line;
import com.ixuea.courses.mymusic.util.CharUtil;
import com.ixuea.courses.mymusic.util.StringUtil;
import com.ixuea.courses.mymusic.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by smile on 2018/5/30.
 */

public class KSCLyricsParser extends LyricsParser {

    public KSCLyricsParser(int type, String content) {
        super(type, content);
    }

    @Override
    public void parse() {
        //拆分成每一行
        String[] strings = content.split("\n");
        //把这些歌词保存为一个对象
        lyric = new KSCLyric();
       //Line用来保存每一行
        TreeMap<Integer, Line> lyrics = new TreeMap<>();
        Map<String, Object> tags = new HashMap<>();

        String lineInfo=null;
        int lineNumber = 0;
        for (int i = 0; i < strings.length; i++) {
            try {
                lineInfo=strings[i];
                Line line =parserLine(tags, lineInfo);
                if (line != null) {
                    lyrics.put(lineNumber, line);
                    lineNumber++;
                }
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }

        lyric.setLyrics(lyrics);
        lyric.setTags(tags);
    }

    /**
     * 解析每一行歌词，它是整行歌词。
     */
    private Line parserLine(Map<String, Object> tags, String lineInfo) {
        String[] left;
        if (lineInfo.startsWith("karaoke.songname")) {
            left = lineInfo.split("\'");
            tags.put(LyricTag.TAG_TITLE, left[1]);
        } else if (lineInfo.startsWith("karaoke.singer")) {
            left = lineInfo.split("\'");
            tags.put(LyricTag.TAG_ARTIST, left[1]);
        } else if (lineInfo.startsWith("karaoke.offset")) {
            left = lineInfo.split("\'");
            tags.put(LyricTag.TAG_OFFSET, left[1]);
        } else if (lineInfo.startsWith("karaoke.tag")) {
            left = lineInfo.split("\'")[1].split(":");
            tags.put(left[0], left[1]);
        } else if (lineInfo.startsWith("karaoke.add")) {
            //歌词的每一行保存为一个Line对象
            Line line = new Line();
            //左边的karaoke.add加上一个括号的长度
            int left1 = "karaoke.add".length() + 1;
            //右侧的长度
            int right = lineInfo.length();
            //把歌词的一行右边的内容拆分成一个数组
            String[] lineComments = lineInfo.substring(left1 + 1, right - 3).split("\'\\s*,\\s*\'", -1);
            String startTimeStr = lineComments[0];
            //截取开始时间
            int startTime = TimeUtil.parseInteger(startTimeStr);
            //获取结束时间
            line.setStartTime(startTime);
            String endTimeStr = lineComments[1];
            int endTime = TimeUtil.parseInteger(endTimeStr);
            line.setEndTime(endTime);
            String lineLyricsStr = lineComments[2];
            List<String> lineLyricsList = getLyricsWord(lineLyricsStr);
            //将歌词转换成数组，每个元素是一个字。
            String[] lyricsWord = (String[]) lineLyricsList.toArray(new String[lineLyricsList.size()]);
            line.setLyricsWord(lyricsWord);
            String lineLyrics = getLineLyric(lineLyricsStr);
            line.setLineLyrics(lineLyrics);
            List<String> wordDurationList = getWordDurationList(lineComments[3]);
            int[] wordDuration = this.getWordDurationList(wordDurationList);
            line.setWordDuration(wordDuration);

            return line;
        }

        return null;
    }

    /**
     * 把歌词的每一行拆成一个字一个字组成一个数组
     * @param line
     * @return
     */
    private List<String> getLyricsWord(String line) {
        List<String> lineLyricsList = new ArrayList();
        String temp = "";
        boolean isEnter = false;

        for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (CharUtil.isChinese(c) || !CharUtil
                    .isWord(c) && c != 91 && c != 93) {
                if (isEnter) {
                    temp = temp + String.valueOf(line.charAt(i));
                } else {
                    lineLyricsList.add(String.valueOf(line.charAt(i)));
                }
            } else if (c == 91) {
                isEnter = true;
            } else if (c == 93) {
                isEnter = false;
                lineLyricsList.add(temp);
                temp = "";
            } else {
                temp = temp + String.valueOf(line.charAt(i));
            }
        }

        return lineLyricsList;
    }

    private int[] getWordDurationList(List<String> wordDurationList) {
        int[] wordDuration = new int[wordDurationList.size()];

        for (int i = 0; i < wordDurationList.size(); ++i) {
            String wordDurationStr = wordDurationList.get(i);
            if (StringUtil.isNumber(wordDurationStr)) {
                wordDuration[i] = Integer.parseInt(wordDurationStr);
            }
        }

        return wordDuration;
    }

    private List<String> getWordDurationList(String wordDurationString) {
        ArrayList wordDurationList = new ArrayList();
        String temp = "";

        for (int i = 0; i < wordDurationString.length(); ++i) {
            char c = wordDurationString.charAt(i);
            switch (c) {
                case ',':
                    wordDurationList.add(temp);
                    temp = "";
                    break;
                default:
                    if (Character.isDigit(c)) {
                        temp = temp + String.valueOf(wordDurationString.charAt(i));
                    }
            }
        }

        if (!temp.equals("")) {
            wordDurationList.add(temp);
        }

        return wordDurationList;
    }


    private String getLineLyric(String lineLyricsStr) {
        String temp = "";
        int i = 0;

        while (i < lineLyricsStr.length()) {
            switch (lineLyricsStr.charAt(i)) {
                case '\\':
                default:
                    temp = temp + String.valueOf(lineLyricsStr.charAt(i));
                case '[':
                case ']':
                    ++i;
            }
        }

        return temp;
    }

    public static class LyricTag {

        public static final String TAG_TITLE = "lyrics.tag.title";
        public static final String TAG_ARTIST = "lyrics.tag.artist";
        public static final String TAG_OFFSET = "lyrics.tag.offset";
        public static final String TAG_BY = "lyrics.tag.by";
        public static final String TAG_TOTAL = "lyrics.tag.total";
    }

}
