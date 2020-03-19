package cn;

import java.util.Date;

/**退款单
 * @author guoxs
 *
 */
public class RefundBean {
	private String orderId;
	private String refundId;
	private Double refund_fee;
	private String state;
	private String cause;
	private Date time;
	private String notifyFlag;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public Double getRefund_fee() {
		return refund_fee;
	}

	public void setRefund_fee(Double refund_fee) {
		this.refund_fee = refund_fee;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getNotifyFlag() {
		return notifyFlag;
	}

	public void setNotifyFlag(String notifyFlag) {
		this.notifyFlag = notifyFlag;
	}

}
