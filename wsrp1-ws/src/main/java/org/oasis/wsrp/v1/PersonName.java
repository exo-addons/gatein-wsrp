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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for PersonName complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="PersonName">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="given" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="family" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="suffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nickname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extensions" type="{urn:oasis:names:tc:wsrp:v1:types}Extension" maxOccurs="unbounded"
 * minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonName", propOrder = {
   "prefix",
   "given",
   "family",
   "middle",
   "suffix",
   "nickname",
   "extensions"
})
public class PersonName
{

   protected String prefix;
   protected String given;
   protected String family;
   protected String middle;
   protected String suffix;
   protected String nickname;
   protected List<Extension> extensions;

   /**
    * Gets the value of the prefix property.
    *
    * @return possible object is {@link String }
    */
   public String getPrefix()
   {
      return prefix;
   }

   /**
    * Sets the value of the prefix property.
    *
    * @param value allowed object is {@link String }
    */
   public void setPrefix(String value)
   {
      this.prefix = value;
   }

   /**
    * Gets the value of the given property.
    *
    * @return possible object is {@link String }
    */
   public String getGiven()
   {
      return given;
   }

   /**
    * Sets the value of the given property.
    *
    * @param value allowed object is {@link String }
    */
   public void setGiven(String value)
   {
      this.given = value;
   }

   /**
    * Gets the value of the family property.
    *
    * @return possible object is {@link String }
    */
   public String getFamily()
   {
      return family;
   }

   /**
    * Sets the value of the family property.
    *
    * @param value allowed object is {@link String }
    */
   public void setFamily(String value)
   {
      this.family = value;
   }

   /**
    * Gets the value of the middle property.
    *
    * @return possible object is {@link String }
    */
   public String getMiddle()
   {
      return middle;
   }

   /**
    * Sets the value of the middle property.
    *
    * @param value allowed object is {@link String }
    */
   public void setMiddle(String value)
   {
      this.middle = value;
   }

   /**
    * Gets the value of the suffix property.
    *
    * @return possible object is {@link String }
    */
   public String getSuffix()
   {
      return suffix;
   }

   /**
    * Sets the value of the suffix property.
    *
    * @param value allowed object is {@link String }
    */
   public void setSuffix(String value)
   {
      this.suffix = value;
   }

   /**
    * Gets the value of the nickname property.
    *
    * @return possible object is {@link String }
    */
   public String getNickname()
   {
      return nickname;
   }

   /**
    * Sets the value of the nickname property.
    *
    * @param value allowed object is {@link String }
    */
   public void setNickname(String value)
   {
      this.nickname = value;
   }

   /**
    * Gets the value of the extensions property.
    * <p/>
    * <p/>
    * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
    * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
    * the extensions property.
    * <p/>
    * <p/>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getExtensions().add(newItem);
    * </pre>
    * <p/>
    * <p/>
    * <p/>
    * Objects of the following type(s) are allowed in the list {@link Extension }
    */
   public List<Extension> getExtensions()
   {
      if (extensions == null)
      {
         extensions = new ArrayList<Extension>();
      }
      return this.extensions;
   }

}
