package com.joe.utilities.core.util;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;



/**
 * The Class XMLStreamElementReader.
 * Abstraction above an XMLStreamReader which return a targeted element on the given stream.
 * For a given block of XML
 * 
 * 	<file>
 * 			<record...     </record>
 * 			<record...     </record>
 * 			<record...     </record>
 * 	</file>
 * 
 * This method can read/iterate retrieving the "<record...      </record>" elements 
 */
public class XMLStreamElementReader
{
	
	/** The underlying stax xml reader. */
	private XMLStreamReader staxXmlReader;
	
	/** The element name. */
	private String elementName;

	/**
	 * Instantiates a new xML stream element reader.
	 *
	 * @param staxXmlReader the stax xml reader
	 * @param elementName the element name
	 */
	public XMLStreamElementReader(XMLStreamReader staxXmlReader, String elementName)
	{
		this.staxXmlReader = staxXmlReader;
		this.elementName = elementName;
	}

	/**
	 * Next.
	 *
	 * @return the string
	 * @throws XMLStreamException the xML stream exception
	 */
	public String next() throws XMLStreamException
	{
		for (int event = staxXmlReader.next(); event != XMLStreamConstants.END_DOCUMENT; event = staxXmlReader.next())
		{
			if (event == XMLStreamConstants.START_ELEMENT)
			{
				// Located start element
				if (staxXmlReader.getLocalName().equals(elementName))
				{
					// Copy contents of 
					StringWriter xmlStringWriter = new StringWriter(256);
					XMLOutputFactory factory = XMLOutputFactory.newInstance();
					XMLStreamWriter staxXmlWriter = factory.createXMLStreamWriter(xmlStringWriter);
					StaxUtils.copyCurrentElement(staxXmlReader, staxXmlWriter);
					return xmlStringWriter.toString();
				}
			}
		}

		return null;
	}
	
	/**
	 * Close underlying reader
	 *
	 * @throws XMLStreamException the xML stream exception
	 */
	public void close() throws XMLStreamException
	{
		if (staxXmlReader!=null)
			staxXmlReader.close();
	}
}
