package edu.bnu.vocabuary;

// List里面的结构体 ：包括属性值和属性名。

public class Mention {
	//mention 是属性值
	//attributesName 是属性名
	private String mention;

	private String attributesName ;

	public String getAttributesName() {
		return  attributesName;
	}
	public void setAttributesName(String attributesNamevalue) {
		this.attributesName = attributesNamevalue;
	}
	public String getmention() {
		return mention;
	}
	public void setmention(String mention) {
		this.mention = mention;
		
	}
}
