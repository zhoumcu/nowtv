package com.pccw.nowplayer.model;

/**
 * Created by Swifty on 5/3/2016.
 */
public class NodeType {
    public static final String BANNER = "banner";
    public static final String RESUME = "resume";
    public static final String WATCHLIST = "watchlist";
    public static final String LIVE = "live";
    public static final String RECOMMENDATION = "recommendation";
    public static final String AD = "ad";
    public static final String VODS = "vod";
    // attributes
    public static final long VOD = (1 << 1);
    public static final long EPG = (1 << 2);
    public static final long NPVR = (1 << 3);
    public static final long PVR = (1 << 4);
    public static final long Premium = (1 << 5);
    public static final long VE = (1 << 6);
    public static final long Sports = (1 << 7);
    public static final long OnTV = (1 << 8);
    public static final long OnApp = (1 << 9);
    public static final long OnWeb = (1 << 10);
    public static final long Recordable = (1 << 11);
    public static final long AdultContent = (1 << 12);
    public static final long ThreeD = (1 << 13);
    public static final long FreeToWatch = (1 << 14);
    public static final long Episodic = (1 << 15);
    public static final long Downloadable = (1 << 16);

    // node type
    public static final long Program = (1 << 20);
    public static final long Channel = (1 << 21);
    public static final long Episode = (1 << 22);
    public static final long Series = (1 << 23);
    public static final long Node = (1 << 24);
    public static final long Genre = (1 << 25);
    public static final long SubGenre = (1 << 26);
    public static final long Category = (1 << 27);
    public static final long Cat3Parent = (1 << 28);
    public static final long Cat3 = (1 << 29);
    public static final long LandingSection = (1 << 30);
    public static final long Group = (1 << 31); // a group of programs, no further sub-category / sub-genre etc.
    public static final long LandingResume = 1L << 32;
    public static final long LandingBanner = 1L << 33;
    public static final long Trailer = 1L << 34;
    public static final long BonusVideo = 1L << 35;
    public static final long LandingWatchList = 1L << 36;
    public static final long LandingLive = 1L << 37;
    public static final long LandingRecommendation = 1L << 38;
    public static final long AdSection = 1L << 39;
    public static final long AdBanner = 1L << 40;

    // combo
    public static final long EPGChannel = EPG | Channel;
    public static final long EPGProgram = EPG | Program;
    public static final long EPGSeries = EPG | Series;
    public static final long VODProgram = VOD | Program;
    public static final long VODSeries = VOD | Series;
    public static final long Leaf = Program | Episode;
    public static final long Hybrid = Series | Cat3 | Channel;
    public static final long Product = Leaf | Hybrid;
    public static final long Branch = Node | Genre | SubGenre | Category | LandingSection | Group;
    public static final long ChannelItemStyle = EPG | Channel | Cat3;

    public static boolean isType(long myType, long compareType) {
        return isType(myType, compareType, false);
    }

    public static boolean isType(long myType, long compareType, boolean strict) {
        return strict ? (myType & compareType) == compareType : (myType & compareType) != 0;
    }
}
