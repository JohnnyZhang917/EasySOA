/**
 * EasySOA Proxy
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
package org.easysoa.proxy.core.api.configuration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jguillemotte
 *
 */
@XmlRootElement(name="proxyConfiguration")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProxyConfiguration extends EasySOAGeneratedAppConfiguration implements Serializable {

    public static final String PROXY_PORT_PARAM_NAME = "proxyPort";
    public static final String PROXY_PATH_PARAM_NAME = "proxyPath";
    public static final String PROXY_HOST_PARAM_NAME = "proxyHost";

    /**
     * Constructor
     */
    public ProxyConfiguration(){
        super();
    }

}
