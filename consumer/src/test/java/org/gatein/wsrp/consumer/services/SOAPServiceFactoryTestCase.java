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

package org.gatein.wsrp.consumer.services;

import junit.framework.TestCase;
import org.gatein.wsrp.services.SOAPServiceFactory;
import org.oasis.wsrp.v1.WSRPV1MarkupPortType;
import org.oasis.wsrp.v1.WSRPV1PortletManagementPortType;
import org.oasis.wsrp.v1.WSRPV1RegistrationPortType;
import org.oasis.wsrp.v1.WSRPV1ServiceDescriptionPortType;
import org.oasis.wsrp.v2.WSRPV2MarkupPortType;
import org.oasis.wsrp.v2.WSRPV2PortletManagementPortType;
import org.oasis.wsrp.v2.WSRPV2RegistrationPortType;
import org.oasis.wsrp.v2.WSRPV2ServiceDescriptionPortType;

import javax.wsdl.WSDLException;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class SOAPServiceFactoryTestCase extends TestCase
{
   private SOAPServiceFactory factory;
   public static final Class[] WSRP2_PORT_TYPES = new Class[]{WSRPV2MarkupPortType.class, WSRPV2ServiceDescriptionPortType.class, WSRPV2PortletManagementPortType.class, WSRPV2RegistrationPortType.class};
   public static final Class[] WSRP1_PORT_TYPES = new Class[]{WSRPV1MarkupPortType.class, WSRPV1ServiceDescriptionPortType.class, WSRPV1PortletManagementPortType.class, WSRPV1RegistrationPortType.class};

   @Override
   protected void setUp() throws Exception
   {
      factory = new SOAPServiceFactory();
   }

   public void testSimpleV2Service() throws Exception
   {
      factory.setWsdlDefinitionURL("http://www.netunitysoftware.com/wsrp2interop/WsrpProducer.asmx?Operation=WSDL&WsrpVersion=Two");
      checkPorts(WSRP2_PORT_TYPES);
   }

   public void testSimpleV1Service() throws Exception
   {
      factory.setWsdlDefinitionURL("http://www.netunitysoftware.com/wsrp2interop/WsrpProducer.asmx?Operation=WSDL&WsrpVersion=One");
      checkPorts(WSRP1_PORT_TYPES);
   }

   public void testBothServices() throws Exception
   {
      factory.setWsdlDefinitionURL("http://www.netunitysoftware.com/wsrp2interop/WsrpProducer.asmx?Operation=WSDL&WsrpVersion=All");
      checkPorts(WSRP2_PORT_TYPES);
   }

   public void testDefaultDotNetWSDLCompositeService() throws Exception
   {
      factory.setWsdlDefinitionURL("http://www.netunitysoftware.com/wsrp2interop/WsrpProducer.asmx?WSDL");
      try
      {
         checkPorts(WSRP2_PORT_TYPES);
         fail();
      }
      catch (Exception e)
      {
         assertTrue(e instanceof WSDLException);
         WSDLException wsdlEx = (WSDLException)e;
         assertEquals(WSDLException.INVALID_WSDL, wsdlEx.getFaultCode());
      }
   }

   private void checkPorts(Class[] ports) throws Exception
   {
      for (Class portClass : ports)
      {
         assertNotNull(factory.getService(portClass));
      }
   }
}