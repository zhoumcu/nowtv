package com.pccw.nowplayer.model.node;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.CatalogClient;
import com.pccw.nowplayer.model.DownloadClient;
import com.pccw.nowplayer.model.DownloadStatusTracker;
import com.pccw.nowplayer.model.EPGClient;
import com.pccw.nowplayer.model.FavoriteChannelsClient;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.model.NowIDClient;
import com.pccw.nowplayer.model.PVRClient;
import com.pccw.nowplayer.model.WatchListClient;
import com.pccw.nowplayer.utils.DateBuilder;
import com.pccw.nowplayer.utils.Is;
import com.pccw.nowplayer.utils.LString;
import com.pccw.nowplayer.utils.PromiseUtils;
import com.pccw.nowplayer.utils.Ref;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowplayer.utils.gson.GsonUtil;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.networking.WebTVAPIModels;
import com.pccw.nowtv.nmaf.npx.mynow.DataModels;
import com.pccw.nowtv.nmaf.npx.recommendationEngine.NPXRecommendationEngine;

import org.jdeferred.Promise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kriz on 2016-06-16.
 */
public class BaseNode implements Serializable {
    public static final String TAG = Node.class.getSimpleName();
    protected boolean hasMore;
    transient protected Ref<Node> parent;
    protected long type;
    private LString actorsText;
    private Date addDate;
    private boolean autoPlayNextEpisodeDisabled;
    private String availableOnDevicesText;
    private String availablePlatformsText;
    private int channelId;
    private String cid;
    private String classificationText;
    private String dateText;
    private LString directorsText;
    private transient DownloadStatusTracker downloadTracker;
    private long duration;
    private String durationText;
    private String endDateText;
    private Date endTime;
    private String endTimeText;
    private int episodeCount;
    private String episodeId;
    private String episodeName;
    private int episodeNum;
    private boolean hasOtherTimes;
    private LString imageUrl;
    private boolean isDetached;
    private LString languagesText;
    private String libraryId;
    private LString libraryImageUrl;
    private LString libraryName;
    private boolean loading;
    private String nodeId;
    private String offAirDate;
    private String onAirDate;
    private String recommendationGenreId;
    private String recommendationSubGenreId;
    private String remarks;
    private String seasonName;
    private int seasonNum;
    private String seriesId;
    private LString seriesImageUrl;
    private LString seriesName;
    private String shortTitle;
    private Date startTime;
    private String startTimeText;
    private String statusText;
    private boolean subscribed;
    private String subtitle;
    private String subtitleLanguagesText;
    private LString synopsis;
    private LString title;
    private transient WebTVAPIModels.GetProductDetailOutputModel.ProductDetailModel veDetails;
    private String veDetailsJson;
    private int viewingPeriod;
    private String vodNodeId;

    public void addTypeMask(long mask) {
        this.type |= mask;
    }

    public boolean canAddWatchList() {
        boolean enabled = false;

        // basic allow rules
        if (isProgram() && Is.notEmpty(getNodeId())) {
            enabled = true;
        } else if (isSeries() && Is.notEmpty(getSeriesId())) {
            enabled = true;
        }

        // reject rules
        if (isTrailer() || isBonusVideo()) {
            enabled = false;
        }
        return enabled;
    }

    public boolean canScreencastOnly() {
        boolean enabled = false;
        boolean loggedIn = NowIDClient.getInstance().isLoggedIn();

        if (isEPG()) {
            if (isChannel()) {
                enabled = isOnTVOnly() && (isSubscribed() || !loggedIn);
            } else if (isProgram()) {
                Node channel = EPGClient.getInstance().getChannel(getChannelId());
                enabled = channel != null && channel.canScreencastOnly() && isLive();
            }
        } else if (isVOD() && isProgram() && isOnTVOnly()) {
            if (isVE()) {
                enabled = isRented();
            } else if (isTrailer() || isBonusVideo()) {
                enabled = true;
            } else if (isSubscribable()) {
                enabled = false;
            } else if (isEpisode() && getParent() != null && getParent().isSubscribable()) {
                enabled = false;
            } else {
                enabled = true;
            }
        }
        return enabled;
    }

    public String getActorsText() {
        return actorsText == null ? null : actorsText.toString();
    }

