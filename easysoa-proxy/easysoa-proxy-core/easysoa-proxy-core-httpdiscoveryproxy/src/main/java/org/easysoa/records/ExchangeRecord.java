/**
 * EasySOA Registry
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

package org.easysoa.records;

import java.io.IOException;
import javax.xml.bind.annotation.XmlRootElement;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;

@XmlRootElement
public class ExchangeRecord {

	// TODO : Not possible to record directly the ServletRequest and ServeltResponse objects
	// Time to use an unified MessageApi ??
	// See RestMessageExchange interface from SOAPUI at http://www.soapui.org/apidocs/com/eviware/soapui/impl/wsdl/submit/RestMessageExchange.html
	// See also com.openwide.easysoa.monitoring.Message

	// TODO : Add fields to store :
	// - IN message (request)
	// - OUT message (response)
	// All informations about an error about the response : status, timeout, error code & message ...
	// An ID and the message in / out times.

	// Several solutions :
	// - make a message API from scratch
	// - Use HAR lib : store HTTP logs (collection of in/out messages) in JSON format (file or database), already implemented,
	// recent lib, not in Maven repo's, needs mappers to work with cxf or soapUI model
	// - Use CXF Messages : Complete message lib with interceptors but works only with cxf ...

	// in / out messages
	private InMessage inMessage;
	private OutMessage outMessage;
	// Message timeouts Add here or in the response message ?
	//private Timeouts timeouts;
	// Exchange ID
	private String exchangeID;
	// Exchange type
	private ExchangeType exchangeType;
	
	// TODO add a constructor from a servletRequest object
	// Add a setter for a ServletResponse object
	// Add also constructors/methods for CXF messages
	// Goal is to obtain a neutral messaging API

	/**
	 *	Message types 
	 */
	public enum ExchangeType {
	    WSDL, REST, SOAP
	}	
	
	public ExchangeRecord(){
		this.exchangeID = "";
		this.inMessage = null;
		this.outMessage = null;
	}

	/**
	 * Build a new ExchangeRecord object
	 * @param request The request content
	 * @param response The response content
	 * @throws IOException
	 */
	public ExchangeRecord(String exchangeID, InMessage inMessage, OutMessage outMessage) throws IllegalArgumentException {
		//this.requestContent = new Scanner(request.getInputStream()).useDelimiter("\\A").next();
		if(exchangeID == null || "".equals(exchangeID) || inMessage == null){
			throw new IllegalArgumentException("the parameters exchangeID and inMessage must not be null or empty");
		}
		this.exchangeID = exchangeID;
		this.inMessage = inMessage;
		this.outMessage = outMessage;
	}
	
	/**
	 * 
	 * @param inMessage
	 */
	public ExchangeRecord(String exchangeID, InMessage inMessage) throws IllegalArgumentException {
		this(exchangeID, inMessage, null);
	}
	
	public void setExchangeID(String exchangeID){
		this.exchangeID = exchangeID;
	}
	
	/**
	 * Returns the exchange ID
	 * @return The Exchange ID
	 */
	public String getExchangeID(){
		return this.exchangeID;
	}
	
	/**
	 * Set the outgoing message
	 * @param outMessage The outgoing message
	 */
	public void setInMessage(InMessage inMessage){
		this.inMessage = inMessage;
	}	
	
	/**
	 * Set the outgoing message
	 * @param outMessage The outgoing message
	 */
	public void setOutMessage(OutMessage outMessage){
		this.outMessage = outMessage;
	}
	
	/**
	 * returns the inbound request
	 * @return <code>InMessage</code> the inbound message
	 */
	public InMessage getInMessage(){
		return this.inMessage;
	}
	
	/**
	 * Returns the outgoing message
	 * @return <code>OutMessage</code> The outgoing message
	 */
	public OutMessage getOutMessage(){
		return this.outMessage;
	}

	/**
	 * Get the exchange type
	 * @return The exchange type
	 */
	public ExchangeType getExchangeType() {
		return exchangeType;
	}

	/**
	 * Set the exchange type
	 * @param exchangeType The exchange type
	 */
	public void setExchangeType(ExchangeType exchangeType) {
		this.exchangeType = exchangeType;
	}	
}