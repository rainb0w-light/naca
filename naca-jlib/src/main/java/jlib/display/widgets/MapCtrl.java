/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.display.widgets;

import java.util.Vector;

public class MapCtrl
{
	public class MapMarker
	{
		public String csLabel = "" ;
		public double lng = 0 ;
		public double lat = 0 ;
	}
	private double centerLat = 0 ;
	private double centerLng = 0 ;
	private int nZoom = 0 ;
	
	public double getCenterLng()
	{
		return centerLng;
	}
	public void setCenterLng(double d)
	{
		centerLng = d ;
	}
	public double getCenterLat()
	{
		return centerLat;
	}
	public void setCenterLat(double d)
	{
		centerLat = d ;
	}
	/**
	 * @return Returns the zoom.
	 */
	public int getZoom()
	{
		return nZoom;
	}
	/**
	 * @param zoom The zoom to set.
	 */
	public void setZoom(int zoom)
	{
		nZoom = zoom;
	}
	public void setCenter(double lat, double lng)
	{
		centerLat = lat ;
		centerLng = lng ;
	}
	public void AddMarker(String label, double lat, double lng)
	{
		MapMarker mark = new MapMarker() ;
		mark.csLabel = label ;
		mark.lat = lat ;
		mark.lng = lng ;
		markers.add(mark) ;
	}
	protected Vector<MapMarker> markers = new Vector<MapMarker>() ;

	public int getNbmarkers()
	{
		return markers.size() ;
	}
	public MapMarker getMarker(int i)
	{
		return markers.get(i) ;
	}
}
