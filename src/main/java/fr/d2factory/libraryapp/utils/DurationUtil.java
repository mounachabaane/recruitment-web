package fr.d2factory.libraryapp.utils;

import java.time.Duration;
import java.time.LocalDate;

public class DurationUtil {

	/**
	 * this method returns the number of days between to days.
	 * 
	 * @author mouna.chabaane
	 * @param borrowAt
	 * @return numberOfDays
	 */
	public int numberOfDays(LocalDate borrowAt) {
		LocalDate today = LocalDate.now();
		int numberOfDays = 0;
		if (borrowAt != null) {
			numberOfDays = (int) Duration.between(today.atStartOfDay(), borrowAt.atStartOfDay()).toDays() * -1;
			System.out.println("numberOfDays => " + numberOfDays);
		}
		return numberOfDays;

	}

}
