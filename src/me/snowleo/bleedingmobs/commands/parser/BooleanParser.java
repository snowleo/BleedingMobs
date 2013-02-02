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

import java.util.Arrays;
import java.util.List;


public class BooleanParser extends SingleValueParser<Boolean>
{
	private static final List<String> VALID_VALUES = Arrays.asList(new String[]
			{
				"true", "false", "on", "off"
			});

	@Override
	protected Boolean parse(final String arg) throws InvalidArgumentException
	{
		if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("on"))
		{
			return Boolean.TRUE;
		}
		if (arg.equalsIgnoreCase("false") || arg.equalsIgnoreCase("off"))
		{
			return Boolean.FALSE;
		}
		throw new InvalidArgumentException();
	}

	@Override
	protected List<String> getValidValues()
	{
		return VALID_VALUES;
	}
}
