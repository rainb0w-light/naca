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
		public double dLng = 0 ;
		public double dLat = 0 ;
	}
	private double dCenterLat = 0 ;
	private double dCenterLng = 0 ;
	private int nZoom = 0 ;
	
	public double getCenterLng()
	{
		return dCenterLng ;
	}
	public void setCenterLng(double d)
	{
		dCenterLng = d ;
	}
	public double getCenterLat()
	{
		return dCenterLat ;
	}
	public void setCenterLat(double d)
	{
		dCenterLat = d ;
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
		dCenterLat = lat ;
		dCenterLng = lng ;
	}
	public void AddMarker(String label, double lat, double lng)
	{
		MapMarker mark = new MapMarker() ;
		mark.csLabel = label ;
		mark.dLat = lat ;
		mark.dLng = lng ;
		arrMarkers.add(mark) ;
	}
	protected Vector<MapMarker> arrMarkers = new Vector<MapMarker>() ;

	public int getNbmarkers()
	{
		return arrMarkers.size() ;
	}
	public MapMarker getMarker(int i)
	{
		return arrMarkers.get(i) ;
	}
}
