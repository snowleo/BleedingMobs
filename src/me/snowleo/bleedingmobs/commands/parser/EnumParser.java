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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class EnumParser<E extends Enum<E>> extends SingleValueParser<E>
{
	private final Map<String, E> enumMap = new HashMap<String, E>();

	public EnumParser(final Class<E> enumValue)
	{
		for (E t : enumValue.getEnumConstants())
		{
			enumMap.put(t.name().replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH), t);
		}
	}

	@Override
	protected E parse(final String arg) throws InvalidArgumentException
	{
		E e = enumMap.get(arg.replaceAll("[_-]", "").toLowerCase(Locale.ENGLISH));
		if (e == null)
		{
			throw new InvalidArgumentException();
		}
		return e;
	}

	@Override
	protected List<String> getValidValues()
	{
		return new ArrayList<String>(enumMap.keySet());
	}
}
