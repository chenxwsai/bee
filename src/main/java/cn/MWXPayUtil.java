package cn;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;

/**
 * 支付模块入口
 * 
 * @author guoxs
 *
 */
public class MWXPayUtil {
	private WXPayConfigImpl config;
	private WXPay wxpay;

	public MWXPayUtil() {
		try {
			config = WXPayConfigImpl.getInstance();
			wxpay = new WXPay(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信统一下单接口
	 *
	 * @param orderNo
	 *            商户订单号
	 * @param amount
	 *            金额
	 * @param describe
	 *            商品描述
	 * @param UserIp
	 *            用户端实际ip
	 * @param orderType
	 *            订单附加信息 (业务需要数据，自定义的)
	 * @return 返回整理的数据模型
	 */
	public WXOrderResponse order(String orderNo, Integer amount, String describe, String UserIp, String orderType) {
		HashMap<String, String> data = new HashMap<>();
		data.put("body", describe);
		data.put("out_trade_no", orderNo);
		// data.put("device_info", "");//调用接口提交的终端设备号
		data.put("fee_type", "CNY");
		data.put("total_fee", amount + "");
		data.put("spbill_create_ip", UserIp);
		data.put("notify_url", config.NOTIFY_URL);
		data.put("trade_type", TradTypeEnum.JSAPI.getCode());// 支付类型
		data.put("attach", orderType);// 订单附加信息 (业务需要数据，自定义的)
		Map<String, String> orderInfo;
		try {
			orderInfo = wxpay.unifiedOrder(data);
			System.out.println(orderInfo);
			if (orderInfo == null)
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return parseWXOrderResponse(orderInfo);
	}

	/**
	 * 提现
	 * 
	 *
	 * @param orderNo
	 *            商户订单号
	 * @param amount
	 *            金额
	 * @param describe
	 *            商品描述
	 * @param UserIp
	 *            用户端实际ip
	 * @param orderType
	 *            订单附加信息 (业务需要数据，自定义的)
	 * @return 返回整理的数据模型
	 */
	public WXOrderResponse rePay(String orderNo, Integer amount, String describe, String UserIp, String orderType) {
		HashMap<String, String> data = new HashMap<>();
		data.put("check_name", "FORCE_CHECK");// NO_CHECK：不校验真实姓名
												// FORCE_CHECK：强校验真实姓名
		/**
		 * 收款用户真实姓名。 如果check_name设置为FORCE_CHECK，则必填用户真实姓名
		 */
		data.put("re_user_name", "FORCE_CHECK");
		data.put("out_trade_no", orderNo);
		// data.put("device_info", "");//调用接口提交的终端设备号
		data.put("fee_type", "CNY");
		data.put("total_fee", amount + "");
		data.put("spbill_create_ip", UserIp);
		// data.put("spbill_create_ip", "123.12.12.123");
		data.put("notify_url", config.NOTIFY_URL);
		data.put("trade_type", TradTypeEnum.JSAPI.getCode());// 支付类型
		data.put("attach", orderType);// 订单附加信息
		// data.put("product_id", "12");
		SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		data.put("time_start", yyyyMMddHHmmss.format(date));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, 30);
		data.put("time_expire", yyyyMMddHHmmss.format(calendar.getTime()));
		Map<String, String> orderInfo = null;
		try {
			// orderInfo = wxpay.repay(data);
			System.out.println(orderInfo);
			if (orderInfo == null)
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return parseWXOrderResponse(orderInfo);
	}

	/**
	 * 退款
	 *
	 * @param orderNo
	 *            商户订单id
	 * @param refundId
	 *            退款单id
	 * @param totalAmount
	 *            订单总金额
	 * @param refundAmount
	 *            退款金额
	 * @return 返回map（已做过签名验证），具体数据参见微信退款API
	 */
	public Map<String, String> doRefund(String orderNo, String refundId, Integer totalAmount, Integer refundAmount)
			throws Exception {
		HashMap<String, String> data = new HashMap<>();
		data.put("out_trade_no", orderNo);
		data.put("out_refund_no", refundId);

		data.put("total_fee", totalAmount + "");
		data.put("refund_fee", refundAmount + "");
		data.put("refund_fee_type", "CNY");
		data.put("op_user_id", config.getMchID());

		try {
			Map<String, String> r = wxpay.refund(data);
			System.out.println(r);
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, String> getWithdrawBankMap(String orderNo) throws Exception {
		Map<String, String> data = new HashMap<>();
		return data;
	}

	/**
	 * 查微信订单
	 *
	 * @param orderNo
	 */
	public WXOrderResponse queryOrder(String orderNo) {
		System.out.println("查询订单");
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("out_trade_no", orderNo);// 订单号
		// data.put("transaction_id", "");//微信端的预支付单号（二选一）
		try {
			Map<String, String> r = wxpay.orderQuery(data);
			System.out.println(r);
			return parseWXOrderResponse(r);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 查退款
	 *
	 * @param orderNo
	 */
	public WXOrderResponse queryRefund(String orderNo) {
		System.out.println("查询退款");
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("out_refund_no", orderNo);// 退单号
		try {
			Map<String, String> r = wxpay.refundQuery(data);
			System.out.println(r);
			return parseWXOrderResponse(r);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 撤销订单
	 *
	 * @param orderNo
	 * @return
	 */
	public Map<String, String> reverseOrder(String orderNo) {
		System.out.println("撤销");
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("out_trade_no", orderNo);
		// data.put("transaction_id", "");
		try {
			Map<String, String> r = wxpay.reverse(data);
			System.out.println(r);
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 关闭订单
	 *
	 * @param orderNo
	 * @return
	 */
	public Map<String, String> closeOrder(String orderNo) {
		System.out.println("关闭订单");
		HashMap<String, String> data = new HashMap<>();
		data.put("out_trade_no", orderNo);
		try {
			Map<String, String> r = wxpay.closeOrder(data);
			System.out.println(r);
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将map转成用户端用的封装体
	 *
	 * @param map
	 *            map
	 * @return 用户端用的封装体
	 */
	private WXOrderResponse parseWXOrderResponse(Map<String, String> map) {
		WXOrderResponse response = new WXOrderResponse();
		response.setAppid(map.get("appid"));
		response.setCode_url(map.get("code_url"));
		response.setMch_id(map.get("mch_id"));
		response.setNonce_str(map.get("nonce_str"));
		response.setPrepay_id(map.get("prepay_id"));
		response.setResult_code(map.get("result_code"));
		response.setReturn_msg(map.get("return_msg"));
		response.setReturn_code(map.get("return_code"));
		response.setPack("Sign=WXPay");
		// 坑 todo 超级坑
		// response.setSign(map.get("sign"));
		// String time = new Date().getTime() + "";
		String substring = System.currentTimeMillis() / 1000 + "";
		response.setTime(substring);
		// 坑！！！！！！！！！！
		// sgin（签名），不是拿微信返回的sgin，而是自己再签一次，返回给客户端
		// 注意：key不能是大写
		Map<String, String> params = new HashMap<>();
		params.put("appid", config.getAppID());
		params.put("partnerid", config.getMchID());
		params.put("prepayid", map.get("prepay_id"));
		params.put("package", "Sign=WXPay");
		params.put("noncestr", map.get("nonce_str"));
		params.put("timestamp", substring);
		try {
			String sgin = WXPayUtil.generateSignature(params, config.getKey());
			response.setSign(sgin);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		response.setErr_code_des(map.get("err_code_des"));
		return response;
	}

	/**
	 * 是否成功接收微信支付回调 用于回复微信，否则微信回默认为商户后端没有收到回调
	 *
	 * @return
	 */
	public String returnWXPayVerifyMsg() {
		return "<xml>\n" + "\n" + "  <return_code><![CDATA[SUCCESS]]></return_code>\n"
				+ "  <return_msg><![CDATA[OK]]></return_msg>\n" + "</xml>";
	}

	/**
	 * 通知结果为失败，回复没有收到回调
	 *
	 * @return
	 */
	public String returnWXPayVerifyMsgFail() {
		return "<xml>\n" + "\n" + "  <return_code><![CDATA[FAIL]]></return_code>\n"
				+ "  <return_msg><![CDATA[ERR]]></return_msg>\n" + "</xml>";
	}

	public WXPay getWxpay() {
		return wxpay;
	}
}
