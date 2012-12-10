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


public class IntegerParser extends SingleValueParser<Integer>
{
	private static final List<String> validValues = Collections.emptyList();

	@Override
	public Integer parse(String arg) throws InvalidArgumentException
	{
		try
		{
			return Integer.parseInt(arg);
		}
		catch (NumberFormatException e)
		{
			throw new InvalidArgumentException();
		}
	}

	@Override
	public List<String> getValidValues()
	{
		return validValues;
	}
}
