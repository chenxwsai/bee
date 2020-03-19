package cn.job;

import java.util.ArrayList;

import javax.annotation.Resource;

import com.github.wxpay.sdk.WXPayConstants;

import cn.bean.WXOrderResponse;
import cn.enums.TradeStateEnum;
import cn.util.MWXPayUtil;

public class PayJob {
	@Resource
	private MWXPayUtil mwxPayUtil;

	/**查询订单支付状态
	 * 
	 */
	public void queryOrder() {
		// 查询已支付、未通知、超过期限（10分钟）的订单
		ArrayList list = new ArrayList();
		
		for (int i = 0; i < list.size(); i++) {
			// 查询微信端订单状态
			String orderId = "";
			WXOrderResponse rs = mwxPayUtil.queryOrder(orderId);
			if(WXPayConstants.SUCCESS.equals(rs.getReturn_code())){
				if(WXPayConstants.SUCCESS.equals(rs.getResult_code())){
					// 更新支付状态
					String state = rs.getTrade_state();

				}else{
					//业务结果为失败，不处理
				}
			}else{
				//通信状态为失败，不处理
			}
		}

	}
	
	/**查询退款状态
	 * 
	 */
	public void queryRefund() {
		// 查询未通知、超过期限（10分钟）的订单
		ArrayList list = new ArrayList();
		
		for (int i = 0; i < list.size(); i++) {
			// 查询微信端退款状态
			String refundId = "";//按照商户退单号查询
			WXOrderResponse rs = mwxPayUtil.queryRefund(refundId);
			if(WXPayConstants.SUCCESS.equals(rs.getReturn_code())){
				if(WXPayConstants.SUCCESS.equals(rs.getResult_code())){
					// 更新支付状态
					String state = rs.getRefund_status_0();

				}else{
					//业务结果为失败，不处理
				}
			}else{
				//通信状态为失败，不处理
			}
		}

	}
	
	/**对账
	 * 
	 */
	public void report() {
		// 查询支付不成功、昨日的订单
		ArrayList list = new ArrayList();
		
		for (int i = 0; i < list.size(); i++) {
			// 查询微信端订单状态
			String orderId = "";
			WXOrderResponse rs = mwxPayUtil.queryOrder(orderId);
			if(WXPayConstants.SUCCESS.equals(rs.getReturn_code())){
				if(WXPayConstants.SUCCESS.equals(rs.getResult_code())){					
					String state = rs.getTrade_state();
					if(TradeStateEnum.SUCCESS.getCode().equals(state)){
						// 对状态为已支付的订单，更新订单支付状态为已支付
						
					}
				}else{
					//业务结果为失败，不处理
				}
			}else{
				//通信状态为失败，不处理
			}
		}

	}
}
