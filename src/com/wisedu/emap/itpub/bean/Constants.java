package com.wisedu.emap.itpub.bean;

import com.google.common.collect.ImmutableList;
import com.ibm.icu.math.BigDecimal;

import java.util.List;

import org.apache.http.message.BasicHeader;

/**
 * 配置常量
 */
public interface Constants {

	/** 页面大小 */
	String PAGE_SIZE = "pageSize";

	/** 默认的页面大小 */
	String PAGE_SIZE_DEFAULT = "10";

	/** 页码 */
	String PAGE_NUM = "pageNum";

	/** 开始的页码 */
	String PAGE_NUM_START = "1";

	/** 翻页时候传输的页码key */
	String PAGE_NUMBER = "pageNumber";

	/** 开始页码的key */
	String PAGE_START = "pageStart";

	/** 结束页码的key */
	String PAGE_END = "pageEnd";

	/** 日期字符串 */
	String DATE_STR = "date";

	/** 页面排序的key */
	String QUERY_SETTING_ORDER = "*order";

	/** 降序 */
	String DESC = "desc";

	/** 升序 */
	String ASC = "asc";

	/** 降序符号 */
	String DESC_SYMBOL = "-";

	/** 升序符号 */
	String ASC_SYMBOL = "+";

	/** 页面的key */
	String PAGE = "page";

	/** 总计总共 */
	String TOTAL = "total";

	/** 总计大小 */
	String TOTAL_SIZE = "totalSize";

	/** 行 */
	String ROWS = "rows";

	/** 代码 */
	String CODE = "code";

	/** 正常代码 */
	String CODE_OK = "0";

	/** 状态 */
	String STATUS = "status";

	/** 数据组key */
	String DATAS = "datas";

	/** 单个数据key */
	String DATA = "data";

	/** 名称 */
	String NAME = "name";

	/** 值 */
	String VALUE = "value";

	/** 图例 */
	String LEGEND = "legend";

	/** 系列的颜色 */
	String SERIS_COLOR = "serisColor";

	/** 分页查询的后台传值key */
	String PAGE_QUERY_SETTING = "querySetting";

	/** 系列的值 */
	String CHART_SERIS_DATA = "serisData";

	/** json的请求头 */
	BasicHeader JSON_HEADER = new BasicHeader("Content-Type", "application/json");

	/** 参数对象名称 */
	String PARAM = "param";

	/** id字符串 */
	String ID_STR = "id";

	/** id数组key */
	String IDS_STR = "ids";

	/** userType */
	String USER_TYPE = "userType";

	/** 展示搜索[全部]的key */
	String ALL_OPTION = "allOption";

	/** 状态成功 */
	String SUCCESS = "success";

	/** 状态失败 */
	String ERROR = "error";

	/** 表的主键 */
	String WID = "WID";

	/** 输出的信息 */
	String MSG = "msg";

	/** Y的字符串 表示成功或者存在 */
	String YES = "Y";

	/** N的字符串 表示失败或者不存在 */
	String NO = "N";

	/** 开关开启 */
	String SWITCH_ON = "ON";

	/** 开关关闭 */
	String SWITCH_OFF = "OFF";

	/** 开关开启状态 通用的判定使用 */
	public String SWITCH_ON_VAL = "1";

	/** 开关关闭状态 通用的判定使用 */
	public String SWITCH_OFF_VAL = "0";

	/** 消息提醒类型 其下标为所对应的发送type */
	List<String> REMAIND_TYPE_LIST = ImmutableList.<String> builder().add("pc&mobile") // PC门户和手机门户
																						// -
																						// 0
			.add("pc") // PC门户 - 1
			.add("mobile") // 手机门户 - 2
			.add("email") // 邮件 - 3
			.add("sms") // 短信 - 4
			.add("wechat") // 微信 - 5
			.build();

	/** 学校的code的key */
	String SCHOOL_CODE_KEY = "MR_PROPS.SCHOOL_CODE";

	/** 消息提醒 当前系统对应的tag */
	String TAG_ID_KEY = "MR_PROPS.TAG_ID";

	/** 响应成功 */
	BigDecimal RESP_OK = new BigDecimal("200");

	/** 审核通用状态 草稿 */
	String APPLY_DRAFT = "D";

	/** 审核通用状态 审核中 */
	String APPLY_AUDIT = "A";

	/** 审核通用状态 审核通过 */
	String APPLY_PASS = "P";

	/** 审核通用状态 审核不通过 */
	String APPLY_NO_PASS = "N";

	/** 获取单位的rest的id */
	String DEPT_REST_ID = "getDeptInfo";

	String DEPT_TREE_REST_ID = "getDeptTreeInfo";

	/** 消息提醒开关的key */
	String DR_SWITCH_NAME = "mrSwitch";

	/** 业务流程IP:PROT的常量 */
	String TASK_IP_KEY = "TASK_PROPS.TASK_IP";

	/** 业务流程的主业务域的值 */
	String TASK_DOMAIN_KEY = "TASK_PROPS.BIZ_DOMIAN";

	/** 通用常量 服务地址 */
	String COMMON_RES_SERVER = "COMMON_PROPS.RES_SERVER";

	/** 流程开启 */
	String TASK_START = "start";

	/** 流程完成 */
	String TASK_COMPLETE = "complete";

	/** 流程结束 */
	String TASK_END = "end";

	/** 配置类型的key */
	String CONFIG_TYPE = "configType";

	/** 普通路由即 权限路由配置 */
	String CONFIG_NORMAL = "noraml";

	/** 工作流配置流程 */
	String CONFIG_WORKFLOW = "workflow";

	/** 分享 */
	String CONFIG_SHARE = "share";

	String CONFIG_DETAIL = "msgDetail";

	/** 业务流程的任务ID */
	String TASK_ID = "taskId";

	/** 容器的路径的key 到/sys之前 */
	String ENV_ROOT = "rootPath";

	/** 消息类型集合的key */
	String MR_TYPES = "types";

	/** 工作流程的步骤 */
	String TASK_STEP = "step";

	/** 流程绑定的key 即业务的WID */
	String TASK_BIZ_KEY = "bizKey";

	String COMMON_DIC_ID = "2c4b900c-a031-4980-9555-0c32fe4491cc";

	/** 校园活动发布状态 */
	String FBZT_FB = "FB";

	String FBZT_CG = "CG";

	/** 会议类型是否涉密 */
	String SECRET_YES = "SM_YES";

	String SECRET_NO = "SM_NO";

	/** 相应成功状态码(字符串形式) */
	String RESP_OK_STR = "200";

	/** 相应失败的状态码(字符串形式) */
	String RESP_FAIL_STR = "0";

	/** 不可见字符 用于分割字符串 */
	String INVISIBLE_CHAR = String.valueOf((char) 30);

	/** 公有云学校CODE的KEY */
	String CLOUD_SCHOOL_HEADER = "wecSchoolId";

	/** 公有云oAuth的授权码 */
	String USER_TOKEN_KEY = "user_token";

}
