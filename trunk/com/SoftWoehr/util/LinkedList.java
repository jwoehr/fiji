// LinkedList.java
// A linked list implementation.
/*   Copyright *C* 1998, 2001 Jack J. Woehr  */
/*	      All Rights Reserved	     */
/* PO Box 51 Golden, Colorado 80402-0051 USA */
/*	    http://www.softwoehr.com	     */
/*	  http://fiji.sourceforge.net	     */

package com.SoftWoehr.util;

import java.util.*;

public class LinkedList {
    
    public interface Linkable {
        /**
         * @return  */        
        public Linkable getNext();
        /**
         * @param node  */        
        public void setNext(Linkable node);
    }
    
    Linkable head = null;
    
    /**
     * @param node  */    
    public void addHead (Linkable node) {
        node.setNext(head);
        head = node;
    }
    
    /**
     * @param node  */    
    public void addTail (Linkable node) {
        Enumeration e = enumerate();
        if (!e.hasMoreElements()) {
            addHead(node);
        }
        else {
            Linkable lastNode = null;
            while (e.hasMoreElements()) {
                lastNode = (Linkable) e.nextElement();
            }
            if (lastNode == null) {
                head = node;
            }
            else {
                lastNode.setNext(node);
            }
            node.setNext(null); // probably redundant
        }
    }
    
    /**
     * @param node  */    
    public void remove (Linkable node) {
        Linkable previous = null;
        Linkable current = head;
        Enumeration e = enumerate();
        while (e.hasMoreElements()) {
            if (current == e.nextElement()) {
                
                if (previous == null) {
                    
                    if (e.hasMoreElements()) {
                        head = (Linkable) e.nextElement();
                    }
                    else {
                        head = null;
                    }
                }
                
                else {
                    if (e.hasMoreElements()) {
                        previous.setNext((Linkable) e.nextElement());
                        current.setNext(null);
                    }
                    else {
                        previous.setNext(null);
                    }
                }
                
                break;
            }
        }
    }
    
    /**
     * @return  */    
    public int numberOfElements() {
        Enumeration e = enumerate();
        int elements = 0;
        while (e.hasMoreElements()) {
            e.nextElement();
            elements++;
        }
        return elements;
    }
    
    /**
     * @return  */    
    public Enumeration enumerate() {
        
        return new Enumeration() {
            
            Linkable current = head;
            
            public boolean hasMoreElements() {
                return (current != null);
            }
            
            public Object nextElement() {
                if (current == null) {
                    throw new NoSuchElementException("LinkedList");
                }
                Object value = current;
                current = (Linkable) current.getNext();
                return value;
            }
        };
    }
    
    /**
     * @param argv  */    
    public static void main (String argv[]) {
        
        if (argv.length == 0) {
            System.out.println("usage: java LinkedList [arg] [arg] ...");
            System.out.println("... manipulates a linked list of the text argument strings you provide.");
            return;
        }
        
        LinkedList l = new LinkedList();
        
        for (int i = 0; i < argv.length; i++) {
            l.addHead (new LinkableString(argv[i]));
        }
        
        System.out.println("I created a LinkedList of your command-line arguments.");
        System.out.println("It has " + l.numberOfElements() + " elements.");
        System.out.println("Here's a backwards-linked-list listing of all (if any) of the command line arguments:");
        Enumeration e = l.enumerate();
        while (e.hasMoreElements()) {
            System.out.println (((LinkableString) e.nextElement()).string());
        }
        
        System.out.println("Now I'll add an element to the tail.");
        l.addTail (new LinkableString("Hi, I'm the tail node!"));
        System.out.println("Here's a backwards-linked-list listing of all (if any) of the command line arguments:");
        e = l.enumerate();
        while (e.hasMoreElements()) {
            System.out.println (((LinkableString) e.nextElement()).string());
        }
        
        while (l.head != null) {
            System.out.println ("Now I'll remove the head element.");
            l.remove(l.head);
            System.out.println("The list now has " + l.numberOfElements() + " element(s) remaining.");
            e = l.enumerate();
            while (e.hasMoreElements()) {
                System.out.println (((LinkableString) e.nextElement()).string());
            }
        }
        
        return;
    }
    
}

class LinkableString implements LinkedList.Linkable {
    
    public String s;
    private LinkedList.Linkable next = null;
    
    public LinkableString() {this("");}
    /**
     * @param s  */    
    public LinkableString(String s) { this.s = s; }
    /**
     * @return  */    
    public String string() { return s; }
    /**
     * @return  */    
    public LinkedList.Linkable getNext () { return next; }
    /**
     * @param node  */    
    public void setNext(LinkedList.Linkable node) { next = node; }
}

// End of LinkedList.java

