package com.sdi.castivate.model;

/**
 * Created by Balachandar on 20-Apr-15.
 */
/** Holds Casting data. */
public class TotalFavModel {


	public FavImagesModel imagesModel;
	public CastingDetailsModel castingModel;
	
	public boolean imageShow;
	public boolean castingShow;
	
	
	public TotalFavModel( CastingDetailsModel castingModel,FavImagesModel imagesModel, boolean imageShow, boolean castingShow) {
		super();
		this.imagesModel = imagesModel;
		this.castingModel = castingModel;
		this.imageShow = imageShow;
		this.castingShow = castingShow;
	}

	

}
