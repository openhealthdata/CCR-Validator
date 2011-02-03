/*
 * Copyright 2010 OpenHealthData, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openhealthdata.validator;

import java.io.File;
import java.io.FilenameFilter;

import org.drools.builder.ResourceType;

public class DroolsUtil {

	public static FilenameFilter getFilter(ResourceType rt) {
		if (rt == ResourceType.DRL) {
			return new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".drl");
				}
			};
		}
		
		if (rt == ResourceType.DRF) {
			return new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".drf");
				}
			};
		}
		
		// TODO Finish adding the other resource type file filter
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".drl");
			}
		};
		
	}
}
