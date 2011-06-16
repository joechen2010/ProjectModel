package com.med.sql.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import schemacrawler.schema.Schema;

import com.med.sql.SchemaNames;

/**
 * DatabaseNames reports table and or column names based on JDBC metadata. 
 * It is a wrapper for the SchemaNames.printReport method.
 * 
 * Usage: dbnames.bat <properties file> [<output file>]
 * If no output file is defined, will write to standard out.
 * 
 * @author Jane Eisenstein
 */

public class DatabaseNames implements SqlToolsConstants {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		System.out.println("args.length = "+args.length);
			
		// must supply at least the property file path
		if (args.length < 1) {
			System.out.println("Usage: databaseNames.bat <properties file> [<output file>]");
			System.out.println("If no output file is defined, writes to standard out.");
		} else {

			String propsPath = null;
			String outPath = "standard out";
			PrintStream out = null;
			
			// get the properties file path
			propsPath = args[0];
//			System.out.println("args[0]="+propsPath);
			System.setProperty(properties_key, propsPath);

			// get the output file
			if (args.length == 1) {
				out = System.out;
			} else {
				outPath = args[1];
//				System.out.println("args[1]="+outPath);
				File f = new File(outPath);
				if (f.exists())
					f.delete();
				try {
					out = new PrintStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					out = System.out;
					outPath = "standard out";
				}
			}

			if (propsPath != null && out != null) {
				SqlToolsProperties props = new SqlToolsProperties();
				boolean printSchemas = "true".equalsIgnoreCase(System.getProperty("printSchemas"));
				boolean printTables = "true".equalsIgnoreCase(props.getString("printTables"));
				boolean printColumns = "true".equalsIgnoreCase(props.getString("printColumns"));
				
//				System.out.println(driver_key+"="+props.getString(driver_key));
				System.out.println(url_key+" = "+props.getString(url_key));
//				System.out.println(user_key+"="+props.getString(user_key));
//				System.out.println(password_key+"="+props.getString(password_key));
				
				System.out.println("Printing report to "+outPath);
				SqlToolsSchemas sts = new SqlToolsSchemas(props);
				List<Schema> schemas = sts.getSchemas(true); // sort columns
				
				try {
					SchemaNames.printReport(schemas, printSchemas, printTables, printColumns, out);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
