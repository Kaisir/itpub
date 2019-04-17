package com.wisedu.emap.itpub.util.exception;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wisedu.emap.itpub.constants.ProductCodeConsts;
import com.wisedu.emap.mvc.CurrentThread;
import com.wisedu.emap.pedestal.core.AppManager;

/**
 * 应用产品异常接口,标识接口
 *
 */
public class AppProductException extends RuntimeException {

    private static final long serialVersionUID = -5389117046403046838L;

    private static final Log LOGGER = LogFactory.getLog(AppProductException.class);

    /**
     * 未知APP的编码
     */
    private static final String UNKNOWN_APP_CODE = "0000";

    /**
     * 后台TASK应用编码
     */
    private static final String TASK_APP_CODE = "9999";

    /**
     * 分配给日志前缀标志编码
     */
    public static final String LOG_PRE_CODE = "#E1";

    private static Properties cache;

    static {
        InputStream is = null;
        try {
            cache = new Properties();
            is = AppManager.getRootApp().getResource("app_exception_code.properties").getAsStream();
            cache.load(is);
        } catch (Exception e) {
            LOGGER.error("获取应用编码失败", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 开发API,用于替换应用映射关系
     *
     * @param props
     */
    public static void changeAppMapping(Properties props) {
        cache = props;
    }

    /**
     * 异常编码,{2位产品线编码}{2位异常类型编码}{4位应用编码}000, 例如: 02080123000
     */
    private String code;

    private String logId;

    public AppProductException(String code) {
        this(code, null, null);
    }

    public AppProductException(String code, String msg) {
        this(code, msg, null);
    }

    public AppProductException(String code, Throwable e) {
        this(code, null, e);
    }

    public AppProductException(String code, String msg, Throwable e) {
        super(msg, e);
        this.code = code;
        this.logId = CurrentThread.getLogSeriesId();
    }

    /**
     * 获取异常编码和异常消息
     *
     * @param code
     *            异常编码
     * @param msg
     *            提示消息 可为空
     * @return
     */
    public static String getAppExceptionMsg(String code, String msg) {
        return code + " " + StringUtils.defaultString(msg);
    }

    /**
     * 获取异常消息, 形如: #E102080123000 姓名不能为空
     *
     * @param productCode
     *            业务线编码 ProductCodeConsts常量
     * @param exceptionCode
     *            具体异常编码 AppProductException子类的CODE属性
     * @param msg
     *            异常消息描述
     * @return
     */
    public static String getAppExceptionMsg(String productCode, String exceptionCode, String msg) {
        return getAppExceptionCode(productCode, exceptionCode) + " " + StringUtils.defaultString(msg);
    }

    /**
     * 获取应用异常编码,组成结构 #105{异常编码}{应用编码}000
     *
     * @param productCode
     *            业务线编码
     * @param exceptionCode
     *            异常类型编码
     * @return
     */
    public static String getAppExceptionCode(String productCode, String exceptionCode) {
        String appName = null;
        if (isBackground()) {
            appName = TASK_APP_CODE;
        } else {
            appName = AppManager.currentApp().getName();
        }
        String appCode = getAppCode(appName);
        String productCodeTmp = StringUtils.isEmpty(productCode) ? ProductCodeConsts.UNKNOWN_CODE : productCode;
        return LOG_PRE_CODE + productCodeTmp + exceptionCode + appCode + "000";
    }

    /**
     * 判断是否在后台运行,例如是否在定时任务中
     *
     * @return
     */
    private static boolean isBackground() {
        return CurrentThread.getCurrentRequest() == null;
    }

    /**
     * 获取应用编码
     *
     * @param appName
     *            应用名称
     * @return
     */
    private static String getAppCode(String appName) {
        String appCode = cache.getProperty(appName);
        return StringUtils.isEmpty(appCode) ? UNKNOWN_APP_CODE : appCode;
    }

    @Override
    public String getMessage() {
        return getCode() + " " + super.getMessage();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }
}
