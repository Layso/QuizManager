package com.layso.quizmanager.datamodel;



public interface Searchable {
	/**
	 * Method to search any class who implements it
	 * @param criteria  Criteria to search/compare with object itself
	 * @param term      SearchTerm to look for within object
	 * @return          Boolean result of search
	 */
	boolean Search(String criteria, String term);
}
