package com.github.jlgrock.javascriptframework.jspreprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Array;

import spock.lang.Specification;

class CompilePreProcessorTest extends Specification {
	def BufferedReader readerIn = Mock()

	def "test regex" () {
		expect:
		'$$assert()' =~ CompilePreProcessor.ASSERT_REGEX_STR
		'$$assert() //comments \'' =~ CompilePreProcessor.ASSERT_REGEX_STR  
		'	\t$$assert(f(x,y), "stuff") //comments \'' =~ CompilePreProcessor.ASSERT_REGEX_STR
		'   	return $$assert((x>=5)) ; //tests stuff' =~ CompilePreProcessor.ASSERT_REGEX_STR
		
		'goog.require("$$assert");' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR
		'  goog.require (\t"$$assert") ;' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR
		'  goog.require (\'$$assert\'\t); //asserts' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR
		!('goog.require("abc.def.ghi.jkl.Simple");' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR)
		
		'goog.provide("$$assert");' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR
		'  goog.provide (\t"$$assert") ;' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR
		'  goog.provide (\'$$assert\'\t); //asserts' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR
		!('goog.provide("abc.def.ghi.jkl.Simple");' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR)
		
		!('var abc = xyz;' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR)
		'$$assert = xyz;' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR
		'var xyz = $$assert' =~ CompilePreProcessor.ASSERT_REQUIRE_REGEX_STR
		
	}
	
	def "test startpoint" () {
		setup:
		def a1 = CompilePreProcessor.ASSERT_PATTERN.matcher('$$assert()')
		def a2 = CompilePreProcessor.ASSERT_PATTERN.matcher('$$assert() //comments \'')
		def a3 = CompilePreProcessor.ASSERT_PATTERN.matcher('	 \t$$assert(f(x,y), "stuff") //comments \'')
		def a4 = CompilePreProcessor.ASSERT_PATTERN.matcher('   	return $$assert((x>=5)) ; //tests stuff')
		a1.matches()
		a2.matches()
		a3.matches()
		a4.matches()

		expect:
		a1.start(1) == 0
		a2.start(1) == 0
		a3.start(1) == 3
		a4.start(1) == 11
	}
	
	def "can't find assertion" () {
		setup:
		char[] stringBuff = 'var x = myfunction( var1, var2 );'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		when:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == 'var x = myfunction( var1, var2 );'
		then:
		thrown(IllegalArgumentException)
	}

