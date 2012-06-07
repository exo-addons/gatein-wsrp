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

package org.gatein.wsrp.protocol.v2;

import org.gatein.pc.api.Mode;
import org.gatein.pc.api.OpaqueStateString;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.WindowState;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.FragmentResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.UpdateNavigationalStateResponse;
import org.gatein.pc.portlet.impl.spi.AbstractInstanceContext;
import org.gatein.pc.portlet.impl.spi.AbstractPortalContext;
import org.gatein.pc.portlet.impl.spi.AbstractSecurityContext;
import org.gatein.pc.portlet.impl.spi.AbstractUserContext;
import org.gatein.pc.portlet.impl.spi.AbstractWindowContext;
import org.gatein.wsrp.WSRPResourceURL;
import org.gatein.wsrp.api.extensions.ExtensionAccess;
import org.gatein.wsrp.api.extensions.InvocationHandlerDelegate;
import org.gatein.wsrp.api.extensions.UnmarshalledExtension;
import org.gatein.wsrp.consumer.handlers.ProducerSessionInformation;
import org.gatein.wsrp.payload.PayloadUtils;
import org.gatein.wsrp.test.ExtendedAssert;
import org.gatein.wsrp.test.protocol.v2.BehaviorRegistry;
import org.gatein.wsrp.test.protocol.v2.behaviors.BasicMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.EmptyMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.ExtensionMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.GroupedPortletsServiceDescriptionBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.InitCookieMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.InitCookieNotRequiredMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.NullMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.PerGroupInitCookieMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.PerUserInitCookieMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.ResourceMarkupBehavior;
import org.gatein.wsrp.test.protocol.v2.behaviors.SessionMarkupBehavior;
import org.gatein.wsrp.test.support.MockHttpServletRequest;
import org.gatein.wsrp.test.support.RequestedMarkupBehavior;
import org.gatein.wsrp.test.support.TestPortletInvocationContext;
import org.oasis.wsrp.v2.CookieProtocol;
import org.oasis.wsrp.v2.EventDescription;
import org.oasis.wsrp.v2.ExportDescription;
import org.oasis.wsrp.v2.Extension;
import org.oasis.wsrp.v2.InvalidHandle;
import org.oasis.wsrp.v2.InvalidRegistration;
import org.oasis.wsrp.v2.ItemDescription;
import org.oasis.wsrp.v2.MarkupParams;
import org.oasis.wsrp.v2.MarkupResponse;
import org.oasis.wsrp.v2.ModelDescription;
import org.oasis.wsrp.v2.ModelTypes;
import org.oasis.wsrp.v2.ModifyRegistrationRequired;
import org.oasis.wsrp.v2.OperationFailed;
import org.oasis.wsrp.v2.PortletDescription;
import org.oasis.wsrp.v2.ResourceList;
import org.oasis.wsrp.v2.ResourceSuspended;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.http.HttpSession;
import javax.xml.ws.Holder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 11320 $
 * @since 2.4 (May 4, 2006)
 */
public class MarkupTestCase extends V2ConsumerBaseTest
{

   public MarkupTestCase() throws Exception
   {
      super();
   }

   @Override
   protected void tearDown() throws Exception
   {
      InvocationHandlerDelegate.registerConsumerDelegate(null);
      super.tearDown();
   }

   public void testInvalidHandle()
   {
      try
      {
         consumer.invoke(createRenderInvocation("Invalid portlet handle"));
         ExtendedAssert.fail("Should have failed on invalid portlet handle");
      }
      catch (PortletInvokerException expected)
      {
         // expected
      }
   }

   public void testEmptyRender() throws Exception
   {
      checkRenderResult(consumer.invoke(createRenderInvocation(EmptyMarkupBehavior.PORTLET_HANDLE)), "");
   }

   public void testNullAction() throws Exception
   {
      ExtendedAssert.assertTrue(consumer.invoke(createActionInvocation(NullMarkupBehavior.PORTLET_HANDLE)) instanceof ErrorResponse);
   }

   public void testNullRender() throws Exception
   {
      ExtendedAssert.assertTrue(consumer.invoke(createRenderInvocation(NullMarkupBehavior.PORTLET_HANDLE)) instanceof ErrorResponse);
   }

   public void testRender() throws Exception
   {
      RenderInvocation render = createRenderInvocation(BasicMarkupBehavior.PORTLET_HANDLE, Mode.EDIT,
         WindowState.NORMAL, "someNS");
      FragmentResponse result = checkRenderResult(consumer.invoke(render), "portlet1:edit:normal:someNS");
      ExtendedAssert.assertEquals(15, result.getCacheControl().getExpirationSecs());

      render = createRenderInvocation(SessionMarkupBehavior.PORTLET_HANDLE);
      result = checkRenderResult(consumer.invoke(render), "portlet2:0:view:maximized");
      ExtendedAssert.assertEquals(0, result.getCacheControl().getExpirationSecs());
   }

