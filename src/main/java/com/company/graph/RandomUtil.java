package com.company.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

public class RandomUtil {
    private final Random m_random;
    SortedArrayList<Integer> m_excludeList;

    int m_start=0;
    int m_end=0;

    /**
     * @param start start of range (inclusive)
     * @param end end of range (exclusive)
     */
    public RandomUtil(int start, int end) {
        m_random = new Random(System.currentTimeMillis());
        m_start=start;
        m_end=end-1;
        m_excludeList = new SortedArrayList<Integer>();
    }


    public void init(){
        m_excludeList.clear();
    }
    /**
     * @return the random number within start-end but not one of excludes. Otherwise (no free random) returns -1
     */
    public int getNonRepeatingRandomInt() {
        Integer[] array = m_excludeList.toArray (new Integer [m_excludeList.size()]);
        int toExclude= getRandomWithExclusion(m_random,m_start,m_end, array);
        if(toExclude<0) return -1;
        else if(m_excludeList.contains(toExclude)) {
            return -1;
        }
        else {
            //it's a new valid random!
            m_excludeList.insertSorted(toExclude);
            return toExclude;

        }
    }

    /**
     * @param excludeList
     * @return the random number within start-end but not one of excludes. Otherwise (no free random) returns -1
     */
    public int getNonRepeatingRandomInt(SortedArrayList<Integer> excludeList) {
        //Integer[] array = m_excludeList.toArray (new Integer [m_excludeList.size()]);
        Integer[] array = (Integer[]) excludeList.toArray();
        int toExclude= getRandomWithExclusion(m_random,m_start,m_end, array);
        if(toExclude<0) return -1;
        else if(excludeList.contains(toExclude)) {
            return -1;
        }
        else {
            //it's a new valid random!
            excludeList.insertSorted(toExclude);
            return toExclude;

        }
    }

    /**
     * @param start start of range (inclusive)
     * @param end end of range (exclusive)
     * @param exclude numbers to exclude (= numbers you do not want)
     * @return the random number within start-end but not one of excludes.
     */
    public int getRandomWithExclusion(Random rnd, int start, int end, Integer... exclude) {
        int bound = end - start + 1 - exclude.length;
        if(bound>0) {
            try {
                int random = start + rnd.nextInt(bound);
                for (int ex : exclude) {
                    if (random < ex) {
                        break;
                    }
                    random++;
                }
                return random;
            } catch (Exception e) {
                throw e;
            }
        }
        else return -1;
    }

    public class SortedArrayList<T> extends ArrayList<T> {

        @SuppressWarnings("unchecked")
        public void insertSorted(T value) {
            add(value);
            Comparable<T> cmp = (Comparable<T>) value;
            for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
                Collections.swap(this, i, i-1);
        }
    }
}
