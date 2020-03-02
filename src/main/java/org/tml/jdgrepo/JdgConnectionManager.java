/**
 * 
 */
package org.tml.jdgrepo;

import java.util.Date;

import org.apache.camel.Exchange;

/**
 * @author Soumya
 *
 */
public interface JdgConnectionManager {

	/*
	 * Override this method to put single entry in JDG Cache
	 */
	public boolean put(String key,Exchange value);

	/*
	 * Override this method to put multiple entry in JDG Cache
	 */
	public int putAll(Date startDate, Date endDate);
	
	/*
	 * Override this method to delete entry in JDG Cache
	 */
	public void delete();

	/*
	 * Override this method to get entry in JDG Cache
	 */
	public void getData(String key,Exchange ex) throws Exception;
	/*
	 * Update or add value  to index map
	 */
	public boolean updateOrAddIndex(Date startDate,Date endDate) throws Exception;
	
}
