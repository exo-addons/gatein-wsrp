
package org.oasis.wsrp.v2;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebFault(name = "InvalidSession", targetNamespace = "urn:oasis:names:tc:wsrp:v2:types")
public class InvalidSession
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private InvalidSessionFault faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public InvalidSession(String message, InvalidSessionFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public InvalidSession(String message, InvalidSessionFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.oasis.wsrp.v2.InvalidSessionFault
     */
    public InvalidSessionFault getFaultInfo() {
        return faultInfo;
    }

}
