/*
 * @author - Jeremy Trifilo (Digistr).
*/

package com;

import org.powerbot.game.api.methods.Environment;

public class RSItemDefinitionLoader
{
	public static final RSItemDefinition DUMMY_DEFINITION = new RSItemDefinition();
	private static final String DEFINITIONS = Environment.getStorageDirectory() + "/" + "def.dat";
	private static final String INDEX_LIST = Environment.getStorageDirectory() + "/" + "idx.dat";

	public static RSItemDefinition getRSItemDefinition(int itemId, int value)
	{
		FileBuilder stream = getStream(itemId);
		if (stream.readableBytes() > 13)
		{
			if (itemId == stream.readSignedShort())
				return new RSItemDefinition(stream, value);
		}
		return DUMMY_DEFINITION;
	}	

	private static FileBuilder getStream(int itemId) {
		FileBuilder idx = FileBuilder.read(INDEX_LIST, itemId * 4, 8);
		int start = idx.readInt();
		return FileBuilder.read(DEFINITIONS, start, idx.readInt() - start);
	}
}