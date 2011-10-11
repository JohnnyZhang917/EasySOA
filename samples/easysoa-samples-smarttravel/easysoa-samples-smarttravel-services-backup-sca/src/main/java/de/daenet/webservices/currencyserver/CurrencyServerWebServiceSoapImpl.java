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


/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package de.daenet.webservices.currencyserver;

import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 2.3.3
 * 2011-07-20T14:27:53.352+02:00
 * Generated source version: 2.3.3
 * 
 */

@javax.jws.WebService(
                      serviceName = "CurrencyServerWebService",
                      portName = "CurrencyServerWebServiceSoap12",
                      targetNamespace = "http://www.daenet.de/webservices/CurrencyServer",
                      wsdlLocation = "http://www.currencyserver.de/webservice/currencyserverwebservice.asmx?wsdl",
                      endpointInterface = "de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap")
                      
public class CurrencyServerWebServiceSoapImpl implements CurrencyServerWebServiceSoap {

    private static final Logger LOG = Logger.getLogger(CurrencyServerWebServiceSoapImpl.class.getName());

    /* (non-Javadoc)
     * @see de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap#getDollarValue(java.lang.String  provider ,)java.lang.String  currency )*
     */
    public double getDollarValue(java.lang.String provider,java.lang.String currency) { 
        LOG.info("Executing operation getDollarValue");
        System.out.println(provider);
        System.out.println(currency);
        try {
            double _return = 0.53915437336021;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap#getProviderTimestamp(java.lang.String  providerId ,)java.lang.String  provider )*
     */
    public java.lang.String getProviderTimestamp(java.lang.String providerId,java.lang.String provider) { 
        LOG.info("Executing operation getProviderTimestamp");
        System.out.println(providerId);
        System.out.println(provider);
        try {
            java.lang.String _return = "_return558604341";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap#getProviderList(*
     */
    public java.lang.String getProviderList() { 
        LOG.info("Executing operation getProviderList");
        try {
            java.lang.String _return = "_return1000591350";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap#getDataSet(java.lang.String  provider )*
     */
    public de.daenet.webservices.currencyserver.GetDataSetResponse.GetDataSetResult getDataSet(java.lang.String provider) { 
        LOG.info("Executing operation getDataSet");
        System.out.println(provider);
        try {
            de.daenet.webservices.currencyserver.GetDataSetResponse.GetDataSetResult _return = new de.daenet.webservices.currencyserver.GetDataSetResponse.GetDataSetResult();
            java.lang.Object _returnAny = null;
            _return.setAny(_returnAny);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap#getXmlStream(java.lang.String  provider )*
     */
    public java.lang.String getXmlStream(java.lang.String provider) { 
        LOG.info("Executing operation getXmlStream");
        System.out.println(provider);
        try {
            java.lang.String _return = "_return-1663090591";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap#getCurrencyValue(java.lang.String  provider ,)java.lang.String  srcCurrency ,)java.lang.String  dstCurrency )*
     */
    public double getCurrencyValue(java.lang.String provider,java.lang.String srcCurrency,java.lang.String dstCurrency) { 
        LOG.info("Executing operation getCurrencyValue");
        System.out.println(provider);
        System.out.println(srcCurrency);
        System.out.println(dstCurrency);
        try {
            double _return = 0.4836833535167646;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap#getProviderDescription(java.lang.String  provider )*
     */
    public java.lang.String getProviderDescription(java.lang.String provider) { 
        LOG.info("Executing operation getProviderDescription");
        System.out.println(provider);
        try {
            java.lang.String _return = "_return687145621";
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
