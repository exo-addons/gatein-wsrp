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

package org.gatein.wsrp.endpoints;

import org.oasis.wsrp.v1.CookieProtocol;
import org.oasis.wsrp.v1.Extension;
import org.oasis.wsrp.v1.GetServiceDescription;
import org.oasis.wsrp.v1.InvalidRegistration;
import org.oasis.wsrp.v1.ItemDescription;
import org.oasis.wsrp.v1.ModelDescription;
import org.oasis.wsrp.v1.OperationFailed;
import org.oasis.wsrp.v1.PortletDescription;
import org.oasis.wsrp.v1.RegistrationContext;
import org.oasis.wsrp.v1.ResourceList;
import org.oasis.wsrp.v1.ServiceDescription;
import org.oasis.wsrp.v1.WSRPV1ServiceDescriptionPortType;

import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.xml.ws.Holder;
import java.util.List;

/**
 * @author <a href="mailto:palber@novell.com">Polina Alber</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 8784 $
 */
@javax.jws.WebService(
   name = "WSRPV1ServiceDescriptionPortType",
   serviceName = "WSRPV1Service",
   portName = "WSRPServiceDescriptionService",
   targetNamespace = "urn:oasis:names:tc:wsrp:v1:wsdl",
   wsdlLocation = "/WEB-INF/wsdl/wsrp_services.wsdl",
   endpointInterface = "org.oasis.wsrp.v1.WSRPV1ServiceDescriptionPortType"
)
@HandlerChain(file = "wshandlers.xml")
public class ServiceDescriptionEndpoint extends WSRPBaseEndpoint implements WSRPV1ServiceDescriptionPortType
{
   public void getServiceDescription(
      @WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") RegistrationContext registrationContext,
      @WebParam(name = "desiredLocales", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") List<String> desiredLocales,
      @WebParam(mode = WebParam.Mode.OUT, name = "requiresRegistration", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<Boolean> requiresRegistration,
      @WebParam(mode = WebParam.Mode.OUT, name = "offeredPortlets", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<PortletDescription>> offeredPortlets,
      @WebParam(mode = WebParam.Mode.OUT, name = "userCategoryDescriptions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<ItemDescription>> userCategoryDescriptions,
      @WebParam(mode = WebParam.Mode.OUT, name = "customUserProfileItemDescriptions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<ItemDescription>> customUserProfileItemDescriptions,
      @WebParam(mode = WebParam.Mode.OUT, name = "customWindowStateDescriptions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<ItemDescription>> customWindowStateDescriptions,
      @WebParam(mode = WebParam.Mode.OUT, name = "customModeDescriptions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<ItemDescription>> customModeDescriptions,
      @WebParam(mode = WebParam.Mode.OUT, name = "requiresInitCookie", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<CookieProtocol> requiresInitCookie,
      @WebParam(mode = WebParam.Mode.OUT, name = "registrationPropertyDescription", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<ModelDescription> registrationPropertyDescription,
      @WebParam(mode = WebParam.Mode.OUT, name = "locales", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<String>> locales,
      @WebParam(mode = WebParam.Mode.OUT, name = "resourceList", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<ResourceList> resourceList,
      @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<Extension>> extensions
   ) throws InvalidRegistration, OperationFailed
   {
      GetServiceDescription getServiceDescription = new GetServiceDescription();
      getServiceDescription.setRegistrationContext(registrationContext);
      getServiceDescription.getDesiredLocales().addAll(desiredLocales);

      ServiceDescription description = producer.getServiceDescription(getServiceDescription);

      requiresRegistration.value = description.isRequiresRegistration();
      offeredPortlets.value = description.getOfferedPortlets();
      userCategoryDescriptions.value = description.getUserCategoryDescriptions();
      customUserProfileItemDescriptions.value = description.getCustomUserProfileItemDescriptions();
      customWindowStateDescriptions.value = description.getCustomWindowStateDescriptions();
      customModeDescriptions.value = description.getCustomModeDescriptions();
      requiresInitCookie.value = description.getRequiresInitCookie();
      registrationPropertyDescription.value = description.getRegistrationPropertyDescription();
      locales.value = description.getLocales();
      resourceList.value = description.getResourceList();
      extensions.value = description.getExtensions();
   }
}
