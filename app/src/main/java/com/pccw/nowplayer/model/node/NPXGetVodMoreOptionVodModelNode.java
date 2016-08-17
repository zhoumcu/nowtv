package com.pccw.nowplayer.model.node;

import android.content.res.Resources;
import android.text.TextUtils;

import com.pccw.nowplayer.PlayerApplication;
import com.pccw.nowplayer.R;
import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.DateBuilder;
import com.pccw.nowplayer.utils.Is;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowtv.nmaf.checkout.NMAFBasicCheckout;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

import java.util.Date;
import java.util.List;

/**
 * Created by Kriz on 2016-6-16.
 */
@NodeWrapper(underlyingClass = DataModels.NPXGetVodMoreOptionVodModel.class)
public class NPXGetVodMoreOptionVodModelNode extends Node {

    private boolean hasHD;
    private boolean hasSD;

    public boolean canBuyHD(boolean prepaid) {
        return hasHD && getVeDetails() != null && (prepaid ? getVeDetails().cashPointPriceHD : getVeDetails().priceHD) >= 0.f;
    }

    public boolean canBuySD(boolean prepaid) {
        return hasSD && getVeDetails() != null && (prepaid ? getVeDetails().cashPointPriceSD : getVeDetails().priceSD) >= 0.f;
    }

    public float getPostPaidPrice() {
        if (isSeries()) {
            Node program = getFirstEpisode();
            if (program == null) return -1;
            return program.getPostPaidPrice();
        }

        if (canBuyHD(false)) return getVeDetails().priceHD;
        if (canBuySD(false)) return getVeDetails().priceSD;
        if (isFreeToWatch()) return 0.f;
        return -1;
    }

    @Override
    public float getPrePaidPrice() {
        if (isSeries()) {
            Node program = getFirstEpisode();
            if (program == null) return -1;
            return program.getPrePaidPrice();
        }

        if (canBuyHD(true)) return getVeDetails().cashPointPriceHD;
        if (canBuySD(true)) return getVeDetails().cashPointPriceSD;
        if (isFreeToWatch()) return 0.f;
        return -1;
    }

    @Override
    public String getVeCheckoutMovieFormat() {
        if (isSeries()) {
            Node ep = getFirstEpisode();
            if (ep == null) return null;
            return ep.getVeCheckoutMovieFormat();
        }

        String payment = getRentalPaymentType();
        if (payment == null) return null;

        boolean prepaid = NMAFBasicCheckout.NMAFBCPaymentType_Prepaid.equals(payment);
        if (canBuyHD(prepaid)) return NMAFBasicCheckout.CHECKOUT_VE_MOVIE_FORMAT_HD;
        if (canBuySD(prepaid)) return NMAFBasicCheckout.CHECKOUT_VE_MOVIE_FORMAT_SD;
        if (isFreeToWatch()) return NMAFBasicCheckout.CHECKOUT_VE_MOVIE_FORMAT_FREE;
        return null;
    }

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXGetVodMoreOptionVodModel data = (DataModels.NPXGetVodMoreOptionVodModel) object;
        Resources res = PlayerApplication.getContext().getResources();

        addTypeMask(NodeType.VODProgram);

        if (parent != null && parent.isPremium()) addTypeMask(NodeType.Premium);
        setTypeMask(NodeType.VE, TypeUtils.toBoolean(data.isPayPerView, false) || "PPV".equals(data.paymentType) || "PPS".equals(data.paymentType) || "PPE".equals(data.paymentType));
        setTypeMask(NodeType.ThreeD, TypeUtils.toBoolean(data.is3dContent, false));
        setTypeMask(NodeType.AdultContent, TypeUtils.toBoolean(data.isAdultContent, false));
        setTypeMask(NodeType.OnTV, TypeUtils.toBoolean(data.tvAssetStatus, false) || TypeUtils.toBoolean(data.isTVPlatform, false));
        setTypeMask(NodeType.OnApp, !TypeUtils.toBoolean(data.noAppCheckout, false) && TypeUtils.toBoolean(data.productHLSOnAirHLS, false));
        setTypeMask(NodeType.OnWeb, TypeUtils.toBoolean(data.webCheckout, false));
        setTypeMask(NodeType.FreeToWatch, TypeUtils.toBoolean(data.freeToWatch, false));
        setTypeMask(NodeType.Downloadable, TypeUtils.toBoolean(data.downloadable, false));

