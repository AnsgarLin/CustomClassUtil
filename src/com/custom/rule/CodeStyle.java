/**
 * Column limit: 80 or 100(Android doc recommended)
 * 
 * Exceptions:
 * 1. Lines where obeying the column limit is not possible (for example, a long URL in Javadoc, or a long JSNI method reference).
 * 2. package and import statements (see Sections 3.2 Package statement and 3.3 Import statements).
 * 3. Command lines in a comment that may be cut-and-pasted into a shell.
 */
/**
 * The package statement is not line-wrapped
 */
package com.custom.rule;

import java.util.HashSet;
import java.util.Set;
/**
 * Import statements are not line-wrapped.
 * 
 * Import statements are divided into the following groups, in this order, with each group separated by a single blank line:
 * 1. All static imports in a single group
 * 2. com.google imports (only if this source file is in the com.google package space)
 * 3. Third-party imports, one group per top-level package, in ASCII sort order
 * 4. for example: android, com, junit, org, sun
 * 5. java imports
 * 6. javax imports
 * Within a group there are no blank lines, and the imported names appear in ASCII sort order.
 */

/**
 * Vertical Whitespace 
 * A single blank line appears:
 * 1. Between consecutive members (or initializers) of a class: fields, constructors, methods, nested classes, static initializers, instance initializers.
 * 1.a. Exception: A blank line between two consecutive fields (having no other code between them) is optional. Such blank lines are used as needed to create logical groupings of fields.
 * 2. Within method bodies, as needed to create logical groupings of statements.
 * 3. Optionally before the first member or after the last member of the class (neither encouraged nor discouraged).
 * 4. As required by other sections of this document (such as Section 3.3, Import statements).
 * Multiple consecutive blank lines are permitted, but never required (or encouraged).
 */

/**
 * Horizontal whitespace
 * 
 * 1. Separating any reserved word, such as if, for or catch, from an open parenthesis (() that follows it on that line. 
 *    ex: if (, for (, catch (
 * 2. Separating any reserved word, such as else or catch, from a closing curly brace (}) that precedes it on that line.
 *    ex: } else, * } catch
 * 3. Before any open curly brace ({).
 *    ex: Class Test { exception: @SomeAnnotation({a, b}) String[][] x = {{"foo"}}; (no space is required between {{, by item 8 below)
 * 4. On both sides of any binary or ternary operator.
 *    ex: a == b, a + b, a - b, Foo & Bar, FooException | BarException, for(String temp : a){} 
 * 5. After ,:; or the closing parenthesis ()) of a cast.
 *    ex: (a, b), (a : b), fun() {
 * 6. On both sides of the double slash (//) that begins an end-of-line comment. Here, multiple spaces are allowed, but not required.
 *    ex: // , 
 * 7. Between the type and variable of a declaration.
 *    ex: List<String> list
 */

/**
 * Class names are written in UpperCamelCase. 
 * Test classes are named starting with the name of the class they are testing, and ending with Test.
 * For example, HashTest or HashIntegrationTest.
 */
/**
 * Here should have the class or interface declaration to describe what the class or interface does.
 * 
 * Class for implementing playfair's algorithm
 */
public class CodeStyle {
	/**
	 * Access Levels
	 * 
	 * Modifier | Class | Package | Subclass | World | public | Y | Y | Y | Y | protected| Y | Y | Y | N | default | Y | Y | N | N | private | Y | N |
	 * N | N |
	 */
	/**
	 * Follow Field Naming Conventions
	 */
	// Non-public, non-static field names start with m.
	int mPackagePrivate;
	private int mPrivate;
	protected int mProtected;
	// Static field names start with s.
	private static String sSingleton;
	// Other fields start with a lower case letter.
	public int publicField;

	/**
	 * Follow up, Constant names use CONSTANT_CASE: all uppercase letters, with words separated by underscores. But what is a constant, exactly?
	 */
	/*
	 * Constants
	 */
	static final int NUMBER = 5;

