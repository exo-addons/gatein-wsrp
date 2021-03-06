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

package org.gatein.wsrp.test.protocol.v1.behaviors;

import org.gatein.common.util.ParameterValidation;
import org.gatein.wsrp.spec.v1.WSRP1ExceptionFactory;
import org.gatein.wsrp.spec.v1.WSRP1TypeFactory;
import org.gatein.wsrp.test.protocol.v1.BehaviorRegistry;
import org.gatein.wsrp.test.protocol.v1.MarkupBehavior;
import org.gatein.wsrp.test.protocol.v1.PortletManagementBehavior;
import org.oasis.wsrp.v1.V1AccessDenied;
import org.oasis.wsrp.v1.V1DestroyFailed;
import org.oasis.wsrp.v1.V1Extension;
import org.oasis.wsrp.v1.V1InconsistentParameters;
import org.oasis.wsrp.v1.V1InvalidHandle;
import org.oasis.wsrp.v1.V1InvalidRegistration;
import org.oasis.wsrp.v1.V1InvalidUserCategory;
import org.oasis.wsrp.v1.V1MissingParameters;
import org.oasis.wsrp.v1.V1OperationFailed;
import org.oasis.wsrp.v1.V1PortletContext;
import org.oasis.wsrp.v1.V1PortletDescription;
import org.oasis.wsrp.v1.V1Property;
import org.oasis.wsrp.v1.V1PropertyList;
import org.oasis.wsrp.v1.V1RegistrationContext;
import org.oasis.wsrp.v1.V1ResetProperty;
import org.oasis.wsrp.v1.V1ResourceList;
import org.oasis.wsrp.v1.V1UserContext;

import javax.jws.WebParam;
import javax.xml.ws.Holder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 8784 $
 * @since 2.6
 */
public class BasicPortletManagementBehavior extends PortletManagementBehavior
{
   private static final String CLONE_SUFFIX = "_clone";
   public static final String PROPERTY_NAME = "prop1";
   public static final String PROPERTY_VALUE = "value1";
   public static final String PROPERTY_NEW_VALUE = "value2";
   public static final String CLONED_HANDLE = BasicMarkupBehavior.PORTLET_HANDLE + CLONE_SUFFIX;
   public static final String CANNOT_BOTH_SET_AND_RESET_A_PROPERTY_AT_THE_SAME_TIME = "Cannot both set and reset a property at the same time!";
   private BehaviorRegistry registry;
   private String propValue = PROPERTY_VALUE;

   public BasicPortletManagementBehavior(BehaviorRegistry registry)
   {
      super();
      this.registry = registry;
   }

