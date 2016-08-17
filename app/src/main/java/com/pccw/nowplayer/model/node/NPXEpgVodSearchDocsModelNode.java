package com.pccw.nowplayer.model.node;

import android.text.TextUtils;

import com.pccw.nowplayer.model.NodeType;
import com.pccw.nowplayer.utils.StringUtils;
import com.pccw.nowplayer.utils.TypeUtils;
import com.pccw.nowtv.nmaf.npx.catalog.DataModels;

import java.util.Date;

/**
 * Created by Swifty on 5/20/2016.
 */
@NodeWrapper(underlyingClass = DataModels.NPXEpgVodSearchDocsModel.class)
public class NPXEpgVodSearchDocsModelNode extends Node {

    @Override
    public void setData(Object object, Node parent) {
        super.setData(object, parent);
        DataModels.NPXEpgVodSearchDocsModel data = (DataModels.NPXEpgVodSearchDocsModel) object;

        // Types
        String type = data.product_cp_type_t;
        if (type == null) type = "";
        if (type.startsWith("VOD") || type.startsWith("SVOD") || type.equals("PPV") || type.equals("PPS") || type.equals("PPE")) {
            addTypeMask(NodeType.VODProgram);
        } else {
            // what type is it?
        }
        if (type.equals("SVOD_BPL")) addTypeMask(NodeType.Sports);
        setPaymentType(data.product_payment_type_t);
        setTypeMask(NodeType.ThreeD, TypeUtils.toBoolean(data.product_is_3d_content_t, false));
        setTypeMask(NodeType.OnTV, TypeUtils.toBoolean(data.asset_tv_ready_b, false));
        // setTypeMask(NodeType.OnApp, !TypeUtils.toBoolean(data.product_no_app_checkout_t, false));
        setTypeMask(NodeType.AdultContent, TypeUtils.toBoolean(data.product_is_adult_t, false));

        // ID
        setNodeId(data.product_id_t);

        // Title
        setTitle(TextUtils.isEmpty(data.product_en_us_name_t) ? data.series_en_us_name_t : data.product_en_us_name_t,
                TextUtils.isEmpty(data.product_zh_tw_name_t) ? data.series_zh_tw_name_t : data.product_zh_tw_name_t);

        // Image
        if (!TextUtils.isEmpty(data.product_en_us_hd_image_file1_t) || !TextUtils.isEmpty(data.product_zh_tw_hd_image_file1_t)) {
            setImageUrl(data.product_en_us_hd_image_file1_t, data.product_zh_tw_hd_image_file1_t);
        } else if (!TextUtils.isEmpty(data.series_en_us_hd_image_1_t) || !TextUtils.isEmpty(data.series_zh_tw_hd_image_1_t)) {
            setImageUrl(data.series_en_us_hd_image_1_t, data.series_zh_tw_hd_image_1_t);
        }

        // synopsis
        if (!TextUtils.isEmpty(data.product_en_us_synopsis_t) || !TextUtils.isEmpty(data.product_zh_tw_synopsis_t)) {
            setSynopsis(data.product_en_us_synopsis_t, data.product_zh_tw_synopsis_t);
        } else if (!TextUtils.isEmpty(data.series_en_us_synopsis_t) || !TextUtils.isEmpty(data.series_zh_tw_synopsis_t)) {
            setSynopsis(data.series_en_us_synopsis_t, data.series_zh_tw_synopsis_t);
        }

        // actors / director
        if (!TextUtils.isEmpty(data.product_en_us_actor_t) || !TextUtils.isEmpty(data.product_zh_tw_actor_t)) {
            setActorsText(data.product_en_us_actor_t, data.product_zh_tw_actor_t);
        } else if (!TextUtils.isEmpty(data.series_en_us_actor_t) || !TextUtils.isEmpty(data.series_zh_tw_actor_t)) {
            setActorsText(data.series_en_us_actor_t, data.series_zh_tw_actor_t);
        }
        setDirectorsText(data.product_en_us_director_t, data.product_zh_tw_director_t);

        // Library
        setLibraryId(data.product_library_id_t);
        setLibraryName(data.library_en_us_library_display_name_t, data.library_zh_tw_library_display_name_t);
        setLibraryImageUrl(data.library_en_us_library_img1_t, data.library_zh_tw_library_img1_t);

        // Series
        setSeriesId(data.series_series_id_t);
        setSeriesName(data.series_en_us_name_t, data.series_zh_tw_name_t);
        setSeriesImageUrl(data.series_en_us_hd_image_1_t);

        // Others
        setDuration(data.product_duration_td);
        if (data.schedule_end_tl != 0) {
            setEndTime(new Date(data.schedule_end_tl));
            // setEndTimeText(); TODO format the date
        }
        setLanguagesText(data.product_en_us_language_t, data.product_zh_tw_language_t);
        setAvailablePlatformsText(StringUtils.join(data.extra_platform, ", "));
    }
}
