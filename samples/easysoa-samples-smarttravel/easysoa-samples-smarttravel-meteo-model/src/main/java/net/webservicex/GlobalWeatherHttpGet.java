/**
 * EasySOA Samples - Smart Travel
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package net.webservicex;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.1
 * Mon May 18 19:03:28 CEST 2009
 * Generated source version: 2.2.1
 * 
 */
 
@WebService(targetNamespace = "http://www.webserviceX.NET", name = "GlobalWeatherHttpGet")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface GlobalWeatherHttpGet {

    @WebResult(name = "string", targetNamespace = "http://www.webserviceX.NET", partName = "Body")
    @WebMethod(operationName = "GetCitiesByCountry")
    public java.lang.String getCitiesByCountry(
        @WebParam(partName = "CountryName", name = "CountryName", targetNamespace = "")
        java.lang.String countryName
    );

    @WebResult(name = "string", targetNamespace = "http://www.webserviceX.NET", partName = "Body")
    @WebMethod(operationName = "GetWeather")
    public java.lang.String getWeather(
        @WebParam(partName = "CityName", name = "CityName", targetNamespace = "")
        java.lang.String cityName,
        @WebParam(partName = "CountryName", name = "CountryName", targetNamespace = "")
        java.lang.String countryName
    );
}
