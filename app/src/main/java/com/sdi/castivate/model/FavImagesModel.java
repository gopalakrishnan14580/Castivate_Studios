package com.sdi.castivate.model;

/** Holds casting image url data */
public class FavImagesModel {

	public String image_id, casting_image, imgThumb,userName;

	public FavImagesModel(String image_id, String casting_image,String imgThumb,String userName) {
		super();
		this.image_id = image_id;
		this.casting_image = casting_image;
		this.imgThumb = imgThumb;
		this.userName = userName;
	}

	// image_id,casting_image

}
