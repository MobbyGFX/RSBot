/*
 * @author - Jeremy Trifilo (Digistr).
*/

package com;

import java.net.*;
import java.io.*;
import java.util.*;

public class OnDemandLoader
{
	private static final String GRAND_EXCHANGE_BASE = "http://services.runescape.com/m=itemdb_rs/api/graph/";
	private static final Map<Short, RSItemDefinition> DEFINITIONS = new HashMap<Short, RSItemDefinition>();
	private static final ItemContainer REQUESTS = new ItemContainer(4);

	public static RSItemDefinition getRSItemDefinition(short itemId)
	{
		if(DEFINITIONS.get(itemId) == null) 
		{
			REQUESTS.add(itemId);
			return null;
		}
		return DEFINITIONS.get(itemId);
    	}

	public static void handleRequests()
	{
		int size = REQUESTS.size();
		for (int i = 0; i < size; i++)
		{
			short id;
			if ((id = REQUESTS.poll()) == -1)
				break;
			if (DEFINITIONS.get(id) == null)
			{
				RSItemDefinition definition = sendRequest(id);
				if (definition != null)
				{
					DEFINITIONS.put(id, definition);
				}
			}
		}
	}

	private static RSItemDefinition sendRequest(short itemId)
	{
		try {
			StringBuilder sb = new StringBuilder(64);
			sb.append(GRAND_EXCHANGE_BASE);
			sb.append(itemId);
			sb.append(".json?item=");
			sb.append(itemId);
			URL url = new URL(sb.toString());
        		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        		char[] input = in.readLine().toCharArray();
        		in.close();
			int value = formatExchangeValue(input);
			return RSItemDefinitionLoader.getRSItemDefinition(itemId, value);
		} catch (FileNotFoundException fnf) {

		} catch (MalformedURLException mu) {
			return null;
		} catch (IOException io) {
			return null;
		}
		return RSItemDefinitionLoader.getRSItemDefinition(itemId, -1);
	}

	private static int formatExchangeValue(char[] input)
	{
		int end = -1;
		for (int i = 0; i < input.length; i++)
		{
			if (input[i] == '}')
			{
				end = i;
				break;
			}
		}
		if (end != -1)
		{
			int start = -1;
			for (int i = end - 1; i >= 0; --i)
			{
				if (input[i] == ':')
				{
					start = i + 1;
					break;
				}
			}
			if (start != -1 && end > start)
			{
				return Integer.parseInt(new String(input, start, end - start));
			}
		}
		return -1;
	}

}