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

package org.gatein.wsrp.consumer;

import org.gatein.common.net.URLTools;
import org.gatein.common.util.Tools;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.api.cache.CacheScope;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.FragmentResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.PortletInvocationContext;
import org.gatein.wsrp.WSRPConstants;
import org.gatein.wsrp.WSRPConsumer;
import org.gatein.wsrp.WSRPPortletURL;
import org.gatein.wsrp.WSRPResourceURL;
import org.gatein.wsrp.WSRPRewritingConstants;
import org.gatein.wsrp.WSRPTypeFactory;
import org.oasis.wsrp.v1.CacheControl;
import org.oasis.wsrp.v1.Extension;
import org.oasis.wsrp.v1.GetMarkup;
import org.oasis.wsrp.v1.MarkupContext;
import org.oasis.wsrp.v1.MarkupResponse;
import org.oasis.wsrp.v1.PortletContext;
import org.oasis.wsrp.v1.RuntimeContext;
import org.oasis.wsrp.v1.SessionContext;
import org.oasis.wsrp.v1.UserContext;

import javax.xml.ws.Holder;
import java.util.List;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 12082 $
 * @since 2.4 (May 31, 2006)
 */
public class RenderHandler extends InvocationHandler
{

   /** The separator constant. */
   private static final String SEPARATOR = "_";

   private static final org.gatein.pc.api.cache.CacheControl DEFAULT_CACHE_CONTROL = new org.gatein.pc.api.cache.CacheControl(0, CacheScope.PRIVATE, null);

   public RenderHandler(WSRPConsumerImpl consumer)
   {
      super(consumer);
   }

   protected Object prepareRequest(RequestPrecursor requestPrecursor, PortletInvocation invocation)
   {
      if (!(invocation instanceof RenderInvocation))
      {
         throw new IllegalArgumentException("RenderHandler can only handle RenderInvocations!");
      }

      // Create the markup request
      PortletContext portletContext = requestPrecursor.getPortletContext();
      log.debug("Consumer about to attempt rendering portlet '" + portletContext.getPortletHandle() + "'");
      return WSRPTypeFactory.createMarkupRequest(portletContext, requestPrecursor.runtimeContext, requestPrecursor.markupParams);
   }

   protected PortletInvocationResponse processResponse(Object response, PortletInvocation invocation, RequestPrecursor requestPrecursor)
   {
      MarkupResponse markupResponse = (MarkupResponse)response;
      log.debug("Starting processing response");

      // process the response
      consumer.getSessionHandler().updateSessionIfNeeded(markupResponse.getSessionContext(), invocation,
         requestPrecursor.getPortletHandle());

      MarkupContext markupContext = markupResponse.getMarkupContext();
      String markup = markupContext.getMarkupString();
      byte[] binary = markupContext.getMarkupBinary();
      if (markup != null && binary != null)
      {
         return new ErrorResponse(new IllegalArgumentException("Markup response cannot contain both string and binary " +
            "markup. Per Section 6.1.10 of the WSRP specification, this is a Producer error."));
      }

      if (markup == null && binary == null)
      {
         if (markupContext.isUseCachedMarkup())
         {
            //todo: deal with cache
         }
         else
         {
            return new ErrorResponse(new IllegalArgumentException("Markup response must contain at least string or binary" +
               " markup. Per Section 6.1.10 of the WSRP specification, this is a Producer error."));
         }
      }

      if (markup != null && markup.length() > 0)
      {
         markup = processMarkup(markup, invocation, Boolean.TRUE.equals(markupContext.isRequiresUrlRewriting()));
      }
      else
      {
         // todo: need to deal with binary
      }

      String mimeType = markupContext.getMimeType();
      if (mimeType == null || mimeType.length() == 0)
      {
         return new ErrorResponse(new IllegalArgumentException("No MIME type was provided for portlet content."));
      }

      // generate appropriate CacheControl
      org.gatein.pc.api.cache.CacheControl cacheControl = createCacheControl(markupContext);

      FragmentResponse result = new FragmentResponse(null, null, mimeType, null, markup,
         markupContext.getPreferredTitle(), cacheControl, invocation.getPortalContext().getModes());

      log.debug("Response processed");
      return result;
   }

   protected void updateUserContext(Object request, UserContext userContext)
   {
      getRenderRequest(request).setUserContext(userContext);
   }

   protected void updateRegistrationContext(Object request) throws PortletInvokerException
   {
      getRenderRequest(request).setRegistrationContext(consumer.getRegistrationContext());
   }

   protected RuntimeContext getRuntimeContextFrom(Object request)
   {
      return getRenderRequest(request).getRuntimeContext();
   }

   protected Object performRequest(Object request) throws Exception
   {
      GetMarkup renderRequest = getRenderRequest(request);
      log.debug("getMarkup on '" + renderRequest.getPortletContext().getPortletHandle() + "'");

      // invocation
      Holder<SessionContext> sessionContextHolder = new Holder<SessionContext>();
      Holder<MarkupContext> markupContextHolder = new Holder<MarkupContext>();
      consumer.getMarkupService().getMarkup(renderRequest.getRegistrationContext(), renderRequest.getPortletContext(),
         renderRequest.getRuntimeContext(), renderRequest.getUserContext(), renderRequest.getMarkupParams(),
         markupContextHolder, sessionContextHolder, new Holder<List<Extension>>());
      MarkupResponse markupResponse = new MarkupResponse();
      markupResponse.setMarkupContext(markupContextHolder.value);
      markupResponse.setSessionContext(sessionContextHolder.value);
      return markupResponse;
   }

