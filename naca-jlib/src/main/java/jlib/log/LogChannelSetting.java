/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;
/**
 * Class containing the default settings for one channel.
 * The default settings are merged with the event settings before sending
 * the event to the {@link LogCenter}.
 */
public class LogChannelSetting
{
	LogChannelSetting()
	{
	}
/**
 * Returns the default <i>RunId</i> identifier for the channel.
 * @return The default <i>RunId</i> identifier for the channel.
 * @see {@link Log} for a discussion about the meaning of the <i>RunId</i>,
 * <i>RuntimeId</i>, and <i>Product</i> identifiers.
 */	
	String getRunId()
	{
		return csRunId;
	}
/**
 * Sets the default <i>RunId</i> for the channel. 
 * @param csRunId Default <i>RunId</i> to be used if events sent to a channel
 * have no <i>RunId</i> specified.
 * @see {@link Log} for a discussion about the meaning of the <i>RunId</i>,
 * <i>RuntimeId</i>, and <i>Product</i> identifiers.
 */
	void setRunId(String csRunId)
	{
		csRunId = csRunId;
	}

/**
 * Sets the default <i>RuntimeId</i> for the channel. 
 * @param csRuntimeId Default <i>RuntimeId</i> to be used if events sent to a channel
 * have no <i>RuntimeId</i> specified.
 * @see {@link Log} for a discussion about the meaning of the <i>RunId</i>,
 * <i>RuntimeId</i>, and <i>Product</i> identifiers.
 */
	void setRuntimeId(String csRuntimeId)
	{
		csRuntimeId = csRuntimeId;
	}

/**
 * Returns the default <i>RuntimeId</i> identifier for the channel.
 * @return The default <i>RuntimeId</i> identifier for the channel.
 * @see {@link Log} for a discussion about the meaning of the <i>RunId</i>,
 * <i>RuntimeId</i>, and <i>Product</i> identifiers.
 */	
	String getRuntimeId()
	{
		return csRuntimeId;
	}

/**
 * Returns the default <i>RuntimeId</i> identifier for the channel.
 * @return The default <i>RuntimeId</i> identifier for the channel.
 * @see {@link Log} for a discussion about the meaning of the <i>RunId</i>,
 * <i>RuntimeId</i>, and <i>Product</i> identifiers.
 */		
	String getProduct()
	{
		return csProduct;
	}
	
/**
 * Sets the default <i>Product</i> identifier for the channel.
 * @param csProduct The default <i>Product</i> identifier for the channel.
 * @see {@link Log} for a discussion about the meaning of the <i>RunId</i>,
 * <i>RuntimeId</i>, and <i>Product</i> identifiers.
 */		
	void setProduct(String csProduct)
	{
		csProduct = csProduct;
	}
	
	private String csRunId;		// manual or generated runtime id
	private String csRuntimeId;	// always generated runtime id
	private String csProduct;
}
