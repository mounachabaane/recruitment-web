package fr.d2factory.libraryapp.utils;

import java.util.UUID;

public class IdGenerator {

	public String generateUniqueId() {
		UUID idOne = UUID.randomUUID();
		String str = "" + idOne;
		int uid = str.hashCode();
		String filterStr = "" + uid;
		str = filterStr.replaceAll("-", "");
		return str;

	}
}