   private GetMarkup getRenderRequest(Object request)
   {
      if (request instanceof GetMarkup)
      {
         return (GetMarkup)request;
      }

      throw new IllegalArgumentException("RenderHandler: Request is not a GetMarkup request!");
   }

   private String processMarkup(String markup, PortletInvocation invocation, boolean rewriteURLs)
   {
      // fix-me: how to deal with fragment header? => interceptor?
      String prefix = getNamespaceFrom(invocation.getWindowContext());

      markup = Tools.replace(markup, WSRPRewritingConstants.WSRP_REWRITE_TOKEN, prefix);

      if (rewriteURLs)
      {
         URLFormat format = new URLFormat(invocation.getSecurityContext().isSecure(),
            invocation.getSecurityContext().isAuthenticated(), true, true);
         WSRPURLRewriter rewriter = new WSRPURLRewriter(invocation.getContext(), format, consumer);
         markup = URLTools.replaceURLsBy(markup, rewriter);
      }

      // means that the producer generated the URLs, so handle resources...
      ResourceURLRewriter rewriter = new ResourceURLRewriter();
      return URLTools.replaceURLsBy(markup, rewriter);
   }

   private org.gatein.pc.api.cache.CacheControl createCacheControl(MarkupContext markupContext)
   {
      CacheControl cacheControl = markupContext.getCacheControl();
      org.gatein.pc.api.cache.CacheControl result = DEFAULT_CACHE_CONTROL;

      int expires;
      if (cacheControl != null)
      {
         expires = cacheControl.getExpires();
         String userScope = cacheControl.getUserScope();

         // check that we support the user scope...
         if (consumer.supportsUserScope(userScope))
         {
            log.debug("RenderHandler.processRenderRequest: trying to cache markup " + userScope + " for " + expires + " seconds.");
            CacheScope scope;
            if (WSRPConstants.CACHE_FOR_ALL.equals(userScope))
            {
               scope = CacheScope.PUBLIC;
            }
            else if (WSRPConstants.CACHE_PER_USER.equals(userScope))
            {
               scope = CacheScope.PRIVATE;
            }
            else
            {
               throw new IllegalArgumentException("Unknown CacheControl user scope: " + userScope); // should not happen
            }

            result = new org.gatein.pc.api.cache.CacheControl(expires, scope, cacheControl.getValidateTag());
         }
      }

      return result;
   }

   private static class WSRPURLRewriter extends URLTools.URLReplacementGenerator
   {
      private PortletInvocationContext context;
      private URLFormat format;
      private WSRPConsumer consumer;
      private static final String SLASH = "/";

      private WSRPURLRewriter(PortletInvocationContext context, URLFormat format, WSRPConsumer consumer)
      {
         this.context = context;
         this.format = format;
         this.consumer = consumer;
      }

      public String getReplacementFor(int currentIndex, URLTools.URLMatch currentMatch)
      {
         String urlAsString = currentMatch.getURLAsString();
         ProducerInfo info = consumer.getProducerInfo();
         if (urlAsString.startsWith(WSRPRewritingConstants.BEGIN_WSRP_REWRITE))
         {
            WSRPPortletURL portletURL = WSRPPortletURL.create(urlAsString,
               info.getSupportedCustomModes(), info.getSupportedCustomWindowStates());
            if (portletURL instanceof WSRPResourceURL)
            {
               log.debug("URL '" + urlAsString + "' seems to refer to a resource which are not currently supported. " +
                  "Trying to use the raw URL but this probably won't work...");
               return portletURL.toString();
            }

            // todo: this is a hack to circumvent frameworks that don't properly request resource encoding (icefaces)
            if (urlAsString.startsWith(SLASH))
            {
               return info.getEndpointConfigurationInfo().getRemoteHostAddress() + urlAsString;
            }

            return context.renderURL(portletURL, format);
         }
         return urlAsString;
      }
   }

   // @todo: public for tests
   public static class ResourceURLRewriter extends URLTools.URLReplacementGenerator
   {
      public String getReplacementFor(int currentIndex, URLTools.URLMatch currentMatch)
      {
         String urlAsString = currentMatch.getURLAsString();
         String prefix = WSRPRewritingConstants.FAKE_RESOURCE_START;
         if (urlAsString.startsWith(prefix))
         {
            int index = urlAsString.indexOf(WSRPRewritingConstants.FAKE_RESOURCE_REQ_REW);
            String requireRewriteStr = urlAsString.substring(index + WSRPRewritingConstants.FAKE_RESOURCE_REQ_REW.length());
            boolean requireRewrite = Boolean.valueOf(requireRewriteStr);

            urlAsString = urlAsString.substring(prefix.length(), index);
            if (requireRewrite)
            {
               // FIX-ME: do something
               log.debug("Required re-writing but this is not yet implemented...");
            }
            return URLTools.decodeXWWWFormURL(urlAsString);
         }

         return urlAsString;
      }
   }
}
