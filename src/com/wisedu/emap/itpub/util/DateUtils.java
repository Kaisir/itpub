/*
 * @Title: DateUtils.java 
 * @Package com.wisedu.emap.wdyktzd.util 
 * @author wjfu 01116035
 */
package com.wisedu.emap.itpub.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wisedu.emap.base.util.StringUtil;

/**
 * 日期处理工具
 * 
 * @ClassName: DateUtils
 * @author wjfu 01116035
 * @date 2016年3月22日 上午9:41:18
 */
@Slf4j
public class DateUtils {

	private DateUtils() {
	}

	private static final SimpleDateFormat YYYY = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat YYYY_MM_DD2 = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat YYYY_MM_DD_HH_MM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final SimpleDateFormat YYYY_MM = new SimpleDateFormat("yyyy-MM");
	private static final SimpleDateFormat HH_MM = new SimpleDateFormat("HH:mm");

	/**
	 * calendar获取到的星期的数字所对应的日期
	 */
	public static final List<String> CALENDAR_WEEK_NAME = ImmutableList.<String> builder().add("") // 下标从1开始
			.add("周日").add("周一").add("周二").add("周三").add("周四").add("周五").add("周六").build();

	/**
	 * 正常使用的日期
	 */
	public final static List<String> WEEK_NAME = ImmutableList.<String> builder().add("周一").add("周二").add("周三")
			.add("周四").add("周五").add("周六").add("周日").build();

	/**
	 * 获取日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @author zhuangyuhao
	 * @date 2016年4月26日 下午3:42:18
	 * @param date
	 * @return
	 */
	public static String formatDateTime(Date date) {
		synchronized (YYYY_MM_DD_HH_MM_SS) {
			return YYYY_MM_DD_HH_MM_SS.format(date);
		}
	}

	/**
	 * 格式化日期为yyyy-MM-dd HH:mm类型
	 * 
	 * @date 2016年9月14日 下午2:45:50
	 * @author wjfu 01116035
	 * @param date
	 * @return
	 */
	public static String formatDateTime2(Date date) {
		synchronized (YYYY_MM_DD_HH_MM) {
			return YYYY_MM_DD_HH_MM.format(date);
		}
	}

	/**
	 * 获取当前时间，格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @author zhuangyuhao
	 * @date 2016年4月26日 下午3:42:23
	 * @return
	 * @throws Exception
	 */
	public static String getCurrentDateTimeStr() throws Exception {
		return formatDateTime(new Date());
	}

	/**
	 * 获取当前日期，格式：yyyy-MM-dd
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月18日 上午11:03:28
	 * @return
	 * @throws Exception
	 */
	public static String getCurDateStr() throws Exception {
		return getYMDDate(new Date());
	}

	public static String getYMDDate(Date date) throws Exception {
		synchronized (YYYY_MM_DD) {
			return YYYY_MM_DD.format(date);
		}
	}

	/**
	 * 获取当前日期，格式：yyyy/MM/dd
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月18日 上午11:06:22
	 * @param inDate
	 * @return
	 * @throws Exception
	 */
	public static String getCurDateStr2(Date inDate) throws Exception {
		Date date = null == inDate ? new Date() : inDate;
		synchronized (YYYY_MM_DD2) {
			return YYYY_MM_DD2.format(date);
		}
	}

