
package com.wisedu.emap.itpub.bean.soap;

import com.wisedu.emap.itpub.bean.BaseRequest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for userAppRequest complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="userAppRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://app.ws.api.mdbservice.wisedu.com/}baseRequest">
 *       &lt;sequence>
 *         &lt;element name="appId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userAppRequest", propOrder = { "appId", "userId" })
public class UserAppRequest extends BaseRequest {

	protected String appId;
	protected String userId;

	/**
	 * Gets the value of the appId property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * Sets the value of the appId property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAppId(String value) {
		this.appId = value;
	}

	/**
	 * Gets the value of the userId property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the value of the userId property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUserId(String value) {
		this.userId = value;
	}

}
