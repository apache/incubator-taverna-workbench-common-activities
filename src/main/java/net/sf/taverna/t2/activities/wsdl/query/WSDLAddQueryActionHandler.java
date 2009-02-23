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
package net.sf.taverna.t2.activities.wsdl.query;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sf.taverna.t2.partition.AddQueryActionHandler;

@SuppressWarnings("serial")
public class WSDLAddQueryActionHandler extends AddQueryActionHandler {
	

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		String wsdl = JOptionPane.showInputDialog(null,"Address of the WSDL document","WSDL location",JOptionPane.INFORMATION_MESSAGE);
		if (wsdl!=null) {
			WSDLQuery query = new WSDLQuery(wsdl);
			addQuery(query);
		}
	}

	@Override
	protected Icon getIcon() {
		return new ImageIcon(WSDLAddQueryActionHandler.class.getResource("/wsdl.png"));
	}

	@Override
	protected String getText() {
		return "WSDL...";
	}

}
