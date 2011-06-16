package com.joe.utilities.core.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.joe.utilities.core.configuration.Globals;
import com.joe.utilities.core.serviceLocator.ServiceLocator;

/**
 * Creation date: (2/27/01 11:50:29 AM)
 * 
 * @author Dave Ousey
 * @version 1.0
 * 
 * Creation date: 8/2/2004 10:35:07 AM Copyright (c) 2004 MEDecision, Inc. All rights reserved.
 */

public class Utils
{
    private Utils()
    {
    }

    public final static long MS_PER_MINUTE = 60L * 1000L;

    public final static long MS_PER_HOUR = 60L * MS_PER_MINUTE;

    public final static long MS_PER_DAY = 24L * MS_PER_HOUR;

    public final static long MS_PER_WEEK = 7L * MS_PER_DAY;

    public final static Date LOWER_DATE_LIMIT;

    public final static Date UPPER_DATE_LIMIT;
	private static final ThreadLocal<SAXBuilder> threadSaxBuilder = new ThreadLocal<SAXBuilder>();
	private static final String TIMESTATE_START = "START";
	private static final String TIMESTATE_END = "END";
	
	
    static
    {
        LOWER_DATE_LIMIT = Utils.parseDate("01/01/1841");
        UPPER_DATE_LIMIT = Utils.parseDate("12/31/9999");
    }
    
    /**
     * Method getIntValue. Returns the integer value associated with the passed string. Note Leading and Trailing blanks
     * are removed from the string before converting the value to an integer.
     * 
     * @param str
     *            The String being converted to an integer.
     * @param value
     *            Return this value instead if data when non-numeric character or null object is passed.
     * @return int The converted integer result
     */
    public static final int getIntValue(String str, int value)
    {
        int retVal = value;
        try
        {
            // String has a value
            if (str != null)
            {

                // Remove leading and trailing spaces.
                str = str.trim();

                // Chop decimal point and later
                int decimalPos = str.indexOf('.');
                if (decimalPos > -1)
                    str = str.substring(0, decimalPos);

                // Convert String to integer value
                retVal = Integer.parseInt(str);
            }

        }
        catch (Exception ignore)
        {
        }

        return retVal;
    }

    /**
     * Method getLongValue. Returns the long integer value associated with the passed string. Note Leading and Trailing
     * blanks are removed from the string before converting the value to an integer.
     * 
     * @param str
     *            The String being converted to an integer.
     * @param value
     *            Return this value instead if data when non-numeric character or null object is passed.
     * @return long The converted long result
     */
    public static final long getLongValue(String str, long value)
    {
        long retVal = value;
        try
        {
            // String has a value
            if (str != null)
            {
                // Remove leading and trailing spaces.
                str = str.trim();

                // Convert String to integer value
                retVal = Long.parseLong(str);
            }

        }
        catch (Exception ignore)
        {
        }

        return retVal;
    }

    /**
     * Method getDoubleValue. Returns the double value associated with the passed string. Note Leading and Trailing
     * blanks are removed from the string before converting the value to an integer.
     * 
     * @param str
     *            The String being converted to a d ouble.
     * @param value
     *            Return this value instead if data when non-numeric character or null object is passed.
     * @return long The converted double result
     */
    public static final double getDoubleValue(String str, double value)
    {
        double retVal = value;
        try
        {
            // String has a value
            if (str != null)
            {
                // Remove leading and trailing spaces.
                str = str.trim();

                // Convert String to integer value
                retVal = Double.parseDouble(str);
            }

        }
        catch (Exception ignore)
        {
        }

        return retVal;
    }

    /**
     * Method replace. Replaces substrings within one string with another string
     * 
     * @param source
     * @param oldChars
     * @param newChars
     * @return String
     */
    public static String replace(String source, String oldChars, String newChars)
    {
        if (source == null)
            return null;

        if (oldChars == null || oldChars.length() == 0 || newChars == null || source.indexOf(oldChars) == -1)
            return source;

        // Exact match
        if (source.equals(oldChars))
            return newChars;

        // Split into list of tokens delimited on old characters and create new buffer using the same tokens but
        // with the new characters inserted between the tokens.
        StringBuffer buffer = new StringBuffer(source.length());
        List<String> tokens = split(source, oldChars);
        boolean firstToken = true;
        Iterator<String> iter = tokens.iterator();
        while (iter.hasNext())
        {
            String token = iter.next();
            if (!firstToken && newChars.length() > 0)
                buffer.append(newChars);
            buffer.append(token);
            firstToken = false;
        }

        return buffer.toString();
    }

    /**
     * Method loadFile. Load in the contents of a file
     * 
     * @param fileName
     * @return StringBuffer
     * @throws IOException
     */
    public static StringBuffer loadFile(String fileName) throws IOException
    {
        StringBuffer fileData = new StringBuffer(100);

        char[] buffer = new char[32000];
        int charsRead;

        // Read file contents and append to StringBuffer
        Reader reader = new FileReader(fileName);
        while ((charsRead = reader.read(buffer)) != -1)
        {
            fileData.append(buffer, 0, charsRead);
        }
        reader.close();
        reader = null;

        // Return data
        return fileData;

    }

    /**
     * Method loadZipFile. Load in the contents of the specified file within the specified zip file.
     * 
     * @param zipFileName
     * @param fileName
     * @return StringBuffer
     * @throws IOException
     */
    public static StringBuffer loadZipFile(String zipFileName, String fileName) throws IOException
    {
        return loadZipFile(zipFileName, fileName, null);
    }

    /**
     * Method loadZipFile. Load in the contents of the specified file within the specified zip file.
     * 
     * @param zipFileName
     * @param fileName
     * @return StringBuffer
     * @throws IOException
     */
    public static StringBuffer loadZipFile(String zipFileName, String fileName, StringBuffer fileData) throws IOException
    {
        if (fileData == null)
            fileData = new StringBuffer(100);

        fileData.setLength(0);

        // REad file contents and append to StringBuffer
        ZipFile zipFile = new ZipFile(zipFileName);

        // Validate file was found
        ZipEntry zipEntry = zipFile.getEntry(fileName);
        if (zipEntry == null)
            throw new IOException("Could not locate " + fileName + " within " + zipFileName);

        // Read in file
        InputStream instream = zipFile.getInputStream(zipEntry);
        byte[] buffer = new byte[32000];
        int bytesRead;
        while ((bytesRead = instream.read(buffer)) != -1)
        {
            fileData.append(new String(buffer, 0, bytesRead));
        }
        instream.close();
        instream = null;

        zipFile.close();
        zipFile = null;

        // Return data
        return fileData;
    }

