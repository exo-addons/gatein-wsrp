/*
 * JBoss, a division of Red Hat
 * Copyright 2009, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.oasis.wsrp.v1;

import javax.xml.ws.WebFault;


/** This class was generated by Apache CXF 2.2.2 Mon Aug 03 23:27:29 CEST 2009 Generated source version: 2.2.2 */

@WebFault(name = "PortletStateChangeRequired", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types")
public class PortletStateChangeRequired extends Exception
{
   public static final long serialVersionUID = 20090803232729L;

   private org.oasis.wsrp.v1.PortletStateChangeRequiredFault portletStateChangeRequired;

   public PortletStateChangeRequired()
   {
      super();
   }

   public PortletStateChangeRequired(String message)
   {
      super(message);
   }

   public PortletStateChangeRequired(String message, Throwable cause)
   {
      super(message, cause);
   }

   public PortletStateChangeRequired(String message, org.oasis.wsrp.v1.PortletStateChangeRequiredFault portletStateChangeRequired)
   {
      super(message);
      this.portletStateChangeRequired = portletStateChangeRequired;
   }

   public PortletStateChangeRequired(String message, org.oasis.wsrp.v1.PortletStateChangeRequiredFault portletStateChangeRequired, Throwable cause)
   {
      super(message, cause);
      this.portletStateChangeRequired = portletStateChangeRequired;
   }

   public org.oasis.wsrp.v1.PortletStateChangeRequiredFault getFaultInfo()
   {
      return this.portletStateChangeRequired;
   }
}
