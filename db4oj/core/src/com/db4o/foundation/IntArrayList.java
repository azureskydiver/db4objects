/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class IntArrayList implements Iterable4 {
    
    protected int[] i_content;
    
    private int i_count;
    
    public IntArrayList(){
        this(10);
    }
    
    public IntArrayList(int initialSize){
        i_content = new int[initialSize];
    }
    
    public void add(int a_value){
        if(i_count >= i_content.length){
        	int inc = i_content.length / 2;
        	if(inc < 10){
        		inc = 10;
        	}
            int[] temp = new int[i_content.length + inc];
            System.arraycopy(i_content, 0, temp, 0, i_content.length);
            i_content = temp;
        }
        i_content[i_count++] = a_value;
    }
    
    public int indexOf(int a_value) {
        for (int i = 0; i < i_count; i++) {
            if (i_content[i] == a_value){
                return i;
            }
        }
        return -1;
    }
    
    public int size(){
        return i_count;
    }
    
    public long[] asLong(){
        long[] longs = new long[i_count];
        for (int i = 0; i < i_count; i++) {
            longs[i] = i_content[i]; 
        }
        return longs;
    }

	public IntIterator4 intIterator() {
		return new IntIterator4Impl(i_content, i_count);
	}
	
	public Iterator4 iterator() {
		return intIterator();
	}
	
	public int get(int index) {
		return i_content[index];
	}
	
	public void swap(int left, int right) {
		if(left!=right) {
			int swap=i_content[left];
			i_content[left]=i_content[right];
			i_content[right]=swap;
		}
	}

}