    /**
     * Method listZipFileContents. Lists names of zip file entries.
     * 
     * @param zipFileName
     * @return List
     * @throws IOException
     */
    public static List<String> listZipFileContents(String zipFileName) throws IOException
    {
        ArrayList<String> list = new ArrayList<String>(10);

        // Read file contents and append to StringBuffer
        ZipFile zipFile = new ZipFile(zipFileName);

        Enumeration<? extends ZipEntry> enumerator = zipFile.entries();
        while (enumerator.hasMoreElements())
        {
            ZipEntry zipEntry = enumerator.nextElement();
            list.add(zipEntry.getName());
        }

        zipFile.close();
        zipFile = null;

        list.trimToSize();
        return list;
    }

    /**
     * Method writeFile. Writes string data to a file overwriting existing data.
     * 
     * @param fileName
     * @param fileData
     * @throws IOException
     */
    public static void writeFile(String fileName, String fileData) throws IOException
    {
        writeFile(fileName, fileData, false);
    }

    /**
     * Method writeFile. Writes string data to a file overwriting or appending to existing data based on the appendFlag.
     * 
     * @param fileName
     * @param fileData
     * @param appendFlag
     * @throws IOException
     */
    public static void writeFile(String fileName, String fileData, boolean appendFlag) throws IOException
    {
        Writer fileWriter = new FileWriter(fileName, appendFlag);
        fileWriter.write(fileData);
        fileWriter.close();
        fileWriter = null;
    }

    /**
     * Method dumpException.
     * 
     * @param fileName
     * @param t
     */
    public static void dumpException(String fileName, Throwable t)
    {
        dumpException(fileName, t, false);
    }

    /**
     * Method dumpException.
     * 
     * @param fileName
     * @param t
     * @param appendFlag
     */
    public static void dumpException(String fileName, Throwable t, boolean appendFlag)
    {
        try
        {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            Writer fileWriter = new FileWriter(fileName, appendFlag);
            fileWriter.write(sw.toString());
            fileWriter.close();
            fileWriter = null;
        }
        catch (Throwable t2)
        {
            t2.printStackTrace();
        }
    }

    /**
     * Method parseDate. Return java.util.Date for a given MM/DD/YYYY format string date.
     * 
     * @param dateString
     * @return Date
     */
    public static Date parseDate(String dateString)
    {
        // Null conditions
        if (dateString == null)
            return null;
        if (dateString.length() == 0)
            return null;

        
        // Initialize to null
        Date javaDate = null;

        // If there is a date
        dateString = dateString.trim();
        if (dateString.length() <= 10) // Just date
        {
            try
            {
                javaDate = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
            }
            catch (ParseException pe)
            {
            }
        }

        else if (dateString.length() > 10) // Date + time
        {
            // Try a couple of different formats for date/time
            try
            {
                javaDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").parse(dateString);
            }
            catch (ParseException pe)
            {
            }
            if (javaDate == null)
            {
                try
                {
                    javaDate = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa").parse(dateString);
                }
                catch (ParseException pe)
                {
                }
            }
        }

        return javaDate;
    }
    

    /**
     * parseTime: Returns a Time SQL object corresponding to the given time string.  Date portion returned is 1/1/1970
     * so the returned Time is only meant to be treated as a Time object.
     * @param timeString
     * @return Time
     */
    public static Time parseTime(String timeString)
    {
    	// Try basic format (e.g. "2:00 AM")
    	Date javaDate = null;
    	try
		{
			javaDate = new SimpleDateFormat("hh:mm aaa").parse(timeString);
		} 
    	catch (ParseException e)
		{
		}
 
    	// Try another format if no result yet (e.g. "2 AM")
    	if (javaDate == null)
    	{
        	try
    		{
    			javaDate = new SimpleDateFormat("hh aaa").parse(timeString);
    		} 
        	catch (ParseException e)
    		{
    		}    		
    	}
    	
    	// Try another format if no result yet (e.g. "0200")
    	if (javaDate == null)
    	{
        	try
    		{
    			javaDate = new SimpleDateFormat("HHss").parse(timeString);
    		} 
        	catch (ParseException e)
    		{
    		}    		
    	}
    	
    	if (javaDate == null)
    		throw new IllegalArgumentException("Cannot recognize input as time format: " + timeString);
    	
    	return new Time(javaDate.getTime());
    	
    }
    
    /**
     * Method isValidDate. Validates that the given date string contains a valid date part (MM/DD/YYYY).
     * The "parseDate" method allows for a lot of garbage (e.g. "13/32/2000" translates as "02/01/2001")
     * @param dateVal
     * @return boolean
     */
    public static boolean isValidDate(String dateString)
    {
        // Null conditions
        if (dateString == null)
            return false;
        
        dateString = dateString.trim();
        if (dateString.length() == 0)
            return false;
        
        String datePart = null;
        int firstSpacePos = dateString.indexOf(' ');
        if (firstSpacePos > -1)
        {
            datePart = dateString.substring(0, firstSpacePos);
        }
        else
        {
            datePart = dateString;
        }
        
        // Validate date part: M/D/YYYY or MM/DD/YYYY
        if (datePart.length() < 8)
            return false;
        if (datePart.length() > 10)
            return false;
        int firstSlashPos = datePart.indexOf('/');
        if (firstSlashPos == -1)
            return false;
        int secondSlashPos = datePart.indexOf('/', firstSlashPos+1);
        if (secondSlashPos == -1)
            return false;
        int month = getIntValue(datePart.substring(0, firstSlashPos), -1);
        if (month < 1 || month > 12)
            return false;
        int day = getIntValue(datePart.substring(firstSlashPos+1, secondSlashPos), -1);
        if (day < 1 || day > 31)
            return false;
        int year = getIntValue(datePart.substring(secondSlashPos+1), -1);
        if (year < 1000 || year > 9999)
            return false;
        
        // Month - day cross-checks
        switch (month)
        {
            case 4: case 6: case 9: case 11:
                if (day > 30)
                    return false;
                break;
                
            case 2: 
                
                if (day > 29)
                    return false;
                
                if (year % 4 != 0)
                {
                   if (day > 28)
                    return false;
                }
                
                else if (year % 100 == 0 && year % 400 != 0)
                {
                    if (day > 28)
                        return false;
                }
                
                break;
        }
        
        return true;
    }

