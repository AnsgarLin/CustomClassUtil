/*
 * Copyright (C) 2015 The Android Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.custom.rule;

public class NameStyle {
	boolean style = true;
	/**
	 * Stay consistent: Don't use multiple name for single meaning in a project
	 */
	// Bad
	int single, part, item, unit; // means "one"
	int list, collection, plural; // means "many"
	int add, insert, save, make; // means "create"
	int reset, unset, delete, unlink, drop; // means "remove"
	// Good: choose one and stick to it
	int singleOne; // means "one" in whole project
	int addTo; // means "create" in whole project

	/**
	 * Avoid redundancy with argument, while the method's function can be described well by putting class name and method name together.</p>
	 */
	// Bad: NameStyle -> changeStyle, duplicate on "Style"
	protected void changeStyle(boolean style) {}

	// Good: NameStyle -> change
	protected void change(boolean style) {}

	/**
	 * Don't use "AND" or "OR"
	 */
	// Bad
	protected void checkStyleAndRewrite(boolean style) {}
	// Good: Spread into methods
	protected void check() {}
	protected void Rewrite() {}

	/**
	 * Ask question for boolean
	 */
	protected boolean isStyleTrue() {
		return style == true;
	}
}
