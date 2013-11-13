/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
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
package org.gatein.wsrp.services;

import org.gatein.common.util.Version;

/**
 * A factory that gives access to remote services.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11484 $
 */
public interface ServiceFactory
{
   int DEFAULT_TIMEOUT_MS = 10000;
   Version WSRP2 = new Version("WSRP", 2, 0, 0, new Version.Qualifier(Version.Qualifier.Prefix.GA), "WSRP2");
   Version WSRP1 = new Version("WSRP", 1, 0, 0, new Version.Qualifier(Version.Qualifier.Prefix.GA), "WSRP1");

   <T> T getService(Class<T> clazz) throws Exception;

   /**
    * Determines whether or not this ServiceFactory is able to provide services. A non-available ServiceFactory might be
    * in a temporary state of non-availability (e.g. if the remote host is not currently reachable) or permanently
    * (because, e.g. its configuration is invalid). Permanent failure is indicated by {@link #isFailed()} status.
    *
    * @return <code>true</code> if this ServiceFactory is ready to provide services, <code>false</code> otherwise.
    */
   boolean isAvailable();

   /**
    * Determines whether or not this ServiceFactory is in a permanent state of failure which cannot be recovered from
    * without user intervention. This notably happens if the configuration is incorrect (i.e. remote host URLs are
    * invalid).
    *
    * @return <code>true</code> if this ServiceFactory is not configured properly, <code>false</code> otherwise.
    */
   boolean isFailed();

   void start() throws Exception;

   void stop();

   void setWsdlDefinitionURL(String wsdlDefinitionURL);

   String getWsdlDefinitionURL();

   /**
    * Number of milliseconds before a WS operation is considered as having timed out.
    *
    * @param msBeforeTimeOut number of milliseconds to wait for a WS operation to return before timing out. Will be set
    *                        to {@link #DEFAULT_TIMEOUT_MS} if negative.
    */
   void setWSOperationTimeOut(int msBeforeTimeOut);

   int getWSOperationTimeOut();

   ServiceDescriptionService getServiceDescriptionService() throws Exception;

   MarkupService getMarkupService() throws Exception;

   PortletManagementService getPortletManagementService() throws Exception;

   RegistrationService getRegistrationService() throws Exception;

   /**
    * Returns the WSRP version of the remote service that this ServiceFactory connects to or <code>null</code> if the
    * ServiceFactory is not available.
    *
    * @return
    */
   Version getWSRPVersion();

   boolean refresh(boolean force) throws Exception;
}
