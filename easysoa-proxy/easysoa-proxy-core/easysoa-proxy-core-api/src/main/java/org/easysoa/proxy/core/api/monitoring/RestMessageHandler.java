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

package org.easysoa.proxy.core.api.monitoring;

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.esper.EsperEngine;
import org.easysoa.proxy.core.api.monitoring.soa.Node;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.Exchange.ExchangeType;

/**
 * Handler for REST exchanges 
 * 
 * @author jguillemotte
 *
 */
public class RestMessageHandler implements MessageHandler {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(RestMessageHandler.class.getName());	

	@Override
	public boolean isOkFor(ExchangeRecord exchangeRecord) {		
		// TODO : How to determine if a message is a pure rest message ....
		// TODO : Use a Data Mining framework to discover URI patterns 
		return true;
	}
    
    /**
     * @obsolete handle() uses no more custom built service name but merely full url
     * @return
     */
    protected String buildServiceName(ExchangeRecord exchangeRecord) {
        String serviceName = exchangeRecord.getInMessage().getPath();
        if(serviceName.startsWith("/")){
            serviceName = serviceName.substring(1);
        }
        serviceName = serviceName.replace('/', '_');
        return serviceName;
    }

	@Override
	public boolean handle(ExchangeRecord exchangeRecord, MonitoringService monitoringService, EsperEngine esperEngine) {
		// Add the url in the url tree structure
		logger.debug("REST message found");
		exchangeRecord.getExchange().setExchangeType(ExchangeType.REST);
		if(MonitoringService.MonitoringMode.DISCOVERY.equals(monitoringService.getMode())){
			logger.debug("Discovery mode, message added in tree");
			monitoringService.getUrlTree().addUrlNode(exchangeRecord);
		}
		else if(MonitoringService.MonitoringMode.VALIDATED.equals(monitoringService.getMode())){		    
			logger.debug("Validated mode, checking if message exists in urlSoaModel");
			MonitoringModel monitoringModel = monitoringService.getModel();
			logger.debug("searched key : " + exchangeRecord.getInMessage().buildCompleteUrl());
			//TODO change this to match with partial url
			String urlSoaModelType =  monitoringModel.getSoaModelUrlToTypeMap().get(exchangeRecord.getInMessage().buildCompleteUrl());
			// if none, maybe it is a resource :
			if (urlSoaModelType == null) {
				logger.debug("urlSoaModelType is null, searched key not found ..");
				int lastSlashIndex = exchangeRecord.getInMessage().buildCompleteUrl().lastIndexOf('/'); // TODO BETTER regexp or finite automat OR ESPER OR SHARED MODEL WITH TREE OR ABSTRACT TREE ??!!
				String serviceUrlOfResource = exchangeRecord.getInMessage().buildCompleteUrl().substring(0, lastSlashIndex);
				// TODO setCompleteUrl method has been removed, see how to replace the following line with other methods
				//exchangeRecord.getInMessage().setCompleteUrl(serviceUrlOfResource); // HACK TODO rather add a field
				//exchangeRecord.getInMessage().setPath(serviceUrlOfResource); // HACK TODO rather add a field
				urlSoaModelType = monitoringModel.getSoaModelUrlToTypeMap().get(serviceUrlOfResource);
			}
			if (urlSoaModelType != null) {
				logger.debug("Validated mode, message send to esper");
				// if there, feed it to esper
				// TODO write listener that group by serviceUrl and register to nuxeo every minute
				Node soaNode = null;
				for(Node node : monitoringService.getModel().getSoaNodes()){
					if(node.getUrl().equals(exchangeRecord.getInMessage().buildCompleteUrl())){
						soaNode = node;
						logger.debug("Node found ! " + soaNode.getTitle());
						break;
					}
				}
				esperEngine.sendEvent(soaNode);
			} else {
				logger.debug("Validated mode, Adding message to unknwown message list");
				// Unknown message
				monitoringService.getUnknownExchangeRecordList().add(exchangeRecord);
			}			
		}
		return true;
	}

}
