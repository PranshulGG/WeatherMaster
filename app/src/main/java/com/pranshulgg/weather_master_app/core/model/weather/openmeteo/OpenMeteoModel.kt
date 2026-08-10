package com.pranshulgg.weather_master_app.core.model.weather.openmeteo

import com.pranshulgg.weather_master_app.R

enum class OpenMeteoModelType {
    BEST_MATCH,
    NCEP,
    JMA,
    KMA,

    DWD,

    GEM,

    METEO_FRANCE,
    ITALIA_METEO,

    MET_NORWAY,
    KNMI,
    DMI,

    UK_MET_OFFICE,
    METEOSWISS,
    GEOSPHERE,
    CHMI,

    UNDEFINED
}

enum class OpenMeteoModel(
    val displayName: String,
    val stringValue: Int? = null,
    val modelId: String,
    val modelType: OpenMeteoModelType = OpenMeteoModelType.UNDEFINED
) {
    BEST_MATCH(
        "Best Match",
        R.string.open_meteo_model_best_match,
        "best_match",
        OpenMeteoModelType.BEST_MATCH
    ),

    ECMWF_IFS_HRES_9KM(
        "ECMWF IFS HRES 9km",
        modelId = "ecmwf_ifs"
    ),
    ECMWF_IFS_025(
        "ECMWF IFS 0.25°",
        modelId = "ecmwf_ifs025"
    ),
    ECMWF_AIFS_025_SINGLE(
        "ECMWF AIFS 0.25° Single",
        modelId = "ecmwf_aifs025_single"
    ),
    CMA_GRAPES_GLOBAL(
        "CMA GRAPES Global",
        modelId = "cma_grapes_global"
    ),
    BOM_ACCESS_GLOBAL(
        "BOM ACCESS Global",
        modelId = "bom_access_global"
    ),

    NCEP_GFS_SEAMLESS(
        "NCEP GFS Seamless",
        R.string.open_meteo_model_seamless,
        "ncep_gfs_seamless",
        OpenMeteoModelType.NCEP
    ),
    NCEP_GFS_GLOBAL_011_025(
        "NCEP GFS Global 0.11°/0.25°",
        modelId = "ncep_gfs_global",
        modelType = OpenMeteoModelType.NCEP
    ),
    NCEP_HRRR_US_CONUS(
        "NCEP HRRR U.S. Conus",
        modelId = "ncep_hrrr_conus",
        modelType = OpenMeteoModelType.NCEP
    ),
    NCEP_NBM_US_CONUS(
        "NCEP NBM U.S. Conus",
        modelId = "ncep_nbm_conus",
        modelType = OpenMeteoModelType.NCEP
    ),
    NCEP_NAM_US_CONUS(
        "NCEP NAM U.S. Conus",
        modelId = "ncep_nam_conus",
        modelType = OpenMeteoModelType.NCEP
    ),
    NCEP_AIGFS_025(
        "NCEP AIGFS 0.25°",
        modelId = "ncep_aigfs025",
        modelType = OpenMeteoModelType.NCEP
    ),
    NCEP_HGEFS_025_ENSEMBLE_MEAN(
        "NCEP HGEFS 0.25° Ensemble Mean",
        modelId = "ncep_hgefs025_ensemble_mean",
        modelType = OpenMeteoModelType.NCEP
    ),

    JMA_SEAMLESS(
        "JMA Seamless",
        R.string.open_meteo_model_seamless,
        "jma_seamless",
        modelType = OpenMeteoModelType.JMA
    ),
    JMA_MSM(
        "JMA MSM",
        modelId = "jma_msm",
        modelType = OpenMeteoModelType.JMA
    ),
    JMA_GSM(
        "JMA GSM",
        modelId = "jma_gsm",
        modelType = OpenMeteoModelType.JMA
    ),

    KMA_SEAMLESS(
        "KMA Seamless",
        R.string.open_meteo_model_seamless,
        "kma_seamless",
        modelType = OpenMeteoModelType.KMA
    ),
    KMA_LDPS(
        "KMA LDPS",
        modelId = "kma_ldps",
        modelType = OpenMeteoModelType.KMA
    ),
    KMA_GDPS(
        "KMA GDPS",
        modelId = "kma_gdps",
        modelType = OpenMeteoModelType.KMA
    ),

    DWD_ICON_SEAMLESS(
        "DWD ICON Seamless",
        R.string.open_meteo_model_seamless,
        "dwd_icon_seamless",
        modelType = OpenMeteoModelType.DWD
    ),
    DWD_ICON_GLOBAL(
        "DWD ICON Global",
        modelId = "dwd_icon_global",
        modelType = OpenMeteoModelType.DWD
    ),
    DWD_ICON_EU(
        "DWD ICON EU",
        modelId = "dwd_icon_eu",
        modelType = OpenMeteoModelType.DWD
    ),
    DWD_ICON_D2(
        "DWD ICON D2",
        modelId = "dwd_icon_d2",
        modelType = OpenMeteoModelType.DWD
    ),

    GEM_SEAMLESS(
        "GEM Seamless",
        R.string.open_meteo_model_seamless,
        "cmc_gem_seamless",
        modelType = OpenMeteoModelType.GEM
    ),
    GEM_GLOBAL(
        "GEM Global",
        modelId = "cmc_gem_gdps",
        modelType = OpenMeteoModelType.GEM
    ),
    GEM_REGIONAL(
        "GEM Regional",
        modelId = "cmc_gem_rdps",
        modelType = OpenMeteoModelType.GEM
    ),
    GEM_HRDPS_CONTINENTAL(
        "GEM HRDPS Continental",
        modelId = "cmc_gem_hrdps",
        modelType = OpenMeteoModelType.GEM
    ),
    GEM_HRDPS_WEST(
        "GEM HRDPS West",
        modelId = "cmc_gem_hrdps_west",
        modelType = OpenMeteoModelType.GEM
    ),

    METEO_FRANCE_SEAMLESS(
        "Météo-France Seamless",
        R.string.open_meteo_model_seamless,
        "meteofrance_seamless",
        modelType = OpenMeteoModelType.METEO_FRANCE
    ),
    METEO_FRANCE_ARPEGE_WORLD(
        "Météo-France ARPEGE World",
        modelId = "meteofrance_arpege_world",
        modelType = OpenMeteoModelType.METEO_FRANCE
    ),
    METEO_FRANCE_ARPEGE_EUROPE(
        "Météo-France ARPEGE Europe",
        modelId = "meteofrance_arpege_europe",
        modelType = OpenMeteoModelType.METEO_FRANCE
    ),
    METEO_FRANCE_AROME_FRANCE(
        "Météo-France AROME France",
        modelId = "meteofrance_arome_france",
        modelType = OpenMeteoModelType.METEO_FRANCE
    ),
    METEO_FRANCE_AROME_FRANCE_HD(
        "Météo-France AROME France HD",
        modelId = "meteofrance_arome_france_hd",
        modelType = OpenMeteoModelType.METEO_FRANCE
    ),

    ITALIAMETEO_ARPAE_ICON_2I(
        "ItaliaMeteo ARPAE ICON 2I",
        modelId = "italia_meteo_arpae_icon_2i",
        modelType = OpenMeteoModelType.ITALIA_METEO
    ),

    MET_NORWAY_NORDIC_SEAMLESS_WITH_ECMWF(
        "MET Norway Nordic (with ECMWF) Seamless",
        R.string.open_meteo_model_seamless,
        "metno_seamless",
        modelType = OpenMeteoModelType.MET_NORWAY
    ),
    MET_NORWAY_NORDIC(
        "MET Norway Nordic",
        modelId = "metno_nordic",
        modelType = OpenMeteoModelType.MET_NORWAY
    ),

    KNMI_SEAMLESS_WITH_ECMWF(
        "KNMI (with ECMWF) Seamless",
        R.string.open_meteo_model_seamless,
        "knmi_seamless",
        modelType = OpenMeteoModelType.KNMI
    ),
    KNMI_HARMONIE_AROME_EUROPE(
        "KNMI Harmonie AROME Europe",
        modelId = "knmi_harmonie_arome_europe",
        modelType = OpenMeteoModelType.KNMI
    ),
    KNMI_HARMONIE_AROME_NETHERLANDS(
        "KNMI Harmonie AROME Netherlands",
        modelId = "knmi_harmonie_arome_netherlands",
        modelType = OpenMeteoModelType.KNMI
    ),

    DMI_SEAMLESS_WITH_ECMWF(
        "DMI (with ECMWF) Seamless",
        R.string.open_meteo_model_seamless,
        "dmi_seamless",
        modelType = OpenMeteoModelType.DMI
    ),
    DMI_HARMONIE_AROME_EUROPE(
        "DMI Harmonie AROME Europe",
        modelId = "dmi_harmonie_arome_europe",
        modelType = OpenMeteoModelType.DMI
    ),

    UK_MET_OFFICE_SEAMLESS(
        "UK Met Office Seamless",
        R.string.open_meteo_model_seamless,
        "ukmo_seamless",
        modelType = OpenMeteoModelType.UK_MET_OFFICE
    ),
    UK_MET_OFFICE_GLOBAL_10KM(
        "UK Met Office Global 10km",
        modelId = "ukmo_global_deterministic_10km",
        modelType = OpenMeteoModelType.UK_MET_OFFICE
    ),
    UK_MET_OFFICE_UK_2KM(
        "UK Met Office UK 2km",
        modelId = "ukmo_uk_deterministic_2km",
        modelType = OpenMeteoModelType.UK_MET_OFFICE
    ),

    METEOSWISS_ICON_SEAMLESS(
        "MeteoSwiss ICON Seamless",
        R.string.open_meteo_model_seamless,
        "meteoswiss_icon_seamless",
        modelType = OpenMeteoModelType.METEOSWISS
    ),
    METEOSWISS_ICON_CH1(
        "MeteoSwiss ICON CH1",
        modelId = "meteoswiss_icon_ch1",
        modelType = OpenMeteoModelType.METEOSWISS
    ),
    METEOSWISS_ICON_CH2(
        "MeteoSwiss ICON CH2",
        modelId = "meteoswiss_icon_ch2",
        modelType = OpenMeteoModelType.METEOSWISS
    ),

    GEOSPHERE_SEAMLESS_WITH_ECMWF(
        "GeoSphere (with ECMWF) Seamless",
        R.string.open_meteo_model_seamless,
        "geosphere_seamless",
        modelType = OpenMeteoModelType.GEOSPHERE
    ),
    GEOSPHERE_AROME_AUSTRIA(
        "GeoSphere AROME Austria",
        modelId = "geosphere_arome_austria",
        modelType = OpenMeteoModelType.GEOSPHERE
    ),

    CHMI_ALADIN_SEAMLESS(
        "CHMI Aladin Seamless",
        R.string.open_meteo_model_seamless,
        "chmi_aladin_seamless",
        modelType = OpenMeteoModelType.CHMI
    ),
    CHMI_ALADIN_CENTRAL_EUROPE_2KM(
        "CHMI Aladin Central Europe 2km",
        modelId = "chmi_aladin_central_europe_2km",
        modelType = OpenMeteoModelType.CHMI
    ),
    CHMI_ALADIN_CZ_1KM(
        "CHMI Aladin CZ 1km",
        modelId = "chmi_aladin_cz_1km",
        modelType = OpenMeteoModelType.CHMI
    ),
}