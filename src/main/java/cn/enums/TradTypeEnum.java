package cn.enums;

public enum TradTypeEnum {
	JSAPI("JSAPI", "公众号支付"), NATIVE("NATIVE", "原生支付"), APP("APP", "APP支付"), MWEB("MWEB", "H5支付"), MICROPAY("MICROPAY",
			"付款码支付");
	TradTypeEnum(String code, String name) {
	}

	private String code;

	private String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
