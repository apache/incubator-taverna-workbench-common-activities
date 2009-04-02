/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.activities.soaplab.query;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

/**
 * Convenience methods for simpler calling of SOAP services using the Axis
 * client.
 * 
 * Note that for complex scenarious you might want to construct the Call object
 * yourself.
 * 
 * @author Stian Soiland
 * 
 */
public class Soap {
	/**
	 * Invoke the web service, passing no parameters, and return the result.
	 *  
	 * @see callWebService(String target, String operation,	Object[] parameters)
	 * 
	 */
	public static Object callWebService(String target, String operation)
			throws ServiceException, RemoteException {
		return callWebService(target, operation, new Object[0]);
	}

	/**
	 * Invoke the web service, passing a single String parameter, and return the result.
	 *  
	 * @see callWebService(String target, String operation,	Object[] parameters)
	 * 
	 */
	public static Object callWebService(String target, String operation,
			String parameter) throws ServiceException, RemoteException {
		return callWebService(target, operation, new String[] { parameter });
	}

	/**
	 * Invoke the web service and return the result.
	 * 
	 * @param target The full URL to the service, example "http://www.ebi.ac.uk/soaplab/services/AnalysisFactory"
	 * @param operation The operation name, example "getAvailableCategories"
	 * @param parameters A (possibly empty) list of parameters
	 * @return The result returned from calling the webservice operation
	 * @throws ServiceException If web service facilities are not available
	 * @throws RemoteException If remote call failed
	 */
	public static Object callWebService(String target, String operation,
			Object[] parameters) throws ServiceException, RemoteException {
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(target);
		// No need to do new Qname(operation) with unspecified namespaces
		call.setOperationName(operation);
		return call.invoke(parameters);
	}
}
