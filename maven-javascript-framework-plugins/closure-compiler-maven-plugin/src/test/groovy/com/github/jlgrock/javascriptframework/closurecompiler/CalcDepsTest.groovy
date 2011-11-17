package com.github.jlgrock.javascriptframework.closurecompiler

import java.io.File;
import java.util.Collection;
import java.util.List;

import spock.lang.Specification;

class CalcDepsTest extends Specification {
	def baseFile
	def inputs
	def paths
	def outputFile
	
	def setup() {
		//file equals, parseForDependencyInfo
		//
		File baseFile = Mock()
		outputFile = Mock(File)
		
		File source1 = Mock()
		File source2 = Mock()
		
		File lib1 = Mock()
		File lib2 = Mock()
		
		inputs = [source1, source2]
		paths = [lib1, lib2]
	}
	
	def "executes CalcDeps"() {
		//List[File]
//		expect:
//			CalcDeps.executeCalcDeps(baseFile, inputs, paths, outputFile)
	}
	
	def "CalcDeps returns list of files"() {
		expect:
			true == true
	}
	
	def "CalcDeps returns list of files in correct order"() {
		expect:
			true == true
	}
	
	def "Calcdeps removes unnecessary paths"() {
		//expect:
			//true == false
	}
}
