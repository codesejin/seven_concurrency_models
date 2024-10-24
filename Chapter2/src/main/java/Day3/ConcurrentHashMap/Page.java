/***
 * Excerpted from "Seven Concurrency Models in Seven Weeks",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
***/
package Day3.ConcurrentHashMap;

abstract class Page {
  public String getTitle() { throw new UnsupportedOperationException(); }
  public String getText() { throw new UnsupportedOperationException(); }
  public boolean isPoisonPill() { return false; }
}