    /**
     * Method extractDate. 
     * Return java.util.Date with time information reset.
     * 
     * @param dateString
     * @return Date
     */
    public static Date extractDate(Date originalDate)
    {

    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(originalDate);
    	calendar.set(Calendar.AM_PM, Calendar.AM);
    	calendar.set(Calendar.HOUR, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    
    /**
     * Method timeToMillis. Returns # of milliseconds from midnight that the given time value represents.
     * 
     * @param time
     * @return long
     */
    public static long timeToMillis(Time time)
    {
        if (time == null)
            throw new RuntimeException("Null time value passed to Utils.timeToMillis.");

        // Convert to hh:mm:ss
        String timeAsString = time.toString();
        long timeVal = 0;
        timeVal = (60L * 60L * 1000L * Utils.getLongValue(timeAsString.substring(0, 2), 0));
        timeVal += (60L * 1000L * Utils.getLongValue(timeAsString.substring(3, 5), 0));
        timeVal += (1000L * Utils.getLongValue(timeAsString.substring(6, 8), 0));
        return timeVal;
    }

    /**
     * Method currentSecond. Return current second without milliseconds.
     * 
     * @return Date
     */
    public static Date currentSecond()
    {
        long now = new Date().getTime();
        now -= (now % 1000); // Shave off milliseconds
        return new Date(now);
    }

    /**
     * Method formatSQLTimeStamp.
     * 
     * @param dateTime
     * @return String
     */
    public static String formatSQLTimeStamp(Date dateTime)
    {
        if (dateTime != null)
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateTime);
        else
            return "";
    }

    /**
     * Method dateAdd.
     * 
     * @param baseDateTime
     * @param daysToAdd
     * @param chopTime
     * @return Date
     */
    public static Date dateAdd(final Date baseDateTime, int daysToAdd, boolean chopTime)
    {
        if (baseDateTime == null)
            throw new RuntimeException("Null date passed to Utils.dateAdd.");

        Date dateTime = baseDateTime;
        if (chopTime)
        	dateTime = getMidnight(dateTime);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        calendar.add(Calendar.DATE, daysToAdd);
        return calendar.getTime();
    }

    /**
     * Method dateAdd. Return midight-based date by adding the number of days to the given date. Account for changes
     * to/from daylight savings time.
     * 
     * @param baseDateTime
     * @param daysToAdd
     * @return Date
     */
    public static Date dateAdd(Date baseDateTime, int daysToAdd)
    {
        return dateAdd(baseDateTime, daysToAdd, true);
    }

    /**
     * Method timeAdd.
     * 
     * @param baseDateTime
     * @param milliSecondsToAdd
     * @return Date
     */
    public static Date timeAdd(Date baseDateTime, long milliSecondsToAdd)
    {
        if (baseDateTime == null)
            throw new RuntimeException("Call made to Utils.timeAdd method with null Date parameter.");

        return new Date(baseDateTime.getTime() + milliSecondsToAdd);
    }
    
    /**
     * Gets the midnight.
     * 
     * @param date the date
     * 
     * @return the midnight
     */
    public static Date getMidnight(Date date)
    {
    	if (date == null)
    		throw new IllegalArgumentException("'date' parameter must be valued.");
    	
    	try
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.parse(formatter.format(date));
		}
		catch (ParseException e)
		{
			throw new RuntimeException("Unexpected ParseException for date '"+date+"'.", e);
		}
    }
    
    /**
     * Returns the testDate if it has a value otherwise returns the current time.
     * @param testDate The date being testsed.
     * @return the testDate if it has a value otherwise returns the current time.
     */
    public static Date getCurrentTimeIfCalendareIsNull(Calendar testDate){
    	if ( testDate == null ){
    		return Calendar.getInstance().getTime();
    	} else {
    		return testDate.getTime();
    	}
    }
    
    /**
     * Gets the midnight java.util.Date for the midnight at the beginning of the current date
     * 
     * @return the midnight
     */
    public static Date getMidnight()
    {
    	return getMidnight(new Date());
    }

