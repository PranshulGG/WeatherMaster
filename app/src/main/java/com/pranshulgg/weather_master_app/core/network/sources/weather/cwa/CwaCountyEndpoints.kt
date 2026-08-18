package com.pranshulgg.weather_master_app.core.network.sources.weather.cwa

object CwaCountyEndpoints {

    data class CountyEndpoint(
        val countyName: String, // Chinese name as returned by CWA's LocationName field
        val shortRangeId: String,
        val weeklyId: String
    )

    // The full, fixed set of Taiwan's 22 counties/cities and their per-county dataset ids -
    // sourced directly from CWA's own Swagger listing (opendata.cwa.gov.tw/dist/opendata-swagger.html),
    // not fetched live, since this list is effectively static (Taiwan's counties don't get renamed).
    val ALL = listOf(
        CountyEndpoint("宜蘭縣", "F-D0047-001", "F-D0047-003"), // Yilan County
        CountyEndpoint("桃園市", "F-D0047-005", "F-D0047-007"), // Taoyuan City
        CountyEndpoint("新竹縣", "F-D0047-009", "F-D0047-011"), // Hsinchu County
        CountyEndpoint("苗栗縣", "F-D0047-013", "F-D0047-015"), // Miaoli County
        CountyEndpoint("彰化縣", "F-D0047-017", "F-D0047-019"), // Changhua County
        CountyEndpoint("南投縣", "F-D0047-021", "F-D0047-023"), // Nantou County
        CountyEndpoint("雲林縣", "F-D0047-025", "F-D0047-027"), // Yunlin County
        CountyEndpoint("嘉義縣", "F-D0047-029", "F-D0047-031"), // Chiayi County
        CountyEndpoint("屏東縣", "F-D0047-033", "F-D0047-035"), // Pingtung County
        CountyEndpoint("臺東縣", "F-D0047-037", "F-D0047-039"), // Taitung County
        CountyEndpoint("花蓮縣", "F-D0047-041", "F-D0047-043"), // Hualien County
        CountyEndpoint("澎湖縣", "F-D0047-045", "F-D0047-047"), // Penghu County
        CountyEndpoint("基隆市", "F-D0047-049", "F-D0047-051"), // Keelung City
        CountyEndpoint("新竹市", "F-D0047-053", "F-D0047-055"), // Hsinchu City
        CountyEndpoint("嘉義市", "F-D0047-057", "F-D0047-059"), // Chiayi City
        CountyEndpoint("臺北市", "F-D0047-061", "F-D0047-063"), // Taipei City
        CountyEndpoint("高雄市", "F-D0047-065", "F-D0047-067"), // Kaohsiung City
        CountyEndpoint("新北市", "F-D0047-069", "F-D0047-071"), // New Taipei City
        CountyEndpoint("臺中市", "F-D0047-073", "F-D0047-075"), // Taichung City
        CountyEndpoint("臺南市", "F-D0047-077", "F-D0047-079"), // Tainan City
        CountyEndpoint("連江縣", "F-D0047-081", "F-D0047-083"), // Lienchiang County (Matsu)
        CountyEndpoint("金門縣", "F-D0047-085", "F-D0047-087"), // Kinmen County
    )

    // Nationwide dataset returning all 22 counties' centroid lat/lon in one call - used only to
    // resolve which county a location falls in (step 1 of location resolution), not for weather data.
    const val NATIONWIDE_SHORT_RANGE = "F-D0047-089"

    fun byShortRangeId(shortRangeId: String): CountyEndpoint? =
        ALL.firstOrNull { it.shortRangeId == shortRangeId }
}
