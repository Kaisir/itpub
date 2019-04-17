package com.wisedu.emap.itpub.service;

import com.wisedu.emap.itpub.bean.BizException;
import com.wisedu.emap.itpub.bean.Receivers;
import com.wisedu.emap.itpub.bean.analysis.AnalysisData;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 公共的service 一些相同的业务接口 不可与单系统相关
 */
public interface ICommonService {

	/**
	 * 通过rest接口获取部门结构列表
	 * 
	 * @date 2016年4月16日 下午1:15:37
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getDeptsWithRestService() throws Exception;

	/**
	 * 获取原始的部门数据
	 * 
	 * @date 2016年5月6日 下午3:52:18
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getDeptArr() throws Exception;

	/**
	 * 获取rest的部门树数据
	 * 
	 * @date 2016年5月6日 下午5:47:31
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getDeptTree(String deptCode) throws Exception;

	/**
	 * 获取指定系统的消息提醒设置
	 * 
	 * @date 2016年4月16日 下午3:40:11
	 * @author wjfu 01116035
	 * @param xtName
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getMrSetting(String xtName) throws Exception;

	/**
	 * 消息提醒开关的状态变动
	 * 
	 * @date 2016年4月16日 下午4:01:59
	 * @author wjfu 01116035
	 * @param remaindStatus
	 *            提醒的状态
	 * @param xtName
	 *            系统的名称
	 * @param remaindInfo
	 *            所对应系统的提醒的提示信息
	 * @return
	 * @throws Exception
	 */
	public String messageRemaindStatusChange(boolean remaindStatus, String xtName, String remaindInfo) throws Exception;

	/**
	 * 保存当前的提醒类型
	 * 
	 * @date 2016年4月11日 下午1:54:49
	 * @author wjfu 01116035
	 * @param types
	 * @return
	 * @throws Exception
	 */
	public String saveMrTypes(String types, String xtName) throws Exception;

	/**
	 * 通过token获取第一张照片的流
	 * 
	 * @date 2016年5月9日 下午3:01:10
	 * @author wjfu 01116035
	 * @param token
	 * @param imageType
	 *            获取图片流的时候 此字段1代表低质量图片 0为原图 2为小图
	 * @return
	 * @throws Exception
	 */
	public InputStream getFistPicStreamByToken(String token, Integer imageType) throws Exception;

	/**
	 * 获取当前APP用户组
	 * 
	 * @date 2016年5月18日 下午1:38:52
	 * @author wjfu 01116035
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getAppUserGroups(String appId) throws Exception;

	/**
	 * 获取工作流程的下一级处理人
	 * 
	 * @date 2016年5月24日 下午3:26:13
	 * @author wjfu 01116035
	 * @param groupId
	 * @param deptCode
	 * @return
	 * @throws Exception
	 */
	public String getWorkflowActors(String groupId, String deptCode) throws Exception;

	/**
	 * 发送通用信息,指的是 通过IT平台搭建的消息提醒系统
	 * 
	 * @date 2016年6月1日 上午9:19:45
	 * @author wjfu 01116035
	 * @param xtName
	 *            发送消息的系统名称 eg:ymsqxt
	 * @param title
	 *            消息提醒的标题
	 * @param content
	 *            消息提醒的内容
	 * @param url
	 *            移动或者PC的url 如果含有移动和PC需要使用Constants.INVISIBLE_CHAR分割
	 *            其中如果没有PC的URL则前面为空 以Constants.INVISIBLE_CHAR开头
	 * @param recs
	 *            消息发送的接受用户
	 * @param isRecord
	 *            是否入库
	 * @throws Exception
	 */
	public void sendCommonMsg(String xtName, String title, String content, String url, List<Receivers> recs,
			String... isRecord) throws Exception;

	/**
	 * 回复消息发送的旧接口 避免因为边长参数的引入导致旧的调用代码报错
	 * 
	 * @date 2017年5月17日 下午2:58:01
	 * @author wjfu 01116035
	 * @param xtName
	 * @param title
	 * @param content
	 * @param url
	 * @param recs
	 * @throws Exception
	 */
	public void sendCommonMsg(String xtName, String title, String content, String url, List<Receivers> recs)
			throws Exception;

	/**
	 * 获取指定用户和应用的用户组
	 * 
	 * @date 2016年6月20日 上午10:04:36
	 * @author wjfu 01116035
	 * @param userId
	 * @param appId
	 * @return
	 */
	public List<String> getUserGroups(String userId, String appId) throws BizException;
	/**
	 * 获取指定用户和应用的用户组
	 * 
	 * @date 2019年3月26日15:06:29
	 * @author xlfu 01114221
	 * @param userId
	 * @param appId
	 * @return
	 */
	public List<Map<String, Object>> getUserGroupsWithName(String userId, String appId,String currentRole) throws BizException;

	/**
	 * 获取当前登陆用户的具体信息
	 * 
	 * @date 2016年9月9日 上午11:54:46
	 * @author wjfu 01116035
	 * @return
	 */
	public Map<String, Object> getUserDetailInfo() throws Exception;

	/**
	 * 本科生基本信息
	 * 
	 * @date 2016年9月9日 上午11:54:46
	 * @author wjfu 01116035
	 * @return
	 */
	public Map<String, Object> getUndergraduateInfo() throws Exception;

	/**
	 * 获取人员信息
	 * 
	 * @date 2016年9月9日 上午11:54:46
	 * @author wjfu 01116035
	 * @return
	 */
	public Map<String, Object> getUserInfosByIds(String userId, String userType) throws Exception;

	/**
	 * 获取用户的数据分析项目
	 * 
	 * @date 2016年12月19日 上午11:13:40
	 * @author wjfu 01116035
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	public AnalysisData queryAppAndUserDatas(String appName) throws Exception;

	/**
	 * 获取当前学校的全部人员的用户ID
	 * 
	 * @date 2017年4月28日 下午4:27:06
	 * @author wjfu 01116035
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> findSchoolGroupUserId(Integer pageSize, Integer pageNum, String type) throws Exception;
}
