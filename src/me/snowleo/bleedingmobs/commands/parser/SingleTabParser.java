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

import java.util.List;


public abstract class SingleTabParser<T> extends AbstractParser<T>
{
	@Override
	public final List<String> getTabValues(String[] args)
	{
		List<String> validValues = getValidValues();
		if (args.length == 0)
		{
			return validValues;
		}
		return searchList(validValues, prepareTabValue(args[0]));
	}

	public String prepareTabValue(String arg)
	{
		return arg;
	}

	public abstract List<String> getValidValues();
}