	// static final ImmutableList<String> NAMES = ImmutableList.of("Ed", "Ann");
	// static final Joiner COMMA_JOINER = Joiner.on(','); // because Joiner is immutable
	// static final SomeMutableType[] EMPTY_ARRAY = {};
	enum SomeEnum {
		ENUM_CONSTANT
	}

	/*
	 * Not Constants
	 */
	static String nonFinal = "non-final";
	final String nonStatic = "non-static";
	static final Set<String> mutableCollection = new HashSet<String>();
	// static final ImmutableSet<SomeMutableType> mutableElements = ImmutableSet.of(mutable);
	// static final Logger logger = Logger.getLogger(MyClass.getName());
	static final String[] nonEmptyArray = { "these", "can", "change" };

	/**
	 * Class members should be ordered in some logical order
	 */
	private static final int TABLE_COLUMN_COUNT = 5;
	/**
	 * One variable per declaration
	 */
	private static final int TABLE_SIZE = TABLE_COLUMN_COUNT * TABLE_COLUMN_COUNT;

	/**
	 * Any array initializer may optionally be formatted as if it were a "block-like construct."
	 */
	private int[] arrayTypeOne = new int[] { 0, 1, 2, 3 };
	private int[] arrayTypeSec = new int[] { 0, 1, 2, 3 };
	private int[] arrayTypeThird = new int[] { 0, 1, 2, 3 };

	/**
	 * When line-wrapping, each line after the first (each continuation line) is indented at least +4(ex: 8) from the original line
	 */
	private int[] intArray = new int[] { 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2,
			3, 0, 1, 2, 3, };

	/**
	 * Never split multiple methods with the same name.
	 */
	public CodeStyle() {
		super();
	}

	private int initValue;

	public CodeStyle(int initValue) {
		this.initValue = initValue;
	}

	/**
	 * An empty block or block-like construct may be closed immediately after it is opened, with no characters or line break in between ({}),
	 */
	private void doNothing() {}

	// unless it is part of a multi-block statement like if/else-if/else or try/catch/finally.
	private void doSomething() {
		int condition = 0;
		/**
		 * Grouping parentheses:
		 * 
		 * Optional grouping parentheses, "()", are omitted only when author and reviewer agree that there is no reasonable chance the code will be
		 * misinterpreted without them, nor would they have made the code easier to read. It is not reasonable to assume that every reader has the
		 * entire Java operator precedence table memorized.
		 */
		if (((condition != 0) && (condition > 0)) || ((condition != 0) && (condition < 0))) {
		} else {
		}
	}

	/**
	 * Braces follow the Kernighan and Ritchie style ("Egyptian brackets") for nonempty blocks and block-like constructs
	 */
	/**
	 * Method names are written in lowerCamelCase.
	 */
	/**
	 * Write Short Methods, which means less than 40 lines.
	 */
	// No line break before the opening brace.
	public void exampleMethord() { // Line break after the opening brace.
		/**
		 * Local variables are declared close to the point they are first used (within reason), to minimize their scope. Local variable declarations
		 * typically have initializers, or are initialized immediately after declaration.
		 */
		boolean condition = false;
		if (condition) {
			// Line break before the closing brace.
		} else {
			// No line break after the brace if it is followed by else or a comma.
		}
		// Line break after the closing brace if that brace terminates a statement or the body of a method, constructor or named class
	}

	/**
	 * An enum class with no methods and no documentation on its constants may optionally be formatted as if it were an "array" initializer
	 */
	private enum EnumVariable {
		CLUBS, HEARTS, SPADES, DIAMONDS
	}

	/**
	 * Enum classes are classes, all other rules for formatting classes apply.
	 */
	private enum EnumClass {
		CLUBS, HEARTS, SPADES, DIAMOND;

		private final int value;

		private EnumClass() {
			this.value = this.ordinal();
		}

		public int getValue() {
			return value;
		}
	}
	/**
	 * Javadoc is not always present on a method that overrides a supertype method.
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