	def "assert expression is null" () {
		setup:
		char[] stringBuff = '()'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( null , null, { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'\' } );'
	}

	def "returns simple replacement?" () {
		setup:
		char[] stringBuff = '(x==2, "This is my message")'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( x==2 ,  "This is my message", { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'x==2\' } );'
	}

	def "returns replacement when there is no message" () {
		setup:
		char[] stringBuff = '(x>=5)'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( x>=5 , null, { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'x>=5\' } );'
	}
	
	def "extra spaces before expression" () {
		setup:
		char[] stringBuff = '  		\n(x>=5)'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( x>=5 , null, { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'x>=5\' } );'
	}
	
	def "typeof in expression" () {
		setup:
		char[] stringBuff = '(typeof(r)===\'object\',"r must be an object");'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( typeof(r)===\'object\' , "r must be an object", { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'typeof(r)===\\\'object\\\'\' } );'
	}
	
	def "function in expression" () {
		setup:
		char[] stringBuff = '(f(r,y)==\'object\', \'fx must be an object\');'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( f(r,y)==\'object\' ,  \'fx must be an object\', { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'f(r,y)==\\\'object\\\'\' } );'
	}
	
	def "line break after comma" () {
		setup:
		char[] stringBuff = '(f(r,y)==\'object\', \n\n "fr must be an object");'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( f(r,y)==\'object\' ,  \n\n "fr must be an object", { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'f(r,y)==\\\'object\\\'\' } );'
	}
	
	def "line breaks in expression" () {
		setup:
		char[] stringBuff = '(f(a,b)\n==\n\'object\', "f(a,b) must be an object");'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( f(a,b)\n==\n\'object\' ,  "f(a,b) must be an object", { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'f(a,b)==\\\'object\\\'\' } );'
	}
	
	def "line breaks in expression and message" () {
		setup:
		char[] stringBuff = '(f(d,e)\n==\n\'object\', "f(d,e) must be " + \n "an object");'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( f(d,e)\n==\n\'object\' ,  "f(d,e) must be \" + \n "an object", { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'f(d,e)==\\\'object\\\'\' } );'
	}
	
	def "has json object containing commas" () {
		setup:
		char[] stringBuff = '(functionEquals({\'x\' : \'5\', \'test\' : "mystring"}), \'check Equality\')'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( functionEquals({\'x\' : \'5\', \'test\' : "mystring"}) ,  \'check Equality\', { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'functionEquals({\\\'x\\\' : \\\'5\\\', \\\'test\\\' : "mystring"})\' } );'
	}
	
	def "has brackets and will have single quotes in the expression" () {
		setup:
		char[] stringBuff = '([1,2,3] == ["a", \'b\', "c"])'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( [1,2,3] == ["a", \'b\', "c"] , null, { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'[1,2,3] == ["a", \\\'b\\\', "c"]\' } );'
		
	}
	
	def "has comma in double quotes" () {
		setup:
		char[] stringBuff = '(f(x) == "This, the only way")'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( f(x) == "This, the only way" , null, { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'f(x) == "This, the only way"\' } );'
		
	}

	def "has comma in single quotes" () {
		setup:
		char[] stringBuff = '(f(x) == \'This, the only way\')'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( f(x) == \'This, the only way\' , null, { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'f(x) == \\\'This, the only way\\\'\' } );'
		
	}
	
	def "occurs multiple times on a line" () {
		setup:
		char[] stringBuff = '(x>=5)    $$assert(x>=5)'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( x>=5 , null, { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'x>=5\' } );'
		
	}
	def "ignores extra after" () {
		setup:
		char[] stringBuff = '(x>=5) ; //tests stuff'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( x>=5 , null, { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'x>=5\' } );'
	}

	def "handles escaped single quotes with commas"() {
		char[] stringBuff = '(x == \'I\\\'m here, you know, at the place?\', "my message")' ; //tests stuff'.toCharArray()
		Reader charReader = new CharArrayReader(stringBuff);
		readerIn = new BufferedReader(charReader);
		expect:
		CompilePreProcessor.assertReplacement(readerIn, 'test.js', '565', false) == '$$assert( x == \'I\\\'m here, you know, at the place?\' ,  "my message", { \'file\' : \'test.js\', \'line\' : \'565\', \'expression\' : \'x == \\\'I\\\\\\\'m here, you know, at the place?\\\'\' } );'
	}
	
	def "test escape String with single quotes"() {
		expect:
		CompilePreProcessor.escapeStringForJson("test's") == "test\\'s"
	}
	
	def "test escape String with backslashes"() {
		expect:
		CompilePreProcessor.escapeStringForJson("test \\t") == "test \\\\t"
	}
	
	def "test escape String with backslashes and quotes"() {
		expect:
		CompilePreProcessor.escapeStringForJson("test \\t steve's place") == "test \\\\t steve\\'s place"
	}
	
	def "buffer should throw exception for bad code" () {
		setup:
		char[] stringBuff = '''  $$assert, (y >= x , "My Message");'''.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		when:
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		then:
		thrown(IllegalArgumentException)
	
	}
	def "test read of small buffer"() {
		setup:
		char[] stringBuff = '   	return $$assert(x>=5) ; //tests stuff'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		expect:
		charWriter.toString() == '   	return $$assert( x>=5 , null, { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'x>=5\' } ); //tests stuff\r\n'
	}
	
	def "occurs multiple times in a buffer" () {
		setup:
		char[] stringBuff = 'return $$assert(x>=5);    $$assert(x>=5)'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		expect:
		charWriter.toString() == 'return $$assert( x>=5 , null, { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'x>=5\' } );    $$assert( x>=5 , null, { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'x>=5\' } );'
	}
	
	def "ignore comment block in a buffer" () {
		setup:
		char[] stringBuff = '''  $$assert (y >= x /*this is the random comment
that spans multiple lines before finishing */, "My Message");'''.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		expect:
		charWriter.toString() == '''  $$assert( y >= x /*this is the random comment
that spans multiple lines before finishing */ ,  "My Message", { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'y >= x /*this is the random commentthat spans multiple lines before finishing */' } );'''
	
	}
	
	def "ignore comment at end of line in a buffer"() {
		setup:
		char[] stringBuff = '''  $$assert (y >= x, //this is the random comment at the end of a line
													//that continues on multiple lines
								 "My Message");'''.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		expect:
		charWriter.toString() == '''  $$assert( y >= x ,  //this is the random comment at the end of a line
													//that continues on multiple lines
								 "My Message", { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'y >= x\' } );'''
	
	}
	
	def "missing ending paren in a buffer" () {
		setup:
		char[] stringBuff = 'return $$assert(x>=5'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		when:
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		then:
		thrown(IllegalArgumentException)
		
	}
	
	def "run into semicolon before ending paren in a buffer" () {
		char[] stringBuff = 'return $$assert(x>=5;'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		when:
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		then:
		thrown(IllegalArgumentException)
	}
	
	def "run into eof before semicolor or end paren" () {
		char[] stringBuff = 'return $$assert(x>=5'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		when:
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		then:
		thrown(IllegalArgumentException)
	}
	
	def "too many characters before end of assert" () {
		char[] stringBuff = '''return $$assert(                       
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
                                                              
x>4)'''.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		when:
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		then:
		thrown(IllegalArgumentException)
	}
	
	def "run into semicolon within string in a buffer" () {
		setup:
		char[] stringBuff = 'return $$assert(x>=\';\')'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		expect:
		charWriter.toString() == 'return $$assert( x>=\';\' , null, { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'x>=\\\';\\\'\' } );'
		
	}
	
	def "run into semicolon within double quoted string in a buffer" () {
		setup:
		char[] stringBuff = 'return $$assert(x>=";")'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		expect:
		charWriter.toString() == 'return $$assert( x>=";" , null, { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'x>=";"\' } );'
		
	}
	
	def "remove assertions" () {
		setup:
		char[] stringBuff = 'return $$assert(x>=";")'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", true)
		expect:
		charWriter.toString() == 'return '
		
	}
	
	def "doesn't match goog.requires" () {
		setup:
		char[] stringBuff = '   goog.require   ("\$\$assert") ; '.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", true)
		expect:
		charWriter.toString() == '   goog.require   ("\$\$assert") ; \r\n'
	}
	
	def "parsing example 1" () {
		setup:
		char[] stringBuff = '	$$assert(array!==null, "array cannot be null");'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		expect:
		charWriter.toString() == '	$$assert( array!==null ,  "array cannot be null", { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'array!==null\' } );'
	}
	def "parsing example 2" () {
		setup:
		char[] stringBuff = '	$$assert(array instanceof Array, "array must be an array");'.toCharArray()
		def charReader = new CharArrayReader(stringBuff)
		def charWriter = new CharArrayWriter()
		CompilePreProcessor.readAndWriteBuffer(charReader, charWriter, "testfile.js", false)
		expect:
		charWriter.toString() == '	$$assert( array instanceof Array ,  "array must be an array", { \'file\' : \'testfile.js\', \'line\' : \'1\', \'expression\' : \'array instanceof Array\' } );'
	}
	
}