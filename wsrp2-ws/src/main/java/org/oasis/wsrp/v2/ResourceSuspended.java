
package org.oasis.wsrp.v2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebFault(name = "ResourceSuspended", targetNamespace = "urn:oasis:names:tc:wsrp:v2:types")
public class ResourceSuspended
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private ResourceSuspendedFault faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public ResourceSuspended(String message, ResourceSuspendedFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ResourceSuspended(String message, ResourceSuspendedFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.oasis.wsrp.v2.ResourceSuspendedFault
     */
    public ResourceSuspendedFault getFaultInfo() {
        return faultInfo;
    }

}
