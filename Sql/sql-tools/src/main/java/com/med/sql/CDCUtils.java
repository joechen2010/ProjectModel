package com.med.sql;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;

import org.apache.xmlbeans.XmlOptions;

import com.medecision.eda.cdcEventBody100.CDCEventBody;
import com.medecision.eda.cdcEventBody100.FieldType;
import com.medecision.eda.event100.ChrononType;
import com.medecision.eda.event100.EventDocument;
import com.medecision.eda.event100.EventPayloadType;
import com.medecision.eda.event100.EventType;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Table;

/**
 * Prototypical support for CDC event generater newly installed/updated Alineo databases.
 * 
 * @author Jane Eisenstein
 */
public class CDCUtils {
	
	// shared types
	public final static String DATE = "DATE";
	public final static String INTEGER = "INTEGER";
	public final static String NUMBER = "NUMBER";
	public final static String NUMERIC = "NUMERIC";
	public final static String TIMESTAMP = "TIMESTAMP";
	public final static String VARCHAR = "VARCHAR";
	
	static final String[] NONAUDITED_ENTITIES = {
	"ADMIN.AAEQSTNR_SEQ",
	"ADMIN.AUTOCODER_TRANS_CNT",
	"ADMIN.AUTOCODER_VERSION_CACHE",
	"ADMIN.QSTNR_SEQ",
	"ADMIN.RQST_REF_ID_SEQ",
	"CODES.POSTAL_CODE",
	"CONFIG.DB_VERSION",
	"CONFIG.MEMBER_SEARCH_TYPE",
	"CONFIG.PROV_SEARCH_TYPE",
	"CONFIG.SBSCR_SEARCH_TYPE",
	"DXPROC.AUTOCODER_DX_CACHE",
	"DXPROC.AUTOCODER_PROC_CACHE",
	"ENROLL.ENROLLMENT_SRCH_FLDS",
	"IMM.LOCK_TABLE",
	"IMM.PHYSICAL_LOCK_TABLE",
	"MEMBER.MEMBER_EDIT_STATUS",
	"PERFMON.METHODMETRIC",
	"PERFMON.TRANSACTIONMETRIC",
	"PGMENROLL.PGM_EDIT_STATUS",
	"PGMENROLL.PGM_REF_ID_SEQ",
	"REQUEST.RQST_KEY_INFO",
	"REQUEST.RQST_TX_SRCH"
	};
	
	static boolean isNonauditedEntity(String entityType) {
		if (entityType != null) {
			for (String word : NONAUDITED_ENTITIES) {
				if (word.equalsIgnoreCase(entityType))
					return true;
			}
		}
		return false;
	}
	
	static EventDocument createEventDoc(final String tableName,
			Column[] dataColumns, int colCount, String[] values, String pkValue) {
		
		EventDocument eventDoc = EventDocument.Factory.newInstance();
		EventType event = eventDoc.addNewEvent();
		populateCreateEntityEvent(event, tableName, dataColumns, colCount, values, pkValue);
		return eventDoc;
	}

	static void populateCreateEntityEvent(EventType event, final String tableName,
			Column[] dataColumns, int colCount, String[] values, String pkValue) {
		
		Calendar now = Calendar.getInstance();

		event.setEventType(EventPayloadType.CREATE_ENTITY);
		event.setChronon(ChrononType.MS);
		event.setIsComposite(false);
		event.setId(""+System.nanoTime());	// TODO use real GUID
		event.setDetectionTime(now);
		event.setSource("AlineoInstaller");
		
		CDCEventBody body = event.addNewBody();
		body.setUserId("mgr");

		body.setEntityType(tableName);
		body.setInternalId(pkValue);
		
		FieldType field = null;
		for (int j = 0; j < colCount; j++) {
			String value = values[j];
			// TODO: verify this logic is right
			if (value != null) {	
				String columnId = dataColumns[j].getName();				;
				field = body.addNewField();
				field.setEntityFldType(columnId);
				field.setValue(value);
			}
		}
	}

	static String getEventXML(EventDocument event) throws IOException {
		StringWriter writer = new StringWriter();
		XmlOptions options = new XmlOptions();
		options.setSavePrettyPrint();
		options.setUseDefaultNamespace();
		event.save(writer, options);
		String marshalledXML = writer.toString();
		return marshalledXML;
	}

	public static String getEntityType(Table table) {
		final String catalogName = table.getSchema().getCatalogName();
		String schemaName = table.getSchema().getName();
		if (catalogName != null) { // strip catalogName from schemaName
			schemaName = schemaName.substring(catalogName.length()+1);
		}
		schemaName = stripDoubleQuotes(schemaName);
		final String entityType = schemaName + "." + stripDoubleQuotes(table.getName());
		return entityType;
	}

	public static String stripDoubleQuotes(String name) {
		if (name.startsWith("\"")) {
			name = name.substring(1, name.length()-2);
		}
		return name;
	}

    static String getColumnValueString(Column column, String value) {
		ColumnDataType colType = column.getType();
		String type = colType.getName();
		// TODO: verify this logic is right
//		if (DATE.equalsIgnoreCase(type)) {
//			value = ???
//		} else if (TIMESTAMP.equalsIgnoreCase(type)) {
//			value = ???
//		} else 
		{
			String prefix = colType.getLiteralPrefix();
			String suffix = colType.getLiteralSuffix();
			if (prefix != null && suffix != null) {
				value = SqlUtils.escapeStringLiteralDelimiter(value, type);
//				if (value.length() == 0 && !column.isNullable())
//					value = "_null_";
				value = prefix + value + suffix;
			}
		}
		return value;
	}
	
}


