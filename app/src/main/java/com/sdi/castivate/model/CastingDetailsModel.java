package com.sdi.castivate.model;

/**
 * Created by Balachandar on 20-Apr-15.
 */
/** Holds Casting data. */
public class CastingDetailsModel {


	public String roleId;
	public String castingTitle;
	public String castingType;
	public String castingPaidStatus;
	public String castingUnionStatus;
	public String castingUnionType;
	public String castingSubmissionDetail;
	public String castingSynopsis;
	public String imageRole;
	public String roleDescription;
	public String ageRange;
	public String roleForGender;
	public String roleForEthnicity;
	public String favCasting;
	public String favCount;
	public String distance;
	public String totalCasting;
  
	
	public String state;
	public String country;
	public CastingDetailsModel(String roleId, String castingTitle,
			String castingType, String castingPaidStatus,
			String castingUnionStatus, String castingUnionType,
			String castingSubmissionDetail, String castingSynopsis,
			String imageRole, String roleDescription, String ageRange,
			String roleForGender, String roleForEthnicity, String favCasting,
			String favCount, String distance, String totalCasting,
			String state, String country) {
		super();
		this.roleId = roleId;
		this.castingTitle = castingTitle;
		this.castingType = castingType;
		this.castingPaidStatus = castingPaidStatus;
		this.castingUnionStatus = castingUnionStatus;
		this.castingUnionType = castingUnionType;
		this.castingSubmissionDetail = castingSubmissionDetail;
		this.castingSynopsis = castingSynopsis;
		this.imageRole = imageRole;
		this.roleDescription = roleDescription;
		this.ageRange = ageRange;
		this.roleForGender = roleForGender;
		this.roleForEthnicity = roleForEthnicity;
		this.favCasting = favCasting;
		this.favCount = favCount;
		this.distance = distance;
		this.totalCasting = totalCasting;
		this.state = state;
		this.country = country;
	}

	

}