        //setSubscribed(data.isSubscribed);

        setActorsText(data.actors);
        // audioLanguageShort
        // brandName
        // categoryIdPath
        setClassificationText(data.classification);
        // classificationDisplayOrder
        // classificationImage
        setCid(data.cid);
        // cpId
        setDirectorsText(data.director);
        // disclaimer
        // displayOrder
        // displayWhenAssetReady
        setDuration(data.duration);
        setDurationText(String.valueOf(data.duration));
        setNodeId(data.episodeId);
        setEpisodeId(data.episodeId);
        if (Is.notEmpty(data.episodeTitle)) {
            setTitle(data.episodeTitle);
        } else if (Is.notEmpty(data.episodeName)) {
            setTitle(data.episodeName);
        } else {
            setTitle(String.format(res.getString(R.string.episode_n), data.episodeNum));
        }
        setEpisodeName(data.episodeName);
        setEpisodeNum(data.episodeNum);
        // episodeTitle
        // hasChapter
        // hasMoreNonAdult
        // hasMoreOptions
        // hdImg1Path
        // hdImg2Path
        // id
        // img1Path
        // img2Path
        // isInSportsCat
        // isIncludeAdultCategory
        // isLock
        setTypeMask(NodeType.NPVR, TypeUtils.toBoolean(data.isNPVR, false));
        // isRestricted
        // landscapeImage
        // language
        setLanguagesText(data.languages);
        // libId
        setLibraryImageUrl(data.logoImg1Path);
        // logoImg2Path

        setOffAirDate(data.offAirDate);
        Date offair = DateBuilder.create().setEnglishFormat("yyyy-MM-dd HH:mm:ss.S").parseEnglish(data.offAirDate);
        if (offair != null) {
            setEndDateText(DateBuilder.create().setTime(offair).formatEnglish("dd-MM-yyyy"));
        }

        setImageUrl(data.portraitImage);
        // productEncodingInHD
        // productEncodingInSD
        // productEncodingInSuperHD
        // productEncodingInUHD
        // productOnAirMPEG4HD
        // productOnAirMPEG4HD18
        // productOnAirMPEG4SD
        // productOnAirUHD
        setSubtitleLanguagesText(data.productSubtitle);
        // productType
        // relatedProduct
        // scheduleId
        setSeasonName(data.seasonName);
        setSeriesId(data.sid);
        // shortSynopsis
        // sponsor
        // subscriptionCheck
        // subtitleLanguageShort
        // support_5_1_audio
        setSynopsis(data.synopsis);
        // type
        // version
        setViewingPeriod(data.viewingPeriod);
        // withImageTitle

        List<Node> bonusVideos = addSubNodes(data.trailers);
        for (int i = 0; i < bonusVideos.size(); i++) {
            Node bonusVideo = bonusVideos.get(i);
            bonusVideo.addTypeMask(i == 0 ? NodeType.Trailer : NodeType.BonusVideo);
        }

        hasHD = TypeUtils.toBoolean(data.productOnAirMPEG4HD, false);
        hasSD = TypeUtils.toBoolean(data.productOnAirMPEG4SD, false);

        // get sub-genre-id for recommendations
        // sub-genre id is inherited, or using self's categoryIdPath
        if (parent != null && !TextUtils.isEmpty(parent.getRecommendationSubGenreId())) {
            setRecommendationSubGenreId(parent.getRecommendationSubGenreId());
        } else if (data.categoryIdPath != null) {
            String[] catComp = data.categoryIdPath.split("\\|\\|\\|");
            int idx = catComp.length - 2;
            if (idx >= 0) {
                setRecommendationSubGenreId(catComp[idx]);
            }
        }
    }
}