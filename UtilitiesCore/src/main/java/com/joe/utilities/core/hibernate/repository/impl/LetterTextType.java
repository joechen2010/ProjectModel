package com.joe.utilities.core.hibernate.repository.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/***
 * User Type for Client Letter Text, so that we can update a secondary column
 * with a regular expression processed value.
 * @author minger
 *
 */
public class LetterTextType
	implements UserType {
	
	private static final String substitutions[][] = {
		{ "\\}", "{ASIS(\"}\")}" }, // replace all }
		{ "\\{(?!ASIS)", "{ASIS(\"{\")}" }, // replace all { not followed by ASIS
		{ "\\|", "{ASIS(\"|\")}" }, // replace all |
		{ "\\t", "{TAB}" }, // replace all tabs
		{ "\\r\\n?", "{PAR}" }, // replace all CRLF
	};
	
	public LetterTextType() {
		super();
	}

	static String process(String string) {
		for (String sub[] : substitutions) {
			Pattern p = Pattern.compile(sub[0]);
			Matcher m = p.matcher(string);
			string = m.replaceAll(sub[1]);
		}
		return string;
	}
	
	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object x) throws HibernateException {
		return x;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable)deepCopy(value);
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}
		else if (x == null || y == null) {
			return false;
		}
		else {
			return x.equals(y);
		}
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		return Hibernate.TEXT.nullSafeGet(rs, names[0]);
	}

	@Override
	public void nullSafeSet(PreparedStatement ps, Object value, int index)
			throws HibernateException, SQLException {
		Hibernate.TEXT.nullSafeSet(ps, value, index);
		
		String processed = (String)value;
		if (value != null) {
			processed = process((String)value);
		}
		Hibernate.TEXT.nullSafeSet(ps, processed, index+1);
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	@Override
	public Class<?> returnedClass() {
		return String.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.CLOB, Types.CLOB };
	}

}