   @Override
   public void clonePortlet(@WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext, @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext, @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext, @WebParam(mode = WebParam.Mode.OUT, name = "portletHandle", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<String> portletHandle, @WebParam(mode = WebParam.Mode.OUT, name = "portletState", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<byte[]> portletState, @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      String handle = getHandleFrom(portletContext, "portlet context");

      if (!BasicMarkupBehavior.PORTLET_HANDLE.equals(handle))
      {
         throw WSRP1ExceptionFactory.throwWSException(V1OperationFailed.class, "Cannot modify portlet '" + handle + "'", null);
      }

      portletHandle.value = CLONED_HANDLE;
   }

   @Override
   public void getPortletDescription(@WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext, @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext, @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext, @WebParam(name = "desiredLocales", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") List<String> desiredLocales, @WebParam(mode = WebParam.Mode.OUT, name = "portletDescription", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<V1PortletDescription> portletDescription, @WebParam(mode = WebParam.Mode.OUT, name = "resourceList", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<V1ResourceList> resourceList, @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      super.getPortletDescription(registrationContext, portletContext, userContext, desiredLocales, portletDescription, resourceList, extensions);

      String handle = getHandleFrom(portletContext, "portlet context");

      // need to fake that the clone exists... so remove suffix to get the description of the POP
      int index = handle.indexOf(CLONE_SUFFIX);
      if (index != -1)
      {
         handle = handle.substring(0, index);
      }

      // get the POP description...
      MarkupBehavior markupBehaviorFor = registry.getMarkupBehaviorFor(handle);

      V1PortletDescription description = markupBehaviorFor.getPortletDescriptionFor(handle);

      // if it was a clone, add the suffix back to the handle.
      if (index != -1)
      {
         description.setPortletHandle(handle + CLONE_SUFFIX);
      }

      portletDescription.value = description;
   }

   @Override
   public void getPortletProperties(@WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext, @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext, @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext, @WebParam(name = "names", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") List<String> names, @WebParam(mode = WebParam.Mode.OUT, name = "properties", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Property>> properties, @WebParam(mode = WebParam.Mode.OUT, name = "resetProperties", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1ResetProperty>> resetProperties, @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      String handle = getHandleFrom(portletContext, "portlet context");

      List<V1Property> propertyList = new ArrayList<V1Property>(1);

      if (BasicMarkupBehavior.PORTLET_HANDLE.equals(handle))
      {
         propertyList.add(WSRP1TypeFactory.createProperty(PROPERTY_NAME, "en", PROPERTY_VALUE));
      }
      else if (CLONED_HANDLE.equals(handle))
      {
         if (propValue != null)
         {
            propertyList.add(WSRP1TypeFactory.createProperty(PROPERTY_NAME, "en", propValue));
         }
         if (callCount > 4)
         {
            throw new IllegalStateException("Shouldn't have been called more than four times!");
         }
      }
      else
      {
         WSRP1ExceptionFactory.throwWSException(V1InvalidHandle.class, "Unknown handle '" + handle + "'", null);
      }

      incrementCallCount();
      properties.value = propertyList;
   }

   @Override
   public void setPortletProperties(@WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext, @WebParam(name = "portletContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PortletContext portletContext, @WebParam(name = "userContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1UserContext userContext, @WebParam(name = "propertyList", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1PropertyList propertyList, @WebParam(mode = WebParam.Mode.OUT, name = "portletHandle", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<String> portletHandle, @WebParam(mode = WebParam.Mode.OUT, name = "portletState", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<byte[]> portletState, @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions) throws V1MissingParameters, V1InconsistentParameters, V1InvalidHandle, V1InvalidRegistration, V1InvalidUserCategory, V1AccessDenied, V1OperationFailed
   {
      String handle = getHandleFrom(portletContext, "portlet context");

      if (!(CLONED_HANDLE).equals(handle))
      {
         throw WSRP1ExceptionFactory.throwWSException(V1OperationFailed.class, "Cannot modify portlet '" + handle + "'", null);
      }

      List<V1Property> properties = propertyList.getProperties();
      List<V1ResetProperty> resetProperties = propertyList.getResetProperties();

      // check that we don't try to set and reset the same property
      if (ParameterValidation.existsAndIsNotEmpty(properties) && ParameterValidation.existsAndIsNotEmpty(resetProperties))
      {
         if (properties.size() != 1 && resetProperties.size() != 1)
         {
            WSRP1ExceptionFactory.throwWSException(V1InconsistentParameters.class, "Invalid number of properties!", null);
         }

         if (properties.get(0).getName().equals(resetProperties.get(0).getName()))
         {
            WSRP1ExceptionFactory.throwWSException(V1InconsistentParameters.class, CANNOT_BOTH_SET_AND_RESET_A_PROPERTY_AT_THE_SAME_TIME, null);
         }
      }

      // if we have the expected property name, update the value
      if (ParameterValidation.existsAndIsNotEmpty(properties))
      {
         if (properties.size() != 1)
         {
            WSRP1ExceptionFactory.throwWSException(V1InconsistentParameters.class, "Invalid number of properties!", null);
         }

         V1Property property = properties.get(0);
         String name = property.getName();
         if (name.equals(PROPERTY_NAME))
         {
            propValue = property.getStringValue();
         }
         else
         {
            WSRP1ExceptionFactory.throwWSException(V1InconsistentParameters.class, "Unknown property '" + name + "'", null);
         }
      }

      // if we have the proper reset property, reset it
      if (ParameterValidation.existsAndIsNotEmpty(resetProperties))
      {
         if (resetProperties.size() != 1)
         {
            WSRP1ExceptionFactory.throwWSException(V1InconsistentParameters.class, "Invalid number of reset properties!", null);
         }

         V1ResetProperty resetProperty = resetProperties.get(0);
         String name = resetProperty.getName();
         if (name.equals(PROPERTY_NAME))
         {
            propValue = PROPERTY_VALUE;
         }
         else
         {
            WSRP1ExceptionFactory.throwWSException(V1InconsistentParameters.class, "Unknown property '" + name + "'", null);
         }
      }


      portletHandle.value = handle;
   }

   private String getHandleFrom(V1PortletContext portletContext, String context) throws V1MissingParameters, V1InvalidHandle
   {
      WSRP1ExceptionFactory.throwMissingParametersIfValueIsMissing(portletContext, "portlet context", context);
      String handle = portletContext.getPortletHandle();
      WSRP1ExceptionFactory.throwMissingParametersIfValueIsMissing(handle, "portlet handle", "PortletContext");
      if (handle.length() == 0)
      {
         throw WSRP1ExceptionFactory.throwWSException(V1InvalidHandle.class, "Portlet handle is empty", null);
      }

      return handle;
   }

   @Override
   public void destroyPortlets(@WebParam(name = "registrationContext", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") V1RegistrationContext registrationContext, @WebParam(name = "portletHandles", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") List<String> portletHandles, @WebParam(mode = WebParam.Mode.OUT, name = "destroyFailed", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1DestroyFailed>> destroyFailed, @WebParam(mode = WebParam.Mode.OUT, name = "extensions", targetNamespace = "urn:oasis:names:tc:wsrp:v1:types") Holder<List<V1Extension>> extensions) throws V1MissingParameters, V1InconsistentParameters, V1InvalidRegistration, V1OperationFailed
   {
      super.destroyPortlets(registrationContext, portletHandles, destroyFailed, extensions);
      WSRP1ExceptionFactory.throwMissingParametersIfValueIsMissing(portletHandles, "portlet handles", "destroyPortlets");
      if (portletHandles.isEmpty())
      {
         WSRP1ExceptionFactory.throwMissingParametersIfValueIsMissing(portletHandles, "portlet handles", "DestroyPortlets");
      }

      List<V1DestroyFailed> list = destroyFailed.value;
      if (list == null)
      {
         list = new ArrayList<V1DestroyFailed>();
         destroyFailed.value = list;
      }
      for (String handle : portletHandles)
      {
         if (!CLONED_HANDLE.equals(handle))
         {

            list.add(WSRP1TypeFactory.createDestroyFailed(handle, "Handle '" + handle + "' doesn't exist"));
         }
      }
   }
}