   public void testRenderWithSimpleExtensions() throws PortletInvokerException
   {
      // Register delegate sending "foo" String as an extension to MarkupParams and expects to retrieve "bar" as an extension of MarkupResponse
      InvocationHandlerDelegate.registerConsumerDelegate(new InvocationHandlerDelegate()
      {
         @Override
         public void processInvocation(PortletInvocation invocation)
         {
            ExtensionAccess.getConsumerExtensionAccessor().addRequestExtension(MarkupParams.class, ExtensionMarkupBehavior.EXPECTED_REQUEST_EXTENSION_VALUE);
         }

         @Override
         public void processInvocationResponse(PortletInvocationResponse response, PortletInvocation invocation)
         {
            final List<UnmarshalledExtension> extensions = ExtensionAccess.getConsumerExtensionAccessor().getResponseExtensionsFrom(MarkupResponse.class);
            assertEquals(1, extensions.size());
            assertEquals(ExtensionMarkupBehavior.EXPECTED_RESPONSE_EXTENSION_VALUE, extensions.get(0).getValue());
         }
      });

      checkRenderResult(consumer.invoke(createRenderInvocation(ExtensionMarkupBehavior.PORTLET_HANDLE)), ExtensionMarkupBehavior.SIMPLE_SUCCESS);
   }

   public void testRenderWithElementExtensions() throws PortletInvokerException
   {
      // Register delegate sending an Element as an extension to MarkupParams and expects to retrieve an element as an extension of MarkupResponse
      InvocationHandlerDelegate.registerConsumerDelegate(new InvocationHandlerDelegate()
      {
         @Override
         public void processInvocation(PortletInvocation invocation)
         {
            /*
            <ext1:MarkupRequestState xmlns:ext1='urn:bea:wsrp:ext:v1:types'>
                              <ext1:state>
                                           foo
                              </ext1:state>
                     </ext1:MarkupRequestState>
             */
            final String namespaceURI = "urn:bea:wsrp:ext:v1:types";
            Element extension = PayloadUtils.createElement(namespaceURI, "MarkupRequestState");
            Node state = extension.getOwnerDocument().createElementNS(namespaceURI, "state");
            state = extension.appendChild(state);
            state.setTextContent(ExtensionMarkupBehavior.EXPECTED_REQUEST_EXTENSION_VALUE);

//            System.out.println("extension = " + PayloadUtils.outputToXML(extension));

            ExtensionAccess.getConsumerExtensionAccessor().addRequestExtension(MarkupParams.class, extension);
         }

         @Override
         public void processInvocationResponse(PortletInvocationResponse response, PortletInvocation invocation)
         {
            final List<UnmarshalledExtension> extensions = ExtensionAccess.getConsumerExtensionAccessor().getResponseExtensionsFrom(MarkupResponse.class);
            assertEquals(1, extensions.size());
            final UnmarshalledExtension unmarshalledExtension = extensions.get(0);
            assertTrue(unmarshalledExtension.isElement());
            Element element = (Element)unmarshalledExtension.getValue();
            assertEquals(ExtensionMarkupBehavior.EXPECTED_RESPONSE_EXTENSION_VALUE, element.getTextContent());
         }
      });

      checkRenderResult(consumer.invoke(createRenderInvocation(ExtensionMarkupBehavior.PORTLET_HANDLE)), ExtensionMarkupBehavior.ELEMENT_SUCCESS);
   }


   public void testAction() throws Exception
   {
      ActionInvocation action = createActionInvocation(BasicMarkupBehavior.PORTLET_HANDLE);

      PortletInvocationResponse response = consumer.invoke(action);
      ExtendedAssert.assertNotNull(response);
      ExtendedAssert.assertTrue("Was expecting a RenderResponse. Got: " + response, response instanceof UpdateNavigationalStateResponse);
      UpdateNavigationalStateResponse render = (UpdateNavigationalStateResponse)response;
      ExtendedAssert.assertEquals(BasicMarkupBehavior.NS, render.getNavigationalState().getStringValue());
   }

