package com.pranshulgg.weather_master_app.core.network.sources.weather.fmi.xml

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(
    name = "FeatureCollection",
    namespace = "http://www.opengis.net/wfs/2.0"
)
@XmlAccessorType(XmlAccessType.FIELD)
class FmiWeatherForecastXml {

    @field:XmlElement(
        name = "member",
        namespace = "http://www.opengis.net/wfs/2.0"
    )
    var members: List<Member> = emptyList()
}

@XmlAccessorType(XmlAccessType.FIELD)
data class Member(

    @field:XmlElement(
        name = "BsWfsElement",
        namespace = "http://xml.fmi.fi/schema/wfs/2.0"
    )
    var element: BsWfsElement? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class BsWfsElement(

    @field:XmlElement(name = "Time")
    var time: String? = null,

    @field:XmlElement(name = "ParameterName")
    var parameterName: String? = null,

    @field:XmlElement(name = "ParameterValue")
    var parameterValue: String? = null
)