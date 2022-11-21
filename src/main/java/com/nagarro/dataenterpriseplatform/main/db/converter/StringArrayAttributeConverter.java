package com.nagarro.dataenterpriseplatform.main.db.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringArrayAttributeConverter implements AttributeConverter<List<String>, String> {
	@Override
	public String convertToDatabaseColumn(List<String> list) {

		if(list != null && !list.isEmpty()) {
			return String.join(",", list);
		} else
			return "";
	}

	@Override
	public List<String> convertToEntityAttribute(String joined) {
		if (joined != null && !joined.isEmpty()) 
			return new ArrayList<>(Arrays.asList(joined.split(",")));
		else
			return new ArrayList<String>();
	}
}