   public void testSessionHandling() throws Exception
   {
      RenderInvocation render = createRenderInvocation(SessionMarkupBehavior.PORTLET_HANDLE);

      PortletInvocationResponse response = consumer.invoke(render);

      checkRenderResult(response, "portlet2:0:view:maximized");

      // checking session information
      ProducerSessionInformation sessionInfo = consumer.getProducerSessionInformationFrom(render);
      String sessionId = sessionInfo.getSessionIdForPortlet(SessionMarkupBehavior.PORTLET_HANDLE);
      ExtendedAssert.assertNotNull(sessionId);
      ExtendedAssert.assertEquals(SessionMarkupBehavior.SESSION_ID, sessionId);
      ExtendedAssert.assertFalse(sessionInfo.isPerGroupCookies());
      ExtendedAssert.assertFalse(sessionInfo.isInitCookieDone());

      response = consumer.invoke(render);
      checkRenderResult(response, "portlet2:1:view:maximized");
   }

   public void testInitCookieNotCalledWhenNotNeeded() throws Exception
   {
      String handle = InitCookieNotRequiredMarkupBehavior.INIT_COOKIE_NOT_REQUIRED_HANDLE;
      InitCookieMarkupBehavior behavior = (InitCookieMarkupBehavior)producer.getBehaviorRegistry().getMarkupBehaviorFor(handle);

      // this test requires that the consumer refreshes which is not the case with the setUp, so force refresh
      consumer.getProducerInfo().setExpirationCacheSeconds(0);
      ExtendedAssert.assertTrue(consumer.getProducerInfo().isRefreshNeeded(true));

      ProducerSessionInformation sessionInfo = commonInitCookieTest(handle, behavior, CookieProtocol.NONE.value());

      ExtendedAssert.assertNotNull(sessionInfo);
      ExtendedAssert.assertFalse(sessionInfo.isPerGroupCookies());
      ExtendedAssert.assertFalse(sessionInfo.isInitCookieDone());

      ExtendedAssert.assertEquals(0, behavior.getInitCookieCallCount());
   }

   public void testInitCookiePerUser() throws PortletInvokerException, InvalidHandle
   {
      String handle = PerUserInitCookieMarkupBehavior.PER_USER_INIT_COOKIE_HANDLE;
      InitCookieMarkupBehavior behavior = (InitCookieMarkupBehavior)producer.getBehaviorRegistry().getMarkupBehaviorFor(handle);

      ProducerSessionInformation sessionInfo = commonInitCookieTest(handle, behavior, CookieProtocol.PER_USER.value());

      ExtendedAssert.assertFalse(sessionInfo.isPerGroupCookies());
      ExtendedAssert.assertTrue(sessionInfo.isInitCookieDone());

      ExtendedAssert.assertNotNull(sessionInfo.getUserCookie());

      ExtendedAssert.assertEquals(1, behavior.getInitCookieCallCount());
   }

   public void testInitCookiePerGroup() throws PortletInvokerException, OperationFailed, ResourceSuspended, ModifyRegistrationRequired, InvalidRegistration, InvalidHandle
   {
      BehaviorRegistry registry = producer.getBehaviorRegistry();

      // need to setup with a specific service description behavior: we wrap the current service description
      Holder<List<PortletDescription>> offeredPortlets = new Holder<List<PortletDescription>>();
      registry.getServiceDescriptionBehavior().getServiceDescription(null, null, null, null, new Holder<Boolean>(),
         offeredPortlets, new Holder<List<ItemDescription>>(),
         null, new Holder<List<ItemDescription>>(), new Holder<List<ItemDescription>>(), new Holder<CookieProtocol>(), new Holder<ModelDescription>(), new Holder<List<String>>(),
         new Holder<ResourceList>(), new Holder<List<EventDescription>>(), new Holder<ModelTypes>(), new Holder<List<String>>(), new Holder<ExportDescription>(), new Holder<Boolean>(), new Holder<List<Extension>>());
      setServiceDescriptionBehavior(new GroupedPortletsServiceDescriptionBehavior(offeredPortlets.value));

      String handle = PerGroupInitCookieMarkupBehavior.PER_GROUP_INIT_COOKIE_HANDLE;
      InitCookieMarkupBehavior behavior = (InitCookieMarkupBehavior)registry.getMarkupBehaviorFor(handle);

      ProducerSessionInformation sessionInfo = commonInitCookieTest(handle, behavior, CookieProtocol.PER_GROUP.value());
      ExtendedAssert.assertTrue(sessionInfo.isPerGroupCookies());
      ExtendedAssert.assertTrue(sessionInfo.isInitCookieDone());
      ExtendedAssert.assertNull(sessionInfo.getUserCookie());

      ExtendedAssert.assertEquals(3, behavior.getInitCookieCallCount());
   }