    public void setActorsText(String actorsText) {
        this.actorsText = LString.make(actorsText, actorsText);
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Object getAttribute(String name) {
        return null;
    }

    public String getAvailablePlatformsText() {
        ArrayList<String> platforms = new ArrayList<>();
        Resources res = PlayerApplication.getContext().getResources();
        String tv = res.getString(R.string.tv);
        String mobile = res.getString(R.string.mobile);
        String pc = res.getString(R.string.pc);
        if (isOnTV()) platforms.add(tv);
        if (isOnApp()) platforms.add(mobile);
        if (isOnWeb()) platforms.add(pc);
        String ret = TextUtils.join(", ", platforms);
        return ret;
    }

    public void setAvailablePlatformsText(String availablePlatformsText) {
        this.availablePlatformsText = availablePlatformsText;
    }

    public Node getChannel() {
        return EPGClient.getInstance().getChannel(channelId);
    }

    public String getChannelCode() {
        if (channelId == 0) return null;
        String ret = String.valueOf(channelId);
        while (ret.length() < 3) {
            ret = "0" + ret;
        }
        return ret;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = TypeUtils.toInt(channelId, 0);
    }

    public NMAFBasicCheckout.ItemType getCheckoutType() {

        if (isEPG()) {
            return NMAFBasicCheckout.ItemType.Live;
        }

        if (isVOD()) {
            if (isVE()) {
                return NMAFBasicCheckout.ItemType.VE;
            }

            if (isProgram()) {
                return NMAFBasicCheckout.ItemType.Vod;
            }

            if (isSeries()) {
                return NMAFBasicCheckout.ItemType.SVod;
            }
        }

        // unknown type!
        return null;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getClassificationImageResourceID() {

        String clsType = getClassificationText();
        if (clsType == null) clsType = "";

        int imgName = 0;
        if (isOnApp()) {
            if (clsType.equals("I") || clsType.equals("CAT-I") || clsType.equals("CATXI") || clsType.equals("G")) {
                imgName = R.drawable.ic_class_i;
            } else if (clsType.equals("IIA") || clsType.equals("CAT-IIA") || clsType.equals("CATXIIA") || clsType.equals("PG")) {
                imgName = R.drawable.ic_class_iia;
            } else if (clsType.equals("IIB") || clsType.equals("CAT-IIB") || clsType.equals("CATXIIB") || clsType.equals("M")) {
                imgName = R.drawable.ic_class_iib;
            } else if (clsType.equals("III") || clsType.equals("CAT-III") || clsType.equals("CATXIII") || clsType.equals("M+") || clsType.equals("R18") || clsType.equals("R/18") || clsType.equals("R")) {
                imgName = R.drawable.ic_class_iii;
            } else {
                // unknown!
            }
        } else {
            if (clsType.equals("I") || clsType.equals("CAT-I") || clsType.equals("CATXI") || clsType.equals("G")) {
                imgName = R.drawable.ic_class_g;
            } else if (clsType.equals("IIA") || clsType.equals("CAT-IIA") || clsType.equals("CATXIIA") || clsType.equals("PG")) {
                imgName = R.drawable.ic_class_pg;
            } else if (clsType.equals("IIB") || clsType.equals("CAT-IIB") || clsType.equals("CATXIIB") || clsType.equals("M")) {
                imgName = R.drawable.ic_class_m;
            } else if (clsType.equals("III") || clsType.equals("CAT-III") || clsType.equals("CATXIII") || clsType.equals("M+") || clsType.equals("R18") || clsType.equals("R/18") || clsType.equals("R")) {
                imgName = R.drawable.ic_class_r18;
            } else {
                // unknown!
            }
        }
        return imgName;
    }

    public String getClassificationText() {
        return classificationText;
    }

    public void setClassificationText(String classificationText) {
        this.classificationText = classificationText;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getDirectorsText() {
        return directorsText == null ? null : directorsText.toString();
    }

    public void setDirectorsText(String directorsText) {
        this.directorsText = LString.make(directorsText, directorsText);
    }

    public DownloadStatusTracker getDownloadTracker() {
        return DownloadClient.getInstance().getTracker(this);
    }

    public void setDownloadTracker(DownloadStatusTracker tracker) {
        this.downloadTracker = tracker;
        // TODO inform UI
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getEndDateText() {
        return endDateText;
    }

    public void setEndDateText(String endDateText) {
        this.endDateText = endDateText;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getEndTimeText() {
        return endTimeText;
    }

    public void setEndTimeText(String endTimeText) {
        this.endTimeText = endTimeText;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public int getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(int episodeNum) {
        this.episodeNum = episodeNum;
    }

    public DataModels.NPXMyNowAddWatchlistItemDataModel getHistoryModel() {
        DataModels.NPXMyNowAddWatchlistItemDataModel ret = getWatchListModel();

        if (ret != null) {
            if (DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeNPvrSeries.equals(ret.itemType)) {
                ret.itemType = DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeEpgSeries;
            } else if (DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeEpg.equals(ret.itemType)) {
                ret.itemType = DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeEpg;
            }
        }

        return ret;
    }

    public String getImageUrl() {
        return imageUrl == null ? null : imageUrl.toString();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = LString.make(imageUrl, imageUrl);
    }

    public String getLanguagesText() {
        return languagesText == null ? null : languagesText.toString();
    }

    public void setLanguagesText(String languagesText) {
        this.languagesText = LString.make(languagesText, languagesText);
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public String getLibraryImageUrl() {
        return libraryImageUrl == null ? null : libraryImageUrl.toString();
    }

    public void setLibraryImageUrl(String libraryImageUrl) {
        this.libraryImageUrl = LString.make(libraryImageUrl, libraryImageUrl);
    }

    public String getLibraryName() {
        return libraryName == null ? null : libraryName.toString();
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = LString.make(libraryName, libraryName);
    }

    public String getMyNowItemType() {
        String itemType = null;
        if (isNPVRProgram()) {
            itemType = DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeNPvr;
        } else if (isNPVRSeries()) {
            itemType = DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeNPvrSeries;
        } else if (isEPGProgram()) {
            itemType = DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeEpg;
        } else if (isEPGSeries()) {
            itemType = DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeEpgSeries;
        } else if (isVODProgram()) {
            itemType = DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeVod;
        } else if (isVODSeries()) {
            itemType = DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeVodSeries;
        }
        return itemType;
    }

    public void setMyNowItemType(String itemType) {
        if (itemType == null) {
            // null
        } else if (itemType.equals(DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeEpg)) {
            addTypeMask(NodeType.EPGProgram);
        } else if (itemType.equals(DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeEpgSeries)) {
            addTypeMask(NodeType.EPGSeries);
        } else if (itemType.equals(DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeVod)) {
            addTypeMask(NodeType.VODProgram);
        } else if (itemType.equals(DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeVodSeries)) {
            addTypeMask(NodeType.VODSeries);
        } else if (itemType.equals(DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeNPvr)) {
            addTypeMask(NodeType.EPGProgram | NodeType.NPVR);
        } else if (itemType.equals(DataModels.NPXMyNowAddWatchlistItemDataModel.ItemTypeNPvrSeries)) {
            addTypeMask(NodeType.EPGSeries | NodeType.NPVR);
        }
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getOffAirDate() {
        return offAirDate;
    }

    public void setOffAirDate(String offAirDate) {
        this.offAirDate = offAirDate;
    }

    public String getOnAirDate() {
        return onAirDate;
    }

    public void setOnAirDate(String onAirDate) {
        this.onAirDate = onAirDate;
    }

    public Node getParent() {
        if (parent == null) return null;
        return parent.get();
    }

    public void setParent(Node parent) {
        if (this.parent == null) this.parent = new Ref<Node>();
        this.parent.setWeak(parent);
    }

    public float getPostPaidPrice() {
        throw new RuntimeException("This node does not support VE price");
    }

    public float getPrePaidPrice() {
        throw new RuntimeException("This node does not support VE price");
    }

    public String getRecommendationGenreId() {
        return recommendationGenreId;
    }

    public void setRecommendationGenreId(String recommendationGenreId) {
        this.recommendationGenreId = recommendationGenreId;
    }

    public String getRecommendationSubGenreId() {
        return recommendationSubGenreId;
    }

    public void setRecommendationSubGenreId(String recommendationSubGenreId) {
        this.recommendationSubGenreId = recommendationSubGenreId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public float getRentPrice() {
        float balance = NowIDClient.getInstance().getNowDollarBalance();
        float postPaidPrice = getPostPaidPrice();
        float prePaidPrice = getPrePaidPrice();
        return (balance >= prePaidPrice) ? prePaidPrice : postPaidPrice;
    }

    public Date getRentalExpiry() {
        if (veDetails == null) return null;
        if (veDetails.expiryTimeHD > 0L) return new Date(veDetails.expiryTimeHD);
        if (veDetails.expiryTimeSD > 0L) return new Date(veDetails.expiryTimeSD);
        return null;
    }

    public String getRentalExpiryText() {
        Date expiry = getRentalExpiry();
        Context ctx = PlayerApplication.getContext();
        if (expiry == null) return ctx.getString(R.string.na);
        String dateStr = new DateBuilder().setTime(expiry).formatEnglish("MM-dd-yyyy HH:mm");
        String ret = String.format(ctx.getString(R.string.exp_date), dateStr);
        return ret;
    }

    public String getRentalPaymentType() {
        float balance = NowIDClient.getInstance().getNowDollarBalance();
        float prePaidPrice = getPrePaidPrice();
        float postPaidPrice = getPostPaidPrice();
        return (prePaidPrice >= 0.f && balance >= prePaidPrice) ? NMAFBasicCheckout.NMAFBCPaymentType_Prepaid : (postPaidPrice >= 0.f ? NMAFBasicCheckout.NMAFBCPaymentType_Postpaid : null);
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public int getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(int seasonNum) {
        this.seasonNum = seasonNum;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesImageUrl() {
        return seriesImageUrl == null ? null : seriesImageUrl.toString();
    }

    public void setSeriesImageUrl(String seriesImageUrl) {
        this.seriesImageUrl = LString.make(seriesImageUrl, seriesImageUrl);
    }

    public String getSeriesName() {
        return seriesName == null ? null : seriesName.toString();
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = LString.make(seriesName, seriesName);
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        setStartTime(startTime == 0 ? null : new Date(startTime));
    }

    public String getStartTimeText() {
        return startTimeText;
    }

    public void setStartTimeText(String startTimeText) {
        this.startTimeText = startTimeText;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSubtitleLanguagesText() {
        return subtitleLanguagesText;
    }

    public void setSubtitleLanguagesText(String subtitleLanguagesText) {
        this.subtitleLanguagesText = subtitleLanguagesText;
    }

    public String getSynopsis() {
        return synopsis == null ? null : synopsis.toString();
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = LString.make(synopsis, synopsis);
    }

    public String getTitle() {
        return title == null ? null : title.toString();
    }

    public void setTitle(String title) {
        this.title = LString.make(title, title);
    }

    public long getType() {
        return type;
    }

    public String getVeCheckoutMovieFormat() {
        return null;
    }

    public WebTVAPIModels.GetProductDetailOutputModel.ProductDetailModel getVeDetails() {
        if (veDetails != null) return veDetails;
        if (!TextUtils.isEmpty(veDetailsJson)) {
            veDetails = GsonUtil.fromJson(veDetailsJson, WebTVAPIModels.GetProductDetailOutputModel.ProductDetailModel.class);
        }
        return veDetails;
    }

    public void setVeDetails(WebTVAPIModels.GetProductDetailOutputModel.ProductDetailModel veDetails) {
        this.veDetails = veDetails;
        veDetailsJson = veDetails == null ? null : GsonUtil.toJson(veDetails);
    }

    public int getViewingPeriod() {
        return viewingPeriod;
    }

    public void setViewingPeriod(int viewingPeriod) {
        this.viewingPeriod = viewingPeriod;
    }

    public Node getVodNode() {
        if (TextUtils.isEmpty(vodNodeId)) return null;
        Node node = new Node();
        node.setNodeId(vodNodeId);
        node.setChannelId(channelId);
        node.addTypeMask(NodeType.Cat3 | NodeType.VOD | NodeType.Node);
        node.setHasMore(true);
        return node;
    }

    public String getVodNodeId() {
        return vodNodeId;
    }

    public void setVodNodeId(String vodNodeId) {
        this.vodNodeId = vodNodeId;
    }

    public List<String> getWatchListKeys() {
        List<String> keys = new ArrayList<>(3);

        String keyPrefix = (isEPG() ? "epg/" : "vod/");

        if (isSeries()) {
            if (!TextUtils.isEmpty(getSeriesId())) {
                keys.add(keyPrefix + "series/" + getSeriesId());
            }
        }
        if (isEpisodic()) {
            if (!TextUtils.isEmpty(getSeriesId())) {
                keys.add(keyPrefix + "episodic/" + getSeriesId());
            }
        }

        if (isProgram()) {
            if (!TextUtils.isEmpty(getNodeId())) {
                keys.add(keyPrefix + "single/" + getNodeId());
            }
        }
        return keys;
    }

    public DataModels.NPXMyNowAddWatchlistItemDataModel getWatchListModel() {

        // determine type
        String itemType = getMyNowItemType();
        if (TextUtils.isEmpty(itemType)) return null;

        // determine item id
        String itemId = isSeries() ? getSeriesId() : getNodeId();
        if (TextUtils.isEmpty(itemId)) return null;

        // create object
        Uri imageUrl = TypeUtils.toUri(getImageUrl());
        String path = imageUrl == null ? null : imageUrl.getPath();
        if (path != null)
            path = path.replace("/shares/vod_images/", ""); // image url is for TV-box, which is a relative path, so we take out the host and the first 2 path components.

        return new DataModels.NPXMyNowAddWatchlistItemDataModel(itemId, itemType, getCid(), getSeriesId(), path);
    }

    public boolean hasMore() {
        return hasMore;
    }

    public boolean hasOtherTimes() {
        return hasOtherTimes;
    }

    public boolean is3D() {
        return isType(NodeType.ThreeD, true);
    }

    public boolean isAutoPlayNextEpisodeDisabled() {
        return autoPlayNextEpisodeDisabled;
    }

    public void setAutoPlayNextEpisodeDisabled(boolean autoPlayNextEpisodeDisabled) {
        this.autoPlayNextEpisodeDisabled = autoPlayNextEpisodeDisabled;
    }

    public boolean isBonusVideo() {
        return isType(NodeType.BonusVideo, true);
    }

    public boolean isCat3() {
        return isType(NodeType.Cat3);
    }

    public boolean isCat3Parent() {
        return isType(NodeType.Cat3Parent);
    }

    public boolean isCategory() {
        return isType(NodeType.Category, true);
    }

    public boolean isChannel() {
        return !isType(NodeType.Program) && isType(NodeType.Channel, true);
    }

    public boolean isCheckoutMovieFormatHD() {
        return (veDetails != null && (veDetails.cashPointPriceHD > 0.f || veDetails.priceHD > 0.f));
    }

    public boolean isDetached() {
        return isDetached;
    }

    public void setDetached(boolean detached) {
        isDetached = detached;
    }

    public boolean isDownloadInitiated() {
        return false; // TODO implement download
    }

    public boolean isDownloadable() {
        boolean enabled = false;

        if (isVOD() && isProgram() && isOnApp()) {
            if (isVE() || isBonusVideo() || isTrailer()) {
                enabled = false;
            } else if (isSubscribable()) {
                enabled = false;
            } else if (isEpisode() && getParent() != null && getParent().isSubscribable()) {
                enabled = false;
            } else {
                enabled = isType(NodeType.Downloadable);
            }
        }
        return enabled;
    }

    public boolean isDownloaded() {
        return false; // TODO implement download
    }

    public boolean isDownloading() {
        return false; // TODO implement download
    }

    public boolean isEPG() {
        return isType(NodeType.EPG, true);
    }

    public boolean isEPGProgram() {
        return isType(NodeType.EPGProgram, true);
    }

    public boolean isEPGSeries() {
        return isType(NodeType.EPGSeries, true);
    }

    public boolean isEpisode() {
        return isType(NodeType.Episode, true);
    }

    public boolean isEpisodic() {
        return isType(NodeType.Episodic, true);
    }

    public boolean isFavorite() {
        return FavoriteChannelsClient.getInstance().isFavoriteChannel(this);
    }

    public boolean isFeaturedLibrary() {
        return isType(NodeType.Premium, true) && isType(NodeType.Cat3);
    }

    public boolean isFreeToWatch() {
        return isType(NodeType.FreeToWatch, true);
    }

    public boolean isGenre() {
        return isType(NodeType.Genre, true);
    }

    public boolean isGroup() {
        return isType(NodeType.Group, true);
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public boolean isInFuture() {
        return startTime != null && startTime.after(new Date());
    }

    public boolean isInPVRList() {
        return PVRClient.getInstance().isInPVRList(this);
    }

    public boolean isInWatchList() {
        return WatchListClient.getInstance().isInWatchList(this);
    }

    public boolean isLive() {
        return isLiveAt(System.currentTimeMillis());
    }

    public boolean isLiveAt(long timestamp) {
        if (getStartTime() == null) return false;
        if (getEndTime() == null) return false;

        long start = getStartTime().getTime();
        long end = getEndTime().getTime();

        return start <= timestamp && timestamp < end;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isNPVR() {
        return isType(NodeType.NPVR, true);
    }

    public boolean isNPVRProgram() {
        return isType(NodeType.NPVR | NodeType.Program, true);
    }

    public boolean isNPVRSeries() {
        return isType(NodeType.NPVR | NodeType.Series, true);
    }

    public boolean isNode() {
        return isType(NodeType.Node, true);
    }

    public boolean isOnApp() {
        return isType(NodeType.OnApp, true);
    }

    public boolean isOnTV() {
        return isType(NodeType.OnTV, true);
    }

    public boolean isOnTVOnly() {
        return isOnTV() && !isOnApp();
    }

    public boolean isOnWeb() {
        return isType(NodeType.OnWeb, true);
    }

    public boolean isPVR() {
        return isType(NodeType.PVR, true);
    }

    public boolean isPlayEnabled() {
        boolean enabled = isPlayable();
        if (!enabled) return false;

        if (isVOD() && isOnApp() && !isVE()) {
            enabled = (!isDownloadInitiated() || isDownloaded());
        }
        return enabled;
    }

    public boolean isPlayable() {
        boolean enabled = false;
        boolean loggedIn = NowIDClient.getInstance().isLoggedIn();

        if (isEPG()) {
            if (isChannel()) {
                enabled = isOnApp() && (isSubscribed() || !loggedIn);
            } else if (isProgram()) {
                Node channel = EPGClient.getInstance().getChannel(getChannelId());
                enabled = channel != null && channel.isPlayable() && isLive();
            }
        } else if (isVOD() && isProgram() && isOnApp()) {
            if (isVE()) {
                enabled = isRented();
            } else if (isTrailer() || isBonusVideo()) {
                enabled = true;
            } else if (isSubscribable()) {
                enabled = false;
            } else if (isEpisode() && getParent() != null && getParent().isSubscribable()) {
                enabled = false;
            } else {
                enabled = true;
            }
        }
        return enabled;
    }

    public boolean isPremium() {
        return isType(NodeType.Premium, true);
    }

    public boolean isPremiumCatalog() {
        return isType(NodeType.Premium, true);
    }

    public boolean isProgram() {
        return isType(NodeType.Program, true);
    }

    public boolean isRecommended() {
        return CatalogClient.getInstance().isRecommended(this);
    }

    public boolean isRecordable() {
        return isEPG() &&
                isProgram() &&
                isSubscribed() &&
                NowIDClient.getInstance().isLoggedIn() &&
                (isLive() || isInFuture());
    }

    public boolean isRentable() {
        return isVE() && !isRented() && getRentPrice() >= 0;
    }

    public boolean isRented() {
        if (veDetails != null && (veDetails.isPaidHD || veDetails.isPaidSD)) {
            return true;
        }
        return false;
    }

    public boolean isSeries() {
        return isType(NodeType.Series, true);
    }

    public boolean isSubGenre() {
        return isType(NodeType.SubGenre, true);
    }

    public boolean isSubscribable() {
        boolean enabled = false;
        if (!isSubscribed()) {
            if (isEPG()) {
                enabled = NowIDClient.getInstance().isLoggedIn();
            } else if (isVE()) {
                enabled = false;
            } else if (isVOD()) {
                enabled = isPremium();
            }
        }
        return enabled;
    }

    public boolean isSubscribed() {
        if (isEPG() && channelId > 0) {
            return EPGClient.getInstance().isSubscribed(this);
        } else {
            return subscribed;
        }
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public boolean isTrailer() {
        return isType(NodeType.Trailer, true);
    }

    public boolean isType(long type) {
        return isType(type, false);
    }

    public boolean isType(long type, boolean strict) {
        long intersect = (this.type & type);
        return intersect == type || (!strict && intersect != 0);
    }

    public boolean isVE() {
        return isType(NodeType.VE, true);
    }

    public boolean isVOD() {
        return isType(NodeType.VOD, true);
    }

    public boolean isVODProgram() {
        return isType(NodeType.VODProgram, true);
    }

    public boolean isVODSeries() {
        return isType(NodeType.VODSeries, true);
    }

    public DataModels.NPXMyNowAddWatchlistItemDataModel makeWatchListItem() {
        String sid = getSeriesId();
        if (sid == null) sid = "";
        String pid = isSeries() ? sid : getNodeId();
        String itemType = getMyNowItemType();

        // image url is for TV-box, which is a relative path, so we take out the host and the first 2 path components.
        Uri imageUrl = TypeUtils.toUri(getImageUrl());
        String imagePath = null;
        if (imageUrl != null) {
            imagePath = imageUrl.getPath();
            if (imagePath != null) {
                imagePath = imagePath.replace("/shares/vod_images/", "");
            }
        }

        DataModels.NPXMyNowAddWatchlistItemDataModel ret = new DataModels.NPXMyNowAddWatchlistItemDataModel(pid, itemType, getCid(), sid, imagePath);
        return ret;
    }

    public DataModels.NPXMyNowRemoveWatchlistItemDataModel makeWatchListRemoveItem() {
        String itemId = isSeries() ? getSeriesId() : getNodeId();
        String itemType = getMyNowItemType();
        DataModels.NPXMyNowRemoveWatchlistItemDataModel ret = new DataModels.NPXMyNowRemoveWatchlistItemDataModel(itemId, itemType);
        return ret;
    }

    public Promise promiseSave() {
        // TODO
        return PromiseUtils.nil();
    }

    public void removeTypeMask(long mask) {
        type = type & ~mask;
    }

    public void setActorsText(String eng, String chi) {
        this.actorsText = LString.make(eng, chi);
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public void setDirectorsText(String eng, String chi) {
        this.directorsText = LString.make(eng, chi);
    }

    public void setEndTime(long endTime) {
        setEndTime(endTime == 0 ? null : new Date(endTime));
    }

    public void setHasOtherTimes(boolean hasOtherTimes) {
        this.hasOtherTimes = hasOtherTimes;
    }

    public void setImageUrl(String engImageUrl, String chiImageUrl) {
        this.imageUrl = LString.make(engImageUrl, chiImageUrl);
    }

    public void setLanguagesText(String eng, String chi) {
        this.languagesText = LString.make(eng, chi);
    }

    public void setLibraryImageUrl(String engUrl, String chiUrl) {
        this.libraryImageUrl = LString.make(engUrl, chiUrl);
    }

    public void setLibraryName(String eng, String chi) {
        this.libraryName = LString.make(eng, chi);
    }

    public void setSeriesImageUrl(String eng, String chi) {
        this.seriesImageUrl = LString.make(eng, chi);
    }

    public void setSeriesName(String eng, String chi) {
        this.seriesName = LString.make(eng, chi);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setSynopsis(String eng, String chi) {
        this.synopsis = LString.make(eng, chi);
    }

    public void setTitle(String engTitle, String chiTitle) {
        this.title = LString.make(engTitle, chiTitle);
    }

    public void setTypeMask(long mask, boolean onOrOff) {
        if (onOrOff) addTypeMask(mask);
        else removeTypeMask(mask);
    }

    public void setTypeMask(long mask, String booleanString) {
        setTypeMask(mask, TypeUtils.toBoolean(booleanString, false));
    }

    public Promise<Void, Throwable, Float> trackPurchaseEvent() {
        return CatalogClient.getInstance().trackAction(this, NPXRecommendationEngine.NPXTA_ACTION_GEN_PURCHASE);
    }

    public Promise<Void, Throwable, Float> trackRecordEvent() {
        return CatalogClient.getInstance().trackAction(this, NPXRecommendationEngine.NPXTA_ACTION_GEN_RECORD);
    }

    public Promise<Void, Throwable, Float> trackWatchEvent() {
        if (isVOD()) {
            return CatalogClient.getInstance().trackAction(this, NPXRecommendationEngine.NPXTA_ACTION_APP_WEB_VOD_WATCH);
        } else if (isEPG()) {
            CatalogClient.getInstance().trackAction(this, NPXRecommendationEngine.NPXTA_ACTION_APP_WEB_EPG_WATCH);
        }
        return PromiseUtils.resolve(null);
    }

}
