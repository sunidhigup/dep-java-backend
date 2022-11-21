package com.nagarro.dataenterpriseplatform.main.db.converter;

import java.util.HashMap;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


@Converter
public class StringIntegerMapAttributeConverter implements AttributeConverter<HashMap<String, Integer>, String> {

	private String DELIMITER = ":";

	@Override
	public String convertToDatabaseColumn(HashMap<String, Integer> attribute) {

		if (attribute != null && !attribute.isEmpty()) {
			final String mapString = attribute.toString().replaceAll("=", DELIMITER);
			return mapString.substring(1, mapString.length() - 1);
		} else
			return "";
	}

	@Override
	public HashMap<String, Integer> convertToEntityAttribute(String dbData) {

		final HashMap<String, Integer> map = new HashMap<String, Integer>();
		if(dbData != null && !dbData.isEmpty()) {
			final String[] keyValuePairs = dbData.split(",");


			for (final String pair : keyValuePairs) {
				if (!pair.isEmpty()) {
					final String[] entry = pair.split(DELIMITER);
					map.put(entry[0].trim(), Integer.parseInt(entry[1].trim()));
				}
			}
		}
		return map;
	}
}