package com.wisedu.emap.itpub.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.math.BigDecimal;
import com.wisedu.emap.base.util.ListUtil;
import com.wisedu.emap.base.util.StringUtil;
import com.wisedu.emap.itpub.bean.Constants;
import com.wisedu.emap.itpub.bean.MessageRequest;
import com.wisedu.emap.itpub.bean.Receivers;
import com.wisedu.emap.itpub.bean.RestfulBean;
import com.wisedu.emap.pedestal.core.AppManager;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import lombok.extern.slf4j.Slf4j;
import self.micromagic.util.annotation.Config;

/**
 * 消息提醒message remained工具类
 * 
 * @filename MRUtils.java
 * @date 2016年4月11日 下午4:29:50
 * @author wjfu 01116035
 */
@Slf4j
public class MRUtils {

	private MRUtils() {
	}

	private static final String MSG_RESTFUL_ID = "msgRemaindSend";

	@Config(name = "isOpen", defaultValue = "true", description = "是否开启移动端审核")
	private static boolean isOpen;

	/**
	 * 发送当前的提醒信息 不建议后续使用此方法
	 * 
	 * @date 2016年4月12日 下午4:23:05
	 * @author wjfu 01116035
	 * @param types
	 *            消息提醒的类型 分号隔开
	 * @param subject
	 *            消息的标题
	 * @param content
	 *            消息内容
	 * @param url
	 *            PC门户跳转的链接
	 * @param receivers
	 *            接受消息的人员
	 * @throws Exception
	 */
	public static void sendMessage(String xtName, String types, String subject, String content, String url,
			List<Receivers> receivers, String... isRecord) throws Exception {
		String appId = AppManager.getInstance().getApp(xtName).getId();
		MessageRequest mr = getMessageRequest(appId, subject, content);
		mr.setReceivers(receivers);
		// 此接口默认认只有pc的url
		if (StringUtil.isNotEmpty(url)) {
			// 如果使用不可见字符分割过 则认为是有PC和MOBILE的不同URL PC下标为0 MOBILE下标为1
			if (url.contains(Constants.INVISIBLE_CHAR)) {
				String[] urlArr = url.split(Constants.INVISIBLE_CHAR);
				if (StringUtil.isNotEmpty(urlArr[0])) {
					mr.setPcUrl(urlArr[0]);
				}
				if (StringUtil.isNotEmpty(urlArr[1])) {
					mr.setMobileUrl(urlArr[1]);
				}
			} else {
				mr.setPcUrl(url);
			}
			mr.setUrlDesc("查看详情");
		}
		if (isRecord.length > 0) {
			sendMessage(mr, types, false);
		} else {
			sendMessage(mr, types, true);
		}
	}

	/**
	 * 创建消息提醒的bean
	 * 
	 * @date 2016年4月12日 下午2:17:37
	 * @author wjfu 01116035
	 * @param appId
	 *            appid
	 * @param subject
	 *            消息提醒的标题
	 * @param content
	 *            提醒的内容
	 * @return
	 */
	public static MessageRequest getMessageRequest(String appId, String subject, String content) {
		// 构造消息发送的请求参数
		MessageRequest mr = new MessageRequest();
		mr.setAppId(appId);
		mr.setSubject(subject);
		mr.setContent(content);
		// mr.setSendTime(System.currentTimeMillis());
		// IUser user = UserUtils.getCurrentUser();
		// mr.setSenderId(user.getUserId());
		// mr.setSenderName(user.getUserName());
		return mr;
	}

	/**
	 * 发送消息
	 * 
	 * @date 2016年4月11日 下午5:10:10
	 * @author wjfu 01116035
	 * @param mq
	 * @param restId
	 *            默认是 msgRemaindSend 可从外界传入
	 */
	public static String sendMsg(MessageRequest mq, String restId) throws Exception {
		// 判断是否有接受人 没有就直接返回
		if (ListUtil.isEmpty(mq.getReceivers())) {
			log.error("MESSAGE SEND HAS NO RECEIVERS");
			return "";
		}
		String schoolCode = ITPubPropsUtil.getProp(Constants.SCHOOL_CODE_KEY);
		int tagId = Integer.parseInt(ITPubPropsUtil.getProp(Constants.TAG_ID_KEY));
		// 得到rest的token
		StringBuilder sign = new StringBuilder();
		sign.append(ITPubPropsUtil.getProp("RESTFUL.ACCESS_TOKEN"));
		sign.append(schoolCode);
		sign.append(mq.getReceivers().get(0).getUserId());

		// 同一个web服务推送的学校的code应该一致 在此做填充
		mq.setSchoolCode(schoolCode);
		mq.setTagId(tagId);
		// 加密字段需要在这里设置
		mq.setSign(EncryptUtils.md5(sign.toString()));
		mq.setSendNow(true);
		JsonElement params = JsonUtils.toJsonElement(mq);
		String res = HttpUtils.confPost(restId, params);
		return res;
	}

