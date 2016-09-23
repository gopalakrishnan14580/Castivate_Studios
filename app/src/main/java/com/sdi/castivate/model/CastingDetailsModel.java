package com.sdi.castivate.model;

/**
 * Created by Balachandar on 20-Apr-15.
 */

import java.io.Serializable;

/** Holds Casting data. */
public class CastingDetailsModel implements Serializable{

/*{
  "enthicity": "Caucasian",
  "age_range": "18-30\r\n",
  "role_for": "Male",
  "fav_flag": "1",
  "apply_flag": "1",
  "role_id": "6142",
  "casting_title": "Haunted Love",
  "casting_type": "Reality TV",
  "casting_paid_status": "Paid",
  "casting_union_status": "",
  "role_desc": "Seeking people still in love with an ex or with regret in their past. 25-45 years old.",
  "casting_union_type": "",
  "casting_submission_detail": "realitytalent@realtvcasting.com \r\nEmail if interested. Full req: http://bit.ly/2atdNLO",
  "casting_synopsis": "Seeking real people for a new docu-series.",
  "role_type": "Actor,Singer,Dancer,Model",
  "ageRange": "18-30\r\n",
  "role_ethnicity": "Caucasian",
  "casting_state": "",
  "casting_city": "",
  "casting_email": "realitytalent@realtvcasting.com"
}*/
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
	public String castingEmail;
	public String applyFlag;
	public CastingDetailsModel(String roleId, String castingTitle,
			String castingType, String castingPaidStatus,
			String castingUnionStatus, String castingUnionType,
			String castingSubmissionDetail, String castingSynopsis,
			String imageRole, String roleDescription, String ageRange,
			String roleForGender, String roleForEthnicity, String favCasting,
			String favCount, String distance, String totalCasting,
			String state, String country,String castingEmail,String applyFlag ) {
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
		this.castingEmail =castingEmail;
		this.applyFlag=applyFlag;
	}

	

}