   public void testResource() throws PortletInvokerException, MalformedURLException
   {
      RenderInvocation render = createRenderInvocation(ResourceMarkupBehavior.PORTLET_HANDLE);
      PortletInvocationResponse response = consumer.invoke(render);

      String resourceID = WSRPResourceURL.encodeResource(null, new URL("http://localhost:8080/test-resource-portlet/gif/logo.gif"), false);
      String expectedResult = "<img src='http://test/mock:type=resource?mock:ComponentID=foobar&amp;mock:resourceID=" + resourceID + "'/>";
      ;

      //NOTE: the value we get back is from the TestPortletInvocationContext, not what we would normally receive
      checkRenderResult(response, expectedResult);
   }

   private ProducerSessionInformation commonInitCookieTest(String handle, InitCookieMarkupBehavior behavior, String cookieProtocol)
      throws PortletInvokerException
   {
      RenderInvocation render = createRenderInvocation(handle);
      TestPortletInvocationContext invocationContext = (TestPortletInvocationContext)render.getContext();
      HttpSession session = invocationContext.getClientRequest().getSession();

      // set init cookie requirement
      producer.setRequiresInitCookie(CookieProtocol.fromValue(cookieProtocol));

      // Force ProducerInfo refresh so that we make sure that the consumer knows about the new CookieProtocol
      consumer.refreshProducerInfo();

      // tell the producer which markup behavior we want to use
      producer.setCurrentMarkupBehaviorHandle(handle);

      render = createRenderInvocation(handle, invocationContext);

      ExtendedAssert.assertEquals(0, behavior.getInitCookieCallCount());

      consumer.invoke(render);

      ExtendedAssert.assertEquals(cookieProtocol, consumer.getProducerInfo().getRequiresInitCookie().value());

      return consumer.getProducerSessionInformationFrom(session);
   }

   private FragmentResponse checkRenderResult(PortletInvocationResponse response, String markup)
   {
      ExtendedAssert.assertNotNull(response);
      if (response instanceof ErrorResponse)
      {
         ErrorResponse errorResponse = (ErrorResponse)response;
         ExtendedAssert.fail("Got an ErrorResponse instead of a FragmentResponse. Message: " + errorResponse.getMessage());
      }
      ExtendedAssert.assertTrue("Was expecting a FragmentResponse. Got: " + response, response instanceof FragmentResponse);
      FragmentResponse fragment = (FragmentResponse)response;
      ExtendedAssert.assertEquals(markup, fragment.getChars().toString());
      return fragment;
   }

   private RenderInvocation createRenderInvocation(String portletHandle)
   {
      return createRenderInvocation(portletHandle, null);
   }

   private RenderInvocation createRenderInvocation(String portletHandle, TestPortletInvocationContext invocationContext)
   {
      return createRenderInvocation(portletHandle, Mode.VIEW, WindowState.MAXIMIZED, null, invocationContext);
   }

   private RenderInvocation createRenderInvocation(String portletHandle, Mode mode, WindowState state, String navigationalState)
   {
      return createRenderInvocation(portletHandle, mode, state, navigationalState, null);
   }

   private RenderInvocation createRenderInvocation(String portletHandle, Mode mode, WindowState state, String navigationalState, TestPortletInvocationContext invocationContext)
   {
      if (invocationContext == null)
      {
         invocationContext = new TestPortletInvocationContext();
      }

      RenderInvocation render = new RenderInvocation(invocationContext);
      render.setTarget(PortletContext.createPortletContext(portletHandle, false));
      render.setMode(mode);
      render.setWindowState(state);
      if (navigationalState != null)
      {
         render.setNavigationalState(new OpaqueStateString(navigationalState));
      }

      render.setInstanceContext(new AbstractInstanceContext(portletHandle));
      render.setSecurityContext(new AbstractSecurityContext(MockHttpServletRequest.createMockRequest(null)));
      render.setUserContext(new MockUserContext());
      render.setWindowContext(new AbstractWindowContext("windowcontext"));
      render.setPortalContext(new AbstractPortalContext());

      RequestedMarkupBehavior.setRequestedMarkupBehavior(portletHandle);

      return render;
   }

   private ActionInvocation createActionInvocation(String portletHandle)
   {
      TestPortletInvocationContext ac = new TestPortletInvocationContext();
      ActionInvocation action = new ActionInvocation(ac);
      action.setInstanceContext(new AbstractInstanceContext(portletHandle));
      action.setSecurityContext(new AbstractSecurityContext(MockHttpServletRequest.createMockRequest(null)));
      action.setUserContext(new MockUserContext());
      action.setTarget(PortletContext.createPortletContext(portletHandle, false));

      RequestedMarkupBehavior.setRequestedMarkupBehavior(portletHandle);

      return action;
   }

   static class MockUserContext extends AbstractUserContext
   {
      @Override
      public List<Locale> getLocales()
      {
         return Collections.singletonList(Locale.ENGLISH);
      }
   }
}
