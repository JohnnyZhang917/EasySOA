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
 * Contact : easysoa-dev@groups.google.com
 */


/*
 * 
 */

package net.webservicex;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.3.3
 * 2011-07-08T11:01:25.155+02:00
 * Generated source version: 2.3.3
 * 
 */


@WebServiceClient(name = "GlobalWeather", 
                  //wsdlLocation = "http://www.webservicex.net/globalweather.asmx?wsdl",
                  targetNamespace = "http://www.webserviceX.NET") 
public class GlobalWeather extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://www.webserviceX.NET", "GlobalWeather");
    public final static QName GlobalWeatherHttpPost = new QName("http://www.webserviceX.NET", "GlobalWeatherHttpPost");
    public final static QName GlobalWeatherSoap12 = new QName("http://www.webserviceX.NET", "GlobalWeatherSoap12");
    public final static QName GlobalWeatherSoap = new QName("http://www.webserviceX.NET", "GlobalWeatherSoap");
    public final static QName GlobalWeatherHttpGet = new QName("http://www.webserviceX.NET", "GlobalWeatherHttpGet");
    static {
        URL url = null;
        try {
            url = new URL("http://www.webservicex.net/globalweather.asmx?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://www.webservicex.net/globalweather.asmx?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public GlobalWeather(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public GlobalWeather(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public GlobalWeather() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns GlobalWeatherHttpPost
     */
    @WebEndpoint(name = "GlobalWeatherHttpPost")
    public GlobalWeatherHttpPost getGlobalWeatherHttpPost() {
        return super.getPort(GlobalWeatherHttpPost, GlobalWeatherHttpPost.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns GlobalWeatherHttpPost
     */
    @WebEndpoint(name = "GlobalWeatherHttpPost")
    public GlobalWeatherHttpPost getGlobalWeatherHttpPost(WebServiceFeature... features) {
        return super.getPort(GlobalWeatherHttpPost, GlobalWeatherHttpPost.class, features);
    }
    /**
     * 
     * @return
     *     returns GlobalWeatherSoap
     */
    @WebEndpoint(name = "GlobalWeatherSoap12")
    public GlobalWeatherSoap getGlobalWeatherSoap12() {
        return super.getPort(GlobalWeatherSoap12, GlobalWeatherSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns GlobalWeatherSoap
     */
    @WebEndpoint(name = "GlobalWeatherSoap12")
    public GlobalWeatherSoap getGlobalWeatherSoap12(WebServiceFeature... features) {
        return super.getPort(GlobalWeatherSoap12, GlobalWeatherSoap.class, features);
    }
    /**
     * 
     * @return
     *     returns GlobalWeatherSoap
     */
    @WebEndpoint(name = "GlobalWeatherSoap")
    public GlobalWeatherSoap getGlobalWeatherSoap() {
        return super.getPort(GlobalWeatherSoap, GlobalWeatherSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns GlobalWeatherSoap
     */
    @WebEndpoint(name = "GlobalWeatherSoap")
    public GlobalWeatherSoap getGlobalWeatherSoap(WebServiceFeature... features) {
        return super.getPort(GlobalWeatherSoap, GlobalWeatherSoap.class, features);
    }
    /**
     * 
     * @return
     *     returns GlobalWeatherHttpGet
     */
    @WebEndpoint(name = "GlobalWeatherHttpGet")
    public GlobalWeatherHttpGet getGlobalWeatherHttpGet() {
        return super.getPort(GlobalWeatherHttpGet, GlobalWeatherHttpGet.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns GlobalWeatherHttpGet
     */
    @WebEndpoint(name = "GlobalWeatherHttpGet")
    public GlobalWeatherHttpGet getGlobalWeatherHttpGet(WebServiceFeature... features) {
        return super.getPort(GlobalWeatherHttpGet, GlobalWeatherHttpGet.class, features);
    }

}