	/**
	 * 返回消息推送后的结果 并不记录消息推送日志到数据库
	 * 
	 * @date 2016年7月6日 上午11:25:45
	 * @author wjfu 01116035
	 * @param mr
	 *            发送时候填充发送类型和微信发送类型
	 * @param types
	 *            发送的类型的字符串 以分号或者逗号隔开
	 * @param isRecord
	 *            传入是否需要做日志入库的动作
	 * @param restId
	 *            此为可选项 如果不存在则使用默认的消息推送rest配置
	 * @return 返回发送类型日志 类型-类型发送后的返回结果 如无需处理结果可不用理会
	 * @throws Exception
	 */
	public static Map<String, Object> sendMessage(MessageRequest mrOri, String types, boolean isRecord,
			String... restId) throws Exception {
		Map<String, Object> resMap = Maps.newHashMap();
		if (StringUtil.isEmpty(types)) {
			return resMap;
		}
		List<String> typeList = types.contains(";") ? Splitter.on(";").splitToList(types)
				: Splitter.on(",").splitToList(types);
		List<String> msgSendResList = Lists.newArrayList();
		// 此处对系统勾选的通知类型进行通知 循环通知处理
		for (String type : typeList) {
			MessageRequest mr = mrOri.clone();
			// 填充mr的发送类型
			mr.setSendType(Constants.REMAIND_TYPE_LIST.indexOf(type));
			if ("wechat".equals(type) && ListUtil.isEmpty(mr.getAttachments())) {
				mr.setWxSendType("text");
			}
			if (!"pc".equals(type) && !"pc&mobile".equals(type)) {
				mr.setPcUrl(null);
			}
			if ("sms".equals(type) || "wechat".equals(type)) {
				if (isOpen && StringUtil.isNotEmpty(mr.getMobileUrl())) {
					mr.setUrlDesc("<a href='" + mr.getMobileUrl() + "'>点此查看详情</a>");
				} else {
					if (StringUtil.isNotEmpty(mr.getUrlDesc())) {
						mr.setUrlDesc(null);
//						String newContent = mr.getContent() + "请尽快到网上办事大厅处理。";
//						mr.setContent(newContent);
					}
				}
				mr.setMobileUrl(null);
			}
			String tarId = MSG_RESTFUL_ID;
			// 发送消息
			if (restId.length > 0) {
				tarId = restId[0];
			}
			String result = MRUtils.sendMsg(mr, tarId);
			if (StringUtil.isEmpty(result)) {
				return resMap;
			}
			Map<String, Object> msgResult = JsonUtils.json2Map(result);
			msgSendResList.add(result);
			String status = StringUtil.getString(msgResult.get("status"));
			status = StringUtils.isEmpty(status) ? "0" : status;
			if (Constants.RESP_OK.compareTo(new BigDecimal(status)) != 0) {
				log.error("MESSAGE REMAIND FAIL WITH: [" + msgResult.get(Constants.MSG) + "]");
			} else {
				log.info("MSSAGE SEND SUCCESS WITH: [" + result + "]");
			}
			resMap.put(type, msgResult);
		}
		/*
		 * if(isRecord) { saveRecord(mrOri, types,
		 * Joiner.on("|").join(msgSendResList)); }
		 */
		return resMap;
	}

	/**
	 * 保存消息发送记录
	 * 
	 * @date 2016年9月7日 上午9:30:18
	 * @author wjfu 01116035
	 * @param mrOri
	 * @param types
	 * @param recordResult
	 * @throws Exception
	 */
	private static void saveRecord(MessageRequest mrOri, String types, String recordResult) throws Exception {
		// 发送消息无论成功与否 需要记录日志
		Map<String, Object> record = Maps.newHashMap();
		record.put("LX", types);
		record.put("BT", mrOri.getSubject());
		record.put("NR", mrOri.getContent());
		record.put("FSSJ", DateUtils.getCurFullTime());
		record.put("FSRGH", UserUtils.getCurUserId());
		record.put("FSXT", mrOri.getAppId());
		record.put("JSR", JsonUtils.toJsonStr(mrOri.getReceivers()));
		record.put("FSZT", recordResult);
		// 消息发送失败并不影响申请 需要捕捉异常
		try {
			DsUtil.saveOrUpdate("T_IT_XXFSJL", record);
		} catch (Exception e) {
			log.error("MESSAGE RECORD INSERT ERROR WITH: " + e.getMessage(), e);
		}
	}

	/**
	 * 根据传入的消息的id获取消息的读取情况 已读和未读
	 * 
	 * @date 2016年7月11日 下午1:57:22
	 * @author wjfu 01116035
	 * @param msgIds
	 * @return
	 */
	public static String readMsgStatus(List<String> msgIds) throws Exception {
		RestfulBean restBean = RestfulConf.getRestBeanById("getMsgReadStatus");
		StringBuilder signSb = new StringBuilder();
		for (Header header : restBean.getHeaders()) {
			if ("accessToken".equals(header.getName())) {
				signSb.append(header.getValue());
				break;
			}
		}
		String schoolCode = ITPubPropsUtil.getProp(Constants.SCHOOL_CODE_KEY, "wisedu");
		signSb.append(schoolCode);
		JsonObject params = new JsonObject();
		params.addProperty("sign", EncryptUtils.md5(signSb.toString()));
		params.addProperty("schoolCode", schoolCode);
		params.add("msgIds", JsonUtils.toJsonElement(msgIds));
		String res = HttpUtils.confPost("getMsgReadStatus", params);
		return res;
	}

}
