package cn;

/**退款状态
 * @author guoxs
 *
 */
public enum RefundEnum {
	SUCCESS("SUCCESS", "退款成功"),
	CHANGE("CHANGE", "退款异常"),
	REFUNDCLOSE("REFUNDCLOSE", "退款关闭"),
	REFUNDING("REFUNDING", "退款中");

	RefundEnum(String code, String name) {
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
