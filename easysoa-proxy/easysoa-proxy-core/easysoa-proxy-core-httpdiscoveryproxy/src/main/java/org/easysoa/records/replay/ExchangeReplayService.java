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
package org.easysoa.records.replay;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.StoreCollection;
import org.osoa.sca.annotations.Remotable;

@Remotable
public interface ExchangeReplayService {
	
	@GET
	@Path("/getExchangeRecordList/{storeName}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public RecordCollection getExchangeRecordlist(@PathParam("storeName") String exchangeRecordStoreName);

	@GET
	@Path("/getExchangeRecord/{storeName}/{exchangeID}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ExchangeRecord getExchangeRecord(@PathParam("storeName") String exchangeRecordStoreName, @PathParam("exchangeID") String exchangeID);
	
	@GET
	@Path("/getExchangeRecordStorelist")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})	
	public StoreCollection getExchangeRecordStorelist();
	
	@GET
	@Path("/replay/{exchangeRecordStoreName}/{exchangeRecordId}")	
	@Produces("application/json")	
	public String replay(@PathParam("exchangeRecordStoreName") String exchangeRecordStoreName, @PathParam("exchangeRecordId") String exchangeRecordId);
	
	@POST
	@Path("/cloneToEnvironment/{anotherEnvironment}")
	@Produces("application/json")	
	public void cloneToEnvironment(@PathParam("anotherEnvironment") String anotherEnvironment);
	
}