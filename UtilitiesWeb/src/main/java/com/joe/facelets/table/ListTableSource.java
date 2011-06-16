package com.joe.facelets.table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public class ListTableSource extends TableSource
{
	private List<Row> allRows = new ArrayList<Row>();

	private List<Row> visibleRows = new ArrayList<Row>();

	private Comparator<Row> defaultSort = null;

	public ListTableSource(List<Column> columns, List<Row> rows)
	{
		this(columns, rows, null);
	}

	public ListTableSource(List<Column> columns, List<Row> rows, Comparator<Row> defaultSort)
	{
		super(columns);
		this.defaultSort = defaultSort;
		setData(rows);
	}

	public Comparator<Row> getDefaultSort()
	{
		return defaultSort;
	}

	public void setDefaultSort(Comparator<Row> defaultSort)
	{
		this.defaultSort = defaultSort;
	}

	public void setData(List<Row> rows)
	{
		this.allRows = rows;
		this.visibleRows = rows;
		refresh();
	}

	@Override
	public DataModel getDataModel()
	{
		if (hasSortChanged())
		{
			refresh();
		}
		if (hasFilterChanged())
		{
			filterRows();
		}
		return new ListDataModel(visibleRows);
	}

	public void refresh()
	{
		sortRows(allRows);
		filterRows();
	}

	public void sortRows(List<Row> rows)
	{
		if (defaultSort != null && (sortKey == null || "".equals(sortKey)))
		{
			Collections.sort(rows, defaultSort);
		}
		else
		{
			CustomComparator sort = null;

			Column sortColumn = getColumn(sortKey);
			if (sortColumn != null)
			{
				sort = sortColumn.getSort();
			}

			if (sort != null)
			{
				sort.setDecending(sortDecending);
				Collections.sort(rows, sort);
			}
			else
			{
				Collections.sort(rows, new RowComparator(sortKey, sortDecending));
			}
		}
		oldKey = sortKey;
		oldDecending = sortDecending;
	}

	public Column getColumn(String name)
	{
		for (Column c : columns)
		{
			if (c.getKey().equals(name))
			{
				return c;
			}
		}
		return null;
	}

	public void filterRows()
	{
		visibleRows = new ArrayList<Row>();
		for (Row row : allRows)
		{
			boolean match = true;
			for (Column c : columns)
			{
				if (c.getFilter() != null)
				{
					Object o = row.get(c.getKey());
					if (o == null)
						o = "";
					if (!c.getFilter().isMatch(o))
					{
						match = false;
						break;
					}
				}
			}
			if (match)
			{
				visibleRows.add(row);
			}
		}
	}

	public static class RowComparator extends CustomComparator
	{
		String key;

		public RowComparator(String key, boolean dec)
		{
			this.key = key;
			this.setDecending(dec);
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare(Row o1, Row o2)
		{
			Comparable val1 = (Comparable) o1.get(key);
			Comparable val2 = (Comparable) o2.get(key);
			int direction = (isDecending()) ? 1 : -1;
			if (val1 == null && val2 == null)
				return 0;
			if (val1 == null)
			{
				return direction * 1;
			}
			if (val2 == null)
			{
				return direction * -1;
			}
			return direction * val1.compareTo(val2);
		}
	}

	public static class MultiKeyComparator extends CustomComparator
	{
		final LinkedHashMap<String, String> keys;

		public MultiKeyComparator()
		{
			this.keys = new LinkedHashMap<String, String>();
		}

		public void addSortKey(final String key)
		{
			keys.put(key, "");
		}

		public void addSortKey(final String key, final String dateFormat)
		{
			keys.put(key, dateFormat);
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare(final Row o1, final Row o2)
		{
			boolean isFirstColumn = true;

			for (final Map.Entry<String, String> entry : this.keys.entrySet())
			{
				final String key = entry.getKey();
				final String dateFormat = entry.getValue();
				final int direction;
				if (isFirstColumn)
				{
					isFirstColumn = false;
					direction = (super.decending ? 1 : -1);
				}
				else
				{
					// Secondary sort is always ascending (A-Z)
					direction = 1;
				}

				final Comparable val1 = formatForSort(o1.get(key), dateFormat);
				final Comparable val2 = formatForSort(o2.get(key), dateFormat);

				if(val1 == null && val2 == null) continue;
				if(val1 == null) {
					return direction * -1;
				} 
				if(val2 == null) {
				  return direction * 1;
				}
				
				final int compare = val1.compareTo(val2);
				if (0 != compare)
				{
					return direction * compare;
				}
			}
			return 0;
		}

	}

	@SuppressWarnings("unchecked")
	private static Comparable formatForSort(final Object obj, final String dateFormat)
	{
		final Comparable cmp;
		
		if (obj == null) return null;

		if (obj instanceof String)
		{
			if (null != dateFormat && 0 < dateFormat.length())
			{
				cmp = formatDateForSort((String) obj, dateFormat);
			}
			else
			{
				cmp = ((String) obj).toLowerCase().trim();
			}
		}
		else
		{
			cmp = (Comparable) obj;
		}

		return (null != cmp ? cmp : "");
	}

	private static Date formatDateForSort(final String s, final String dateFormat)
	{
		Date d = null;
		try
		{
			d = new SimpleDateFormat(dateFormat).parse(s);
		}
		catch (final ParseException ex)
		{
			// Do nothing.
		}
		return d;
	}

	public static abstract class CustomComparator implements Comparator<Row>
	{
		boolean decending = true;

		public boolean isDecending()
		{
			return decending;
		}

		public void setDecending(boolean decending)
		{
			this.decending = decending;
		}
	}

	@Override
	public List<Row> getFilteredRows()
	{
		return visibleRows;
	}

	@Override
	public List<Row> getRows()
	{
		return allRows;
	}

}
