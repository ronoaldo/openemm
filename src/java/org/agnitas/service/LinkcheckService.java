package org.agnitas.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.LinkcheckerDao;

public class LinkcheckService {

	private LinkcheckerDao linkcheckerDao;	// injected by Spring.
	
	/**
	 * this method checks every item of the given link-list if the 
	 * appropriate url is available.
	 * The returned list contains all urls which sent no response or have other failures.
	 * @param linkList
	 * @return
	 */
	public Collection<String> checkAvailability(Collection<TrackableLink> linkList) {
		// create usual <String> List
		Vector<String> checkList = new Vector<String>();
		TrackableLink tmpLink = null;
		Iterator<TrackableLink> it = linkList.iterator();
		while (it.hasNext()) {
			tmpLink = it.next();
			checkList.add(tmpLink.getFullUrl());
		}
		return checkURLAvailability(checkList);
	}
	
	/**	 
	 * @param linkList
	 * @return
	 */
	public Vector<String> checkURLAvailability(Vector<String> linkList) {
		// create Pool
		ExecutorService executor = Executors.newFixedThreadPool(linkcheckerDao.getThreadCount());
		
		ArrayList<String> loopList = new ArrayList<String>();
		loopList.addAll(linkList);
						
		// create runnables
		for (String url : loopList) {
			executor.execute(new LinkcheckWorker(linkcheckerDao.getLinkTimeout(), linkList, url));
		}
		executor.shutdown();	// no new task are scheduled
		try {
			executor.awaitTermination((linkcheckerDao.getLinkTimeout() + 1000), TimeUnit.MILLISECONDS );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return linkList;
	}	
	
	public LinkcheckerDao getLinkcheckerDao() {
		return linkcheckerDao;
	}

	public void setLinkcheckerDao(LinkcheckerDao linkcheckerDao) {
		this.linkcheckerDao = linkcheckerDao;
	}
}