	/**
	 * 获取当前日期七天之后的日期字符串,格式：yyyy/MM/dd
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月18日 上午11:27:34
	 * @return
	 * @throws Exception
	 */
	public static String getNext7Date() throws Exception {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7);
		Date monday = c.getTime();
		return getCurDateStr2(monday);
	}

	/**
	 * 获取startDay之后n天的日期
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月26日 下午8:29:22
	 * @param n
	 * @param startDay
	 * @return
	 * @throws Exception
	 */
	public static String getNextDate(int n, String startDay) throws Exception {
		Calendar c = Calendar.getInstance();
		Date date = parseYMDDate(startDay);
		c.setTime(date);
		c.add(Calendar.DATE, n);
		return getYMDDate(c.getTime());
	}

	/**
	 * 获取当前月的第一天 2016-05-01 00:00:00
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月19日 下午3:42:26
	 * @return
	 * @throws Exception
	 */
	public static String getFirstDay4MonthFull() throws Exception {
		Calendar calendar = Calendar.getInstance();
		Date theDate = calendar.getTime();
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		String dayFirst = getYMDDate(gcLast.getTime());
		StringBuilder str = new StringBuilder().append(dayFirst).append(" 00:00:00");
		return str.toString();
	}

	/**
	 * 获取当前月的第一天 2016-05-01
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月19日 下午3:45:10
	 * @return
	 * @throws Exception
	 */
	public static String getFirstDay4MonthShort() throws Exception {
		Calendar calendar = Calendar.getInstance();
		Date theDate = calendar.getTime();
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		synchronized (YYYY_MM_DD) {
			return YYYY_MM_DD.format(gcLast.getTime());
		}
	}

	/**
	 * 获取当前月的最后一天、 2016-05-31 23:59:59
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月19日 下午3:42:46
	 * @return
	 * @throws Exception
	 */
	public static String getLastDay4MonthFull() throws Exception {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getYMDDate(ca.getTime()) + " 23:59:59";
	}

	/**
	 * 得到传入月份的月初和月底 返回Map size为2 key为startDate和endDate
	 * 
	 * @date 2016年6月8日 下午4:41:58
	 * @author wjfu 01116035
	 * @param dateYM
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getMonthHeadAndTail(String dateYM) throws Exception {
		Map<String, String> startAndEnd = Maps.newHashMap();
		Date date = parseYMDate(dateYM);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		startAndEnd.put("startDate", getYMDDate(c.getTime()));
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		startAndEnd.put("endDate", getYMDDate(c.getTime()));
		return startAndEnd;
	}

	/**
	 * 获取当前月的最后一天 2016-05-31
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月19日 下午3:46:51
	 * @return
	 * @throws Exception
	 */
	public static String getLastDay4MonthShort() throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String last = df.format(ca.getTime());
		return last;
	}

	/**
	 * 根据传入的类型 返回最近的时间段 month 最近一月 week 最近一星期 year 最近一年 @author wjfu
	 * 01116035 @date 2016年3月22日 上午9:55:11 @return
	 * List<String>(yyyy-MM-dd) @throws
	 */
	public static List<String> getNearlyTypeDays(String type) {
		Calendar c = Calendar.getInstance();
		return getNearlyDateTypeDays(c.getTime(), type);
	}

	/**
	 * 
	 * @date 2016年6月7日 下午8:14:41
	 * @author wjfu 01116035
	 * @param date
	 *            日期yyyy-MM
	 * @param type
	 *            类型 year|month|week
	 * @return
	 */
	public static List<String> getNearlyDateTypeDays(Date date, String type) {
		List<String> oneMonth = Lists.newArrayList();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		Date now = c.getTime();
		// 获取一个月前的日期
		if ("month".equals(type)) {
			c.add(Calendar.DAY_OF_YEAR, -31);
		} else if ("week".equals(type)) {
			c.add(Calendar.WEEK_OF_YEAR, -1);
		} else if ("year".equals(type)) {
			c.add(Calendar.YEAR, -1);
		}
		synchronized (YYYY_MM_DD) {
			// 一个月前的日期与当前日期做比较 如果小于当前日期就填入 直至与当前日期相等
			while (c.getTime().compareTo(now) < 0) {
				oneMonth.add(YYYY_MM_DD.format(c.getTime()));
				c.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		return oneMonth;
	}

	/**
	 * 得到年的日期
	 * 
	 * @date 2016年11月9日 下午3:14:20
	 * @author xianghao
	 * @param date
	 * @return
	 */
	public static String getY(Date date) {
		synchronized (YYYY) {
			return YYYY.format(date);
		}
	}

	/**
	 * 得到日期的年月
	 * 
	 * @author wjfu 01116035 @date 2016年3月22日 下午4:25:31 @param date @return
	 * String @throws
	 */
	public static String getYM(Date date) {
		synchronized (YYYY_MM) {
			return YYYY_MM.format(date);
		}
	}

	public static Date parseYMDate(String dateYM) throws ParseException {
		synchronized (YYYY_MM) {
			return YYYY_MM.parse(dateYM);
		}
	}

	/**
	 * 获取当前时间的年月 yyyy-MM
	 * 
	 * @author wjfu 01116035 @date 2016年3月23日 下午3:15:43 @return String @throws
	 */
	public static String getCurYM() {
		return getYM(new Date());
	}

	public static String getCurY() {
		return getY(new Date());
	}

	/**
	 * 获取从本月到之前months的月份的数据
	 * 
	 * @author wjfu 01116035 @date 2016年3月23日 下午6:46:04 @param months @return
	 * List<String> @throws
	 */
	public static List<String> getYMsWithMonths(int months) {
		Calendar c = Calendar.getInstance();
		List<String> yms = Lists.newArrayList();
		synchronized (YYYY_MM) {
			for (int i = 0; i < months; i++) {
				yms.add(YYYY_MM.format(c.getTime()));
				c.add(Calendar.MONTH, -1);
			}
		}
		return yms;
	}

	/**
	 * 获取yyyy-MM-dd HH:mm:ss的当前日期字符串
	 * 
	 * @author wjfu01116035
	 * @date 2016年3月30日 下午3:47:22
	 * @return
	 */
	public static String getCurFullTime() {
		Calendar c = Calendar.getInstance();
		synchronized (YYYY_MM_DD_HH_MM_SS) {
			return YYYY_MM_DD_HH_MM_SS.format(c.getTime());
		}
	}

	/**
	 * 获取当前的年份
	 * 
	 * @date 2016年4月21日 下午4:17:07
	 * @author wjfu 01116035
	 * @return
	 */
	public static String getCurYear() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		return String.valueOf(year);
	}

	/**
	 * 根据yyyy-MM-dd结构的日期获取对应的星期几 public static final int SUNDAY = 1; public
	 * static final int MONDAY = 2; public static final int TUESDAY = 3; public
	 * static final int WEDNESDAY = 4; public static final int THURSDAY = 5;
	 * public static final int FRIDAY = 6; public static final int SATURDAY = 7;
	 * 
	 * @author zhuangyuhao
	 * @date 2016年5月16日 下午3:35:42
	 * @param pTime
	 * @return
	 * @throws Throwable
	 */
	public static int dayForWeek(String pTime) throws Throwable {
		Date tmpDate = parseYMDDate(pTime);
		Calendar cal = Calendar.getInstance();
		cal.setTime(tmpDate);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取calendar中的星期的数字所对应的中文名
	 * 
	 * @date 2016年5月26日 下午7:50:54
	 * @author wjfu 01116035
	 * @param dayInWeek
	 * @return
	 */
	public static String weekNumConvertWeekDay(int dayInWeek) {
		try {
			return CALENDAR_WEEK_NAME.get(dayInWeek);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return "InvalidWeek";
		}
	}

	/**
	 * 获取传入日期(yyyy-MM-dd)的的星期对应 0-周一 1-周二 2-周三 3-周四 4-周五 5-周六 6-周日
	 * 
	 * @date 2016年5月26日 下午7:19:43
	 * @author wjfu 01116035
	 * @param dateStr
	 *            传入null的时候获取当前时间
	 * @return
	 */
	public static int getDayOfWeek(String dateStr) throws Exception {
		Date date = null;
		if (StringUtils.isEmpty(dateStr)) {
			date = new Date();
		} else {
			date = parseYMDDate(dateStr);
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		// 因为Calendar采用的是周日-周六的计数 且下标从1开始 所以转换时候需要将得到的数字-2
		int real = dayOfWeek - 2;
		// 此时周日变成了 -1 判断 如果其为-1 将其值+7
		return real == -1 ? real + 7 : real;
	}

	/**
	 * 获取当前时间的偏移量 即当前时间+offsetDay的日期 返回 yyyy-MM-dd的格式
	 * 
	 * @date 2016年5月27日 上午10:25:31
	 * @author wjfu 01116035
	 * @param offsetDays
	 * @return
	 */
	public static String getOffsetDateFromNow(int offsetDays) throws Exception {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, offsetDays);
		synchronized (YYYY_MM_DD) {
			return YYYY_MM_DD.format(c.getTime());
		}
	}

	/**
	 * 获取时间段信息 传入时间的开始和结束 传出开始到结束的时间段
	 * 
	 * @date 2016年5月27日 上午10:57:20
	 * @author wjfu 01116035
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public static List<String> timeRangeList(String startTime, String endTime) throws Exception {
		synchronized (HH_MM) {
			Date start = HH_MM.parse(startTime);
			Date end = HH_MM.parse(endTime);
			Calendar c = Calendar.getInstance();
			c.setTime(start);
			List<String> timeList = Lists.newArrayList();
			while (c.getTime().compareTo(end) < 0) {
				String startStr = HH_MM.format(c.getTime());
				c.add(Calendar.HOUR_OF_DAY, 1);
				String endStr = HH_MM.format(c.getTime());
				timeList.add(startStr + "-" + endStr);
			}
			return timeList;
		}
	}

	/**
	 * 得到最近的整点信息(向上取整) 比如 当前为04:10 则返回05:00
	 * 
	 * @date 2016年5月30日 下午2:21:39
	 * @author wjfu 01116035
	 * @return
	 */
	public static String getNearlyUpPoint() throws Exception {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MINUTE, 0);
		c.add(Calendar.HOUR_OF_DAY, 1);
		synchronized (HH_MM) {
			return HH_MM.format(c.getTime());
		}
	}

	/**
	 * 获取当前传入yyyy-MM-dd的日期偏移量
	 * 
	 * @date 2016年7月9日 下午2:34:07
	 * @author wjfu 01116035
	 * @param ymd
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	public static String getOffsetYMDFromSourceYMD(String ymd, int offset) throws Exception {
		Calendar c = Calendar.getInstance();
		Date beginDate = parseYMDDate(ymd);
		c.setTime(beginDate);
		c.add(Calendar.DAY_OF_YEAR, offset);
		return getYMDDate(c.getTime());
	}

	/**
	 * 通过传入的yyyy-MM-dd型的日期获取人的准确年龄(周岁) eg.传入1991-06-02现在日期2016-08-22即25周岁
	 * 传入1991-09-02现在日期2016-08-22即24周岁
	 * 
	 * @date 2016年8月22日 上午10:08:25
	 * @author wjfu 01116035
	 * @param birthStr
	 * @return
	 * @throws Exception
	 */
	public static Integer getAgeByBirthday(String birthStr) throws Exception {
		if (StringUtil.isEmpty(birthStr)) {
			return null;
		}
		Date birthDay = parseYMDDate(birthStr);
		Calendar birth = Calendar.getInstance();
		birth.setTime(birthDay);
		Calendar now = Calendar.getInstance();
		Integer age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		Integer nowDays = now.get(Calendar.DAY_OF_YEAR);
		Integer birthDays = birth.get(Calendar.DAY_OF_YEAR);
		if (nowDays <= birthDays) {
			return age;
		} else {
			return age - 1;
		}
	}

	/**
	 * 传入yyyy-MM-dd类型的开始和结束日期,返回包含开始和结束日期的list
	 * 
	 * @date 2016年8月26日 下午5:45:36
	 * @author wjfu 01116035
	 * @param start
	 * @param end
	 * @param days持续的日期
	 *            如果为0则是单日
	 * @param addType
	 *            传入Calendar.DAY_OF_YEAR/Calendar.MONTH/Calendar.WEEK_OF_YEAR
	 */
	public static List<String> calcuDateList(String start, String end, long days, int addType) throws Exception {
		List<String> rangeList = Lists.newArrayList();
		Calendar c = Calendar.getInstance();
		Date startDate = parseYMDDate(start);
		c.setTime(startDate);
		Date endDate = parseYMDDate(end);
		int oriDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		while (c.getTime().compareTo(endDate) <= 0) {
			// 如果当前为月份 则做下判断 判断当前的月份没有那一天 则将当前月份+1
			// 并不记录 并且将日历的月份归于原始天数
			if (Calendar.MONTH == addType && oriDayOfMonth != c.get(Calendar.DAY_OF_MONTH)) {
				c.add(addType, 1);
				c.set(Calendar.DAY_OF_MONTH, oriDayOfMonth);
				continue;
			}
			// 不为月份或者正常的情况则如实递增
			// 单日事件 则直接添加
			rangeList.add(getYMDDate(c.getTime()));
			// 如果是多日事件 则将持续的事件也添加到list中
			if (days > 0) {
				Calendar cCopy = Calendar.getInstance();
				cCopy.setTime(c.getTime());
				for (int day = 0; day < days; day++) {
					cCopy.add(Calendar.DAY_OF_YEAR, 1);
					rangeList.add(getYMDDate(cCopy.getTime()));
				}
			}
			c.add(addType, 1);
		}
		return rangeList;
	}

	/**
	 * 解析yyyy-MM-dd形式的字符串为文字
	 * 
	 * @date 2016年9月7日 下午2:24:33
	 * @author wjfu 01116035
	 * @param ymd
	 * @return
	 * @throws Exception
	 */
	public static Date parseYMDDate(String ymd) throws Exception {
		synchronized (YYYY_MM_DD) {
			return YYYY_MM_DD.parse(ymd);
		}
	}

	/**
	 * 获取相对于当前时间的偏移量
	 * 
	 * @date 2016年8月29日 上午11:10:16
	 * @author wjfu 01116035
	 * @param yearMonth
	 *            传入的年月
	 * @param monthOffset
	 *            月份的偏移量
	 * @return yyyy-MM类型的便宜年月
	 */
	public static String getMonthOffset(String yearMonth, int monthOffset) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(parseYMDate(yearMonth));
		c.add(Calendar.MONTH, monthOffset);
		return getYM(c.getTime());
	}

	/**
	 * 检测传入的日期类型 转成日期
	 * 
	 * @date 2016年9月1日 下午3:36:10
	 * @author wjfu 01116035
	 * @param timeStr
	 * @return
	 */
	public static Date transStr2Date(String timeStr) throws Exception {
		// yyyy-MM-dd HH:mm
		if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}", timeStr)) {
			synchronized (YYYY_MM_DD_HH_MM) {
				return YYYY_MM_DD_HH_MM.parse(timeStr);
			}
			// yyyy-MM-dd
		} else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", timeStr)) {
			return parseYMDDate(timeStr);
			// yyyy-MM-dd HH:mm:ss
		} else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}", timeStr)) {
			synchronized (YYYY_MM_DD_HH_MM_SS) {
				return YYYY_MM_DD_HH_MM_SS.parse(timeStr);
			}
		}
		return null;
	}

	/**
	 * 获取年月的最后一日的日期 如 传入2016-08 传出 2016-08-31
	 * 
	 * @date 2016年9月7日 下午1:53:28
	 * @author wjfu 01116035
	 * @param last
	 *            yyyy-MM的数据
	 * @return
	 * @throws Exception
	 */
	public static String getLastDayOfYMDate(String dateStr) throws Exception {
		Calendar c = Calendar.getInstance();
		synchronized (YYYY_MM) {
			c.setTime(YYYY_MM.parse(dateStr));
		}
		c.set(Calendar.DAY_OF_MONTH, c.getMaximum(Calendar.DAY_OF_MONTH));
		return getYMDDate(c.getTime());
	}

	/**
	 * 获取当前传入字符串的指定field的值 支持年份月份日期周次等数据获取不支持时间的获取
	 * 
	 * @date 2016年9月8日 上午9:45:55
	 * @author wjfu 01116035
	 * @param timeStr
	 *            转换yyyy-MM-dd类型的日期
	 * @param field
	 */
	public static int getFieldVal(String timeStr, int field) throws Exception {
		Date time = parseYMDDate(timeStr);
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		return c.get(field);
	}
}
