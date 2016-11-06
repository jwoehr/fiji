/* LinkedList.java			     */
/* A classic linked list implementation.     */
/*********************************************/
/*  Copyright *C* 1998, 2001 Jack J. Woehr   */
/*	      All Rights Reserved	     */
/* PO Box 51 Golden, Colorado 80402-0051 USA */
/*	   http://www.softwoehr.com	     */
/*	  http://fiji.sourceforge.net	     */
/*					     */
/*	     This Program is Free	     */
/*		   Softwoehr		     */
/*					     */
/*  Permission to distribute this Softwoehr  */
/* with copyright notice attached is granted.*/
/*					     */
/*  Permission to modify for personal use at */
/*  at home or for your personal use on the  */
/*  job is granted, but you may not publicly */
/*  make available modified versions of this */
/*   program without asking and getting the  */
/*   permission of the author, Jack Woehr.   */
/*					     */
/* The permission will usually be granted if */
/* granted reciprocally by you for the mods. */
/*					     */
/* THERE IS NO GUARANTEE, NO WARRANTY AT ALL */
/*********************************************/
package com.SoftWoehr.util;

import java.util.*;

/** A simple linked list implementation
 */
public class LinkedList {
    
    /** Interface presented by a node
     */
    public interface Linkable {
        
        /** Get next pointer
         * @return next pointer
         */
        public Linkable getNext();
        
        /** Set next pointer to next node
         * @param node next node
         */
        public void setNext(Linkable node);
    }
    
    Linkable head = null;
    
    /** Prepend a node
     * @param node to prepend
     */
    public void addHead(Linkable node) {
        node.setNext(head);
        head = node;
    }
    
    /** Postpend a node
     * @param node to postpend
     */
    public void addTail(Linkable node) {
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
    
    /** Walk through list and find node to remove
     * @param node node to remove
     */
    public void remove(Linkable node) {
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
    
    /** Walk through and count elements (nodes) in list.
     * @return number of elements in list.
     */
    public int numberOfElements() {
        Enumeration e = enumerate();
        int elements = 0;
        while (e.hasMoreElements()) {
            e.nextElement();
            elements++;
        }
        return elements;
    }
    
    /** Return an enumeration of the list
     * @return the enumeration of the list
     */
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
    
    /** Test linked list of argument strings
     * @param argv strings to link
     */
    public static void main(String argv[]) {
        
        if (argv.length == 0) {
            System.out.println("usage: java LinkedList [arg] [arg] ...");
            System.out.println("... manipulates a linked list of the text argument strings you provide.");
            return;
        }
        
        LinkedList l = new LinkedList();
        
        for (int i = 0; i < argv.length; i++) {
            l.addHead(new LinkableString(argv[i]));
        }
        
        System.out.println("I created a LinkedList of your command-line arguments.");
        System.out.println("It has " + l.numberOfElements() + " elements.");
        System.out.println("Here's a backwards-linked-list listing of all (if any) of the command line arguments:");
        Enumeration e = l.enumerate();
        while (e.hasMoreElements()) {
            System.out.println(((LinkableString) e.nextElement()).string());
        }
        
        System.out.println("Now I'll add an element to the tail.");
        l.addTail(new LinkableString("Hi, I'm the tail node!"));
        System.out.println("Here's a backwards-linked-list listing of all (if any) of the command line arguments:");
        e = l.enumerate();
        while (e.hasMoreElements()) {
            System.out.println(((LinkableString) e.nextElement()).string());
        }
        
        while (l.head != null) {
            System.out.println("Now I'll remove the head element.");
            l.remove(l.head);
            System.out.println("The list now has " + l.numberOfElements() + " element(s) remaining.");
            e = l.enumerate();
            while (e.hasMoreElements()) {
                System.out.println(((LinkableString) e.nextElement()).string());
            }
        }
        
        return;
    }
    
}

/** A test class to use to test Linked List. A linkable String class */
class LinkableString implements LinkedList.Linkable {
    
    /** The string itself */
    public String s;
    
    private LinkedList.Linkable next = null;
    
    /** Create empty linkable string */
    public LinkableString() {this("");}
    
    /** Create a linkable string with content.
     * @param s  */
    public LinkableString(String s) { this.s = s; }
    
    /** Return the string itself
     * @return the string
     */
    public String string() { return s; }
    
    /** Get the next node
     * @return the next node */
    public LinkedList.Linkable getNext() { return next; }
    
    /** Set the next node
     * @param node node to set
     */
    public void setNext(LinkedList.Linkable node) { next = node; }
}

// End of LinkedList.java

