package cn.jcenterhome.service;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jcenterhome.dao.DataBaseDao;
import cn.jcenterhome.util.BeanFactory;
import cn.jcenterhome.util.Common;
import cn.jcenterhome.util.FileHelper;
import cn.jcenterhome.util.JavaCenterHome;

public class CronService {

	public synchronized void runCron(HttpServletRequest request, HttpServletResponse response, int cronid)
			throws Exception {
		String where = "cronid='" + cronid + "'";
		runCron(request, response, where);
	}

	public synchronized void runCron(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");
		String where = "available>'0' AND nextrun<='" + sGlobal.get("timestamp") + "'";
		runCron(request, response, where);
	}

	private void runCron(HttpServletRequest request, HttpServletResponse response, String where)
			throws Exception {
		DataBaseDao dataBaseDao = (DataBaseDao) BeanFactory.getBean("dataBaseDao");
		List<Map<String, Object>> query = dataBaseDao.executeQuery("SELECT * FROM "
				+ JavaCenterHome.getTableName("cron") + " WHERE " + where + " ORDER BY nextrun LIMIT 1");
		if (query.size() > 0) {
			Map<String, Object> cron = query.get(0);
			cronRunning(request, response, cron);
		}
		cron_config(request);
	}

	private void cronRunning(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> cron) {
		String scripFileName = (String) cron.get("filename");
		String cronFilePath = JavaCenterHome.jchRoot + "./source/cron/" + scripFileName;
		File cronFile = new File(cronFilePath);
		if (!cronFile.exists()) {
			DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
			Map<String, Object> setarr = new HashMap<String, Object>();
			setarr.put("available", 0);
			Map<String, Object> whereSQLArr = new HashMap<String, Object>();
			whereSQLArr.put("cronid", cron.get("cronid"));
			dataBaseService.updateTable("cron", setarr, whereSQLArr);
			FileHelper.writeLog(request,"CRON", cron.get("name") + " : Cron script(" + cron.get("filename") + " not found");
			return;
		}
		cronNextRun(request, cron);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/source/cron/" + scripFileName);
		try {
			dispatcher.include(request, response);
		} catch (Exception e) {
			FileHelper.writeLog(request,"CRON", cron.get("name") + " : Cron script(" + cron.get("filename")
					+ " exception: " + e.getMessage());
		}
	}
	public synchronized void cron_config(HttpServletRequest request) throws Exception {
		DataBaseDao dataBaseDao = (DataBaseDao) BeanFactory.getBean("dataBaseDao");
		List<Map<String, Object>> query = dataBaseDao.executeQuery("SELECT nextrun FROM "
				+ JavaCenterHome.getTableName("cron") + " WHERE available>'0' ORDER BY nextrun LIMIT 1");

		int nextrun = query.size() > 0 ? (Integer) (query.get(0).get("nextrun")) : 0;
		DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
		Map<String, Object> insertsqlarr = new HashMap<String, Object>();
		insertsqlarr.put("var", "cronnextrun");
		insertsqlarr.put("datavalue", nextrun);
		dataBaseService.insertTable("config", insertsqlarr, false, true);

		CacheService cacheService = (CacheService) BeanFactory.getBean("cacheService");
		cacheService.config_cache(false);
	}

	
	public synchronized boolean cronNextRun(HttpServletRequest request, Map<String, Object> crons) {

		if (Common.empty(crons))
			return false;

		Map<String, Object> sConfig = (Map<String, Object>) request.getAttribute("sConfig");
		Map<String, Object> sGlobal = (Map<String, Object>) request.getAttribute("sGlobal");

		String minute = (String) crons.get("minute");
		Map<String, Object> setarr = new HashMap<String, Object>();
		if (minute.equals("")) {
			setarr.put("available", 0);
		} else {
			short hour = Short.parseShort(String.valueOf(crons.get("hour")));
			short day = Short.parseShort(String.valueOf(crons.get("day")));
			byte weekDay = Byte.parseByte(String.valueOf(crons.get("weekday")));
			Calendar calendar = Common.getCalendar(String.valueOf(sConfig.get("timeoffset")));
			calendar.set(Calendar.SECOND, 0);
			Date date = calendar.getTime();
			String[] minuteArray = minute.split("\t");
			int minuteLengh = minuteArray.length;
			int[] minuteIntArray = new int[minuteLengh];
			for (int i = 0; i < minuteLengh; i++) {
				minuteIntArray[i] = Integer.parseInt(minuteArray[i]);
			}
			Arrays.sort(minuteIntArray);
			int nowMinute = calendar.get(Calendar.MINUTE);
			int minMinute = 0;
			int maxMinute = 0;
			int nextRunM = 0;
			if (minuteLengh > 0) {
				minMinute = minuteIntArray[0];
				maxMinute = minuteIntArray[minuteLengh - 1];
			}
			if (nowMinute >= maxMinute) {
				nextRunM = minMinute;
				if (hour == -1) {
					calendar.add(Calendar.HOUR_OF_DAY, 1);
				}
			} else {
				for (int tempMinute : minuteIntArray) {
					if (tempMinute > nowMinute) {
						nextRunM = tempMinute;
						break;
					}
				}
			}
			calendar.set(Calendar.MINUTE, nextRunM);
			if (hour > -1) {
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				if (!calendar.getTime().after(date) && weekDay == -1 && day == -1) {
					calendar.add(Calendar.DAY_OF_YEAR, 1);
				}
			}
			if (weekDay > -1) {
				calendar.set(Calendar.DAY_OF_WEEK, weekDay + 1);
				if (!calendar.getTime().after(date)) {
					calendar.add(Calendar.WEEK_OF_MONTH, 1);
				}
			} else {
				if (day > -1) {
					calendar.set(Calendar.DAY_OF_MONTH, day);
					if (!calendar.getTime().after(date)) {
						calendar.add(Calendar.MONTH, 1);
					}
				}
			}
			int nextrun = (int) (calendar.getTimeInMillis() / 1000);
			int timestamp = (Integer) sGlobal.get("timestamp");

			setarr.put("lastrun", timestamp);
			setarr.put("nextrun", nextrun);
			if (nextrun <= timestamp) {
				setarr.put("available", 0);
			}
		}
		DataBaseService dataBaseService = (DataBaseService) BeanFactory.getBean("dataBaseService");
		Map<String, Object> whereSQLArr = new HashMap<String, Object>();
		whereSQLArr.put("cronid", crons.get("cronid"));
		dataBaseService.updateTable("cron", setarr, whereSQLArr);
		return true;
	}
}
