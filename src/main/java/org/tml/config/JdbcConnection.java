package org.tml.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcConnection {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> getConnection(String query, int offset, int limit) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
		return list;
	}

}
