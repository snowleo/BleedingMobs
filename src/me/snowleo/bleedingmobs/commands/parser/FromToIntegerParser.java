/*
 * BleedingMobs - make your monsters and players bleed
 *
 * Copyright (C) 2011-2012 snowleo
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.snowleo.bleedingmobs.commands.parser;

import java.util.Collections;
import java.util.List;


public class FromToIntegerParser extends DoubleValueParser<Integer[]>
{
	private final IntegerParser integerParser;

	public FromToIntegerParser(final IntegerParser integerParser)
	{
		this.integerParser = integerParser;
	}

	@Override
	protected Integer[] parse(final String arg1, final String arg2) throws InvalidArgumentException
	{
		Integer[] values = new Integer[2];
		values[0] = integerParser.parse(arg1);
		values[1] = integerParser.parse(arg2);
		if (values[0] > values[1])
		{
			throw new InvalidArgumentException();
		}
		return values;
	}

	@Override
	protected List<String> getValidFirstValues()
	{
		return Collections.emptyList();
	}

	@Override
	protected List<String> getValidSecondValues(final String arg1)
	{
		return Collections.emptyList();
	}
}
