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

import java.util.ArrayList;
import java.util.List;


public class BoundedIntegerParser extends IntegerParser
{
	private final List<String> validValues = new ArrayList<String>();
	private final int lower;
	private final int upper;

	public BoundedIntegerParser(final int lower, final int upper)
	{
		super();
		this.lower = lower;
		this.upper = upper;
	}

	@Override
	protected Integer parse(final String arg) throws InvalidArgumentException
	{
		Integer value = super.parse(arg);
		if (value < lower || value > upper)
		{
			throw new InvalidArgumentException();
		}
		return value;
	}

	@Override
	protected List<String> getValidValues()
	{
		if (validValues.isEmpty())
		{
			for (int i = lower; i <= upper; i++)
			{
				validValues.add(String.valueOf(i));
			}
		}
		return validValues;
	}
}
