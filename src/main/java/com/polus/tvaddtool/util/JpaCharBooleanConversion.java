package com.polus.tvaddtool.util;

import javax.persistence.AttributeConverter;

import com.polus.tvaddtool.constants.Constants;

public class JpaCharBooleanConversion implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean b) {
		if (b == null) {
			return null;
		}
		if (b.booleanValue()) {
			return Constants.DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION;
		}
		return Constants.DATABASE_BOOLEAN_FALSE_STRING_REPRESENTATION;
	}

	@Override
	public Boolean convertToEntityAttribute(String s) {
		if (s == null) {
			return null;
		}
		if (s.equals("Y") || s.equals("y")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
