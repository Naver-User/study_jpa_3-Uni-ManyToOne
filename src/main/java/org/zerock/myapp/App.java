package org.zerock.myapp;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;


//@Log4j2
@Slf4j
public class App {
	
    public static void main( String[] args ) {
        log.trace("main({}) invoked.", Arrays.toString(args));
        
    } // main
    
} // end class
