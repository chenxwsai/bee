package cn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;

/**
 * 回调入口
 * 
 * @author guoxs
 *
 */
public class WXPayController extends BaseController {

	@Resource
	private MWXPayUtil mwxPayUtil;

	/**
	 * 支付通知集合
	 * 
	 */
	private static Map<String, String> callbackOrderMap = new HashMap<String, String>();

	/**
	 * 退款通知集合
	 * 
	 */
	private static Map<String, String> callbackRefundMap = new HashMap<String, String>();

	/**
	 * 微信统一下单接口
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/wxpay")
	@ResponseBody
	public WXOrderResponse wxPrePay(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	/**
	 * 异步回调接口
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/wx-pay/callback", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String WeixinParentNotifyPage(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 读取参数
			InputStream inputStream;
			StringBuffer sb = new StringBuffer();
			inputStream = request.getInputStream();
			String s;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			in.close();
			inputStream.close();
			// 解析xml成map
			Map<String, String> data = new HashMap<String, String>();
			data = WXPayUtil.xmlToMap(sb.toString());
			// 判断签名是否正确
			WXPay wxPay = mwxPayUtil.getWxpay();
			if (!wxPay.isPayResultNotifySignatureValid(data)) {
				// 验签失败
				return mwxPayUtil.returnWXPayVerifyMsg();
			}
			// 过滤空 设置 TreeMap
			SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
			Iterator it = data.keySet().iterator();
			while (it.hasNext()) {
				String parameter = (String) it.next();
				String parameterValue = data.get(parameter);

				String v = "";
				if (null != parameterValue) {
					v = parameterValue.trim();
				}
				packageParams.put(parameter, v);
			}

			if (packageParams.get("return_code").equals(WXPayConstants.SUCCESS)
					&& packageParams.get("result_code").equals(WXPayConstants.SUCCESS)) {
				String orderId = packageParams.get("out_trade_no").toString();
				// 防止重复通知
				if (callbackOrderMap.containsKey(orderId)) {
					return mwxPayUtil.returnWXPayVerifyMsg();
				}
				callbackOrderMap.put(orderId, orderId);

				// 查询订单是否已通知
				boolean noticeFlag = false;

				if (!noticeFlag) {
					// 更新订单支付状态、通知标识
					boolean updateFlag = true;

				}
				// 更新失败，但是返回给微信已接收到，后续定时任务查询状态
				return mwxPayUtil.returnWXPayVerifyMsg();
			} else {
				// 默认未通知，但是回复已通知，后续定时任务查询结果
				return mwxPayUtil.returnWXPayVerifyMsg();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mwxPayUtil.returnWXPayVerifyMsg();
	}

	/**
	 * 退款通知，异步回调接口
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/wx-refund/callback", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String WeixinParentNotifyPage(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 读取参数
			InputStream inputStream;
			StringBuffer sb = new StringBuffer();
			inputStream = request.getInputStream();
			String s;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			in.close();
			inputStream.close();
			// 解析xml成map
			Map<String, String> data = new HashMap<String, String>();
			data = WXPayUtil.xmlToMap(sb.toString());
			// 过滤空 设置 TreeMap
			SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
			Iterator it = data.keySet().iterator();
			while (it.hasNext()) {
				String parameter = (String) it.next();
				String parameterValue = data.get(parameter);

				String v = "";
				if (null != parameterValue) {
					v = parameterValue.trim();
				}
				packageParams.put(parameter, v);
			}

			if (packageParams.get("return_code").equals(WXPayConstants.SUCCESS)) {
				if (null == packageParams.get("req_info")) {
					return mwxPayUtil.returnWXPayVerifyMsg();
				}
				String reqInfo = packageParams.get("req_info").toString();// 加密信息
				String decInfo = new String(Base64.getMimeDecoder().decode(reqInfo));// base64解码
				String rs = AESUtil.decryptData(decInfo);

				Map map = WXPayUtil.xmlToMap(rs);

				String refundId = map.get("out_refund_no").toString();
				// 防止重复通知
				if (callbackRefundMap.containsKey(refundId)) {
					return mwxPayUtil.returnWXPayVerifyMsg();
				}
				callbackRefundMap.put(refundId, refundId);

				// 查询退款是否已通知
				boolean noticeFlag = false;

				if (!noticeFlag) {
					String refund_status = map.get("refund_status").toString();
					// 更新退款申请状态、通知标识
					boolean updateFlag = true;

				}
				// 更新失败，但是返回给微信已接收到，后续定时任务查询状态
				return mwxPayUtil.returnWXPayVerifyMsg();
			} else {
				// 默认未通知，但是回复已通知，后续定时任务查询结果
				return mwxPayUtil.returnWXPayVerifyMsg();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mwxPayUtil.returnWXPayVerifyMsg();
	}

	/**
	 * 微信提现
	 *
	 * @param wxCode
	 *            微信账号
	 * @param price
	 *            金额
	 * @return
	 */
	// @RequestMapping("/extract/wx")
	// @ResponseBody
	// public ResultMsg<String> exWX(String wxCode, double price) {
	// ResultMsg msg = success();
	// return msg;
	// }

	/**
	 * 微信关闭订单接口
	 *
	 * @param orderNo
	 *            订单号
	 * @return
	 */
	@RequestMapping("/wx/close-order")
	@ResponseBody
	public ResultMsg<String> WXCloseOrder(String orderNo) {
		ResultMsg msg = success();
		Map<String, String> map = mwxPayUtil.closeOrder(orderNo);
		if (map != null && map.get("return_code").equals(WXPayConstants.SUCCESS))
			msg.setMsg("订单" + orderNo + " 关闭成功");
		else
			msg.setCode(ResponCode.ERROR).setMsg(map.get("return_msg"));
		return msg;
	}

	/**
	 * 查询订单
	 *
	 * @param orderNo
	 * @return
	 */
	@RequestMapping("/wx/query-order")
	@ResponseBody
	public ResultMsg queryOrder(String orderNo) {
		ResultMsg msg = success(mwxPayUtil.queryOrder(orderNo));
		return msg;
	}

	/**
	 * 手动退款
	 *
	 * @param orderNo
	 * @return
	 */
	@RequestMapping("/wx/refund")
	@ResponseBody
	public ResultMsg refund(String orderId) {
		Map<String, String> rs = new HashMap<String, String>();
		try {
			// 获取订单信息，金额
			double money = 0d;

			// 新建退款申请单
			RefundBean refundBean = new RefundBean();
			refundBean.setOrderId(orderId);
			refundBean.setRefund_fee(money);
			refundBean.setTime(new Date());
			refundBean.setState(RefundEnum.REFUNDING.getCode());
			refundBean.setNotifyFlag("N");
			
			String refundId = refundBean.getRefundId();

			// 微信退款
			int totalAmount = (int) (money * 100);
			int refundAmount = (int) (money * 100);
			rs = mwxPayUtil.doRefund(orderId, refundId, totalAmount, refundAmount);

		} catch (Exception e) {
			e.printStackTrace();
			return error();
		}
		return success(rs);
	}
}