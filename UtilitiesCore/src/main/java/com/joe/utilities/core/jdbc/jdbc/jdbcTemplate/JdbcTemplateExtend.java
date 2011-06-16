package com.joe.utilities.core.jdbc.jdbc.jdbcTemplate;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class JdbcTemplateExtend extends JdbcTemplate {
	private DataSource dataSource;

	/**
	 * é»˜è®¤æž„é€ å™¨ï¼Œè°ƒç”¨æ­¤æ–¹æ³•åˆ�å§‹åŒ–ï¼Œéœ€è¦�è°ƒç”¨setDataSourceè®¾ç½®æ•°æ�®æº�
	 */
	public JdbcTemplateExtend() {
	}

	/**
	 * åˆ�å§‹æž„é€ å™¨
	 * 
	 * @param dataSource
	 *            æ•°æ�®æº�
	 */
	public JdbcTemplateExtend(DataSource dataSource) {
		this.dataSource = dataSource;
		super.setDataSource(dataSource);
	}

	/**
	 * æ™®é€šåˆ†é¡µæŸ¥è¯¢<br>
	 * <b>å¦‚æžœç»“æžœç»“å�ˆæ¯”è¾ƒå¤§åº”è¯¥è°ƒç”¨setFetchsize() å’ŒsetMaxRowä¸¤ä¸ªæ–¹æ³•æ�¥æŽ§åˆ¶ä¸€ä¸‹ï¼Œå�¦åˆ™ä¼šå†…å­˜æº¢å‡º</b>
	 * 
	 * @see #setFetchSize(int)
	 * @see #setMaxRows(int)
	 * @param sql
	 *            æŸ¥è¯¢çš„sqlè¯­å�¥
	 * @param startRow
	 *            èµ·å§‹è¡Œ
	 * @param rowsCount
	 *            èŽ·å�–çš„è¡Œæ•°
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public List<Map> querySP(String sql, int startRow, int rowsCount)
			throws DataAccessException {
		return querySP(sql, startRow, rowsCount, getColumnMapRowMapper());
	}

	/**
	 * è‡ªå®šä¹‰è¡ŒåŒ…è£…å™¨æŸ¥è¯¢<br>
	 * <b>å¦‚æžœç»“æžœç»“å�ˆæ¯”è¾ƒå¤§åº”è¯¥è°ƒç”¨setFetchsize() å’ŒsetMaxRowä¸¤ä¸ªæ–¹æ³•æ�¥æŽ§åˆ¶ä¸€ä¸‹ï¼Œå�¦åˆ™ä¼šå†…å­˜æº¢å‡º</b>
	 * 
	 * @see #setFetchSize(int)
	 * @see #setMaxRows(int)
	 * @param sql
	 *            æŸ¥è¯¢çš„sqlè¯­å�¥
	 * @param startRow
	 *            èµ·å§‹è¡Œ
	 * @param rowsCount
	 *            èŽ·å�–çš„è¡Œæ•°
	 * @param rowMapper
	 *            è¡ŒåŒ…è£…å™¨
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public List<Map> querySP(String sql, int startRow, int rowsCount,
			RowMapper rowMapper) throws DataAccessException {
		return (List) query(sql, new SplitPageResultSetExtractor(rowMapper,
				startRow, rowsCount));
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		super.setDataSource(dataSource);
	}
}