    /**
     * Method padText. Pads the given text with the given character (either left or right padded) so that the returned
     * text does not have fewer than the specified # of characters.
     * 
     * @param text
     * @param numberOfCharacters
     * @param padCharacter
     * @param paddingLeft
     * @return String
     */
    public static String padText(String text, int numberOfCharacters, char padCharacter, boolean paddingLeft)
    {
        StringBuffer buffer = new StringBuffer(text);
        int length = text.length();
        if (numberOfCharacters > 0 && numberOfCharacters > length)
        {
            for (int i = 0; i <= numberOfCharacters; i++)
            {
                if (paddingLeft)
                {
                    if (i < numberOfCharacters - length)
                        buffer.insert(0, padCharacter);
                }
                else
                {
                    if (i > length)
                        buffer.append(padCharacter);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Method split. Splits a string into multiple strings as delimited by the 'delimiter' string
     * 
     * @param input
     * @param delimiter
     * @return List
     */
    public static List<String> split(String input, String delimiter)
    {
        if (input == null)
            return null;

        List<String> splitList = new ArrayList<String>(16);
        if (input.length() == 0)
            return splitList;

        int startIndex = 0;
        int endIndex;

        do
        {
            endIndex = input.indexOf(delimiter, startIndex);
            if (endIndex > -1)
            {
                // Extract the element and adjust new starting point
                splitList.add(input.substring(startIndex, endIndex));
                startIndex = endIndex + delimiter.length();
            }
            else
            {
                // Last element
                splitList.add(input.substring(startIndex));
            }

        }
        while (endIndex > -1);

        // Return the list
        return splitList;
    }

    /**
     * Method equalObjects. Compares two objects dealing with possibility that one or both are equal
     * 
     * @param o1
     * @param o2
     * @return boolean
     */
    public static boolean equalObjects(Object o1, Object o2)
    {
        // Perform brainless checks prior to creating new EqualsBuild object
        if (o1 == o2)
            return true;
        else if (o1 == null || o2 == null)
            return false;

        // Return Commons Language implementation as they did this work for us.
        return new EqualsBuilder().append(o1, o2).isEquals();
    }

    /**
     * Method cleanFreeTextData. Removes/translates characters that cannot be filed into the database tables.
     * 
     * @param text
     * @return String
     */
    public static String cleanFreeTextData(String text)
    {
        // Do nothing if passed nothing
        if (text == null || text.length() == 0)
            return text;

        // Filter data transfer and database delimiters used in applications like CarePlanner
        text = Utils.replace(text, "{", "(");
        text = Utils.replace(text, "}", ")");
        text = Utils.replace(text, "[", "(");
        text = Utils.replace(text, "]", ")");
        text = Utils.replace(text, "`", "'");
        text = Utils.replace(text, "~", "-");
        text = Utils.replace(text, "^", "*");
        text = Utils.replace(text, "|", "/");

        // Convert all linefeed characters to uniform carriage return
        text = Utils.replace(text, "\r\n", "\r");
        text = Utils.replace(text, "\n", "\r");

        // Convert tab to spaces
        text = Utils.replace(text, "\t", "    ");

        // Convert control characters to spaces
        for (int i = 0; i <= 8; i++)
            if (text.indexOf(i) > -1)
                text = Utils.replace(text, String.valueOf((char) i), " ");
        for (int i = 11; i <= 12; i++)
            if (text.indexOf(i) > -1)
                text = Utils.replace(text, String.valueOf((char) i), " ");
        for (int i = 14; i <= 31; i++)
            if (text.indexOf(i) > -1)
                text = Utils.replace(text, String.valueOf((char) i), " ");

        // Convert ASCII characters above 128 to spaces
        for (int i = 129; i <= 255; i++)
            if (text.indexOf(i) > -1)
                text = Utils.replace(text, String.valueOf((char) i), " ");

        return text;
    }

    /**
     * Method processMultiLineFreeText. Split the given free text into elements added to the provided lineList List.
     * Each line may not exceed the provided lineLimit. If the calling function is interested, also record where lines
     * are split with and without end of line characters using the optional endOfLineBitmap parameter.
     * 
     * @param freeText
     * @param linesList
     * @param lineLimit
     * @param endOfLineBitmap
     */
    public static void processMultiLineFreeText(String freeText, List<String> linesList, int lineLimit, StringBuffer endOfLineBitmap)
    {
        // Do nothing if processing nothing.
        if (freeText == null || freeText.length() == 0)
            return;

        // Assert required parameters are valid
        if (linesList == null)
            throw new RuntimeException("Required linesList parameter is null in processMultiLineFreeText.");

        if (lineLimit < 1)
            throw new RuntimeException("Invalid lineLimit value '" + lineLimit + "' passed to processMultiLineFreeText.");

        // "Clean" the text data removing characters that cannot be filed into guideline database tables. This will
        // Remove all '\n' characters
        freeText = cleanFreeTextData(freeText);

        // Split lines on carriage return boundaries
        List<String> splitLines = split(freeText, "\r");
        int splitCount = splitLines.size();

        // Prepare end of line bitmap for expected # of item lines to be created (plus any existing lines before call to
        // this method)
        if (endOfLineBitmap != null)
            endOfLineBitmap.ensureCapacity(endOfLineBitmap.length() + splitLines.size());

        // Iterate across list of lines
        Iterator<String> iter = splitLines.iterator();
        int splitIndex = -1;
        while (iter.hasNext())
        {
            // Note where we are within split list
            splitIndex++;

            // Get current line
            String line = iter.next();

            // Line must be chopped so that each item data is within limit.
            while (line.length() > lineLimit)
            {
                int chopPosition = -1;

                // Look for first occurence of non-letter and non-number
                for (int i = lineLimit; i >= 0; i--)
                {
                    if (!Character.isLetterOrDigit(line.charAt(i)))
                    {
                        chopPosition = i;
                        break;
                    }
                }

                // In unlikely event that there are only letters and numbers, break at boundary limit
                if (chopPosition == -1)
                    chopPosition = lineLimit;

                // Chop the line and set into the guideline
                linesList.add(line.substring(0, chopPosition + 1));
                if (endOfLineBitmap != null)
                    endOfLineBitmap.append('0');

                // Set line to remainder of chopped string
                line = line.substring(chopPosition + 1);
            }

            // Last item or only item already within limit.
            linesList.add(line);

            // Append appropriate end of line byte indicator (0/1) dependings on whether current line is last line or
            // not
            if (endOfLineBitmap != null)
                endOfLineBitmap.append((splitIndex < (splitCount - 1)) ? '1' : '0');
        }
    }

    /**
     * This method escapes a string so that it appear correctly in XML output. To convert a String, each character is
     * examined in turn and the following special characters are replaced with their corresponding entity references: "
     * (double quote) -> &quot; & (ampersand) -> &amp; < (less than) -> &lt; > (greater than) -> &gt;
     * 
     * @param s
     *            String to be translated.
     * @return the translated String.
     */
    public static String encodeXML(String s)
    {
        StringBuffer out = new StringBuffer();

        if (s != null)
        {
            for (int i = 0; i < s.length(); i++)
            {
                char ch = s.charAt(i);
                switch (ch)
                {
                case '"':
                    out.append("&quot;");
                    break;
                case '&':
                    out.append("&amp;");
                    break;
                case '<':
                    out.append("&lt;");
                    break;
                case '>':
                    out.append("&gt;");
                    break;
                case 8217:
                    out.append("&apos;");
                    break; // latin decimal for unicode 0x92 or right quote
                case 0x27:
                	out.append("&apos;");
                	break;
                default:
                    if (ch < 0 || ch > 127)
                        out.append("&#x" + Integer.toHexString(ch) + ";");
                    else
                        out.append(ch);
                }
            }
        }

        return out.toString();
    }

    /**
     * This method joins together a list of Strings and separates them by a delimiter
     * 
     * @param list
     *            List of Strings to join
     * @param delimiter
     *            String to delimit each item in list
     */
    public static String join(List<String> list, String delimiter)
    {
        StringBuffer buf = new StringBuffer("");
        if (list != null && list.size() > 0)
            for (int i = 0; i < list.size(); i++)
                if (i == 0)
                    buf.append(list.get(i));
                else
                    buf.append(delimiter).append(list.get(i));

        return buf.toString();

    }

    /**
     * Method formatDate.
     * 
     * @param date
     * @param includeTime
     * @return
     */
    public static String formatDate(Date date, boolean includeTime)
    {
        if (date != null)
        {
            if (includeTime)
                return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(date);
            else
                return new SimpleDateFormat("MM/dd/yyyy").format(date);
        }
        else
            return null;
    }
    /**
     * Method formatDateForCERMe.
     * 
     * @param date
     * @param includeTime
     * @return
     */
    public static String formatDateForCERMe(Date date, boolean includeTime)
    {
        if (date != null)
        {
            if (includeTime)
                return new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a").format(date);
            else
                return new SimpleDateFormat("MM-dd-yyyy").format(date);
        }
        else
            return null;
    }
    
    /**
     * Formats the given date in the format of "hh:mm:ss.SSSS".
     * 
     * @param date <code>Date</code> to be formatted.
     * @return formatted date or null if parameter is null.
     */
    public static String formatTime(Date date) {
        if (date != null) {
            return (new SimpleDateFormat("hh:mm:ss.SSSS")).format(date);
        }
        return null;
    }
    
//    /**
//     * Formats the given date in the format of "hh:mm:ss a".
//     * 
//     * @param date <code>Date</code> to be formatted.
//     * @return formatted data or null if the parameter is null.
//     */
//    public static String formatTimeWithMeridiem(Date date) {
//        if (date != null) {
//            return new SimpleDateFormat("hh:mm a").format(date);
//        }
//        return null;
//    }

    /**
     * Method formatDateToMinutes.
     * 
     * @param date
     * @return String
     */
    public static String formatDateToMinutes(Date date)
    {
        if (date != null)
            return new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(date);
        else
            return null;
    }

    /**
     * Method nullIfEmpty. Converts empty to null strings.
     * @deprecated use sameButNullIfEmpty instead
     * @param text
     * @return Returns String
     */
    public static String nullIfEmpty(String text)
    {
        if (text != null && text.length() == 0)
            return null;
        else
            return text;
    }

    /**
     * Method wrap. Returns a string that is the "wrapped" version of the input string on the input wrap boundary.
     * 
     * @param text
     * @param boundary
     * @return String
     */
    public static String wrap(String text, int boundary)
    {
        // Return text if null or empty or boundary is invalid.
        if (text == null || text.length() == 0 || boundary <= 0)
            return text;

        // Determine if text contains carriage return/linefeeds or just linefeeds.
        boolean crFlag = (text.indexOf("\r\n") > -1);

        // Split the text on its linefeed borders
        List<String> splitText = split(text, "\n");

        // Add the text to the result buffer, evaluating the need to split up lengthy line as its goes.
        StringBuffer wrappedText = new StringBuffer();
        for (int i = 0; i < splitText.size(); i++)
        {
            // Append line feed after the first line
            if (i > 0)
                wrappedText.append(crFlag ? "\r\n" : "\n");

            // Get current line from the vector. Chop trailing carriage return if it exists.
            String line = splitText.get(i);
            if (line.endsWith("\r"))
                line = line.substring(0, line.length() - 1);

            // Check if there is no need to chop
            if (line.length() <= boundary)
            {
                // Add to the result buffer
                wrappedText.append(line);
            }
            else
            {
                // Line needs some chopping
                int startPos = 0;
                int chopPos = boundary;
                
                // Chop the line into components based on the boundary. Stop before the last line
                while (startPos + boundary < line.length())
                {
                    // Locate position to chop
                    while (chopPos > startPos && line.charAt(chopPos) != ' ') 
                        chopPos--;

                    // Could not locate a space so force a chop at the boundary.
                    if (chopPos == startPos)
                        chopPos = startPos + boundary;

                    // Add wrapped text and line feed
                    if (line.charAt(startPos) == ' ')
                    	wrappedText.append(line.substring(startPos + 1, chopPos));
                    else
                    	wrappedText.append(line.substring(startPos, chopPos));
                    wrappedText.append(crFlag ? "\r\n" : "\n");

                    // Adjust start position and chop position for next round of chopping
                    startPos = chopPos;
                    chopPos = startPos + boundary;                    
                }                
                // Add last line
                if (line.charAt(startPos) == ' ')
                	wrappedText.append(line.substring(startPos + 1));
                else
                	wrappedText.append(line.substring(startPos));
            }
        }

        // Return the result text
        return wrappedText.toString();
    }

    /**
     * Method formatCurrency. Return currency value as string.
     * 
     * @param currencyValue
     * @return String
     */
    public static String formatCurrency(Number currencyValue)
    {
        if (currencyValue == null)
            return "";

        if (currencyValue.doubleValue() == currencyValue.longValue())
            return "" + currencyValue.longValue();
        else
        {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            return nf.format(currencyValue);
        }
    }

    /**
     * Method formatReal
     * 
     * @param numberValue
     * @param numberOfDecimalsToShow
     * @return String
     */
    public static String formatReal(Number numberValue, int numberOfDecimalsToShow)
    {
        if (numberValue == null)
            throw new RuntimeException("Cannot pass a null Number parameter.");

        // Showing a maximum of 2 digits for the score calculate value.
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(numberOfDecimalsToShow);
        nf.setGroupingUsed(false);
        return nf.format(numberValue);
    }

    /**
     * another variant of formatReal method that defaults to show 2 digits of a Number. Method formatReal
     * 
     * @param numberValue
     * @return String
     */
    public static String formatReal(Number numberValue)
    {
        return formatReal(numberValue, 2);
    }
    
    /**
     * Method dateInRange. Determines if the target date is between the start date (inclusive) and end date (exclusive)
     * @param targetDate
     * @param startDate
     * @param endDate
     * @return boolean
     */
    public static boolean dateInRange(Date targetDate, Date startDate, Date endDate)
    {
        if (startDate == null && endDate == null)
            return true;
        else if (startDate == null)
            return targetDate.before(endDate);
        else if (endDate == null)
            return !targetDate.before(startDate);
        else
            return !targetDate.before(startDate) && targetDate.before(endDate);
    }
    
    // At some point this should probably try and use the JDBC database meta data class to determine 
    // what dates are supported by a given db.  To do this it would need to have a JDBC Connection to 
    // retrieve the meta data.  It would also need default actions if the database meta data methods were 
    // not implemented by the people who wrote the database drivers.
    /** Returns false when the database will throw a SQLException if the date is saved.
     * 
     * @param targetDate  The date being tested. 
     * @return False if the database would throw a SQLException if the date was saved.
     */
    public static boolean dbSupportsDateValue(Date targetDate){
        boolean returnValue = true;
        java.util.Calendar supportedDate = null;
        if ( targetDate != null ){
        	supportedDate = java.util.Calendar.getInstance();
        	supportedDate.set(1840, Calendar.DECEMBER, 31, 0,0, 0);
        	if ( targetDate.before(supportedDate.getTime())){
        		returnValue = false;
        	} else  {
            	supportedDate.set(9999, Calendar.DECEMBER, 31, 0,0, 0);
            	if ( targetDate.after(supportedDate.getTime())){
            		returnValue = false;
            	}
        	}
        }
        return returnValue;
    	
    }
    /**
     * Method parseFullName. Parse full name string into first, middle, and/or last name
     * fragments.  Only non-null StringBuffer parameters will be parsed. 
     * @param fullName
     * @param firstName
     * @param middleName
     * @param lastName 
     * @return void
     */
    public static void parseFullName(String fullName, StringBuffer firstName, StringBuffer middleName, StringBuffer lastName)
    {
        int commaPos = fullName.indexOf(',');
        
           // Parse first name if requested.
        if (firstName != null)
        {
            firstName.setLength(0);
            if (commaPos > -1)
            {
                String segment = fullName.substring(commaPos+1).trim();
                int lastSpacePos = segment.lastIndexOf(' ');
                if (lastSpacePos > -1)
                {
                    firstName.append(segment.substring(0, lastSpacePos));
                }
                else
                {
                    firstName.append(segment); 
                }
            }
         }
        
        // Parse middle name if requested.
        if (middleName != null)
        {
            middleName.setLength(0);
            if (commaPos > -1)
            {
                String segment = fullName.substring(commaPos+1).trim();
                int lastSpacePos = segment.lastIndexOf(' ');
                if (lastSpacePos > -1)
                {
                    middleName.append(segment.substring(lastSpacePos+1));
                }
            }
        }
        
        // Parse last name if requested
        if (lastName != null)
        {
            lastName.setLength(0);
            if (commaPos > -1)
                lastName.append(fullName.substring(0, commaPos).trim());
        }
    
    }
    
    /**
     * Format full name.
     * 
     * @param lastName the last name
     * @param firstName the first name
     * @param middleName the middle name
     * 
     * @return the string
     */
    public static String formatFullName(String lastName, String firstName, String middleName)
    {
    	StringBuffer fullName = new StringBuffer(32);
    	if (lastName != null && !lastName.trim().isEmpty())
    		fullName.append(lastName);
    	
    	if (firstName != null && !firstName.trim().isEmpty())
    	{
    		if (fullName.length() > 0)
    			fullName.append(", ");
    		
    		fullName.append(firstName);
    	}
    	
    	if (middleName != null && !middleName.trim().isEmpty())
    	{
    		if (fullName.length() > 0)
    			fullName.append(" ");
    		
    		fullName.append(middleName);
    	}
    	
    	return fullName.toString();
    }
    
    public static String formatFullNameWithMiddleInitial(String lastName, String firstName, String middleName){
		
		String middleNameFragment="";
		String returnValue = ""; 
		if (middleName != null && middleName.length() > 0) 
		{
			middleNameFragment = " " + middleName.charAt(0);
		}
		if ( lastName != null ){
			returnValue = lastName;
		} 
		if ( firstName != null ){
			returnValue = returnValue + ", " + firstName;
		}
		returnValue = returnValue + middleNameFragment;
		return returnValue;
	}
    
    /**
     * Method sameButNullIfEmpty, convert empty to null. 
     * @param value
     * @return String
     */
    public static String sameButNullIfEmpty(String value)
    {
        if (value != null && value.length() > 0)
            return value;
        else
            return null;
    }
    
    /**
     * Method sameButEmptyIfNull, convert null to empty. 
     * @param value
     * @return String
     */
    public static String sameButEmptyIfNull(String value)
    {
        if (value != null)
            return value;
        else
            return "";
    }
	/**
	 * Create a JDOM element from the given XML String 'value'.
	 */
	public static Element createDOMfromXMLString(String value) 
	{
		try
        {
            SAXBuilder builder = threadSaxBuilder.get();
            if(builder == null)
            {
            	builder = new SAXBuilder();
            	threadSaxBuilder.set(builder);
            }
            Element elm = builder.build(new StringReader(value)).getRootElement();
            elm.detach();
            return elm;
        }
        catch (JDOMException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
	}
    
    /**
     * Method createXMLfromDOM. Create a string representing XML data obtained from the given DOM element.
     * @param elm
     * @return String
     */
    public static String createXMLfromDOM(Element elm) {

        XMLOutputter xmlOutputter = new XMLOutputter(Format.getRawFormat().setOmitDeclaration(true));
        return Utils.replace(xmlOutputter.outputString(elm),"@","&#169;");
    }
    
    /**
     * Gets the class.
     * 
     * @param className the class name
     * 
     * @return the class
     */
    public static Class<?> getClass(String className)
    {
    	try
		{
			return Class.forName(className);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException("Error instantiating instance of class = " + className, e);
		}
    }
    
    /**
     * Method newClassInstance. Creates a new Class.forname instances and converts any errors to runtime errors.
     * @param className
     * @return Object
     */
    public static Object newClassInstance(String className) 
    {
        try
        {
            return Class.forName(className).newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error instantiating instance of class = " + className, e);
        }
   }
    
    /**
     * Method newClassInstance. Creates a new Class.forname instances and converts any errors to runtime errors.
     * @param <T>
     * @param classObject
     * @return Object
     */
    public static <T> Object newClassInstance(Class<T> classObject) 
    {
        try
        {
            return classObject.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Error instantiating instance of class = " + classObject.getName(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Error instantiating instance of class = " + classObject.getName(), e);
        }
   }
    
    
    /**
     * Determines if a test value is either blank or null 
     * @param testValue The string to be tested
     * @return True if the test value is blank or null.  Otherwise return false.
     */
    public static boolean isBlankOrNull(String testValue){
    	return (testValue == null || "".equals(testValue.trim()));
    }
    
    
    /**
     * Returns the difference in days between date1 and date2
     * @param date1
     * @param date2
     * @return int
     */
    public static int dateDiff(Date date1, Date date2)
	  {
	    Calendar cal1 = Calendar.getInstance();
	    Calendar cal2 = Calendar.getInstance();
	    cal1.setTime(date1);
	    cal2.setTime(date2);
	    int diff = 0;
	    
	    while(cal1.before(cal2))  
	    {
	      cal1.add(Calendar.DATE,1);
	      diff++;
	    }
	    return diff;
	  }
    
    /**
     * Encode the given string to be HTML compatible by replacing '<', '>' and other such characters.
     * @param s <code>String</code> to encode.
     * @return encoded string.
     */
    public static String encodeHTML(String s) {
        return Utils.encodeXML(s);
    }
    
    
    
	/**
	 * Appends information to the stringBuffer for output
	 * @param processPrefix
	 * @param timeState
	 * @param isNewLineNeeded
	 */
    //This method needs to be removed during the refactor of JMSMessagingSupport.
	public static void appendToStringBuffer(String processPrefix, StringBuffer logSB, String timeState, boolean isNewLineNeeded) {
		Calendar nowCal = Calendar.getInstance();
		
		if (processPrefix!=null && !processPrefix.equals("")) {
			// if there is a prefix to identify the process then append it
			logSB.append(processPrefix);
		}
		
		if (TIMESTATE_START.equalsIgnoreCase(timeState)) {
			logSB.append(" <START= ");
		}
		else if (TIMESTATE_END.equalsIgnoreCase(timeState)) {
			logSB.append(" <END= ");
		}
		else throw new RuntimeException("timeState is invalid");
		
		logSB.append(new SimpleDateFormat("hh:mm:ss.SSSS").format(nowCal.getTime())+">");
		
		if (isNewLineNeeded) {
			logSB.append("\n");
		}
	}
	
    /**
     * Formats the given date into the format "yyyy-MM-dd HH:mm:ss.SSSS".
     * 
     * @param date <code>Date</code> to be formatted.
     * @return formatted date or null if parameter is null.
     */
    public static String formatTimestamp(Date date) {
        if (date != null) {
            return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS")).format(date);
        }
        return null;
    }
    
    public static Date getDateInBusinessDays(Date startDate, Integer numberOfBusinessDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        Integer businessDayCount = 0;
        while(businessDayCount < numberOfBusinessDays) {
            cal.add(Calendar.DATE, 1);
            int day = cal.get(Calendar.DAY_OF_WEEK);
            if(day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
                businessDayCount++;
            }
        }
        return cal.getTime();
    }
    
	/**
	 * Build the date object according to the date and time value from the page.
	 * @param curDate
	 * the date string.
	 * @param curTime
	 * the time string.
	 * @return A Date object representing the date/time.
	 */
    public static Date buildDateTime(String curDate, String curTime, String meridiem)
	{
		Date newDate = null;
    	if(curDate != null &&  !"".equals(curDate.trim()) && curTime != null && !"".equals(curTime.trim())){
	    	String dateToParse = curDate + " " + curTime + " " + meridiem;
			DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
			try
			{
				newDate = format.parse(dateToParse);
			}
			catch (ParseException paex)
			{
				newDate = null;
			}
    	}else if(curDate != null &&  !"".equals(curDate.trim())){
    		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
    		try {
				newDate = format.parse(curDate);
			} catch (ParseException e) {
				newDate = null;
			}
    	}

		return newDate;
	}
    
	/**
	 * Parse the date object according to the formatString and return as String .
	 * @param date
	 * the date object.
	 * @param formatString
	 * the format string.
	 * @return A string representing the date in formatString format.
	 */
    public static String parseStringTimeByFormat(Date date, String formatString)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		SimpleDateFormat formatter = null;
		String dateTime = "";
		formatter = new SimpleDateFormat(formatString);
		dateTime = formatter.format(cal.getTime());
		return dateTime;
	}
    
    /** 
     * Return the Calendar object associated with the date.
     * @param date The calendar object associated with the date.
     * @return The Calendar object associated with the date.
     */
    public static Calendar getCalendar( Date date){
    	Calendar calDate = Calendar.getInstance();
    	calDate.setLenient(false);
    	calDate.setTime(date);
    	return calDate;
    }
    
	/**
	 * Validate the time string is of right format.
	 * @param timeValue
	 * @return boolean
	 */
    public static boolean validateTime(String timeValue){
		Boolean result = false;
		if (timeValue.trim().equalsIgnoreCase("")) return result;			
		int iPos = timeValue.indexOf(":");
		if (iPos > 0)
		{
			String hhStr = timeValue.substring(0, iPos);
			String mmStr = timeValue.substring(iPos+1,timeValue.length());
			if (Integer.parseInt(hhStr)<=12 && Integer.parseInt(hhStr)>0 
			   && Integer.parseInt(mmStr)<60 && Integer.parseInt(mmStr)>=0) result = true;			
		}
		return result;
	}
	        
    /**
     * Setup service locator shutdown hook (if not already setup).
     * This should be applied to JUnit test constructors to ensure that resources held onto by Spring
     * are cleanly release (including HibernateSessionFactory and data sources).
     * Main applications can also take advantage of this or simply all ServiceLocator.clearInstance in the
     * "main" finally clause.
     */
    private static boolean shutdownHookSetup = false;
    public static void setupServiceLocatorShutdown()
    {
    	if (!shutdownHookSetup)
    	{
    		// Avoid repeated hook setup
    		shutdownHookSetup = true;
    		
    		// Register the thread to shutdown the ServiceLocator
    		Runtime.getRuntime().addShutdownHook(new Thread()
    		{
    			@Override
    			public void run()
    			{
    				// Clear Spring
    				ServiceLocator.clearInstance();
    			}
    		});
    	}
    }

	public static long timeToLong(Object timeObj)
	{   
		long returnVal=0;
		if(timeObj instanceof Timestamp )
		{
			returnVal=((Timestamp)timeObj).getTime();
		}
		else if(timeObj instanceof Date)
		{
			returnVal=((Date) timeObj).getTime();
		}
		return returnVal;
	}

    /**
     * Encodes a string for HTML
     * @param str
     * @return
     */
    public static String htmlEncode(String str){
		return StringEscapeUtils.escapeHtml(str);
    }

	public static boolean formatDate(Date date) {
		boolean isValid=false;
		String formatDate=null;
		try{
			if(date!=null){
					SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					formatDate = df.format(date);
					isValid=isValidDate(formatDate);
			}
		}catch(Exception e){
			isValid=false;
		}
		return isValid;
	}
	
	/**
	 * This API convert given timestamp obj in SQL format to java.lang.date object (MM/dd/yyyy hh:mm:ss a or MM/dd/yyyy).
	 * param includeTime is ignored
	 * @param timeStampObj - TimeStamp or String value
	 * @param includeTime - true - format the output date object as MM/dd/yyyy hh:mm:ss a 
	 * 							   otherwise MM/dd/yyyy. *This is not implemented in this method.  TRUE is always used*
	 * @return Date Object
	 * * @exception IllegalArgumentException if the given timeStampObj is otherthan java.lang.Timestamp or String 
	 */
	public static Date getTimeStampDate(Object timeStampObj, boolean includeTime ) {
		Date timeStamp = null;
		if (timeStampObj instanceof Timestamp) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(parseDate(Utils.formatDate((Timestamp) timeStampObj,true)));
			timeStamp = cal.getTime();
		} else if(timeStampObj instanceof String){
			timeStamp = Utils.parseDate(Utils.formatDate((Timestamp.valueOf((String)timeStampObj)), true));
		} else if(timeStampObj instanceof Date){
			timeStamp = ((Date) timeStampObj);
		} else {
			throw new IllegalArgumentException("This API supports only java.sql.Timestamp or String datatype in timeStampObj");
		}

		return timeStamp;
	}
	
	/**
	 * Verifies that the object is not null
	 * 
	 * @param varName the name of the variable
	 * @param o the object to test
	 * @throws NullPointerException when the object is null, with the varName in the exception's message 
	 */
	public static void verifyArgumentNotNull(final String varName, final Object o)
	{
		if(o == null)
		{
			throw new NullPointerException("Argument \"" + varName + "\" cannot be null");
		}
	}
	
	/**
	 * Checks if is email format valid.
	 *
	 * @param emailAddress the email address
	 * @return true, if is email format valid
	 */
	private static final Pattern emailPattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)(@[a-zA-Z0-9_\\-\\.]+)$");
	public static boolean isEmailFormatValid(String emailAddress)
	{
		if (emailAddress == null || emailAddress.trim().length() == 0)
			throw new IllegalArgumentException("Do not empty email address to this method.");
		
		return emailPattern.matcher(emailAddress).matches();
	}
	
	/**
	 * Check for the Client Letter Enabled globals entry
	 * Returns True if Client Letter is enabled.
	 * Returns False if Correspondence Management is enabled
	 * Returns Null is error. Error is to be handled by caller.
	 * @return Boolean
	 */
	public static Boolean isClientLetterEnabled() {
		Boolean clientLetterEnabled = null;
		try {
			clientLetterEnabled = Globals.getBoolean("com.med.clientLetter.enabled");
		}catch (ConversionException conEx) {
			clientLetterEnabled = null;
		}catch (NoSuchElementException elEx) {
			clientLetterEnabled = null;
		}
		return clientLetterEnabled;
	}

	/**
	 * Will take a from and through date as well as a from and through date to compare these to determine if 
	 * they overlap in some way.
	 * Will return true if the dates overlap in some way and false if they do not overlap
	 * 
	 * @param fromDate
	 * @param throughDate
	 * @param comparedFromDate
	 * @param comparedThroughDate
	 * @return boolean
	 */
	public static Boolean containsOverlappingDates (Date fromDate, Date throughDate, Date comparedFromDate, Date comparedThroughDate){
		if ((fromDate != null && Utils.dateInRange(fromDate, comparedFromDate, comparedThroughDate)) 
				|| (throughDate != null && Utils.dateInRange(throughDate, comparedFromDate, comparedThroughDate)) 
					|| Utils.dateInRange(comparedFromDate, fromDate, throughDate)
						|| (comparedThroughDate != null && Utils.dateInRange(comparedThroughDate, fromDate, throughDate))
							|| (fromDate.equals(comparedFromDate) || throughDate.equals(comparedThroughDate))){
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a Calendar without time zone & time
	 * @param date The Date to convert to Calendar
	 * @return the Calendar
	 */
	public static Calendar getDateWithoutTimeZone(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = getCalendarWithoutTimeZone(date);
		cal.clear(Calendar.ZONE_OFFSET);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	/**
	 * Returns a Calendar with no time zone
	 * @param date The Date to convert to Calendar
	 * @return the Calendar
	 */
	public static Calendar getCalendarWithoutTimeZone(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.clear(Calendar.ZONE_OFFSET);
		return cal;
	}

	/**
	 * return fullName like: lastName, firstName M(middleName)
	 * @param firstName
	 * @param lastName
	 * @param midleName
	 * @return
	 */
	public static String getFullName(String firstName, String lastName, String midleName) {
		String fullName = lastName+", "+firstName;
		if(!Utils.isBlankOrNull(midleName)) {
			fullName += " "+ Character.toUpperCase(midleName.charAt(0));
		}
		return fullName;
	}
	
	/**
	 * Returns true if the month day and year for the calendar and date object are the same. 
	 * @param calendar The Calendar object.
	 * @param date The date object.
	 */
	public static boolean isEqual( Calendar calendar, Date date ){
		if ( calendar == null && date == null ){
			return true;
		}
		if ( calendar == null || date == null ){
			return false;
		}
		
		Calendar calendarValueForDateVariable = Calendar.getInstance();
		calendarValueForDateVariable.setTime(date);
	
		return calendarValueForDateVariable.get(Calendar.MONTH) ==  calendar.get(Calendar.MONTH) &&
			   calendarValueForDateVariable.get(Calendar.YEAR) ==  calendar.get(Calendar.YEAR) &&
			   calendarValueForDateVariable.get(Calendar.DAY_OF_MONTH) ==  calendar.get(Calendar.DAY_OF_MONTH);
		
	}
	
	/**
	 * Add escape charaters to special characters found in  String
	 * See  char[] specialCharacters for special chars that are handled.	
	 * @param anyString
	 * @return string with escape characters
	 */
	public static String handleRegexSpecialChars(String anyString){
		
		if (anyString == null) 
			return anyString;
		
    	StringBuffer validString = new StringBuffer();
    	char[] theChars = anyString.toCharArray();
    	char[] specialCharacters = new char[29];
    	boolean specialChar_f = false;
    	
    	specialCharacters[0] = '$';
    	specialCharacters[1] = '\\';
    	specialCharacters[2] = '@';
    	specialCharacters[3] = '#';
    	specialCharacters[4] = '%';
    	specialCharacters[5] = '!';
    	specialCharacters[6] = '^';
    	specialCharacters[7] = '&';
    	specialCharacters[8] = '*';
    	specialCharacters[9] = '(';
    	specialCharacters[10] = ')';
    	specialCharacters[11] = '_';
    	specialCharacters[12] = '+';
    	specialCharacters[13] = '=';
    	specialCharacters[14] = '-';
    	specialCharacters[15] = '{';
    	specialCharacters[16] = '}';
    	specialCharacters[17] = '|';
    	specialCharacters[18] = '[';
    	specialCharacters[19] = ']';
    	specialCharacters[20] = '.';
    	specialCharacters[21] = '?';
    	specialCharacters[22] = '<';
    	specialCharacters[23] = '>';
    	specialCharacters[24] = '~';
    	specialCharacters[25] = '`';
    	specialCharacters[26] = ':';
    	specialCharacters[27] = ';';
    	specialCharacters[28] = '"';

    	
    	
    	
     	for (char c : theChars){
    		specialChar_f = false;
    		for(char d : specialCharacters){
    			if(c == d){
    				validString.append("\\" + c);
    				specialChar_f = true;
    			}
    		}
    		if(!specialChar_f){
    			validString.append(c);
    		}
    	}
		return validString.toString();
    }

	/**
	 * This method takes date(MM/dd/yyyy) and time(hh:mm aa) as string and returns a calender obj 
	 * @param date
	 * @param time
	 * @return
	 */
	public static Calendar convertDateTimeStringToDateObj(String date, String time)
	{
        SimpleDateFormat dateformatter = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        Calendar calDate = Calendar.getInstance();
        Date dateobj = null;
         try {
              	dateobj = dateformatter.parse(date+" "+time);
         } 
         catch (ParseException e) 
         {
        	 	e.printStackTrace();
         }
         calDate.setTime(dateobj);
         return calDate;
    }
	
	/**
	 * This method return the min as string between two dates.
	 * @param startDate
	 * @param stopDate
	 * @return
	 */
    public static String dateDifferenceInMin(Calendar startDate,Calendar stopDate)
    {
         return ""+((stopDate.getTimeInMillis()-startDate.getTimeInMillis()))/(1000*60);
    }

	
}
