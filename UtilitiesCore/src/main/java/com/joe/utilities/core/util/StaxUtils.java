package com.joe.utilities.core.util;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;

public class StaxUtils
{
	/**
	 * Copies the reader to the writer. The start and end document methods must
	 * be handled on the writer manually. 
	 *
	 * @param reader the reader
	 * @param writer the writer
	 * @throws XMLStreamException the xML stream exception
	 */
	public static void copyCurrentElement(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException
	{
		copyCurrentElement(reader, writer, true);
	}
	
	/**
	 * Copies the reader to the writer for the current element. The start and end document methods must
	 * be handled on the writer manually. 
	 *
	 * @param reader the reader
	 * @param writer the writer
	 * @param trimText the trim text
	 * @throws XMLStreamException the xML stream exception
	 */
	public static void copyCurrentElement(XMLStreamReader reader, XMLStreamWriter writer, boolean trimText) throws XMLStreamException
	{

		if (reader.getEventType() != XMLStreamConstants.START_ELEMENT)
			throw new IllegalArgumentException("Reader must be at start element to perform copy");

		int event = reader.getEventType();
		String currentElementName = reader.getLocalName();

		while (reader.hasNext())
		{

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
					writeStartElement(reader, writer);
					break;
				case XMLStreamConstants.END_ELEMENT:
					writer.writeEndElement();
					if (reader.getLocalName().equals(currentElementName))
						return;
					break;
				case XMLStreamConstants.CHARACTERS:
					if (trimText)
						writer.writeCharacters(reader.getText().trim());
					else
						writer.writeCharacters(reader.getText());
					break;
				case XMLStreamConstants.COMMENT:
					if (trimText)
						writer.writeComment(reader.getText().trim());
					else
						writer.writeComment(reader.getText());
					break;
				case XMLStreamConstants.CDATA:
					if (trimText)
						writer.writeCData(reader.getText().trim());
					else
						writer.writeCData(reader.getText());
					break;
				case XMLStreamConstants.START_DOCUMENT:
				case XMLStreamConstants.END_DOCUMENT: 
				case XMLStreamConstants.ATTRIBUTE:
				case XMLStreamConstants.NAMESPACE:
					break;
				default:
					break;
			}
			event = reader.next();
		}
	}

	/**
	 * Write start element.
	 *
	 * @param reader the reader
	 * @param writer the writer
	 * @throws XMLStreamException the xML stream exception
	 */
	private static void writeStartElement(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException
	{
		String local = reader.getLocalName();
		String uri = reader.getNamespaceURI();
		String prefix = reader.getPrefix();
		if (prefix == null)
		{
			prefix = "";
		}

		// local + " namespace URI" + uri);
		boolean writeElementNS = false;
		if (uri != null)
		{
			String boundPrefix = writer.getPrefix(uri);
			if (boundPrefix == null || !prefix.equals(boundPrefix))
			{
				writeElementNS = true;
			}
		}

		// Write out the element name
		if (uri != null)
		{
			if (prefix.length() == 0 && StringUtils.isEmpty(uri))
			{
				writer.writeStartElement(local);
				writer.setDefaultNamespace(uri);

			}
			else
			{
				writer.writeStartElement(prefix, local, uri);
				writer.setPrefix(prefix, uri);
			}
		}
		else
		{
			writer.writeStartElement(local);
		}

		// Write out the namespaces
		for (int i = 0; i < reader.getNamespaceCount(); i++)
		{
			String nsURI = reader.getNamespaceURI(i);
			String nsPrefix = reader.getNamespacePrefix(i);
			if (nsPrefix == null)
			{
				nsPrefix = "";
			}

			if (nsPrefix.length() == 0)
			{
				writer.writeDefaultNamespace(nsURI);
			}
			else
			{
				writer.writeNamespace(nsPrefix, nsURI);
			}

			if (nsURI.equals(uri) && nsPrefix.equals(prefix))
			{
				writeElementNS = false;
			}
		}

		// Check if the namespace still needs to be written.
		// We need this check because namespace writing works
		// different on Woodstox and the RI.
		if (writeElementNS)
		{
			if (prefix == null || prefix.length() == 0)
			{
				writer.writeDefaultNamespace(uri);
			}
			else
			{
				writer.writeNamespace(prefix, uri);
			}
		}

		// Write out attributes
		for (int i = 0; i < reader.getAttributeCount(); i++)
		{
			String ns = reader.getAttributeNamespace(i);
			String nsPrefix = reader.getAttributePrefix(i);
			if (ns == null || ns.length() == 0)
			{
				writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
			}
			else if (nsPrefix == null || nsPrefix.length() == 0)
			{
				writer.writeAttribute(reader.getAttributeNamespace(i), reader.getAttributeLocalName(i), reader.getAttributeValue(i));
			}
			else
			{
				writer.writeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i), reader.getAttributeLocalName(i), reader.getAttributeValue(i));
			}

		}
	}
}
